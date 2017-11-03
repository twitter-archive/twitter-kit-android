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

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.TwitterApi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;
import java.lang.reflect.Method;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.mock.Calls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@SuppressWarnings("checkstyle:linelength")
public class OAuth2ServiceTest  {

    /**
     * Example consumer key and consumer secret values provided by:
     * https://dev.twitter.com/oauth/application-only
     */
    static final String CONSUMER_KEY = "xvz1evFS4wEEPTGEFPHBog";
    static final String CONSUMER_SECRET = "L8qq9PZyRg6ieKGEKhZolGC0vJWLw8iEJ88DRdyOg";
    static final TwitterAuthConfig AUTH_CONFIG = new TwitterAuthConfig(CONSUMER_KEY, CONSUMER_SECRET);
    static final String EXPECTED_BASIC_AUTH = "Basic eHZ6MWV2RlM0d0VFUFRHRUZQSEJvZzpMOHFxOVBaeVJnNmllS0dFS2hab2xHQzB2SldMdzhpRUo4OERSZHlPZw==";
    static final OAuth2Token APP_TOKEN = new OAuth2Token("type", "access");
    static final GuestAuthToken GUEST_TOKEN = new GuestAuthToken("type", "access", "guest");
    static final GuestTokenResponse GUEST_RESPONSE = new GuestTokenResponse("guest");

    private TwitterCore twitterCore;
    private TwitterApi twitterApi;
    private OAuth2Service service;

    @Before
    public void setUp() {
        twitterCore = mock(TwitterCore.class);
        when(twitterCore.getAuthConfig()).thenReturn(AUTH_CONFIG);

        twitterApi = new TwitterApi();
        service = new OAuth2Service(twitterCore, twitterApi);
    }

    public class MockOAuth2Api implements OAuth2Service.OAuth2Api {

        @Override
        public Call<GuestTokenResponse> getGuestToken(@Header(OAuthConstants.HEADER_AUTHORIZATION) String auth) {
            return Calls.response(Response.success(GUEST_RESPONSE));
        }

        @Override
        public Call<OAuth2Token> getAppAuthToken(@Header(OAuthConstants.HEADER_AUTHORIZATION) String auth,
                                                 @Field(OAuthConstants.PARAM_GRANT_TYPE) String grantType) {
            return Calls.response(Response.success(APP_TOKEN));
        }
    }

    @Test
    public void testGetGuestToken_url() throws NoSuchMethodException {
        final Method method = OAuth2Service.OAuth2Api.class
                .getDeclaredMethod("getGuestToken", String.class);
        final POST post = method.getAnnotation(POST.class);
        assertEquals("/1.1/guest/activate.json", post.value());
    }

    @Test
    public void testGetAppAuthToken_url() throws NoSuchMethodException {
        final Method method = OAuth2Service.OAuth2Api.class
                .getDeclaredMethod("getAppAuthToken", String.class, String.class);
        final POST post = method.getAnnotation(POST.class);
        assertEquals("/oauth2/token", post.value());
    }

    @Test
    public void testGetAppAuthToken_contentType() throws NoSuchMethodException {
        final Method method = OAuth2Service.OAuth2Api.class
                .getDeclaredMethod("getAppAuthToken", String.class, String.class);
        final Headers header = method.getAnnotation(Headers.class);
        assertEquals("Content-Type: application/x-www-form-urlencoded;charset=UTF-8",
                header.value()[0]);
    }

    @Test
    public void testApiHost() {
        assertEquals(twitterApi, service.getApi());
    }

    @Test
    public void testGetUserAgent() {
        final String userAgent
                = TwitterApi.buildUserAgent("TwitterAndroidSDK", twitterCore.getVersion());
        assertEquals(userAgent, service.getUserAgent());
    }

    @Test
    public void testRequestAppAuthToken() {
        service.api = spy(new MockOAuth2Api());
        service.requestAppAuthToken(mock(Callback.class));

        verify(service.api).getAppAuthToken(EXPECTED_BASIC_AUTH,
                OAuthConstants.GRANT_TYPE_CLIENT_CREDENTIALS);
    }

    @Test
    public void testRequestGuestToken() {
        final OAuth2Token token = new OAuth2Token("type", "token");
        final String bearerAuth = OAuthConstants.AUTHORIZATION_BEARER + " "
                + token.getAccessToken();

        service.api = spy(new MockOAuth2Api());
        service.requestGuestToken(mock(Callback.class), token);

        verify(service.api).getGuestToken(bearerAuth);
    }

    @Test
    public void testRequestGuestAuthToken_guestAuthSuccess() {

        service.api = new MockOAuth2Api();
        service.requestGuestAuthToken(new Callback<GuestAuthToken>() {
            @Override
            public void success(Result<GuestAuthToken> result) {
                assertEquals(GUEST_TOKEN, result.data);
            }

            @Override
            public void failure(TwitterException error) {
                fail();
            }
        });
    }

    @Test
    public void testRequestGuestAuthToken_guestFailure() {

        service.api = new MockOAuth2Api() {
            @Override
            public Call<GuestTokenResponse> getGuestToken(@Header(OAuthConstants.HEADER_AUTHORIZATION) String auth) {
                return Calls.failure(new IOException());
            }
        };

        service.requestGuestAuthToken(new Callback<GuestAuthToken>() {
            @Override
            public void success(Result<GuestAuthToken> result) {
                fail();
            }

            @Override
            public void failure(TwitterException error) {
                assertNotNull(error);
            }
        });
    }

    @Test
    public void testRequestGuestAuthToken_appAuthFailure() {

        service.api = new MockOAuth2Api() {
            @Override
            public Call<OAuth2Token> getAppAuthToken(@Header(OAuthConstants.HEADER_AUTHORIZATION) String auth,
                    @Field(OAuthConstants.PARAM_GRANT_TYPE) String grantType) {
                return Calls.failure(new IOException());
            }
        };

        service.requestGuestAuthToken(new Callback<GuestAuthToken>() {
            @Override
            public void success(Result<GuestAuthToken> result) {
                fail();
            }

            @Override
            public void failure(TwitterException error) {
                assertNotNull(error);
            }
        });
    }
}
