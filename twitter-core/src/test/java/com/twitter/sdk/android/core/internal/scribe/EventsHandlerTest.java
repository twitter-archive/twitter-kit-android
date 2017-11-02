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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class EventsHandlerTest {

    private EventsStrategy<Object> strategy;
    private ScheduledExecutorService executor;
    private MockEventsHandler eventsHandler;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        strategy = mock(EventsStrategy.class);
        executor = mock(ScheduledExecutorService.class);
        eventsHandler = new MockEventsHandler(RuntimeEnvironment.application, strategy,
                mock(EventsFilesManager.class), executor);
    }

    @Test
    public void testRecordEventAsync() throws Exception {
        final Object object = new Object();
        eventsHandler.recordEventAsync(object, false);

        final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(executor).submit(runnableCaptor.capture());
        final Runnable runnable = runnableCaptor.getValue();

        runnable.run();
        verify(strategy).recordEvent(eq(object));
        verify(strategy, never()).rollFileOver();
    }

    @Test
    public void testRecordEventAsync_sendImmediately() throws Exception {
        final Object object = new Object();
        eventsHandler.recordEventAsync(object, true);

        final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(executor).submit(runnableCaptor.capture());
        final Runnable runnable = runnableCaptor.getValue();

        runnable.run();
        verify(strategy).recordEvent(eq(object));
        verify(strategy).rollFileOver();
    }

    @Test
    public void testRecordEventSync() throws Exception {
        final Object object = new Object();
        eventsHandler.recordEventSync(object);

        final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(executor).submit(runnableCaptor.capture());
        final Runnable runnable = runnableCaptor.getValue();

        runnable.run();
        verify(strategy).recordEvent(eq(object));
    }

    class MockEventsHandler extends EventsHandler<Object> {
        MockEventsHandler(Context context, EventsStrategy<Object> strategy,
                                 EventsFilesManager filesManager,
                                 ScheduledExecutorService executor) {
            super(context, strategy, filesManager, executor);
        }

        @Override
        protected EventsStrategy<Object> getDisabledEventsStrategy() {
            return null;
        }
    }
}
