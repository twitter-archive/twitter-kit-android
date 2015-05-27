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

import io.fabric.sdk.android.FabricAndroidTestCase;
import io.fabric.sdk.android.services.events.DisabledEventsStrategy;
import io.fabric.sdk.android.services.events.EventsFilesManager;
import io.fabric.sdk.android.services.events.EventsStrategy;

import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ScribeHandlerTest extends FabricAndroidTestCase {

    private ScheduledExecutorService mockExecutorService;
    private ScribeHandler scribeHandler;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mockExecutorService = mock(ScheduledExecutorService.class);
        scribeHandler = new ScribeHandler(getContext(), mock(EventsStrategy.class),
                mock(EventsFilesManager.class), mockExecutorService);
    }

    public void testScribe() {
        scribeHandler.scribe(mock(ScribeEvent.class));
        verify(mockExecutorService).submit(any(Runnable.class));
    }

    public void testScribeAndFlush() {
        scribeHandler.scribeAndFlush(mock(ScribeEvent.class));
        verify(mockExecutorService).submit(any(Runnable.class));
    }

    public void testGetDisabledEventsStrategy() {
        final EventsStrategy<ScribeEvent> strategy = scribeHandler.getDisabledEventsStrategy();
        assertTrue(strategy instanceof DisabledEventsStrategy);
    }
}
