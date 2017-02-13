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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Orchestrator for file management and server sending logic when the feature is enabled.
 */
public abstract class EnabledEventsStrategy<T> implements EventsStrategy<T> {

    static final int UNDEFINED_ROLLOVER_INTERVAL_SECONDS = -1;

    protected final Context context;
    protected final EventsFilesManager<T> filesManager;

    final ScheduledExecutorService executorService;
    final AtomicReference<ScheduledFuture<?>> scheduledRolloverFutureRef;
    volatile int rolloverIntervalSeconds = UNDEFINED_ROLLOVER_INTERVAL_SECONDS;

    public EnabledEventsStrategy(Context context, ScheduledExecutorService executorService,
            EventsFilesManager<T> filesManager) {
        this.context = context;
        this.executorService = executorService;
        this.filesManager = filesManager;
        this.scheduledRolloverFutureRef = new AtomicReference<>();
    }

    @Override
    public void scheduleTimeBasedRollOverIfNeeded() {
        final boolean hasRollOverInterval =
                rolloverIntervalSeconds != UNDEFINED_ROLLOVER_INTERVAL_SECONDS;
        if (hasRollOverInterval) {
            scheduleTimeBasedFileRollOver(rolloverIntervalSeconds, rolloverIntervalSeconds);
        }
    }

    @Override
    public void sendEvents() {
        sendAndCleanUpIfSuccess();
    }

    @Override
    public void cancelTimeBasedFileRollOver() {
        if (scheduledRolloverFutureRef.get() != null) {
            CommonUtils.logControlled(context,
                    "Cancelling time-based rollover because no events are" +
                            " currently being generated.");
            scheduledRolloverFutureRef.get().cancel(false);
            scheduledRolloverFutureRef.set(null);
        }
    }

    @Override
    public void deleteAllEvents() {
        filesManager.deleteAllEventsFiles();
    }

    @Override
    public void recordEvent(T event) {
        CommonUtils.logControlled(context, event.toString());
        try {
            filesManager.writeEvent(event);
        } catch (IOException e) {
            CommonUtils.logControlledError(context, "Failed to write event.", e);
        }
        scheduleTimeBasedRollOverIfNeeded();
    }

    @Override
    public boolean rollFileOver() {
        try {
            return filesManager.rollFileOver();
        } catch (IOException e) {
            CommonUtils.logControlledError(context, "Failed to roll file over.", e);
        }
        return false;
    }

    protected void configureRollover(int rolloverIntervalSeconds) {
        this.rolloverIntervalSeconds = rolloverIntervalSeconds;
        scheduleTimeBasedFileRollOver(0, this.rolloverIntervalSeconds);
    }

    public int getRollover() {
        return rolloverIntervalSeconds;
    }

    void scheduleTimeBasedFileRollOver(long initialDelaySecs, long frequencySecs) {
        final boolean noRollOverIsScheduled = scheduledRolloverFutureRef.get() == null;
        if (noRollOverIsScheduled) {
            final Runnable rollOverRunnable = new TimeBasedFileRollOverRunnable(context, this);
            CommonUtils.logControlled(context,
                    "Scheduling time based file roll over every " + frequencySecs + " seconds");
            try {
                scheduledRolloverFutureRef.set(executorService.scheduleAtFixedRate(rollOverRunnable,
                        initialDelaySecs, frequencySecs, TimeUnit.SECONDS));
            } catch (RejectedExecutionException e) {
                CommonUtils.logControlledError(context,
                        "Failed to schedule time based file roll over", e);
            }
        }
    }

    /**
     * Guarantees "at least once" semantics for sending events files over to the server. Due to the
     * over-the-wire nature, this (at least once) is the best we can do. At least once is guaranteed
     * by sending all pending, rolled-over files in a single multi-part POST. Files are sent in
     * batches and are deleted only after the batch succeeds.
     *
     * Note that this method is package private (as opposed to private) for testing purposes. It's
     * not thread safe and not intended to be executed by anything other than the
     * {@link java.util.concurrent.ScheduledExecutorService} in this class.
     */
    void sendAndCleanUpIfSuccess() {
        final FilesSender filesSender = getFilesSender();
        if (filesSender == null) {
            CommonUtils.logControlled(context,
                    "skipping files send because we don't yet know the target endpoint");
            return;
        }
        CommonUtils.logControlled(context, "Sending all files");

        int filesSent = 0;
        List<File> batch = filesManager.getBatchOfFilesToSend();

        try {
            while (batch.size() > 0) {
                CommonUtils.logControlled(context, String.format(
                        Locale.US, "attempt to send batch of %d files", batch.size()));
                final boolean cleanup = filesSender.send(batch);

                if (cleanup) {
                    filesSent += batch.size();
                    filesManager.deleteSentFiles(batch);
                }

                if (!cleanup) {
                    break;
                }

                batch = filesManager.getBatchOfFilesToSend();
            }
        } catch (Exception e) {
            CommonUtils.logControlledError(context,
                    "Failed to send batch of analytics files to server: " + e.getMessage(), e);
        }

        if (filesSent == 0) {
            filesManager.deleteOldestInRollOverIfOverMax();
        }
    }
}
