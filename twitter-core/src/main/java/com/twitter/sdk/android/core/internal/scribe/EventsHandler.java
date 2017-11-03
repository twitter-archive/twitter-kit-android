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

import java.util.concurrent.ScheduledExecutorService;

public abstract class EventsHandler<T> implements EventsStorageListener {

    protected final Context context;
    protected final ScheduledExecutorService executor;

    /**
     * Thread safety guaranteed by accessing through executor.
     */
    protected EventsStrategy<T> strategy;

    public EventsHandler(Context context, EventsStrategy<T> strategy,
            EventsFilesManager filesManager, ScheduledExecutorService executor) {
        this.context = context.getApplicationContext();
        this.executor = executor;
        this.strategy = strategy;

        filesManager.registerRollOverListener(this);
    }

    public void recordEventAsync(final T event, final boolean sendImmediately) {
        executeAsync(() -> {
            try {
                strategy.recordEvent(event);

                if (sendImmediately) {
                    // this triggers call to onRollover()
                    strategy.rollFileOver();
                }
            } catch (Exception e) {
                CommonUtils.logControlledError(context, "Failed to record event.", e);
            }
        });
    }

    public void recordEventSync(final T event) {
        executeSync(() -> {
            try {
                strategy.recordEvent(event);
            } catch (Exception e) {
                CommonUtils.logControlledError(context,
                        "Failed to record event", e);
            }
        });
    }

    @Override
    public void onRollOver(String rolledOverFile) {
        executeAsync(() -> {
            try {
                strategy.sendEvents();
            } catch (Exception e) {
                CommonUtils.logControlledError(context, "Failed to send events files.", e);
            }
        });
    }

    public void disable() {
        executeAsync(() -> {
            try {
                final EventsStrategy<T> prevStrategy = strategy;
                strategy = getDisabledEventsStrategy();
                prevStrategy.deleteAllEvents();
            } catch (Exception e) {
                CommonUtils.logControlledError(context, "Failed to disable events.", e);
            }
        });
    }

    protected abstract EventsStrategy<T> getDisabledEventsStrategy();

    protected void executeSync(Runnable runnable) {
        try {
            executor.submit(runnable).get();
        } catch (Exception e) {
            CommonUtils.logControlledError(context, "Failed to run events task", e);
        }
    }

    protected void executeAsync(Runnable runnable) {
        try {
            executor.submit(runnable);
        } catch (Exception e) {
            CommonUtils.logControlledError(context, "Failed to submit events task", e);
        }
    }
}
