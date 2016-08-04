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

/**
 * Authorization configuration details.
 */
public class TwitterAuthConfig implements Parcelable {

    /**
     * The default request code to use for Single Sign On. This code will
     * be returned in {@link android.app.Activity#onActivityResult(int, int, android.content.Intent)}
     */
    public static final int DEFAULT_AUTH_REQUEST_CODE = 140;

    public static final Parcelable.Creator<TwitterAuthConfig> CREATOR
            = new Parcelable.Creator<TwitterAuthConfig>() {
        public TwitterAuthConfig createFromParcel(Parcel in) {
            return new TwitterAuthConfig(in);
        }

        public TwitterAuthConfig[] newArray(int size) {
            return new TwitterAuthConfig[size];
        }
    };

    private final String consumerKey;
    private final String consumerSecret;

    /**
     * @param consumerKey    The consumer key.
     * @param consumerSecret The consumer secret.
     *
     * @throws java.lang.IllegalArgumentException if consumer key or consumer secret is null.
     */
    public TwitterAuthConfig(String consumerKey, String consumerSecret) {
        if (consumerKey == null || consumerSecret == null) {
            throw new IllegalArgumentException(
                    "TwitterAuthConfig must not be created with null consumer key or secret.");
        }
        this.consumerKey = sanitizeAttribute(consumerKey);
        this.consumerSecret = sanitizeAttribute(consumerSecret);
    }

    private TwitterAuthConfig(Parcel in) {
        consumerKey = in.readString();
        consumerSecret = in.readString();
    }

    /**
     * @return the consumer key
     */
    public String getConsumerKey() {
        return consumerKey;
    }

    /**
     * @return the consumer secret
     */
    public String getConsumerSecret() {
        return consumerSecret;
    }

    /**
     * @return The request code to use for Single Sign On. This code will
     * be returned in {@link android.app.Activity#onActivityResult(int, int, android.content.Intent)}
     * when the activity exits.
     */
    public int getRequestCode() {
        return DEFAULT_AUTH_REQUEST_CODE;
    }

    static String sanitizeAttribute(String input) {
        if (input != null) {
            return input.trim();
        } else {
            return null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(consumerKey);
        out.writeString(consumerSecret);
    }
}
