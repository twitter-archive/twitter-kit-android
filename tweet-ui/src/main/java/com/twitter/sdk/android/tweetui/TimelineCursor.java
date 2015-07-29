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

import com.twitter.sdk.android.core.models.Identifiable;

import java.util.List;

/**
 * TimelineCursor represents the position and containsLastItem data from a Timeline response.
 */
public class TimelineCursor {
    public final Long minPosition;
    public final Long maxPosition;

    /**
     * Constructs a TimelineCursor storing position and containsLastItem data.
     * @param minPosition the minimum position of items received or Null
     * @param maxPosition the maximum position of items received or Null
     */
    public TimelineCursor(Long minPosition, Long maxPosition) {
        this.minPosition = minPosition;
        this.maxPosition = maxPosition;
    }

    /**
     * Constructs a TimelineCursor by reading the maxPosition from the start item and the
     * minPosition from the last item.
     * @param items items from the maxPosition item to the minPosition item
     */
    TimelineCursor(List<? extends Identifiable> items) {
        this.minPosition = items.size() > 0 ? items.get(items.size() - 1).getId() : null;
        this.maxPosition = items.size() > 0 ? items.get(0).getId() : null;
    }
}
