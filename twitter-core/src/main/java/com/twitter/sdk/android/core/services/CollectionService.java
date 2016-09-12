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

package com.twitter.sdk.android.core.services;

import com.twitter.sdk.android.core.models.TwitterCollection;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CollectionService {

    /**
     * Retrieve the identified TwitterCollection, presented as a list of the curated Tweets.
     * The response structure of this method differs significantly from timelines you may be
     * used to working with in the Twitter REST API.
     * Use the response/position hash to navigate through the collection via the min_position
     * and max_position. The was_truncated attribute will indicate to you whether additional tweets
     * exist in the collection outside of what was in range of the current request.
     * @param id The identifier of the Collection to return results for (e.g. "custom-5394878324")
     * @param count Specifies the number of Tweets to try and retrieve, up to a maximum of 200 per
     *              distinct request. The value of count is best thought of as an "up to" parameter;
     *              receiving less results than the specified count does not necessarily mean there
     *              aren't remaining results to fetch.
     * @param maxPosition Returns results with a position value less than or equal to the specified
     *                    position.
     * @param minPosition Returns results with a position greater than the specified position.
     */
    @GET("/1.1/collections/entries.json?" +
            "tweet_mode=extended&include_cards=true&cards_platform=TwitterKit-13")
    Call<TwitterCollection> collection(@Query("id") String id,
                                       @Query("count") Integer count,
                                       @Query("max_position") Long maxPosition,
                                       @Query("min_position") Long minPosition);
}
