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

package com.twitter.sdk.android.tweetui;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.params.Geocode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

/**
 * SearchTimeline provides a timeline of tweets from the search/tweets API source.
 */
public class SearchTimeline extends BaseTimeline implements Timeline<Tweet> {
    static final String FILTER_RETWEETS = " -filter:retweets";   // leading whitespace intentional
    private static final String SCRIBE_SECTION = "search";
    private static final SimpleDateFormat QUERY_DATE =
            new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    final TwitterCore twitterCore;
    final String query;
    final Geocode geocode;
    final String resultType;
    final String languageCode;
    final Integer maxItemsPerRequest;
    final String untilDate;

    SearchTimeline(TwitterCore twitterCore, String query, Geocode geocode, String resultType,
                   String languageCode, Integer maxItemsPerRequest, String untilDate) {
        this.twitterCore = twitterCore;
        this.languageCode = languageCode;
        this.maxItemsPerRequest = maxItemsPerRequest;
        this.untilDate = untilDate;
        this.resultType = resultType;
        // if the query is non-null append the filter Retweets modifier
        this.query = query == null ? null : query + FILTER_RETWEETS;
        this.geocode = geocode;
    }

    /**
     * Loads Tweets with id greater than (newer than) sinceId. If sinceId is null, loads the newest
     * Tweets.
     * @param sinceId minimum id of the Tweets to load (exclusive).
     * @param cb callback.
     */
    @Override
    public void next(Long sinceId, Callback<TimelineResult<Tweet>> cb) {
        createSearchRequest(sinceId, null).enqueue(new SearchCallback(cb));
    }

    /**
     * Loads Tweets with id less than (older than) maxId.
     * @param maxId maximum id of the Tweets to load (exclusive).
     * @param cb callback.
     */
    @Override
    public void previous(Long maxId, Callback<TimelineResult<Tweet>> cb) {
        // api quirk: search api provides results that are inclusive of the maxId iff
        // FILTER_RETWEETS is added to the query (which we currently always add), decrement the
        // maxId to get exclusive results
        createSearchRequest(null, decrementMaxId(maxId)).enqueue(new SearchCallback(cb));
    }

    @Override
    String getTimelineType() {
        return SCRIBE_SECTION;
    }

    Call<Search> createSearchRequest(final Long sinceId, final Long maxId) {
        return twitterCore.getApiClient().getSearchService().tweets(query, geocode,
                languageCode, null, resultType, maxItemsPerRequest, untilDate, sinceId, maxId,
                true);
    }

    /**
     * Wrapper callback which unpacks a Search API result into a TimelineResult (cursor and items).
     */
    class SearchCallback extends Callback<Search> {
        final Callback<TimelineResult<Tweet>> cb;

        /**
         * Constructs a SearchCallback
         * @param cb A Callback which expects a TimelineResult
         */
        SearchCallback(Callback<TimelineResult<Tweet>> cb) {
            this.cb = cb;
        }

        @Override
        public void success(Result<Search> result) {
            final List<Tweet> tweets = result.data.tweets;
            final TimelineResult<Tweet> timelineResult
                    = new TimelineResult<>(new TimelineCursor(tweets), tweets);
            if (cb != null) {
                cb.success(new Result<>(timelineResult, result.response));
            }
        }

        @Override
        public void failure(TwitterException exception) {
            if (cb != null) {
                cb.failure(exception);
            }
        }
    }

    public enum ResultType {
        RECENT("recent"),
        POPULAR("popular"),
        MIXED("mixed"),
        FILTERED("filtered");

        final String type;

        ResultType(String type) {
            this.type = type;
        }
    }

    /**
     * SearchTimeline Builder
     */
    public static class Builder {
        private final TwitterCore twitterCore;
        private String query;
        private String lang;
        private String resultType = ResultType.FILTERED.type;
        private Integer maxItemsPerRequest = 30;
        private String untilDate;
        private Geocode geocode;

        /**
         * Constructs a Builder.
         */
        public Builder() {
            twitterCore = TwitterCore.getInstance();
        }

        // For testing
        Builder(TwitterCore twitterCore) {
            this.twitterCore = twitterCore;
        }

        /**
         * Sets the query for the SearchTimeline.
         * @param query A UTF-8, URL-encoded search query of 500 characters maximum, including
         * operators. Queries may additionally be limited by complexity.
         */
        public Builder query(String query) {
            this.query = query;
            return this;
        }

        /**
         * Sets the geocode for the SearchTimeline.
         * @param geocode Restricts query to a given geolocation
         */
        public Builder geocode(Geocode geocode) {
            this.geocode = geocode;
            return this;
        }

        /**
         *  The result_type parameter allows one to choose if the result set will be represented by
         *  recent or popular Tweets, or a mix of both.
         *
         * @param resultType possible options include recent, popular, mixed, or filtered.
         */
        public Builder resultType(ResultType resultType) {
            this.resultType = resultType.type;
            return this;
        }


        /**
         * Sets the languageCode for the SearchTimeline.
         * @param languageCode Restricts tweets to the given language, given by an ISO 639-1 code.
         * Language detection is best-effort.
         */
        public Builder languageCode(String languageCode) {
            this.lang = languageCode;
            return this;
        }

        /**
         * Sets the number of Tweets returned per request for the SearchTimeline.
         * @param maxItemsPerRequest The number of tweets to return per request, up to a maximum of
         * 100.
         */
        public Builder maxItemsPerRequest(Integer maxItemsPerRequest) {
            this.maxItemsPerRequest = maxItemsPerRequest;
            return this;
        }

        /**
         * Returns tweets generated before the given date. Date should be formatted as YYYY-MM-DD.
         * Keep in mind that the search index may not go back as far as the date you specify here.
         *
         * @param date Date before which the tweets were created.
         */
        public Builder untilDate(Date date) {
            untilDate = QUERY_DATE.format(date);
            return this;
        }

        /**
         * Builds a SearchTimeline from the Builder parameters.
         * @return a SearchTimeline.
         * @throws java.lang.IllegalStateException if query is not set (is null).
         */
        public SearchTimeline build() {
            if (query == null) {
                throw new IllegalStateException("query must not be null");
            }
            return new SearchTimeline(twitterCore, query, geocode, resultType, lang,
                    maxItemsPerRequest, untilDate);
        }
    }
}
