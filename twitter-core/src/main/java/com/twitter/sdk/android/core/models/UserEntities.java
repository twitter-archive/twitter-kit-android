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

import java.util.List;

/**
 * Entities for User Objects describe URLs that appear in the user defined profile URL and
 * description fields.
 */
public class UserEntities {

    @SerializedName("url")
    public final UrlEntities url;

    @SerializedName("description")
    public final UrlEntities description;

    public UserEntities(UrlEntities url, UrlEntities description) {
        this.url = url;
        this.description = description;
    }

    public static class UrlEntities {

        @SerializedName("urls")
        public final List<UrlEntity> urls;

        private UrlEntities() {
            this(null);
        }

        public UrlEntities(List<UrlEntity> urls) {
            this.urls = ModelUtils.getSafeList(urls);
        }
    }
}
