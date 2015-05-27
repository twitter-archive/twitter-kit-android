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

import io.fabric.sdk.android.FabricAndroidTestCase;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.network.DefaultHttpRequestFactory;
import io.fabric.sdk.android.services.network.HttpMethod;
import io.fabric.sdk.android.services.network.HttpRequest;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.TwitterApi;

import org.apache.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import retrofit.client.Response;
import retrofit.http.Header;
import retrofit.http.Query;
import retrofit.mime.TypedInput;

import static org.mockito.Mockito.*;

public class OAuth1aServiceTest extends FabricAndroidTestCase {

    private TwitterAuthConfig authConfig;
    private TwitterCore twitterCore;
    private OAuth1aService service;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        authConfig = new TwitterAuthConfig("key", "secret");
        twitterCore = new TwitterCore(authConfig);
        service = new OAuth1aService(twitterCore, null , new TwitterApi());
    }

    public void testGetTempTokenUrl() {
        assertEquals("https://api.twitter.com/oauth/request_token", service.getTempTokenUrl());
    }

    public void testGetAccessTokenUrl() throws NoSuchMethodException {
        assertEquals("https://api.twitter.com/oauth/access_token", service.getAccessTokenUrl());
    }

    public void testSignRequest() {
        final TwitterAuthConfig config = new TwitterAuthConfig("consumerKey", "consumerSecret");
        final DefaultHttpRequestFactory requestFactory = new DefaultHttpRequestFactory();
        final TwitterAuthToken accessToken = new TwitterAuthToken("token", "tokenSecret");
        final HttpRequest request = requestFactory.buildHttpRequest(HttpMethod.GET,
                "https://api.twitter.com/1.1/statuses/home_timeline.json");
        OAuth1aService.signRequest(config, accessToken, request.getConnection(), null);
        // Verify that request contains authorization header.
        final HttpURLConnection connection = request.getConnection();
        assertNotNull(connection.getRequestProperty(HttpRequest.HEADER_AUTHORIZATION));

        // TODO: Make it so that nonce and timestamp can be specified for testing puproses?
    }

    public void testRequestTempToken() {
        service.api = new MockOAuth1aService() {
            @Override
            public void getTempToken(@Header(AuthHeaders.HEADER_AUTHORIZATION) String auth,
                                     Callback<Response> cb) {
                assertTrue(auth.contains(OAuthConstants.PARAM_CALLBACK));
            }
        };
        service.requestTempToken(null);
    }

    public void testRequestAccessToken() {
        final TwitterAuthToken token = new TwitterAuthToken("token", "secret");
        final String verifier = "verifier";
        service.api = new MockOAuth1aService() {
            @Override
            public void getAccessToken(@Header(AuthHeaders.HEADER_AUTHORIZATION) String auth,
                                       @Query(OAuthConstants.PARAM_VERIFIER) String innerVerifier,
                                       Callback<Response> cb) {

                assertEquals(verifier, innerVerifier);
                assertNotNull(auth);
                assertTrue(auth.contains(token.token));
            }
        };
        service.requestAccessToken(null, token, verifier);
    }

    public void testApiHost() {
        final TwitterApi api = new TwitterApi();
        final OAuth1aService localService = new OAuth1aService(twitterCore, null, api);
        assertEquals(api, localService.getApi());
    }

    public void testGetUserAgent() {
        final String userAgent = TwitterApi.buildUserAgent("TwitterAndroidSDK",
                twitterCore.getVersion());
        assertEquals(userAgent, service.getUserAgent());
    }

    public void testBuildCallbackUrl() {
        final String callbackUrl = service.buildCallbackUrl(authConfig);

        assertEquals(String.format("twittersdk://callback?version=%s&app=%s",
                twitterCore.getVersion(), authConfig.getConsumerKey()), callbackUrl);
    }

    public void testGetAuthorizeUrl() {
        final TwitterAuthToken authToken = new TwitterAuthToken("token", "secret");
        final String authorizeUrl = service.getAuthorizeUrl(authToken);
        assertEquals("https://api.twitter.com/oauth/authorize?oauth_token=token", authorizeUrl);
    }

    public void testParseAuthResponse() {
        final String response = "oauth_token=7588892-kagSNqWge8gB1WwE3plnFsJHAZVfxWD7Vb57p0b4&"
                + "oauth_token_secret=PbKfYqSryyeKDWz4ebtY3o5ogNLG11WJuZBc9fQrQo&"
                + "screen_name=test&user_id=1";
        final OAuthResponse authResponse = OAuth1aService.parseAuthResponse(response);
        assertEquals("7588892-kagSNqWge8gB1WwE3plnFsJHAZVfxWD7Vb57p0b4",
                authResponse.authToken.token);
        assertEquals("PbKfYqSryyeKDWz4ebtY3o5ogNLG11WJuZBc9fQrQo", authResponse.authToken.secret);
        assertEquals("test", authResponse.userName);
        assertEquals(1L, authResponse.userId);
    }

    public void testParseAuthResponse_noQueryParameters() {
        final String response = "noQueryParameters";
        final OAuthResponse authResponse = OAuth1aService.parseAuthResponse(response);
        assertNull(authResponse);
    }

    public void testParseAuthResponse_noToken() {
        final String response = "oauth_token_secret=PbKfYqSryyeKDWz4ebtY3o5ogNLG11WJuZBc9fQrQo&"
                + "screen_name=test&user_id=1";
        final OAuthResponse authResponse = OAuth1aService.parseAuthResponse(response);
        assertNull(authResponse);
    }

    public void testParseAuthResponse_noSecret() {
        final String response = "oauth_token=7588892-kagSNqWge8gB1WwE3plnFsJHAZVfxWD7Vb57p0b4&"
                + "screen_name=test&user_id=1";
        final OAuthResponse authResponse = OAuth1aService.parseAuthResponse(response);
        assertNull(authResponse);
    }

    public void testParseAuthResponse_noScreenName() {
        final String response = "oauth_token=7588892-kagSNqWge8gB1WwE3plnFsJHAZVfxWD7Vb57p0b4&"
                + "oauth_token_secret=PbKfYqSryyeKDWz4ebtY3o5ogNLG11WJuZBc9fQrQo&"
                + "user_id=1";
        final OAuthResponse authResponse = OAuth1aService.parseAuthResponse(response);
        assertEquals("7588892-kagSNqWge8gB1WwE3plnFsJHAZVfxWD7Vb57p0b4",
                authResponse.authToken.token);
        assertEquals("PbKfYqSryyeKDWz4ebtY3o5ogNLG11WJuZBc9fQrQo", authResponse.authToken.secret);
        assertNull(authResponse.userName);
        assertEquals(1L, authResponse.userId);
    }

    public void testParseAuthResponse_noUserId() {
        final String response = "oauth_token=7588892-kagSNqWge8gB1WwE3plnFsJHAZVfxWD7Vb57p0b4&"
                + "oauth_token_secret=PbKfYqSryyeKDWz4ebtY3o5ogNLG11WJuZBc9fQrQo&"
                + "screen_name=test";
        final OAuthResponse authResponse = OAuth1aService.parseAuthResponse(response);
        assertEquals("7588892-kagSNqWge8gB1WwE3plnFsJHAZVfxWD7Vb57p0b4",
                authResponse.authToken.token);
        assertEquals("PbKfYqSryyeKDWz4ebtY3o5ogNLG11WJuZBc9fQrQo", authResponse.authToken.secret);
        assertEquals("test", authResponse.userName);
        assertEquals(0L, authResponse.userId);
    }

    public void testCallbackWrapperSuccess() throws IOException {
        final String response = "oauth_token=7588892-kagSNqWge8gB1WwE3plnFsJHAZVfxWD7Vb57p0b4&"
                + "oauth_token_secret=PbKfYqSryyeKDWz4ebtY3o5ogNLG11WJuZBc9fQrQo&"
                + "screen_name=test&user_id=1";
        final Callback<OAuthResponse> callback = new Callback<OAuthResponse>() {
            @Override
            public void success(Result<OAuthResponse> result) {
                final OAuthResponse authResponse = result.data;
                assertEquals("7588892-kagSNqWge8gB1WwE3plnFsJHAZVfxWD7Vb57p0b4",
                        authResponse.authToken.token);
                assertEquals("PbKfYqSryyeKDWz4ebtY3o5ogNLG11WJuZBc9fQrQo",
                        authResponse.authToken.secret);
                assertEquals("test", authResponse.userName);
                assertEquals(1L, authResponse.userId);
            }

            @Override
            public void failure(TwitterException exception) {
                fail();
            }
        };
        setupCallbackWrapperTest(response, callback);
    }

    private void setupCallbackWrapperTest(String responseStr,
            Callback<OAuthResponse> authResponseCallback) throws IOException {
        final Callback<Response> callbackWrapper = service.getCallbackWrapper(authResponseCallback);
        final TypedInput mockValue = mock(TypedInput.class);
        final Response mockResponse = new Response("url", HttpStatus.SC_OK, "reason",
                new ArrayList<retrofit.client.Header>(), mockValue);
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(responseStr.getBytes("UTF-8"));
            when(mockValue.in()).thenReturn(inputStream);
            callbackWrapper.success(new Result<>(mockResponse, mockResponse));
        } finally {
            CommonUtils.closeQuietly(inputStream);
        }
    }

    public void testCallbackWrapperSuccess_noToken() throws IOException {
        final String response = "oauth_token_secret=PbKfYqSryyeKDWz4ebtY3o5ogNLG11WJuZBc9fQrQo&"
                + "screen_name=test&user_id=1";
        final Callback<OAuthResponse> callback = new Callback<OAuthResponse>() {
            @Override
            public void success(Result<OAuthResponse> result) {
                fail();
            }

            @Override
            public void failure(TwitterException exception) {
                assertNotNull(exception);
            }
        };
        setupCallbackWrapperTest(response, callback);
    }

    public void testCallbackWrapperSuccess_iOException() throws IOException {
        final Callback<OAuthResponse> callback = new Callback<OAuthResponse>() {
            @Override
            public void success(Result<OAuthResponse> result) {
                fail();
            }

            @Override
            public void failure(TwitterException exception) {
                assertNotNull(exception);
            }
        };
        final Callback<Response> callbackWrapper = service.getCallbackWrapper(callback);
        final TypedInput mockValue = mock(TypedInput.class);
        final Response mockResponse = new Response("url", HttpStatus.SC_OK, "reason",
                new ArrayList<retrofit.client.Header>(), mockValue);
        when(mockValue.in()).thenThrow(mock(IOException.class));
        callbackWrapper.success(new Result<>(mockResponse, mockResponse));
    }

    public void testCallbackWrapperFailure() {
        final Callback<OAuthResponse> authResponseCallback = mock(Callback.class);
        final Callback<Response> callbackWrapper = service.getCallbackWrapper(authResponseCallback);
        final TwitterException mockException = mock(TwitterException.class);
        callbackWrapper.failure(mockException);
        verify(authResponseCallback).failure(eq(mockException));
    }

    private static class MockOAuth1aService implements OAuth1aService.OAuthApi {

        @Override
        public void getTempToken(@Header(AuthHeaders.HEADER_AUTHORIZATION) String auth,
                Callback<Response> cb) {
            // Does nothing
        }

        @Override
        public void getAccessToken(@Header(AuthHeaders.HEADER_AUTHORIZATION) String auth,
                @Query(OAuthConstants.PARAM_VERIFIER) String verifier,
                Callback<Response> cb) {
            // Does nothing
        }
    }
}
