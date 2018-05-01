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
import com.twitter.sdk.android.core.models.Identifiable;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import static org.mockito.Mockito.mock;

public class TweetTimelineRecyclerViewAdapterTest extends TweetUiTestCase {
    static final String NULL_CONTEXT_MESSAGE = "Context must not be null";
    static final String NULL_TIMELINE_MESSAGE = "Timeline must not be null";
    static final int ANY_STYLE = R.style.tw__TweetLightWithActionsStyle;
    private TweetTimelineRecyclerViewAdapter recyclerViewAdapter;

    private static final int ITEM_COUNT = 10;


    public void testConstructor() {
        final TimelineDelegate<Tweet> mockTimelineDelegate = mock(TestTimelineDelegate.class);
        final TweetUi tweetUi = mock(TweetUi.class);
        recyclerViewAdapter = new TweetTimelineRecyclerViewAdapter(getContext(),
                mockTimelineDelegate, ANY_STYLE, null, tweetUi);
        if (recyclerViewAdapter.actionCallback instanceof
                TweetTimelineRecyclerViewAdapter.ReplaceTweetCallback) {
            final TweetTimelineRecyclerViewAdapter.ReplaceTweetCallback replaceCallback
                    = (TweetTimelineRecyclerViewAdapter.ReplaceTweetCallback)
                    recyclerViewAdapter.actionCallback;
            assertEquals(mockTimelineDelegate, replaceCallback.delegate);
            assertNull(replaceCallback.cb);
        } else {
            fail("Expected default actionCallback to be a ReplaceTweetCallback");
        }
    }

    public void testConstructor_withActionCallback() {
        final TimelineDelegate<Tweet> mockTimelineDelegate = mock(TestTimelineDelegate.class);
        final Callback<Tweet> mockCallback = mock(Callback.class);
        final TweetUi tweetUi = mock(TweetUi.class);
        recyclerViewAdapter = new TweetTimelineRecyclerViewAdapter(getContext(),
                mockTimelineDelegate, ANY_STYLE, mockCallback, tweetUi);
        // assert that
        // - developer callback wrapped in a ReplaceTweetCallback
        if (recyclerViewAdapter.actionCallback instanceof
                TweetTimelineRecyclerViewAdapter.ReplaceTweetCallback) {
            final TweetTimelineRecyclerViewAdapter.ReplaceTweetCallback replaceCallback
                    = (TweetTimelineRecyclerViewAdapter.ReplaceTweetCallback)
                    recyclerViewAdapter.actionCallback;
            assertEquals(mockTimelineDelegate, replaceCallback.delegate);
            assertEquals(mockCallback, replaceCallback.cb);
        } else {
            fail("Expected actionCallback to be wrapped in ReplaceTweetCallback");
        }
    }

    public void testBuilder() {
        final Timeline<Tweet> mockTimeline = mock(Timeline.class);
        final Callback<Tweet> mockCallback = mock(Callback.class);
        recyclerViewAdapter = new TweetTimelineRecyclerViewAdapter.Builder(getContext())
                .setTimeline(mockTimeline)
                .setOnActionCallback(mockCallback)
                .setViewStyle(R.style.tw__TweetDarkStyle)
                .build();
        assertEquals(R.style.tw__TweetDarkStyle, recyclerViewAdapter.styleResId);
        if (recyclerViewAdapter.actionCallback instanceof
                TweetTimelineRecyclerViewAdapter.ReplaceTweetCallback) {
            final TweetTimelineRecyclerViewAdapter.ReplaceTweetCallback replaceCallback
                    = (TweetTimelineRecyclerViewAdapter.ReplaceTweetCallback)
                    recyclerViewAdapter.actionCallback;
            assertEquals(mockCallback, replaceCallback.cb);
        } else {
            fail("Expected actionCallback to be wrapped in ReplaceTweetCallback");
        }
    }

