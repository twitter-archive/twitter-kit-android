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

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.persistence.SerializationStrategy;

import com.google.gson.annotations.SerializedName;
import com.google.gson.Gson;

/**
 * Represents a Twitter session that is associated with a {@link com.twitter.sdk.android.core.TwitterAuthToken}.
 */
public class TwitterSession extends Session<TwitterAuthToken> {

    public static final long UNKNOWN_USER_ID = -1L;
    public static final String UNKNOWN_USER_NAME = "";

    public static final long LOGGED_OUT_USER_ID = 0L;

    @SerializedName("user_name")
    private final String userName;

    /**
     * @param token     Auth token
     * @param userId    User ID
     * @param userName  User Name
     *
     * @throws {@link java.lang.IllegalArgumentException} if token argument is null
     */
    public TwitterSession(TwitterAuthToken token, long userId, String userName) {
        super(token, userId);

        if (token == null) {
            throw new IllegalArgumentException("AuthToken must not be null.");
        }

        this.userName = userName;
    }

    public long getUserId() {
        return getId();
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final TwitterSession that = (TwitterSession) o;

        if (userName != null ? !userName.equals(that.userName) : that.userName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        return result;
    }

    static class Serializer implements SerializationStrategy<TwitterSession> {

        private final Gson gson;

        public Serializer() {
            this.gson = new Gson();
        }

        @Override
        public String serialize(TwitterSession session) {
            if (session != null && session.getAuthToken() != null) {
                try {
                    return gson.toJson(session);
                } catch (Exception e) {
                    Fabric.getLogger().d(TwitterCore.TAG, e.getMessage());
                }
            }
            return "";
        }

        @Override
        public TwitterSession deserialize(String serializedSession) {
            if (!TextUtils.isEmpty(serializedSession)) {
                try {
                    return gson.fromJson(serializedSession, TwitterSession.class);
                } catch (Exception e) {
                    Fabric.getLogger().d(TwitterCore.TAG, e.getMessage());
                }
            }
            return null;
        }
    }
}
