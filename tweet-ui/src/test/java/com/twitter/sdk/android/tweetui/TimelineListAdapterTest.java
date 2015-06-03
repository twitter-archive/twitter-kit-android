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

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.Identifiable;
import com.twitter.sdk.android.tweetui.internal.TimelineDelegate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class TimelineListAdapterTest extends TweetUiAndroidTestCase {
    private static final int TEST_POSITION = 10;
    private TimelineListAdapter<TestItem> listAdapter;
    private TimelineDelegate<TestItem> mockTimelineDelegate;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        mockTimelineDelegate = mock(TestTimelineDelegate.class);
    }

    @Test
    public void testConstructor() {
        listAdapter = new TestTimelineListAdapter<>(getContext(), mockTimelineDelegate);
        verify(mockTimelineDelegate).refresh(null);
    }

    @Test
    public void testConstructor_nullTimeline() {
        try {
            new TestTimelineListAdapter<>(getContext(), (Timeline) null);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            assertEquals("Timeline must not be null", e.getMessage());
        }
    }

    @Test
    public void testRefresh() {
        listAdapter = new TestTimelineListAdapter<>(getContext(), mockTimelineDelegate);
        final Callback<TimelineResult<TestItem>> mockCallback = mock(Callback.class);
        listAdapter.refresh(mockCallback);
        verify(mockTimelineDelegate).refresh(mockCallback);
    }

    @Test
    public void testGetCount() {
        listAdapter = new TestTimelineListAdapter<>(getContext(), mockTimelineDelegate);
        listAdapter.getCount();
        verify(mockTimelineDelegate).getCount();
    }

    @Test
    public void testGetItem() {
        listAdapter = new TestTimelineListAdapter<>(getContext(), mockTimelineDelegate);
        listAdapter.getItem(TEST_POSITION);
        verify(mockTimelineDelegate).getItem(TEST_POSITION);
    }

    @Test
    public void testGetItemId() {
        listAdapter = new TestTimelineListAdapter<>(getContext(), mockTimelineDelegate);
        listAdapter.getItemId(TEST_POSITION);
        verify(mockTimelineDelegate).getItemId(TEST_POSITION);
    }

    @Test
    public void testRegisterDataSetObserver() {
        listAdapter = new TestTimelineListAdapter<>(getContext(), mockTimelineDelegate);
        listAdapter.registerDataSetObserver(mock(DataSetObserver.class));
        verify(mockTimelineDelegate, times(1)).registerDataSetObserver(any(DataSetObserver.class));
    }

    @Test
    public void testUnregisterDataSetObserver() {
        listAdapter = new TestTimelineListAdapter<>(getContext(), mockTimelineDelegate);
        listAdapter.unregisterDataSetObserver(mock(DataSetObserver.class));
        verify(mockTimelineDelegate, times(1))
                .unregisterDataSetObserver(any(DataSetObserver.class));
    }

    @Test
    public void testNotifyDataSetChanged() {
        listAdapter = new TestTimelineListAdapter<>(getContext(), mockTimelineDelegate);
        listAdapter.notifyDataSetChanged();
        verify(mockTimelineDelegate, times(1)).notifyDataSetChanged();
    }

    @Test
    public void testNotifyDataSetInvalidated() {
        listAdapter = new TestTimelineListAdapter<>(getContext(), mockTimelineDelegate);
        listAdapter.notifyDataSetInvalidated();
        verify(mockTimelineDelegate, times(1)).notifyDataSetInvalidated();
    }

    /**
     * Implement abstract method getView to create a concrete subclass TestTimelineListAdapter so
     * that TimelineListAdapter non-view related behavior can be tested.
     */
    public class TestTimelineListAdapter<T extends Identifiable> extends TimelineListAdapter<T> {

        TestTimelineListAdapter(Context context, Timeline<T> timeline) {
            super(context, timeline);
        }

        TestTimelineListAdapter(Context context, TimelineDelegate<T> delegate) {
           super(context, delegate);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
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
