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

import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.services.params.Geocode;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchService {

    /**
     * Returns a collection of relevant Tweets matching a specified query.
     * <p>
     * Please note that Twitter's search service and, by extension, the Search API is not meant to
     * be an exhaustive source of Tweets. Not all Tweets will be indexed or made available via the
     * search interface.
     * <p>
     * In API v1.1, the response format of the Search API has been improved to return Tweet objects
     * more similar to the objects you'll find across the REST API and platform. You may need to
     * tolerate some inconsistencies and variance in perspectival values (fields that pertain to the
     * perspective of the authenticating user) and embedded user objects.
     * <p>
     * To learn how to use Twitter Search effectively, consult our guide to Using the Twitter Search
     * API. See Working with Timelines to learn best practices for navigating results by since_id
     * and max_id.
     *
     * @param query (required) A UTF-8, URL-encoded search query of 500 characters maximum,
     *              including operators. Queries may additionally be limited by complexity.
     * @param geocode (optional) Returns tweets by users located within a given radius of the given
     *                latitude/longitude. The location is preferentially taking from the Geotagging
     *                API, but will fall back to their Twitter profile. The parameter value is
     *                specified by "latitude,longitude,radius", where radius units must be specified
     *                as either "mi" (miles) or "km" (kilometers). Note that you cannot use the near
     *                operator via the API to geocode arbitrary locations; however you can use this
     *                geocode parameter to search near geocodes directly. A maximum of 1,000
     *                distinct "sub-regions" will be considered when using the radius modifier.
     * @param lang (optional) Restricts tweets to the given language, given by an ISO 639-1 code.
     *             Language detection is best-effort.
     * @param locale (optional) Specify the language of the query you are sending (only ja is
     *               currently effective). This is intended for language-specific consumers and the
     *               default should work in the majority of cases.
     * @param resultType (optional) Specifies what type of search results you would prefer to
     *                   receive. The current default is "mixed." Valid values include:
     * mixed: Include both popular and real time results in the response.
     * recent: return only the most recent results in the response
     * popular: return only the most popular results in the response.
     * @param count (optional) The number of tweets to return per page, up to a maximum of 100.
     *              Defaults to 15. This was formerly the "rpp" parameter in the old Search API.
     * @param until (optional) Returns tweets generated before the given date. Date should be
     *              formatted as YYYY-MM-DD. Keep in mind that the search index may not go back as
     *              far as the date you specify here.
     * @param sinceId (optional) Returns results with an ID greater than (that is, more recent than)
     *                the specified ID. There are limits to the number of Tweets which can be
     *                accessed through the API. If the limit of Tweets has occured since the
     *                since_id, the since_id will be forced to the oldest ID available.
     * @param maxId (optional) Returns results with an ID less than (that is, older than) or equal
     *              to the specified ID.
     * @param includeEntities (optional) The entities node will be disincluded when set to false.
     */
    @GET("/1.1/search/tweets.json?" +
            "tweet_mode=extended&include_cards=true&cards_platform=TwitterKit-13")
    Call<Search> tweets(@Query("q") String query,
                        //EncodedQuery protects commas from encode
                        @Query(value = "geocode", encoded = true) Geocode geocode,
                        @Query("lang") String lang,
                        @Query("locale") String locale,
                        @Query("result_type") String resultType,
                        @Query("count") Integer count,
                        @Query("until") String until,
                        @Query("since_id") Long sinceId,
                        @Query("max_id") Long maxId,
                        @Query("include_entities") Boolean includeEntities);
}
