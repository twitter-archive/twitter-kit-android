/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.twitter.sdk.android.core.internal.oauth;

import com.twitter.sdk.android.core.BuildConfig;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.TwitterApi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Method;

import retrofit.http.Field;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
@SuppressWarnings("checkstyle:linelength")
public class OAuth2ServiceTest  {

    /**
     * Example consumer key and consumer secret values provided by:
     * https://dev.twitter.com/oauth/application-only
     */
    private static final String CONSUMER_KEY = "xvz1evFS4wEEPTGEFPHBog";
    private static final String CONSUMER_SECRET = "L8qq9PZyRg6ieKGEKhZolGC0vJWLw8iEJ88DRdyOg";
    private static final OAuth2Token APP_TOKEN = new OAuth2Token("type", "access");
    private static final GuestAuthToken GUEST_TOKEN = new GuestAuthToken("type", "access", "guest");
    private static final GuestTokenResponse GUEST_RESPONSE = new GuestTokenResponse("guest");

    private final TwitterAuthConfig authConfig;
    private final TwitterCore twitterCore;
    private final OAuth2Service service;

    public OAuth2ServiceTest() {
        authConfig = new TwitterAuthConfig(CONSUMER_KEY, CONSUMER_SECRET);
        twitterCore = new TwitterCore(authConfig);
        service = new OAuth2Service(twitterCore, null , new TwitterApi());
    }

    private class MockOAuth2Api implements OAuth2Service.OAuth2Api {

        @Override
        public void getGuestToken(@Header(AuthHeaders.HEADER_AUTHORIZATION) String auth,
                Callback<GuestTokenResponse> cb) {
            // Does nothing
        }

        @Override
        public void getAppAuthToken(@Header(AuthHeaders.HEADER_AUTHORIZATION) String auth,
                @Field(OAuthConstants.PARAM_GRANT_TYPE) String grantType,
                Callback<OAuth2Token> cb) {
            // Does nothing
        }
    }

    @Test
    public void testGetGuestToken_url() throws NoSuchMethodException {
        final Method method = OAuth2Service.OAuth2Api.class
                .getDeclaredMethod("getGuestToken", String.class, Callback.class);
        final POST post = method.getAnnotation(POST.class);
        assertEquals("/1.1/guest/activate.json", post.value());
    }

    @Test
    public void testGetAppAuthToken_url() throws NoSuchMethodException {
        final Method method = OAuth2Service.OAuth2Api.class
                .getDeclaredMethod("getAppAuthToken", String.class, String.class, Callback.class);
        final POST post = method.getAnnotation(POST.class);
        assertEquals("/oauth2/token", post.value());
    }

    @Test
    public void testGetAppAuthToken_contentType() throws NoSuchMethodException {
        final Method method = OAuth2Service.OAuth2Api.class
                .getDeclaredMethod("getAppAuthToken", String.class, String.class, Callback.class);
        final Headers header = method.getAnnotation(Headers.class);
        assertEquals("Content-Type: application/x-www-form-urlencoded;charset=UTF-8",
                header.value()[0]);
    }

    @Test
    public void testApiHost() {
        final TwitterApi api = new TwitterApi();
        final OAuth2Service service = new OAuth2Service(twitterCore, null, api);
        assertEquals(api, service.getApi());
    }

    @Test
    public void testGetUserAgent() {
        final TwitterApi api = new TwitterApi();
        final OAuth2Service service = new OAuth2Service(twitterCore, null, api);
        final String userAgent
                = TwitterApi.buildUserAgent("TwitterAndroidSDK", twitterCore.getVersion());
        assertEquals(userAgent, service.getUserAgent());
    }

