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

public class SearchMetadata {

    @SerializedName("max_id")
    public final long maxId;

    @SerializedName("since_id")
    public final long sinceId;

    @SerializedName("refresh_url")
    public final String refreshUrl;

    @SerializedName("next_results")
    public final String nextResults;

    @SerializedName("count")
    public final long count;

    @SerializedName("completed_in")
    public final double completedIn;

    @SerializedName("since_id_str")
    public final String sinceIdStr;

    @SerializedName("query")
    public final String query;

    @SerializedName("max_id_str")
    public final String maxIdStr;

    public SearchMetadata(int maxId, int sinceId, String refreshUrl, String nextResults, int count,
                          double completedIn, String sinceIdStr, String query, String maxIdStr) {
        this.maxId = maxId;
        this.sinceId = sinceId;
        this.refreshUrl = refreshUrl;
        this.nextResults = nextResults;
        this.count = count;
        this.completedIn = completedIn;
        this.sinceIdStr = sinceIdStr;
        this.query = query;
        this.maxIdStr = maxIdStr;
    }
}
