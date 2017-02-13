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

import com.twitter.sdk.android.core.internal.CommonUtils;

/**
 * Runnable for performing file rollover based on a set time.
 */
public class TimeBasedFileRollOverRunnable implements Runnable {

    private final Context context;
    private final FileRollOverManager fileRollOverManager;

    public TimeBasedFileRollOverRunnable(Context context, FileRollOverManager fileRollOverManager) {
        this.context = context;
        this.fileRollOverManager = fileRollOverManager;
    }

    @Override
    public void run() {
        try {
            CommonUtils.logControlled(context, "Performing time based file roll over.");
            final boolean fileRolledOver = fileRollOverManager.rollFileOver();

            if (!fileRolledOver) {
                // If no file was rolled over, we didn't have any events. Cancel the scheduled task
                // until we start receiving events again.
                fileRollOverManager.cancelTimeBasedFileRollOver();
            }
        } catch (Exception e) {
            CommonUtils.logControlledError(context, "Failed to roll over file", e);
        }
    }
}
