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
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ListService {

    /**
     * Returns a timeline of tweets authored by members of the specified list. Retweets are included
     * by default.
     * @param listId The numerical id of the list.
     * @param slug You can identify a list by its slug instead of its numerical id. If you decide to
     *             do so, note that you'll also have to specify the list owner using the owner_id or
     *             owner_screen_name parameters.
     * @param ownerScreenName The screen name of the user who owns the list being requested by a
     *                        slug.
     * @param ownerId The user ID of the user who owns the list being requested by a slug.
     * @param sinceId Returns results with an ID greater than (that is, more recent than) the
     *                specified ID. There are limits to the number of Tweets which can be accessed
     *                through the API. If the limit of Tweets has occurred since the since_id, the
     *                since_id will be forced to the oldest ID available.
     * @param maxId Returns results with an ID less than (that is, older than) or equal to the
     *              specified ID.
     * @param count Specifies the number of results to retrieve per "page."
     * @param includeEntities Entities are ON by default in API 1.1, each Tweet includes a node
     *                        called "entities." This node offers a variety of metadata about the
     *                        Tweet in a discrete structure, including: user_mentions, urls, and
     *                        hashtags.
     * @param includeRetweets When set to either true, t or 1, the list timeline will contain native
     *                        retweets (if they exist) in addition to the standard stream of tweets.
     *                        The output format of retweeted tweets is identical to the
     *                        representation you see in home_timeline.
     */
    @GET("/1.1/lists/statuses.json?" +
            "tweet_mode=extended&include_cards=true&cards_platform=TwitterKit-13")
    Call<List<Tweet>> statuses(@Query("list_id") Long listId,
                               @Query("slug") String slug,
                               @Query("owner_screen_name") String ownerScreenName,
                               @Query("owner_id") Long ownerId,
                               @Query("since_id") Long sinceId,
                               @Query("max_id") Long maxId,
                               @Query("count") Integer count,
                               @Query("include_entities") Boolean includeEntities,
                               @Query("include_rts") Boolean includeRetweets);
}
