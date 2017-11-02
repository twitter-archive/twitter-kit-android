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

class EnabledScribeStrategy extends EnabledEventsStrategy<ScribeEvent> {

    private final FilesSender filesSender;

    EnabledScribeStrategy(Context context, ScheduledExecutorService executorService,
            ScribeFilesManager filesManager, ScribeConfig config, ScribeFilesSender filesSender) {
        super(context, executorService, filesManager);
        this.filesSender = filesSender;

        configureRollover(config.sendIntervalSeconds);
    }

    @Override
    public FilesSender getFilesSender() {
        return filesSender;
    }
}
