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
 * Collection of relevant Tweets matching a specified query.
 */
public class Search {
    @SerializedName("statuses")
    public final List<Tweet> tweets;

    @SerializedName("search_metadata")
    public final SearchMetadata searchMetadata;

    private Search() {
        this(null, null);
    }

    public Search(List<Tweet> tweets, SearchMetadata searchMetadata) {
        this.tweets = ModelUtils.getSafeList(tweets);
        this.searchMetadata = searchMetadata;
    }
}
