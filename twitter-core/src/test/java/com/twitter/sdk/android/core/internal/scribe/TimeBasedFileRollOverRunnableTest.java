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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(RobolectricTestRunner.class)
public class TimeBasedFileRollOverRunnableTest {

    TimeBasedFileRollOverRunnable runnable;
    FileRollOverManager mockFileRollOverManager;

    @Before
    public void setUp() {
        mockFileRollOverManager = mock(FileRollOverManager.class);
        runnable = new TimeBasedFileRollOverRunnable(RuntimeEnvironment.application,
                mockFileRollOverManager);
    }

    @Test
    public void testRun() throws Exception {
        doReturn(true).when(mockFileRollOverManager).rollFileOver();
        runnable.run();
        verify(mockFileRollOverManager).rollFileOver();
        verifyNoMoreInteractions(mockFileRollOverManager);
    }

    @Test
    public void testRun_cancelTask() throws Exception {
        doReturn(false).when(mockFileRollOverManager).rollFileOver();
        runnable.run();
        verify(mockFileRollOverManager).rollFileOver();
        verify(mockFileRollOverManager).cancelTimeBasedFileRollOver();
    }
}
