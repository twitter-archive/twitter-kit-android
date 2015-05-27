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

import io.fabric.sdk.android.FabricAndroidTestCase;

public class TwitterSessionSerializerTest extends FabricAndroidTestCase {
    // static unix timestamp so that tests are repeatable and more easily debugged
    private static final long CREATED_AT = 1414450780L;
    public static final String SESSION_JSON = "{\"user_name\":\"\","
            + "\"auth_token\":{"
            + "\"secret\":\"" + TestFixtures.SECRET + "\","
            + "\"token\":\"" + TestFixtures.TOKEN + "\","
            + "\"createdAt\":" + CREATED_AT + "},"
            + "\"id\":-1}";
    public static final String FULL_SESSION_JSON =
            "{\"user_name\":\"" + TestFixtures.SCREEN_NAME + "\","
            + "\"auth_token\":{"
            + "\"secret\":\"" + TestFixtures.SECRET + "\","
            + "\"token\":\"" + TestFixtures.TOKEN + "\","
            + "\"createdAt\":" + CREATED_AT + "},"
            + "\"id\":" + TestFixtures.USER_ID + "}";
    public static final String SESSION_JSON_NULL_USERNAME = "{\"auth_token\":{"
            + "\"secret\":\"secret\","
            + "\"token\":\"token\","
            + "\"createdAt\":" + CREATED_AT + "},"
            + "\"id\":" + TestFixtures.USER_ID + "}";

    private TwitterSession.Serializer serializer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        serializer = new TwitterSession.Serializer();
    }

    public void testDeserialize_sessionWithAuthToken() throws Exception {
        final TwitterSession session = serializer.deserialize(SESSION_JSON);
        final TwitterSession newSession = new TwitterSession(
                new TwitterAuthToken(TestFixtures.TOKEN, TestFixtures.SECRET, CREATED_AT),
                TwitterSession.UNKNOWN_USER_ID, TwitterSession.UNKNOWN_USER_NAME);
        assertEquals(session, newSession);
    }

    public void testDeserialize_session() throws Exception {
        final TwitterSession session = serializer.deserialize(FULL_SESSION_JSON);
        assertEquals(new TwitterSession(new TwitterAuthToken(TestFixtures.TOKEN,
                TestFixtures.SECRET, CREATED_AT), TestFixtures.USER_ID, TestFixtures.SCREEN_NAME),
                session);
    }

    public void testDeserialize_sessionWithNullUserName() throws Exception {
        final TwitterSession session = serializer.deserialize(SESSION_JSON_NULL_USERNAME);
        assertEquals(new TwitterSession(new TwitterAuthToken(TestFixtures.TOKEN,
                TestFixtures.SECRET, CREATED_AT), TestFixtures.USER_ID, null), session);
    }

    public void testDeserialize_nullSerializedSession() throws Exception {
        final TwitterSession session = serializer.deserialize(null);
        assertNull(session);
    }

    public void testSerialize_sessionWithAuthToken() throws Exception {
        final TwitterSession session = new TwitterSession(
                new TwitterAuthToken(TestFixtures.TOKEN, TestFixtures.SECRET, CREATED_AT),
                TwitterSession.UNKNOWN_USER_ID, TwitterSession.UNKNOWN_USER_NAME);
        assertEquals(SESSION_JSON, serializer.serialize(session));
    }

    public void testSerialize_session() throws Exception {
        final TwitterSession session = new TwitterSession(
                new TwitterAuthToken(TestFixtures.TOKEN, TestFixtures.SECRET, CREATED_AT),
                TestFixtures.USER_ID, TestFixtures.SCREEN_NAME);
        assertEquals(FULL_SESSION_JSON, serializer.serialize(session));
    }

    public void testSerialize_sessionWithNullUserName() throws Exception {
        final TwitterSession session = new TwitterSession(
                new TwitterAuthToken(TestFixtures.TOKEN, TestFixtures.SECRET, CREATED_AT),
                TestFixtures.USER_ID, null);
        assertEquals(SESSION_JSON_NULL_USERNAME, serializer.serialize(session));
    }
}
