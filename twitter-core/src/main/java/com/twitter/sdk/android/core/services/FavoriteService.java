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

import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FavoriteService {

    /**
     * Returns recent Tweets favorited by the authenticating or specified user,
     * by default returns 20 tweets.
     *
     * @param userId (optional) The ID of the user for whom to return results for.
     * @param screenName (optional) The screen name of the user for whom to return results for.
     * @param count (optional) Specifies the number of records to retrieve. Must be less than or
     *              equal to 200. Defaults to 20.
     * @param sinceId (optional) Returns results with an ID greater than (that is, more recent than)
     *                the specified ID. There are limits to the number of Tweets which can be
     *                accessed through the API. If the limit of Tweets has occured since the
     *                since_id, the since_id will be forced to the oldest ID available.
     * @param maxId (optional) Returns results with an ID less than (that is, older than) or equal
     *              to the specified ID.
     * @param includeEntities (optional) The entities node will be omitted when set to false.
     */
    @GET("/1.1/favorites/list.json?" +
            "tweet_mode=extended&include_cards=true&cards_platform=TwitterKit-13")
    Call<List<Tweet>> list(@Query("user_id") Long userId,
                           @Query("screen_name") String screenName,
                           @Query("count") Integer count,
                           @Query("since_id") String sinceId,
                           @Query("max_id") String maxId,
                           @Query("include_entities") Boolean includeEntities);

    /**
     * Un-favorites the status specified in the ID parameter as the authenticating user. Returns the
     * un-favorited status in the requested format when successful.
     * <p>
     * This process invoked by this method is asynchronous. The immediately returned status may not
     * indicate the resultant favorited status of the Tweet. A 200 OK response from this method will
     * indicate whether the intended action was successful or not.
     *
     * @param id (required) The numerical ID of the desired status.
     * @param includeEntities (optional) The entities node will be omitted when set to false.
     */
    @FormUrlEncoded
    @POST("/1.1/favorites/destroy.json?" +
            "tweet_mode=extended&include_cards=true&cards_platform=TwitterKit-13")
    Call<Tweet> destroy(@Field("id") Long id,
                        @Field("include_entities") Boolean includeEntities);

    /**
     * Favorites the status specified in the ID parameter as the authenticating user. Returns the
     * favorite status when successful.
     * <p>
     * This process invoked by this method is asynchronous. The immediately returned status may not
     * indicate the resultant favorited status of the Tweet. A 200 OK response from this method will
     * indicate whether the intended action was successful or not.
     *
     * @param id (required) The numerical ID of the desired status.
     * @param includeEntities (optional) The entities node will be omitted when set to false.
     */
    @FormUrlEncoded
    @POST("/1.1/favorites/create.json?" +
            "tweet_mode=extended&include_cards=true&cards_platform=TwitterKit-13")
    Call<Tweet> create(@Field("id") Long id,
                       @Field("include_entities") Boolean includeEntities);
}
