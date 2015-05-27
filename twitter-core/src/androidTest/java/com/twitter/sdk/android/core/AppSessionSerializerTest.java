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

import com.twitter.sdk.android.core.internal.oauth.GuestAuthToken;
import com.twitter.sdk.android.core.internal.oauth.OAuth2Token;

import io.fabric.sdk.android.FabricAndroidTestCase;

public class AppSessionSerializerTest extends FabricAndroidTestCase {
    private static final long CREATED_AT = 1414450780L;
    private static final String JSON_SESSION_APP = "{\"auth_token\":{\"auth_type\":\"oauth2\","
            + "\"auth_token\":{\"access_token\":\"accessToken\","
            + "\"token_type\":\"tokenType\",\"createdAt\":1414450780}},\"id\":0}";
    private static final String JSON_SESSION_GUEST = "{\"auth_token\":{\"auth_type\":\"guest\","
            + "\"auth_token\":{\"guest_token\":\"guestToken\",\"access_token\":\"accessToken\","
            + "\"token_type\":\"tokenType\",\"createdAt\":1414450780}},\"id\":0}";
    private static final String JSON_SESSION_INVALID_AUTH_TYPE =
            "{\"auth_token\":{\"auth_type\":\"INVALID\","
            + "\"auth_token\":{\"guest_token\":\"guestToken\",\"access_token\":\"accessToken\","
            + "\"token_type\":\"tokenType\",\"createdAt\":1414450780}},\"id\":0}";

    private static final String TEST_TOKEN_TYPE = "tokenType";
    private static final String TEST_ACCESS_TOKEN = "accessToken";
    private static final String TEST_GUEST_TOKEN = "guestToken";

    private AppSession.Serializer serializer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        serializer = new AppSession.Serializer();
    }

    public void testSerialize_sessionNull() {
        assertEquals("", serializer.serialize(null));
    }

    public void testSerialize_sessionAuthTokenIsOAuth2Token() {
        final AppSession session = new AppSession(new OAuth2Token(TEST_TOKEN_TYPE,
                TEST_ACCESS_TOKEN, CREATED_AT));
        assertEquals(JSON_SESSION_APP, serializer.serialize(session));
    }

    public void testSerialze_sessionAuthTokenIsGuestAuthToken() {
        final AppSession session = new AppSession(new GuestAuthToken(TEST_TOKEN_TYPE,
                TEST_ACCESS_TOKEN, TEST_GUEST_TOKEN, CREATED_AT));
        assertEquals(JSON_SESSION_GUEST, serializer.serialize(session));
    }

    public void testDeserialize_serializedStringNull() {
        assertEquals(null, serializer.deserialize(null));
    }

    public void testDeserialize_serializedStringEmpty() {
        assertEquals(null, serializer.deserialize(""));
    }

    public void testDeserialize_serializedStringAuthTokenIsOAuth2Token() {
        final AppSession session = serializer.deserialize(JSON_SESSION_APP);
        assertEquals(OAuth2Token.class, session.getAuthToken().getClass());
        assertEquals(TEST_TOKEN_TYPE, session.getAuthToken().getTokenType());
        assertEquals(TEST_ACCESS_TOKEN, session.getAuthToken().getAccessToken());
    }

    public void testDeserialize_serializedStringAuthTokenIsGuestAuthToken() {
        final AppSession session = serializer.deserialize(JSON_SESSION_GUEST);
        assertEquals(GuestAuthToken.class, session.getAuthToken().getClass());
        assertEquals(TEST_TOKEN_TYPE, session.getAuthToken().getTokenType());
        assertEquals(TEST_ACCESS_TOKEN, session.getAuthToken().getAccessToken());
        assertEquals(TEST_GUEST_TOKEN, ((GuestAuthToken) session.getAuthToken()).getGuestToken());
    }

    public void testDeserialize_serializedStringAuthTokenIsInvalid() {
        final AppSession session = serializer.deserialize(JSON_SESSION_INVALID_AUTH_TYPE);
        assertNull(session);
    }
}
