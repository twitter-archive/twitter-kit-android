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

package com.twitter.sdk.android.core.models;

import com.google.gson.annotations.SerializedName;

/**
 * Represents URLs included in the text of a Tweet or within textual fields of a user object.
 */
public class UrlEntity extends Entity {

    /**
     * Wrapped URL, corresponding to the value embedded directly into the raw Tweet text, and the
     * values for the indices parameter.
     */
    @SerializedName("url")
    public final String url;

    /**
     * Expanded version of display_url
     */
    @SerializedName("expanded_url")
    public final String expandedUrl;

    /**
     * Version of the URL to display to clients.
     */
    @SerializedName("display_url")
    public final String displayUrl;

    public UrlEntity(String url, String expandedUrl, String displayUrl, int start, int end) {
        super(start, end);
        this.url = url;
        this.expandedUrl = expandedUrl;
        this.displayUrl = displayUrl;
    }
}
