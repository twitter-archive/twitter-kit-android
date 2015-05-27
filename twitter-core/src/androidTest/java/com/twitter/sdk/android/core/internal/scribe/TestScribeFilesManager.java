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

import io.fabric.sdk.android.services.common.CurrentTimeProvider;
import io.fabric.sdk.android.services.events.EventTransform;
import io.fabric.sdk.android.services.events.QueueFileEventStorage;

import java.io.IOException;

/**
 * Test class to allow mocking of ScribeFilesManager.
 */
public class TestScribeFilesManager extends ScribeFilesManager {

    public TestScribeFilesManager(Context context, EventTransform<ScribeEvent> transform,
            CurrentTimeProvider currentTimeProvider, QueueFileEventStorage eventsStorage,
            int maxFilesToKeep) throws IOException {
        super(context, transform, currentTimeProvider, eventsStorage, maxFilesToKeep);
    }
}
