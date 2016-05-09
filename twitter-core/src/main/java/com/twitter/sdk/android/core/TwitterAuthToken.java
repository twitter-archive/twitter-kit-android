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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Represents an authorization token and its secret.
 */
public class TwitterAuthToken extends AuthToken implements Parcelable {

    public static final Parcelable.Creator<TwitterAuthToken> CREATOR
            = new Parcelable.Creator<TwitterAuthToken>() {
        public TwitterAuthToken createFromParcel(Parcel in) {
            return new TwitterAuthToken(in);
        }

        public TwitterAuthToken[] newArray(int size) {
            return new TwitterAuthToken[size];
        }
    };

    @SerializedName("token")
    public final String token;

    @SerializedName("secret")
    public final String secret;

    public TwitterAuthToken(String token, String secret) {
        super();
        this.token = token;
        this.secret = secret;
    }

    // for testing purposes
    TwitterAuthToken(String token, String secret, long createdAt) {
        super(createdAt);
        this.token = token;
        this.secret = secret;
    }

    private TwitterAuthToken(Parcel in) {
        super();
        this.token = in.readString();
        this.secret = in.readString();
    }

    @Override
    public boolean isExpired() {
        // Twitter does not expire OAuth1a tokens
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder()
                .append("token=").append(this.token)
                .append(",secret=").append(this.secret);
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(token);
        out.writeString(secret);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TwitterAuthToken)) return false;

        final TwitterAuthToken that = (TwitterAuthToken) o;

        if (secret != null ? !secret.equals(that.secret) : that.secret != null) return false;
        if (token != null ? !token.equals(that.token) : that.token != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = token != null ? token.hashCode() : 0;
        result = 31 * result + (secret != null ? secret.hashCode() : 0);
        return result;
    }
}
