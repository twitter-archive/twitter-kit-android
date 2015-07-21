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

import android.view.View;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.internal.TimelineDelegate;

import java.util.List;

import static org.mockito.Mockito.mock;

public class TweetTimelineListAdapterTest extends TweetUiTestCase {
    private static final String NULL_CONTEXT_MESSAGE = "Context must not be null";
    private static final String NULL_TIMELINE_MESSAGE = "Timeline must not be null";
    private TweetTimelineListAdapter listAdapter;

    public void testConstructor() {
        final TimelineDelegate<Tweet> mockTimelineDelegate = mock(TimelineDelegate.class);
        listAdapter = new TweetTimelineListAdapter(getContext(), mockTimelineDelegate,
                null);
        if (listAdapter.actionCallback instanceof TweetTimelineListAdapter.ReplaceTweetCallback) {
            final TweetTimelineListAdapter.ReplaceTweetCallback replaceCallback
                    = (TweetTimelineListAdapter.ReplaceTweetCallback) listAdapter.actionCallback;
            assertEquals(mockTimelineDelegate, replaceCallback.delegate);
            assertNull(replaceCallback.cb);
        } else {
            fail("Expected default actionCallback to be a ReplaceTweetCallback");
        }
    }

    public void testConstructor_withActionCallback() {
        final TimelineDelegate<Tweet> mockTimelineDelegate = mock(TimelineDelegate.class);
        final Callback<Tweet> mockCallback = mock(Callback.class);
        listAdapter = new TweetTimelineListAdapter(getContext(), mockTimelineDelegate,
                mockCallback);
        // assert that
        // - developer callback wrapped in a ReplaceTweetCallback
        if (listAdapter.actionCallback instanceof TweetTimelineListAdapter.ReplaceTweetCallback) {
            final TweetTimelineListAdapter.ReplaceTweetCallback replaceCallback
                    = (TweetTimelineListAdapter.ReplaceTweetCallback) listAdapter.actionCallback;
            assertEquals(mockTimelineDelegate, replaceCallback.delegate);
            assertEquals(mockCallback, replaceCallback.cb);
        } else {
            fail("Expected actionCallback to be wrapped in ReplaceTweetCallback");
        }
    }

    public void testBuilder() {
        final Timeline<Tweet> mockTimeline = mock(Timeline.class);
        final Callback<Tweet> mockCallback = mock(Callback.class);
        listAdapter = new TweetTimelineListAdapter.Builder(getContext())
                .setTimeline(mockTimeline)
                .setOnActionCallback(mockCallback)
                .build();
        if (listAdapter.actionCallback instanceof TweetTimelineListAdapter.ReplaceTweetCallback) {
            final TweetTimelineListAdapter.ReplaceTweetCallback replaceCallback
                    = (TweetTimelineListAdapter.ReplaceTweetCallback) listAdapter.actionCallback;
            assertEquals(mockCallback, replaceCallback.cb);
        } else {
            fail("Expected actionCallback to be wrapped in ReplaceTweetCallback");
        }
    }

    public void testBuilder_nullContext() {
        final Timeline<Tweet> mockTimeline = mock(Timeline.class);
        try {
            listAdapter = new TweetTimelineListAdapter.Builder(null).setTimeline(mockTimeline)
                    .build();
            fail("Null context should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals(NULL_CONTEXT_MESSAGE, e.getMessage());
        }
    }

    public void testBuilder_nullTimeline() {
        try {
            listAdapter = new TweetTimelineListAdapter.Builder(getContext()).setTimeline(null)
                    .build();
            fail("Null timeline should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals(NULL_TIMELINE_MESSAGE, e.getMessage());
        }
    }

    /**
     * Requires TweetUi to be setup by the test class. Without TweetUi, TweetView construction
     * returns before calling setTweet to support IDE edit mode, so getTweetId would always be -1.
     */
    public void testGetView_getsCompactTweetView() {
        final Timeline<Tweet> fakeTimeline = new FakeTweetTimeline(10);
        final TimelineDelegate<Tweet> fakeDelegate = new TimelineDelegate<>(fakeTimeline);
        listAdapter = new TweetTimelineListAdapter(getContext(), fakeDelegate, null);

        final View view = listAdapter.getView(0, null, null);
        // assert that
        // - default implementation of getView returns a CompactTweetView
        // - sanity check that CompactTweetView tweet id matches the Tweet's id
        assertEquals(CompactTweetView.class, view.getClass());
        final BaseTweetView tv = (BaseTweetView) view;
        assertEquals(listAdapter.getItemId(0), tv.getTweetId());
    }

    public static class FakeTweetTimeline implements Timeline<Tweet> {
        private long numItems;

        /**
         * Constructs a FakeTweetTimeline
         * @param numItems the number of Tweets to return per call to next/previous
         */
        public FakeTweetTimeline(long numItems) {
            this.numItems = numItems;
        }

        @Override
        public void next(Long sinceId, Callback<TimelineResult<Tweet>> cb) {
            final List<Tweet> tweets = TestFixtures.getTweetList(numItems);
            final TimelineCursor timelineCursor = new TimelineCursor(tweets);
            final TimelineResult<Tweet> timelineResult
                    = new TimelineResult<>(timelineCursor, tweets);
            cb.success(timelineResult, null);
        }

        @Override
        public void previous(Long maxId, Callback<TimelineResult<Tweet>> cb) {
            final List<Tweet> tweets = TestFixtures.getTweetList(numItems);
            final TimelineCursor timelineCursor = new TimelineCursor(tweets);
            final TimelineResult<Tweet> timelineResult
                    = new TimelineResult<>(timelineCursor, tweets);
            cb.success(timelineResult, null);
        }
    }
}
