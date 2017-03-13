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

import com.twitter.sdk.android.core.internal.CurrentTimeProvider;

import java.io.IOException;

class TestEventsFilesManager extends EventsFilesManager<TestEvent> {

    private final String rollOverFileName;

    TestEventsFilesManager(Context context, EventTransform<TestEvent> transform,
                                  CurrentTimeProvider currentTimeProvider,
                                  EventsStorage eventStorage, String rollOverFileName,
                                  int defaultMaxFilesToKeep) throws IOException {
        super(context, transform, currentTimeProvider, eventStorage, defaultMaxFilesToKeep);

        this.rollOverFileName = rollOverFileName;
    }

    @Override
    protected String generateUniqueRollOverFileName() {
        return rollOverFileName;
    }
}
