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

package com.twitter.sdk.android.core.internal;

import com.twitter.sdk.android.core.Twitter;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.SECONDS;

public final class ExecutorUtils {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 1L;
    private static final long DEFAULT_TERMINATION_TIMEOUT = 1L;

    private ExecutorUtils() {
    }

    public static ExecutorService buildThreadPoolExecutorService(String name) {
        final ThreadFactory threadFactory = ExecutorUtils.getNamedThreadFactory(name);
        final ExecutorService executor =
                new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, SECONDS,
                        new LinkedBlockingQueue<Runnable>(), threadFactory);
        ExecutorUtils.addDelayedShutdownHook(name, executor);
        return executor;
    }

    public static ScheduledExecutorService buildSingleThreadScheduledExecutorService(String name) {
        final ThreadFactory threadFactory = ExecutorUtils.getNamedThreadFactory(name);
        final ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor(threadFactory);
        ExecutorUtils.addDelayedShutdownHook(name, executor);
        return executor;
    }

    static ThreadFactory getNamedThreadFactory(final String threadNameTemplate) {
        final AtomicLong count = new AtomicLong(1);

        return runnable -> {
            final Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setName(threadNameTemplate + count.getAndIncrement());
            return thread;
        };
    }

    static void addDelayedShutdownHook(String serviceName, ExecutorService service){
        ExecutorUtils.addDelayedShutdownHook(serviceName, service, DEFAULT_TERMINATION_TIMEOUT,
                SECONDS);
    }

    static void addDelayedShutdownHook(final String serviceName,
            final ExecutorService service, final long terminationTimeout, final TimeUnit timeUnit) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                service.shutdown();
                if (!service.awaitTermination(terminationTimeout, timeUnit)) {
                    Twitter.getLogger().d(Twitter.TAG, serviceName + " did not shutdown in the"
                            + " allocated time. Requesting immediate shutdown.");
                    service.shutdownNow();
                }
            } catch (InterruptedException e) {
                Twitter.getLogger().d(Twitter.TAG, String.format(Locale.US,
                        "Interrupted while waiting for %s to shut down." +
                                " Requesting immediate shutdown.",
                        serviceName));
                service.shutdownNow();
            }
        }, "Twitter Shutdown Hook for " + serviceName));
    }
}
