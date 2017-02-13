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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.concurrent.ScheduledExecutorService;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class ScribeHandlerTest {

    private ScheduledExecutorService mockExecutorService;
    private ScribeHandler scribeHandler;

    @Before
    public void setUp() throws Exception {
        mockExecutorService = mock(ScheduledExecutorService.class);
        scribeHandler = new ScribeHandler(RuntimeEnvironment.application,
                mock(EventsStrategy.class), mock(EventsFilesManager.class), mockExecutorService);
    }

    @Test
    public void testScribe() {
        scribeHandler.scribe(mock(ScribeEvent.class));
        verify(mockExecutorService).submit(any(Runnable.class));
    }

    @Test
    public void testScribeAndFlush() {
        scribeHandler.scribeAndFlush(mock(ScribeEvent.class));
        verify(mockExecutorService).submit(any(Runnable.class));
    }

    @Test
    public void testGetDisabledEventsStrategy() {
        final EventsStrategy<ScribeEvent> strategy = scribeHandler.getDisabledEventsStrategy();
        assertTrue(strategy instanceof DisabledEventsStrategy);
    }
}
