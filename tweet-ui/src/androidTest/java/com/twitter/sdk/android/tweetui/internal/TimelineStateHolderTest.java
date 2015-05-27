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

import android.test.AndroidTestCase;

import com.twitter.sdk.android.tweetui.TimelineCursor;

import static org.mockito.Mockito.mock;

public class TimelineStateHolderTest extends AndroidTestCase {
    private static final Long ANY_POSITION = 1234L;
    private static final Long TEST_MIN_POSITION = 1111L;
    private static final Long TEST_MAX_POSITION = 3333L;
    private static final TimelineCursor TEST_TIMELINE_CURSOR = new TimelineCursor(ANY_POSITION,
            ANY_POSITION);

    private TimelineCursor mockTimelineCursor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mockTimelineCursor = mock(TimelineCursor.class);
    }

    public void testConstructor() {
        final TimelineStateHolder holder = new TimelineStateHolder();
        assertNull(holder.nextCursor);
        assertNull(holder.previousCursor);
        assertFalse(holder.requestInFlight.get());
    }

    public void testInternalConstructor() {
        final TimelineStateHolder holder = new TimelineStateHolder(TEST_TIMELINE_CURSOR,
                TEST_TIMELINE_CURSOR);
        assertEquals(TEST_TIMELINE_CURSOR, holder.previousCursor);
        assertEquals(TEST_TIMELINE_CURSOR, holder.nextCursor);
        assertFalse(holder.requestInFlight.get());
    }

    public void testResetCursors() {
        final TimelineStateHolder holder = new TimelineStateHolder(TEST_TIMELINE_CURSOR,
                TEST_TIMELINE_CURSOR);
        holder.resetCursors();
        assertNull(holder.nextCursor);
        assertNull(holder.previousCursor);
    }

    public void testPositionForNext() {
        final TimelineStateHolder holder = new TimelineStateHolder(new TimelineCursor(ANY_POSITION,
                TEST_MAX_POSITION), mockTimelineCursor);
        assertEquals(TEST_MAX_POSITION, holder.positionForNext());
    }

    public void testPositionForNext_nullCursor() {
        final TimelineStateHolder holder = new TimelineStateHolder(null, mockTimelineCursor);
        assertNull(holder.positionForNext());
    }

    public void testSetNextCursor() {
        final TimelineCursor previousCursor = new TimelineCursor(ANY_POSITION, ANY_POSITION);
        final TimelineStateHolder holder = new TimelineStateHolder(
                new TimelineCursor(ANY_POSITION, ANY_POSITION),
                previousCursor);
        holder.setNextCursor(TEST_TIMELINE_CURSOR);
        assertEquals(TEST_TIMELINE_CURSOR, holder.nextCursor);
        assertEquals(previousCursor, holder.previousCursor);
    }

    // first next load will set both nextCursor and previousCursor
    public void testSetNextCursor_firstLoad() {
        final TimelineStateHolder holder = new TimelineStateHolder();
        holder.setNextCursor(TEST_TIMELINE_CURSOR);
        assertEquals(TEST_TIMELINE_CURSOR, holder.nextCursor);
        assertEquals(TEST_TIMELINE_CURSOR, holder.previousCursor);
    }

    public void testPositionForPrevious() {
        final TimelineStateHolder holder = new TimelineStateHolder(mockTimelineCursor,
                new TimelineCursor(TEST_MIN_POSITION, ANY_POSITION));
        assertEquals(TEST_MIN_POSITION, holder.positionForPrevious());
    }

    public void testPositionForPrevious_nullCursor() {
        final TimelineStateHolder holder = new TimelineStateHolder(mockTimelineCursor, null);
        assertNull(holder.positionForPrevious());
    }

    public void testSetPreviousCursor() {
        final TimelineCursor nextCursor = new TimelineCursor(ANY_POSITION, ANY_POSITION);
        final TimelineStateHolder holder = new TimelineStateHolder(nextCursor,
                new TimelineCursor(ANY_POSITION, ANY_POSITION));
        holder.setPreviousCursor(TEST_TIMELINE_CURSOR);
        assertEquals(TEST_TIMELINE_CURSOR, holder.previousCursor);
        assertEquals(nextCursor, holder.nextCursor);
    }

    // first previous load will set both nextCursor and previousCursor
    public void testSetPreviousCursor_firstLoad() {
        final TimelineStateHolder holder = new TimelineStateHolder();
        holder.setPreviousCursor(TEST_TIMELINE_CURSOR);
        assertEquals(TEST_TIMELINE_CURSOR, holder.nextCursor);
        assertEquals(TEST_TIMELINE_CURSOR, holder.previousCursor);
    }

    public void testStartTimelineRequest() {
        final TimelineStateHolder holder = new TimelineStateHolder();
        assertFalse(holder.requestInFlight.get());
        assertTrue(holder.startTimelineRequest());
        assertTrue(holder.requestInFlight.get());
        assertFalse(holder.startTimelineRequest());
    }

    public void testFinishTimelineRequest() {
        final TimelineStateHolder holder = new TimelineStateHolder();
        holder.requestInFlight.set(true);
        holder.finishTimelineRequest();
        assertFalse(holder.requestInFlight.get());
    }
}
