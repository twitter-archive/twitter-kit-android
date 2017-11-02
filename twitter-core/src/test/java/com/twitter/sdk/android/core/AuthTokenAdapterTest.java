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

package com.twitter.sdk.android.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twitter.sdk.android.core.internal.oauth.GuestAuthToken;
import com.twitter.sdk.android.core.internal.oauth.OAuth2Token;
import com.twitter.sdk.android.core.internal.oauth.OAuthUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class AuthTokenAdapterTest  {
    private static final long CREATED_AT = 1414450780L;
    private static final String TOKEN_TYPE = "testTokenType";
    private static final String ACCESS_TOKEN = "testAccessToken";
    private static final String GUEST_TOKEN = "testGuestToken";
    private static final String JSON_OAUTH1A_TOKEN
            = "{\"authToken\":{\"auth_type\":\"oauth1a\","
            + "\"auth_token\":{"
            + "\"token\":\"" + TestFixtures.TOKEN + "\","
            + "\"secret\":\"" + TestFixtures.SECRET + "\","
            + "\"created_at\":" + CREATED_AT + "}}}";
    private static final String JSON_OAUTH2_TOKEN
            = "{\"authToken\":{\"auth_type\":\"oauth2\","
            + "\"auth_token\":{"
            + "\"token_type\":\"" + TOKEN_TYPE + "\","
            + "\"access_token\":\"" + ACCESS_TOKEN + "\","
            + "\"created_at\":" + CREATED_AT + "}}}";
    private static final String JSON_GUEST_AUTH_TOKEN
            = "{\"authToken\":{\"auth_type\":\"guest\","
            + "\"auth_token\":{\"guest_token\":\"" + GUEST_TOKEN + "\","
            + "\"token_type\":\"" + TOKEN_TYPE + "\","
            + "\"access_token\":\"" + ACCESS_TOKEN + "\","
            + "\"created_at\":" + CREATED_AT + "}}}";
    private static final String JSON_APP_AUTH_TOKEN
            = "{\"authToken\":{\"auth_type\":\"app\","
            + "\"auth_token\":{"
            + "\"token_type\":\"" + TOKEN_TYPE + "\","
            + "\"access_token\":\"" + ACCESS_TOKEN + "\","
            + "\"created_at\":" + CREATED_AT + "}}}";
    private static final String JSON_OAUTH1A_TOKEN_MISSING_CREATED_AT
            = "{\"authToken\":{\"auth_type\":\"oauth1a\","
            + "\"auth_token\":{\"secret\":\"" + TestFixtures.SECRET + "\","
            + "\"token\":\"" + TestFixtures.TOKEN + "\"}}}";
    private static final String JSON_OAUTH2_TOKEN_MISSING_CREATED_AT
            = "{\"authToken\":{\"auth_type\":\"oauth2\","
            + "\"auth_token\":{\"access_token\":\"" + ACCESS_TOKEN + "\","
            + "\"token_type\":\"" + TOKEN_TYPE + "\"}}}";
    private static final String JSON_GUEST_AUTH_TOKEN_MISSING_CREATED_AT
            = "{\"authToken\":{\"auth_type\":\"guest\","
            + "\"auth_token\":{\"guest_token\":\"" + GUEST_TOKEN + "\","
            + "\"access_token\":\"" + ACCESS_TOKEN + "\","
            + "\"token_type\":\"" + TOKEN_TYPE + "\"}}}";

    private Gson gson;

    @Before
    public void setUp() throws Exception {

        gson = new GsonBuilder()
                .registerTypeAdapter(AuthToken.class, new AuthTokenAdapter())
                .create();
    }

    @Test
    public void testGetAuthTypeString() {
        assertEquals("oauth1a", AuthTokenAdapter.getAuthTypeString(TwitterAuthToken.class));
    }

    @Test
    public void testGetAuthTypeString_unregisteredAuthType() {
        assertEquals("", AuthTokenAdapter.getAuthTypeString(TestAuthToken.class));
    }

    @Test
    public void testSerialize_oauth1aToken() {
        final AuthTokenWrapper authTokenWrapper = new AuthTokenWrapper(
                new TwitterAuthToken(TestFixtures.TOKEN, TestFixtures.SECRET, CREATED_AT));
        final String json = gson.toJson(authTokenWrapper);
        assertEquals(json, JSON_OAUTH1A_TOKEN, json);
    }

    @Test
    public void testSerialize_oauth2Token() {
        final AuthTokenWrapper test = new AuthTokenWrapper(
                OAuthUtils.createOAuth2Token(TOKEN_TYPE, ACCESS_TOKEN, CREATED_AT));
        final String json = gson.toJson(test);
        assertEquals(json, JSON_OAUTH2_TOKEN, json);
    }

    @Test
    public void testSerialize_guestAuthToken() {
        final AuthTokenWrapper test = new AuthTokenWrapper(
                OAuthUtils.createGuestAuthToken(TOKEN_TYPE, ACCESS_TOKEN, GUEST_TOKEN,
                        CREATED_AT));
        final String json = gson.toJson(test);
        assertEquals(json, JSON_GUEST_AUTH_TOKEN, json);
    }

    @Test
    public void testDeserialize_oauth1aToken() {
        final AuthTokenWrapper authTokenWrapper = gson.fromJson(JSON_OAUTH1A_TOKEN,
                AuthTokenWrapper.class);
        assertTrue(authTokenWrapper.authToken instanceof TwitterAuthToken);
        final TwitterAuthToken authToken = (TwitterAuthToken) authTokenWrapper.authToken;
        assertEquals(TestFixtures.TOKEN, authToken.token);
        assertEquals(TestFixtures.SECRET, authToken.secret);
    }

    @Test
    public void testDeserialize_oauth2Token() {
        final AuthTokenWrapper authTokenWrapper = gson.fromJson(JSON_OAUTH2_TOKEN,
                AuthTokenWrapper.class);
        assertTrue(authTokenWrapper.authToken instanceof OAuth2Token);
        final OAuth2Token authToken = (OAuth2Token) authTokenWrapper.authToken;
        assertEquals(TOKEN_TYPE, authToken.getTokenType());
        assertEquals(ACCESS_TOKEN, authToken.getAccessToken());
    }

    @Test
    public void testDeserialize_guestAuthToken() {
        final AuthTokenWrapper authTokenWrapper = gson.fromJson(JSON_GUEST_AUTH_TOKEN,
                AuthTokenWrapper.class);
        assertTrue(authTokenWrapper.authToken instanceof GuestAuthToken);
        final GuestAuthToken authToken = (GuestAuthToken) authTokenWrapper.authToken;
        assertEquals(GUEST_TOKEN, authToken.getGuestToken());
        assertEquals(TOKEN_TYPE, authToken.getTokenType());
        assertEquals(ACCESS_TOKEN, authToken.getAccessToken());
    }

    @Test
    public void testDeserialize_oauth1aTokenMissingCreatedAt() {
        final AuthTokenWrapper authTokenWrapper = gson.fromJson(
                JSON_OAUTH1A_TOKEN_MISSING_CREATED_AT, AuthTokenWrapper.class);
        assertTrue(authTokenWrapper.authToken instanceof TwitterAuthToken);
        final TwitterAuthToken authToken = (TwitterAuthToken) authTokenWrapper.authToken;
        assertEquals(TestFixtures.TOKEN, authToken.token);
        assertEquals(TestFixtures.SECRET, authToken.secret);
        assertEquals(0, authToken.createdAt);
    }

    @Test
    public void testDeserialize_oauth2TokenMissingCreatedAt() {
        final AuthTokenWrapper authTokenWrapper = gson.fromJson(
                JSON_OAUTH2_TOKEN_MISSING_CREATED_AT, AuthTokenWrapper.class);
        assertTrue(authTokenWrapper.authToken instanceof OAuth2Token);
        final OAuth2Token authToken = (OAuth2Token) authTokenWrapper.authToken;
        assertEquals(TOKEN_TYPE, authToken.getTokenType());
        assertEquals(ACCESS_TOKEN, authToken.getAccessToken());
        assertEquals(0, authToken.createdAt);
    }

    @Test
    public void testDeserialize_guestAuthTokenMissingCreatedAt() {
        final AuthTokenWrapper authTokenWrapper = gson.fromJson(
                JSON_GUEST_AUTH_TOKEN_MISSING_CREATED_AT, AuthTokenWrapper.class);
        assertTrue(authTokenWrapper.authToken instanceof GuestAuthToken);
        final GuestAuthToken authToken = (GuestAuthToken) authTokenWrapper.authToken;
        assertEquals(GUEST_TOKEN, authToken.getGuestToken());
        assertEquals(TOKEN_TYPE, authToken.getTokenType());
        assertEquals(ACCESS_TOKEN, authToken.getAccessToken());
        assertEquals(0, authToken.createdAt);
    }

    private static class AuthTokenWrapper {
        final AuthToken authToken;

        AuthTokenWrapper(AuthToken authToken) {
            this.authToken = authToken;
        }
    }

    private static class TestAuthToken extends AuthToken {

        @Override
        public boolean isExpired() {
            return false;
        }
    }
}
