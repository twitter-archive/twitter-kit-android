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

import android.os.Parcel;
import android.os.Parcelable;

import com.twitter.sdk.android.core.TwitterAuthToken;

/**
 * Represents an authorization response.
 */
public class OAuthResponse implements Parcelable {

    public static final Parcelable.Creator<OAuthResponse> CREATOR
            = new Parcelable.Creator<OAuthResponse>() {
        public OAuthResponse createFromParcel(Parcel in) {
            return new OAuthResponse(in);
        }

        public OAuthResponse[] newArray(int size) {
            return new OAuthResponse[size];
        }
    };

    /**
     * The authorization token. May be temporary (request token) or long-lived (access token).
     */
    public final TwitterAuthToken authToken;
    /**
     * The username associated with the access token.
     */
    public final String userName;
    /**
     * The user id associated with the access token.
     */
    public final long userId;

    public OAuthResponse(TwitterAuthToken authToken, String userName, long userId) {
        this.authToken = authToken;
        this.userName = userName;
        this.userId = userId;
    }

    private OAuthResponse(Parcel in) {
        this.authToken = in.readParcelable(TwitterAuthToken.class.getClassLoader());
        this.userName = in.readString();
        this.userId = in.readLong();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("authToken=").append(authToken)
                .append(",userName=").append(userName)
                .append(",userId=").append(userId)
                .toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(this.authToken, flags);
        out.writeString(this.userName);
        out.writeLong(this.userId);
    }
}
