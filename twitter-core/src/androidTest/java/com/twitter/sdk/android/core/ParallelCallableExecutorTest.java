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

import android.test.AndroidTestCase;

import java.util.List;
import java.util.concurrent.Callable;

public class ParallelCallableExecutorTest extends AndroidTestCase {

    public void testGetAllValues_raceCondition() throws Exception {
        final Factory raceConditionFactory = new NonSynchronizedFactory();
        final ParallelCallableExecutor<Object> executor =
                new ParallelCallableExecutor<>(
                        new FactoryCallable(raceConditionFactory),
                        new FactoryCallable(raceConditionFactory));

        final List<Object> results = executor.getAllValues();
        assertNotSame(results.get(0), results.get(1));
    }

    public void testGetAllValues_threadSafe() throws Exception {
        final Factory raceConditionFactory = new SynchronizedFactory();
        final ParallelCallableExecutor<Object> executor =
                new ParallelCallableExecutor<>(
                        new FactoryCallable(raceConditionFactory),
                        new FactoryCallable(raceConditionFactory));

        final List<Object> results = executor.getAllValues();
        assertSame(results.get(0), results.get(1));
    }

    private static class FactoryCallable implements Callable<Object> {
        private Factory factory;

        protected FactoryCallable(Factory factory) {
            this.factory = factory;
        }

        @Override
        public Object call() throws Exception {
            return factory.getObject();
        }
    }

    private interface Factory {
        public Object getObject();
    }

    private static class NonSynchronizedFactory implements Factory {
        private Object object;

        @Override
        public Object getObject() {
            if (object == null) {
                try {
                    Thread.sleep(10); // pretend to do work
                } catch (InterruptedException ie) {
                }
                object = new Object();
            }
            return object;
        }
    }

    private static class SynchronizedFactory implements Factory {
        private volatile Object object;

        @Override
        public Object getObject() {
            if (object == null) {
                createObject();
            }
            return object;
        }

        private synchronized void createObject() {
            if (object == null) {
                try {
                    Thread.sleep(1); // pretend to do work
                } catch (InterruptedException ie) {
                }
                object = new Object();
            }
        }
    }
}

