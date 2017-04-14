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

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twitter.sdk.android.core.internal.oauth.GuestAuthToken;
import com.twitter.sdk.android.core.internal.persistence.SerializationStrategy;

public class GuestSession extends Session<GuestAuthToken> {
    public static final long LOGGED_OUT_USER_ID = 0L;

    /**
     * @param authToken Auth token
     *
     * @throws java.lang.IllegalArgumentException if token argument is null
     */
    public GuestSession(GuestAuthToken authToken) {
        super(authToken, LOGGED_OUT_USER_ID);
    }

    public static class Serializer implements SerializationStrategy<GuestSession> {

        private final Gson gson;

        public Serializer() {
            this.gson = new GsonBuilder()
                    .registerTypeAdapter(GuestAuthToken.class, new AuthTokenAdapter())
                    .create();
        }

        @Override
        public String serialize(GuestSession session) {
            if (session != null && session.getAuthToken() != null) {
                try {
                    return gson.toJson(session);
                } catch (Exception e) {
                    Twitter.getLogger().d(TwitterCore.TAG,
                            "Failed to serialize session " + e.getMessage());
                }
            }
            return "";
        }

        @Override
        public GuestSession deserialize(String serializedSession) {
            if (!TextUtils.isEmpty(serializedSession)) {
                try {
                    return gson.fromJson(serializedSession, GuestSession.class);
                } catch (Exception e) {
                    Twitter.getLogger().d(TwitterCore.TAG,
                            "Failed to deserialize session " + e.getMessage());
                }
            }
            return null;
        }
    }
}
