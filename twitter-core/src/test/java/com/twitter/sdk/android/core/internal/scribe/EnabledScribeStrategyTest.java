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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class EnabledScribeStrategyTest {

    private static final String ANY_USER_AGENT = "ua";
    private static final String ANY_PATH_VERSION = "version";
    private static final String ANY_PATH_TYPE = "type";
    private static final int TEST_SEND_INTERVAL_SECONDS = 60;

    @Test
    public void testConstructor() {
        final ScheduledExecutorService mockExecutor = mock(ScheduledExecutorService.class);
        final ScribeConfig scribeConfig = new ScribeConfig(true, ScribeConfig.BASE_URL,
                ANY_PATH_VERSION, ANY_PATH_TYPE, null, ANY_USER_AGENT,
                ScribeConfig.DEFAULT_MAX_FILES_TO_KEEP, TEST_SEND_INTERVAL_SECONDS);
        new EnabledScribeStrategy(mock(Context.class),
                mockExecutor, mock(ScribeFilesManager.class), scribeConfig,
                mock(ScribeFilesSender.class));

        verify(mockExecutor).scheduleAtFixedRate(any(Runnable.class), anyLong(),
                eq((long) TEST_SEND_INTERVAL_SECONDS), eq(TimeUnit.SECONDS));
    }

    @Test
    public void testGetFileSender() {
        final ScribeConfig scribeConfig = new ScribeConfig(true, ScribeConfig.BASE_URL,
                ANY_PATH_VERSION, ANY_PATH_TYPE, null, ANY_USER_AGENT,
                ScribeConfig.DEFAULT_MAX_FILES_TO_KEEP, TEST_SEND_INTERVAL_SECONDS);
        final ScribeFilesSender mockSender = mock(ScribeFilesSender.class);
        final EnabledScribeStrategy strategy = new EnabledScribeStrategy(mock(Context.class),
                mock(ScheduledExecutorService.class), mock(ScribeFilesManager.class),
                scribeConfig, mockSender);
        assertEquals(mockSender, strategy.getFilesSender());
    }
}
