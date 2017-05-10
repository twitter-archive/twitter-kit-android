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
import java.util.Map;

/**
 * TwitterCollection is a new type of timeline you control: you create the collection, give it a
 * name, and select which Tweets to add, either by hand or programmatically using the REST API.
 */
public class TwitterCollection {

    @SerializedName("objects")
    public final Content contents;

    @SerializedName("response")
    public final Metadata metadata;

    public TwitterCollection(Content contents, Metadata metadata) {
        this.contents = contents;
        this.metadata = metadata;
    }

    /**
     * Contents represent the grouped, decomposed collection objects (tweets, users).
     */
    public static final class Content {
        /**
         * Represents the mapping from string Tweet ids to user-trimmed Tweets.
         */
        @SerializedName("tweets")
        public final Map<Long, Tweet> tweetMap;

        /**
         * Represents the mapping from string user ids to Users who authored Tweets or Timelines.
         */
        @SerializedName("users")
        public final Map<Long, User> userMap;

        public Content(Map<Long, Tweet> tweetMap, Map<Long, User> userMap) {
            this.tweetMap = ModelUtils.getSafeMap(tweetMap);
            this.userMap = ModelUtils.getSafeMap(userMap);
        }
    }

    /**
     * Metadata lists references to decomposed objects and contextual information (such as cursors)
     * needed to navigate the boundaries of the collection in subsequent requests.
     */
    public static final class Metadata {

        /**
         * The collection object identifier (e.g. "custom-393773270547177472")
         */
        @SerializedName("timeline_id")
        public final String timelineId;

        @SerializedName("position")
        public final Position position;

        /**
         * The ordered set of Collection items.
         */
        @SerializedName("timeline")
        public final List<TimelineItem> timelineItems;

        public Metadata(String timelineId, Position position, List<TimelineItem> timelines) {
            this.timelineId = timelineId;
            this.position = position;
            this.timelineItems = timelines;
        }

        /**
         * Position information for navigation.
         */
        public static final class Position {

            /**
             * The exclusive minimum position value of the results (positions will be greater than
             * this value).
             */
            @SerializedName("min_position")
            public final Long minPosition;

            /**
             * The inclusive maximum position value of the results (positions will be less than or
             * equal to this value).
             */
            @SerializedName("max_position")
            public final Long maxPosition;

            public Position(Long maxPosition, Long minPosition) {
                this.maxPosition = maxPosition;
                this.minPosition = minPosition;
            }
        }
    }

    /**
     * Represents an item in a Timeline with a object references.
     */
    public static class TimelineItem {

        /**
         * Represents a reference to a Tweet.
         */
        @SerializedName("tweet")
        public final TweetItem tweetItem;

        public TimelineItem(TweetItem tweetItem) {
            this.tweetItem = tweetItem;
        }

        public static final class TweetItem {

            /**
             * A Tweet id.
             */
            @SerializedName("id")
            public final Long id;

            public TweetItem(Long id) {
                this.id = id;
            }
        }
    }
}
