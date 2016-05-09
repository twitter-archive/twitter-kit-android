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

import android.text.format.DateUtils;

import com.google.gson.annotations.SerializedName;

public class GuestAuthToken extends OAuth2Token {
    public static final String HEADER_GUEST_TOKEN = "x-guest-token";
    /*
     * macaw-login oauth2/token does not return an expires_in field as recommended in RFC 6749,
     * https://tools.ietf.org/html/rfc6749#section-4.2.2. If token expiration policies change,
     * update this constant to help prevent requests with tokens known to be expired.
     * https://cgit.twitter.biz/birdcage/tree/passbird/server/src/main/scala/com/twitter/passbird/profile/PassbirdServerProfile.scala#n186
     */
    private static final long EXPIRES_IN_MS = DateUtils.HOUR_IN_MILLIS * 3;

    @SerializedName("guest_token")
    private final String guestToken;

    public GuestAuthToken(String tokenType, String accessToken, String guestToken) {
        super(tokenType, accessToken);
        this.guestToken = guestToken;
    }

    public GuestAuthToken(String tokenType, String accessToken, String guestToken, long createdAt) {
        super(tokenType, accessToken, createdAt);
        this.guestToken = guestToken;
    }

    public String getGuestToken() {
        return guestToken;
    }

    // Passbird maintains guest tokens for at least 1 hour, but no more than 3 hours. Tokens
    // older than 3 hours are known to have expired and should not be reused.
    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() >= this.createdAt + EXPIRES_IN_MS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final GuestAuthToken that = (GuestAuthToken) o;

        if (guestToken != null ? !guestToken.equals(that.guestToken) : that.guestToken != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (guestToken != null ? guestToken.hashCode() : 0);
        return result;
    }
}