    @Test
    public void testRequestAppAuthToken() {
        service.api = new MockOAuth2Api() {
            @Override
            public void getAppAuthToken(@Header(AuthHeaders.HEADER_AUTHORIZATION) String auth,
                                        @Field(OAuthConstants.PARAM_GRANT_TYPE) String grantType,
                                        Callback<OAuth2Token> cb) {
                assertEquals("Basic eHZ6MWV2RlM0d0VFUFRHRUZQSEJvZzpMOHFxOVBaeVJnNmllS0dFS2hab2xH" +
                                "QzB2SldMdzhpRUo4OERSZHlPZw==",
                        auth);
                assertEquals(OAuthConstants.GRANT_TYPE_CLIENT_CREDENTIALS, grantType);
            }
        };
        service.requestAppAuthToken(null);
    }

    @Test
    public void testRequestGuestToken() {
        final OAuth2Token token = new OAuth2Token("type", "token");
        final String bearerAuth = OAuthConstants.AUTHORIZATION_BEARER + " "
                + token.getAccessToken();

        service.api = new MockOAuth2Api() {
            @Override
            public void getGuestToken(@Header(AuthHeaders.HEADER_AUTHORIZATION) String auth,
                                          Callback<GuestTokenResponse> cb) {
                assertEquals(bearerAuth, auth);
            }
        };

        service.requestGuestToken(null, token);
    }

    @Test
    public void testRequestGuestOrAppAuth_guestAuthSuccess() {

        service.api = new MockOAuth2Api() {
            @Override
            public void getGuestToken(@Header(AuthHeaders.HEADER_AUTHORIZATION) String auth,
                                          Callback<GuestTokenResponse> cb) {
                cb.success(new Result<>(GUEST_RESPONSE, null));
            }

            @Override
            public void getAppAuthToken(@Header(AuthHeaders.HEADER_AUTHORIZATION) String auth,
                                        @Field(OAuthConstants.PARAM_GRANT_TYPE) String grantType,
                                        Callback<OAuth2Token> cb) {
                cb.success(new Result<>(APP_TOKEN, null));
            }
        };

        service.requestGuestOrAppAuthToken(new Callback<OAuth2Token>() {
            @Override
            public void success(Result<OAuth2Token> result) {
                assertEquals(GUEST_TOKEN, result.data);
            }

            @Override
            public void failure(TwitterException error) {
                fail();
            }
        });
    }

    @Test
    public void testRequestGuestOrAppAuth_guestFailure() {

        service.api = new MockOAuth2Api() {
            @Override
            public void getGuestToken(@Header(AuthHeaders.HEADER_AUTHORIZATION) String auth,
                                          Callback<GuestTokenResponse> cb) {
                cb.failure(mock(TwitterException.class));
            }

            @Override
            public void getAppAuthToken(@Header(AuthHeaders.HEADER_AUTHORIZATION) String auth,
                                        @Field(OAuthConstants.PARAM_GRANT_TYPE) String grantType,
                                        Callback<OAuth2Token> cb) {
                cb.success(new Result<>(APP_TOKEN, null));
            }
        };

        service.requestGuestOrAppAuthToken(new Callback<OAuth2Token>() {
            @Override
            public void success(Result<OAuth2Token> result) {
                assertEquals(APP_TOKEN, result.data);
            }

            @Override
            public void failure(TwitterException error) {
                fail();
            }
        });
    }

    @Test
    public void testRequestGuestOrAppAuth_appAuthFailure() {

        service.api = new MockOAuth2Api() {
            @Override
            public void getGuestToken(@Header(AuthHeaders.HEADER_AUTHORIZATION) String auth,
                    Callback<GuestTokenResponse> cb) {
                // We should never get here, since app auth failure would prevent us from making the
                // guest token request.
                fail();
            }

            @Override
            public void getAppAuthToken(@Header(AuthHeaders.HEADER_AUTHORIZATION) String auth,
                    @Field(OAuthConstants.PARAM_GRANT_TYPE) String grantType,
                    Callback<OAuth2Token> cb) {
                cb.failure(mock(TwitterException.class));
            }
        };

        service.requestGuestOrAppAuthToken(new Callback<OAuth2Token>() {
            @Override
            public void success(Result<OAuth2Token> result) {
                fail();
            }

            @Override
            public void failure(TwitterException error) {
                assertNotNull(error);
            }
        });
    }
}
