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

import android.content.Context;

import java.util.concurrent.ScheduledExecutorService;

class ScribeHandler extends EventsHandler<ScribeEvent> {

    ScribeHandler(Context context, EventsStrategy<ScribeEvent> strategy,
            EventsFilesManager filesManager, ScheduledExecutorService executorService) {
        super(context, strategy, filesManager, executorService);
    }

    /**
     * Scribes an event.
     */
    public void scribe(ScribeEvent event) {
        recordEventAsync(event, false);
    }

    /**
     * Scribes an event and immediately flushes the event.
     */
    public void scribeAndFlush(ScribeEvent event) {
        recordEventAsync(event, true);
    }

    @Override
    protected EventsStrategy<ScribeEvent> getDisabledEventsStrategy() {
        return new DisabledEventsStrategy<>();
    }
}