    public void testBuilder_nullContext() {
        final Timeline<Tweet> mockTimeline = mock(Timeline.class);
        try {
            recyclerViewAdapter =
                    new TweetTimelineRecyclerViewAdapter.Builder(null).setTimeline(mockTimeline)
                    .build();
            fail("Null context should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals(NULL_CONTEXT_MESSAGE, e.getMessage());
        }
    }

    public void testBuilder_nullTimeline() {
        try {
            recyclerViewAdapter =
                    new TweetTimelineRecyclerViewAdapter.Builder(getContext()).setTimeline(null)
                    .build();
            fail("Null timeline should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals(NULL_TIMELINE_MESSAGE, e.getMessage());
        }
    }

    public void testBuilder_withTimelineFilter() {
        final Timeline<Tweet> mockTimeline = mock(Timeline.class);
        final TimelineFilter mockTimelineFilter = mock(TimelineFilter.class);
        recyclerViewAdapter = new TweetTimelineRecyclerViewAdapter.Builder(getContext())
                .setTimeline(mockTimeline)
                .setTimelineFilter(mockTimelineFilter)
                .build();

        assertTrue(recyclerViewAdapter.timelineDelegate instanceof FilterTimelineDelegate);
    }

    public void testBuilder_withNullTimelineFilter() {
        final Timeline<Tweet> mockTimeline = mock(Timeline.class);
        recyclerViewAdapter = new TweetTimelineRecyclerViewAdapter.Builder(getContext())
                .setTimeline(mockTimeline)
                .setTimelineFilter(null)
                .build();

        assertTrue(recyclerViewAdapter.timelineDelegate instanceof TimelineDelegate);
    }

    public void testItemCount_viaConstructor() {
        final Timeline<Tweet> fakeTimeline = new FakeTweetTimeline(ITEM_COUNT);
        final TweetTimelineRecyclerViewAdapter recyclerViewAdapter = new
                TweetTimelineRecyclerViewAdapter(getContext(), fakeTimeline);
        assertEquals(recyclerViewAdapter.getItemCount(), ITEM_COUNT);
    }

    public void testItemCount_viaBuilder() {
        final Timeline<Tweet> fakeTimeline = new FakeTweetTimeline(ITEM_COUNT);
        final TweetTimelineRecyclerViewAdapter recyclerViewAdapter =
                new TweetTimelineRecyclerViewAdapter.Builder(getContext())
                        .setTimeline(fakeTimeline)
                        .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                        .build();
        assertEquals(recyclerViewAdapter.getItemCount(), ITEM_COUNT);
    }

    static class FakeTweetTimeline implements Timeline<Tweet> {
        private long numItems;

        /**
         * Constructs a FakeTweetTimeline
         * @param numItems the number of Tweets to return per call to next/previous
         */
        FakeTweetTimeline(long numItems) {
            this.numItems = numItems;
        }

        @Override
        public void next(Long sinceId, Callback<TimelineResult<Tweet>> cb) {
            final List<Tweet> tweets = TestFixtures.getTweetList(numItems);
            final TimelineCursor timelineCursor = new TimelineCursor(tweets);
            final TimelineResult<Tweet> timelineResult
                    = new TimelineResult<>(timelineCursor, tweets);
            cb.success(new Result<>(timelineResult, null));
        }

        @Override
        public void previous(Long maxId, Callback<TimelineResult<Tweet>> cb) {
            final List<Tweet> tweets = TestFixtures.getTweetList(numItems);
            final TimelineCursor timelineCursor = new TimelineCursor(tweets);
            final TimelineResult<Tweet> timelineResult
                    = new TimelineResult<>(timelineCursor, tweets);
            cb.success(new Result<>(timelineResult, null));
        }
    }

    /**
     * Makes class public so it can be mocked on ART runtime.
     * @param <T>
     */
    public class TestTimelineDelegate<T extends Identifiable> extends TimelineDelegate {
        public TestTimelineDelegate(Timeline<T> timeline) {
            super(timeline);
        }
    }
}
