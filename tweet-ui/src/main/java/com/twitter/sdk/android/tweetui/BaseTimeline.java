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
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

/**
 * BaseTimeline which handles TweetUi instance argument.
 */
abstract class BaseTimeline {
    protected final TweetUi tweetUi;

    BaseTimeline(TweetUi tweetUi) {
        if (tweetUi == null) {
            throw new IllegalArgumentException("TweetUi instance must not be null");
        }
        this.tweetUi = tweetUi;
        scribeImpression();
    }

    abstract String getTimelineType();

    private void scribeImpression() {
        tweetUi.scribe(
                ScribeConstants.getSyndicatedSdkTimelineNamespace(getTimelineType()),
                ScribeConstants.getTfwClientTimelineNamespace(getTimelineType())
        );
    }

    /**
     * Returns a decremented maxId if the given id is non-null. Otherwise returns the given maxId.
     * Suitable for REST Timeline endpoints which return inclusive previous results when exclusive
     * is desired.
     */
    static Long decrementMaxId(Long maxId) {
        return maxId == null ? null : maxId - 1;
    }

    /**
     * Adds the request to the guest AuthRequestQueue where guest auth will be setup.
     */
    void addRequest(final Callback<TwitterApiClient> cb) {
        tweetUi.getGuestAuthQueue().addClientRequest(cb);
    }

    /**
     * Wrapper callback which unpacks a list of Tweets into a TimelineResult (cursor and items).
     */
    static class TweetsCallback extends Callback<List<Tweet>> {
        protected final Callback<TimelineResult<Tweet>> cb;

        /**
         * Constructs a TweetsCallback
         * @param cb A callback which expects a TimelineResult
         */
        TweetsCallback(Callback<TimelineResult<Tweet>> cb) {
            this.cb = cb;
        }

        @Override
        public void success(Result<List<Tweet>> result) {
            final List<Tweet> tweets = result.data;
            final TimelineResult<Tweet> timelineResult
                    = new TimelineResult<>(new TimelineCursor(tweets), tweets);
            if (cb != null) {
                cb.success(timelineResult, result.response);
            }
        }

        @Override
        public void failure(TwitterException exception) {
            if (cb != null) {
                cb.failure(exception);
            }
        }
    }
}
