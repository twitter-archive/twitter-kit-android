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

package com.twitter.sdk.android.core.internal.scribe;

public class DisabledEventsStrategy<T> implements EventsStrategy<T> {

    @Override
    public void sendEvents() {
        // Does nothing
    }

    @Override
    public void deleteAllEvents() {
        // Does nothing
    }

    @Override
    public void recordEvent(T event) {
        // Does nothing
    }

    @Override
    public void cancelTimeBasedFileRollOver() {
        // Does nothing
    }

    @Override
    public void scheduleTimeBasedRollOverIfNeeded() {
        // Does nothing
    }

    @Override
    public boolean rollFileOver() {
        return false;
    }

    @Override
    public FilesSender getFilesSender() {
        return null;
    }
}
