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

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.scribe.ScribeItem;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * FilterTimelineDelegate manages and filters timeline data items and loads items from a Timeline.
 */
class FilterTimelineDelegate extends TimelineDelegate<Tweet> {
    final TimelineFilter timelineFilter;
    final TweetUi tweetUi;

    static final String TWEETS_COUNT_JSON_PROP = "tweet_count";
    static final String TWEETS_FILTERED_JSON_PROP = "tweets_filtered";
    static final String TOTAL_APPLIED_FILTERS_JSON_PROP = "total_filters";
    final Gson gson = new Gson();

    /**
     * Constructs a FilterTimelineDelegate with a timeline for requesting data and timelineFilter to
     * filter tweets
     * @param timeline Timeline source
     * @param timelineFilter a timelineFilter for filtering tweets from timeline
     * @throws java.lang.IllegalArgumentException if timeline is null
     */
    FilterTimelineDelegate(Timeline<Tweet> timeline, TimelineFilter timelineFilter) {
        super(timeline);
        this.timelineFilter = timelineFilter;
        this.tweetUi = TweetUi.getInstance();
    }

    @Override
    public void refresh(Callback<TimelineResult<Tweet>> developerCb) {
        // reset scrollStateHolder cursors to be null, loadNext will get latest items
        timelineStateHolder.resetCursors();
        // load latest timeline items and replace existing items
        loadNext(timelineStateHolder.positionForNext(),
                new TimelineFilterCallback(new RefreshCallback(developerCb, timelineStateHolder),
                timelineFilter));
    }

    @Override
    public void next(Callback<TimelineResult<Tweet>> developerCb) {
        loadNext(timelineStateHolder.positionForNext(),
            new TimelineFilterCallback(new NextCallback(developerCb, timelineStateHolder),
                    timelineFilter));
    }

    @Override
    public void previous() {
        loadPrevious(timelineStateHolder.positionForPrevious(),
                new TimelineFilterCallback(new PreviousCallback(timelineStateHolder),
                        timelineFilter));
    }

    /**
     * Handles filtering of tweets from the timeline, provided a given TimelineFilter
     */
    class TimelineFilterCallback extends Callback<TimelineResult<Tweet>> {
        final DefaultCallback callback;
        final TimelineFilter timelineFilter;
        final Handler handler;
        final ExecutorService executorService;

        TimelineFilterCallback(DefaultCallback callback, TimelineFilter timelineFilter) {
            this.callback = callback;
            this.timelineFilter = timelineFilter;
            this.handler = new Handler(Looper.getMainLooper());
            this.executorService = Twitter.getInstance().getExecutorService();
        }

        @Override
        public void success(final Result<TimelineResult<Tweet>> result) {
            final Runnable timelineFilterRunnable = () -> {
                final List<Tweet> filteredTweets = timelineFilter.filter(result.data.items);
                final TimelineResult<Tweet> filteredTimelineResult =
                        buildTimelineResult(result.data.timelineCursor, filteredTweets);

                handler.post(() -> callback.success(new Result<>(filteredTimelineResult, result.response)));

                scribeFilteredTimeline(result.data.items, filteredTweets);
            };

            executorService.execute(timelineFilterRunnable);
        }

        @Override
        public void failure(final TwitterException ex) {
            if (callback != null) {
                callback.failure(ex);
            }
        }

        TimelineResult<Tweet> buildTimelineResult(TimelineCursor timelineCursor,
                                                  List<Tweet> filteredTweets) {
            return new TimelineResult<>(timelineCursor, filteredTweets);
        }
    }

    void scribeFilteredTimeline(List<Tweet> tweets, List<Tweet> filteredTweets) {
        final int tweetCount = tweets.size();
        final int totalTweetsFiltered = tweetCount - filteredTweets.size();
        final int totalFilters = timelineFilter.totalFilters();

        final String jsonMessage = getJsonMessage(tweetCount, totalTweetsFiltered,
                totalFilters);
        final ScribeItem scribeItem = ScribeItem.fromMessage(jsonMessage);
        final List<ScribeItem> items = new ArrayList<>();
        items.add(scribeItem);

        final String timelineType = TweetTimelineListAdapter.getTimelineType(timeline);
        tweetUi.scribe(ScribeConstants.getTfwClientFilterTimelineNamespace(timelineType), items);
    }

    private String getJsonMessage(int totalTweetsSize, int filteredTweetsSize, int totalFilters) {
        final JsonObject message = new JsonObject();
        message.addProperty(TWEETS_COUNT_JSON_PROP, totalTweetsSize);
        message.addProperty(TWEETS_FILTERED_JSON_PROP, totalTweetsSize - filteredTweetsSize);
        message.addProperty(TOTAL_APPLIED_FILTERS_JSON_PROP, totalFilters);
        return gson.toJson(message);
    }
}
