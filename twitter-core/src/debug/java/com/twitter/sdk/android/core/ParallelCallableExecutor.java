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

package com.twitter.sdk.android.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelCallableExecutor<Value> {

    private final CountDownLatch latch;

    private final List<Future<Value>> tasks = new ArrayList<>();
    private final ExecutorService executorService;

    public ParallelCallableExecutor(Callable<Value>... callables) {
        // The countdown will complete when all callables have "countdown" and
        // plus the getAllValues() have been called.
        this.latch = new CountDownLatch(callables.length + 1);
        executorService = Executors.newFixedThreadPool(callables.length);
        Future<Value> futureTask;
        for (Callable callable : callables) {
            futureTask = executorService.submit(new SynchronizedCallable<Value>(latch, callable));
            tasks.add(futureTask);
        }
    }

    public List<Value> getAllValues() throws Exception {
        final List<Value> values = new ArrayList<Value>();
        // This should be the last countdown that will initiate the calls.
        latch.countDown();
        for (Future<Value> task : tasks) {
            values.add(task.get());
        }
        executorService.shutdown();
        return values;
    }

    private static class SynchronizedCallable<Value> implements Callable<Value> {
        private final Callable<Value> callable;
        private final CountDownLatch latch;

        protected SynchronizedCallable(CountDownLatch latch, Callable<Value> callable) {
            this.callable = callable;
            this.latch = latch;
        }

        @Override
        public Value call() throws Exception {
            latch.countDown();
            latch.await();
            return callable.call();
        }
    }
}
