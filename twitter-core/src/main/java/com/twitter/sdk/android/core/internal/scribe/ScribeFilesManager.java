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
import java.util.UUID;

class ScribeFilesManager extends EventsFilesManager<ScribeEvent> {

    static final String FILE_PREFIX = "se";
    static final String FILE_EXTENSION = ".tap";

    ScribeFilesManager(Context context, EventTransform<ScribeEvent> transform,
                              CurrentTimeProvider currentTimeProvider,
                              QueueFileEventStorage eventsStorage, int defaultMaxFilesToKeep
    ) throws IOException {
        super(context, transform, currentTimeProvider, eventsStorage, defaultMaxFilesToKeep);
    }

    @Override
    protected String generateUniqueRollOverFileName() {
        final UUID targetUUIDComponent = UUID.randomUUID();

        return new StringBuilder()
                .append(FILE_PREFIX)
                .append(ROLL_OVER_FILE_NAME_SEPARATOR)
                .append(targetUUIDComponent.toString())
                .append(ROLL_OVER_FILE_NAME_SEPARATOR)
                .append(currentTimeProvider.getCurrentTimeMillis())
                .append(FILE_EXTENSION)
                .toString();
    }
}
