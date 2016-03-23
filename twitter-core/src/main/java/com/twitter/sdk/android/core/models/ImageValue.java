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

public class ImageValue {

    @SerializedName("height")
    public final int height;

    @SerializedName("width")
    public final int width;

    @SerializedName("url")
    public final String url;

    @SerializedName("alt")
    public final String alt;

    public ImageValue(int height, int width, String url, String alt) {
        this.height = height;
        this.width = width;
        this.url = url;
        this.alt = alt;
    }
}
