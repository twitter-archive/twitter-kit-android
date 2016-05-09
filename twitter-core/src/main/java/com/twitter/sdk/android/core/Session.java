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

import com.google.gson.annotations.SerializedName;

/**
 * Base class for session associated with {@link com.twitter.sdk.android.core.AuthToken}.
 */
public class Session<T extends AuthToken> {
    @SerializedName("auth_token")
    private final T authToken;

    @SerializedName("id")
    private final long id;

    public Session(T authToken, long id) {
        if (authToken == null) {
            throw new IllegalArgumentException("AuthToken must not be null.");
        }

        this.authToken = authToken;
        this.id = id;
    }

    public T getAuthToken() {
        return authToken;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Session session = (Session) o;

        if (id != session.id) return false;
        return authToken != null ? authToken.equals(session.authToken) : session.authToken == null;
    }

    @Override
    public int hashCode() {
        int result = authToken != null ? authToken.hashCode() : 0;
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }
}
