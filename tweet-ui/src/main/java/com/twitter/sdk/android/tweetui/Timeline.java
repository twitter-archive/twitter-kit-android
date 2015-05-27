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

/**
 * Abstracts a source of items which are bi-directionally traversable (next and previous).
 * @param <T> timeline item type
 */
public interface Timeline<T> {

    /**
     * Loads items with position greater than (above) minPosition. If minPosition is null, loads
     * the newest items.
     * @param minPosition minimum position of the items to load (exclusive).
     * @param cb callback.
     */
    void next(Long minPosition, final Callback<TimelineResult<T>> cb);

    /**
     * Loads items with position less than (below) maxId.
     * @param maxPosition maximum position of the items to load (exclusive).
     * @param cb callback.
     */
    void previous(Long maxPosition, final Callback<TimelineResult<T>> cb);
}
