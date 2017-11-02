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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Component which holds a TimelineAdapter's data about whether a request is in flight and the
 * scroll position TimelineCursors.
 */
class TimelineStateHolder {
    // cursor for Timeline 'next' calls
    TimelineCursor nextCursor;
    // cursor for Timeline 'previous' calls
    TimelineCursor previousCursor;
    // true while a request is in flight, false otherwise
    public final AtomicBoolean requestInFlight = new AtomicBoolean(false);

    TimelineStateHolder() {
        // intentionally blank
    }

    /* for testing */
    TimelineStateHolder(TimelineCursor nextCursor, TimelineCursor previousCursor) {
        this.nextCursor = nextCursor;
        this.previousCursor = previousCursor;
    }

    /**
     * Nulls the nextCursor and previousCursor
     */
    public void resetCursors() {
        nextCursor = null;
        previousCursor = null;
    }

    /**
     * Returns the position to use for the subsequent Timeline.next call.
     */
    public Long positionForNext() {
        return nextCursor == null ? null : nextCursor.maxPosition;
    }

    /**
     * Returns the position to use for the subsequent Timeline.previous call.
     */
    public Long positionForPrevious() {
        return previousCursor == null ? null : previousCursor.minPosition;
    }

    /**
     * Updates the nextCursor
     */
    public void setNextCursor(TimelineCursor timelineCursor) {
        nextCursor = timelineCursor;
        setCursorsIfNull(timelineCursor);
    }

    /**
     * Updates the previousCursor.
     */
    public void setPreviousCursor(TimelineCursor timelineCursor) {
        previousCursor = timelineCursor;
        setCursorsIfNull(timelineCursor);
    }

    /**
     * If a nextCursor or previousCursor is null, sets it to timelineCursor. Should be called by
     * setNextCursor and setPreviousCursor to handle the very first timeline load which sets
     * both cursors.
     */
    public void setCursorsIfNull(TimelineCursor timelineCursor) {
        if (nextCursor == null) {
            nextCursor = timelineCursor;
        }
        if (previousCursor == null) {
            previousCursor = timelineCursor;
        }
    }

    /**
     * Returns true if a timeline request is not in flight, false otherwise. If true, a caller
     * must later call finishTimelineRequest to remove the requestInFlight lock.
     */
    public boolean startTimelineRequest() {
        return requestInFlight.compareAndSet(false, true);
    }

    /**
     * Unconditionally sets requestInFlight to false.
     */
    public void finishTimelineRequest() {
        requestInFlight.set(false);
    }
}
