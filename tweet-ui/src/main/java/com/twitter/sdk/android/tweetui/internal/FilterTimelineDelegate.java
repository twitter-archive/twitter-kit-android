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

package com.twitter.sdk.android.tweetui.internal;

import android.os.Handler;
import android.os.Looper;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.tweetui.TimelineFilter;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TimelineCursor;
import com.twitter.sdk.android.tweetui.TimelineResult;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * FilterTimelineDelegate manages and filters timeline data items and loads items from a Timeline.
 */
public class FilterTimelineDelegate extends TimelineDelegate<Tweet> {
    final TimelineFilter timelineFilter;

    /**
     * Constructs a FilterTimelineDelegate with a timeline for requesting data and timelineFilter to
     * filter tweets
     * @param timeline Timeline source
     * @param timelineFilter a timelineFilter for filtering tweets from timeline
     * @throws java.lang.IllegalArgumentException if timeline is null
     */
    public FilterTimelineDelegate(Timeline<Tweet> timeline, TimelineFilter timelineFilter) {
        super(timeline);
        this.timelineFilter = timelineFilter;
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
    static class TimelineFilterCallback extends Callback<TimelineResult<Tweet>> {
        final DefaultCallback callback;
        final TimelineFilter timelineFilter;
        final Handler handler;
        final ExecutorService executorService;

        TimelineFilterCallback(DefaultCallback callback, TimelineFilter timelineFilter) {
            this.callback = callback;
            this.timelineFilter = timelineFilter;
            this.handler = new Handler(Looper.getMainLooper());
            this.executorService = TwitterCore.getInstance().getFabric().getExecutorService();
        }

        @Override
        public void success(final Result<TimelineResult<Tweet>> result) {
            final Runnable timelineFilterRunnable = new Runnable() {
                @Override
                public void run() {
                    final List<Tweet> filteredTweets = timelineFilter.filter(result.data.items);
                    final TimelineResult<Tweet> filteredTimelineResult =
                            buildTimelineResult(result.data.timelineCursor, filteredTweets);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.success(new Result<>(filteredTimelineResult, result.response));
                        }
                    });
                }
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
}
