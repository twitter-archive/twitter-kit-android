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
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FixedTweetTimeline extends BaseTimeline implements Timeline<Tweet> {
    private static final String SCRIBE_SECTION = "fixed";
    final List<Tweet> tweets;

    FixedTweetTimeline(List<Tweet> tweets) {
        this.tweets = tweets == null ? new ArrayList<Tweet>() : tweets;
    }

    @Override
    public void next(Long minPosition, Callback<TimelineResult<Tweet>> cb) {
        // always return the same fixed set of 'latest' Tweets
        final TimelineResult<Tweet> timelineResult
                = new TimelineResult<>(new TimelineCursor(tweets), tweets);
        cb.success(new Result(timelineResult, null));
    }

    @Override
    public void previous(Long maxPosition, Callback<TimelineResult<Tweet>> cb) {
        final List<Tweet> empty = Collections.emptyList();
        final TimelineResult<Tweet> timelineResult = new TimelineResult<>(new TimelineCursor(empty),
                empty);
        cb.success(new Result(timelineResult, null));
    }

    @Override
    String getTimelineType() {
        return SCRIBE_SECTION;
    }

    /**
     * FixedTweetTimeline Builder.
     */
    public static class Builder {
        private List<Tweet> tweets;

        /**
         * Constructs a Builder.
         */
        public Builder() {}

        /**
         * Sets the Tweets to be returned by the timeline.
         * @param tweets fixed set of Tweets provided by the timeline.
         */
        public Builder setTweets(List<Tweet> tweets) {
            this.tweets = tweets;
            return this;
        }

        /**
         * Builds a FixedTweetTimeline from the Builder parameters.
         * @return a FixedTweetTimeline.
         */
        public FixedTweetTimeline build() {
            return new FixedTweetTimeline(tweets);
        }
    }
}
