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

package com.twitter.sdk.android.core.internal.scribe;

public class ScribeConfig {

    public static final String BASE_URL = "https://api.twitter.com";
    public static final int DEFAULT_MAX_FILES_TO_KEEP = 100;
    public static final int DEFAULT_SEND_INTERVAL_SECONDS = 10 * 60; // 10 minutes

    /**
     * Whether scribe is enabled.
     */
    public final boolean isEnabled;
    /**
     * The scribe url scheme and host.
     */
    public final String baseUrl;
    /**
     * The scribe path version component "/{version}/jot/{type}."
     */
    public final String pathVersion;
    /**
     * The scribe path type component "/{version}/jot/{type}."
     */
    public final String pathType;
    /**
     * The scribe sequence. Used for echidna testing.
     */
    public final String sequence;
    /**
     * The user agent string to include in scribe requests.
     */
    public final String userAgent;
    /**
     * The maximum number of files to keep in storage.
     */
    public final int maxFilesToKeep;
    /**
     * The send interval in seconds.
     */
    public final int sendIntervalSeconds;

    public ScribeConfig(boolean isEnabled, String baseUrl, String pathVersion, String pathType,
                        String sequence, String userAgent, int maxFilesToKeep,
                        int sendIntervalSeconds) {
        this.isEnabled = isEnabled;
        this.baseUrl = baseUrl;
        this.pathVersion = pathVersion;
        this.pathType = pathType;
        this.sequence = sequence;
        this.userAgent = userAgent;
        this.maxFilesToKeep = maxFilesToKeep;
        this.sendIntervalSeconds = sendIntervalSeconds;
    }
}
