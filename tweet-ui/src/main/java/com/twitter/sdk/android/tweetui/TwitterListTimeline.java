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
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import retrofit2.Call;

/**
 * TwitterListTimeline provides a timeline of tweets from the lists/statuses API source.
 */
public class TwitterListTimeline extends BaseTimeline implements Timeline<Tweet> {
    private static final String SCRIBE_SECTION = "list";

    final TwitterCore twitterCore;
    final Long listId;
    final String slug;
    final String ownerScreenName;
    final Long ownerId;
    final Integer maxItemsPerRequest;
    final Boolean includeRetweets;

    TwitterListTimeline(TwitterCore twitterCore, Long listId, String slug, Long ownerId,
        String ownerScreenName, Integer maxItemsPerRequest, Boolean includeRetweets) {
        this.twitterCore = twitterCore;
        this.listId = listId;
        this.slug = slug;
        this.ownerId = ownerId;
        this.ownerScreenName = ownerScreenName;
        this.maxItemsPerRequest = maxItemsPerRequest;
        this.includeRetweets = includeRetweets;
    }

    /**
     * Loads Tweets with id greater than (newer than) sinceId. If sinceId is null, loads the newest
     * Tweets.
     * @param sinceId minimum id of the Tweets to load (exclusive).
     * @param cb callback.
     */
    @Override
    public void next(Long sinceId, Callback<TimelineResult<Tweet>> cb) {
        createListTimelineRequest(sinceId, null).enqueue(new TweetsCallback(cb));
    }

    /**
     * Loads Tweets with id less than (older than) maxId.
     * @param maxId maximum id of the Tweets to load (exclusive).
     * @param cb callback.
     */
    @Override
    public void previous(Long maxId, Callback<TimelineResult<Tweet>> cb) {
        // lists/statuses api provides results which are inclusive of the maxId, decrement the
        // maxId to get exclusive results
        createListTimelineRequest(null, decrementMaxId(maxId)).enqueue(new TweetsCallback(cb));
    }

    Call<List<Tweet>> createListTimelineRequest(final Long sinceId, final Long maxId) {
        return twitterCore.getApiClient().getListService().statuses(listId, slug,
                ownerScreenName, ownerId, sinceId, maxId, maxItemsPerRequest, true,
                includeRetweets);
    }

    @Override
    String getTimelineType() {
        return SCRIBE_SECTION;
    }

    /**
     * TwitterListTimeline Builder.
     */
    public static class Builder {
        private final TwitterCore twitterCore;
        private Long listId;
        private String slug;
        private Long ownerId;
        private String ownerScreenName;
        private Integer maxItemsPerRequest = 30;
        private Boolean includeRetweets;

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
         * Sets the id for the Twitter List to get Tweets from.
         * @param id The ID of the Tweet list to get Tweets from.
         */
        public Builder id(Long id) {
            this.listId = id;
            return this;
        }

        /**
         * Sets the List slug name and owner id for the TwitterListTimeline.
         * @param slug The list slug name (e.g. 'textile-engineers').
         * @param ownerId The list owner Twitter user id.
         */
        public Builder slugWithOwnerId(String slug, Long ownerId) {
            this.slug = slug;
            this.ownerId = ownerId;
            return this;
        }

        /**
         * Sets the slug name and owner screen name for the TwitterListTimeline.
         * @param slug The list slug name (e.g. "textile-engineers").
         * @param ownerScreenName The list owner screen name (e.g. "twitterdev").
         */
        public Builder slugWithOwnerScreenName(String slug, String ownerScreenName) {
            this.slug = slug;
            this.ownerScreenName = ownerScreenName;
            return this;
        }

        /**
         * Sets the number of Tweets returned per request for the TwitterListTimeline.
         * @param maxItemsPerRequest The number of tweets to return per request.
         */
        public Builder maxItemsPerRequest(Integer maxItemsPerRequest) {
            this.maxItemsPerRequest = maxItemsPerRequest;
            return this;
        }

        /**
         * Sets whether to includeRetweets for the TwitterListTimeline. Defaults to true.
         * @param includeRetweets When set to false, the timeline will strip any native retweets
         * (though they will still count toward both the maximal length of the timeline and the
         * slice selected by the count parameter).
         */
        public Builder includeRetweets(Boolean includeRetweets) {
            this.includeRetweets = includeRetweets;
            return this;
        }

        /**
         * Builds a TwitterListTimeline from the Builder parameters.
         * @return a TwitterListTimeline.
         * @throws java.lang.IllegalStateException if id or slug/owner pair is not set.
         */
        public TwitterListTimeline build() {
            // user must provide either an id or slug, not both
            if (!(listId == null ^ slug == null)) {
                throw new IllegalStateException("must specify either a list id or slug/owner pair");
            }

            // user provides a slug, but ownerId and ownerScreenName are null
            if (slug != null && ownerId == null && ownerScreenName == null) {
                throw new IllegalStateException(
                        "slug/owner pair must set owner via ownerId or ownerScreenName");
            }

            return new TwitterListTimeline(twitterCore, listId, slug, ownerId, ownerScreenName,
                    maxItemsPerRequest, includeRetweets);
        }
    }
}
