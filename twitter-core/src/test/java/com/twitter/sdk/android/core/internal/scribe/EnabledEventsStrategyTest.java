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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(RobolectricTestRunner.class)
public class EnabledEventsStrategyTest {

    TestEnabledEventsStrategy eventsStrategy;
    ScheduledExecutorService mockExecutor;
    EventsFilesManager mockFilesManager;
    FilesSender mockFilesSender;

    @Before
    public void setUp() throws Exception {
        mockExecutor = mock(ScheduledExecutorService.class);
        mockFilesManager = mock(EventsFilesManager.class);
        mockFilesSender = mock(FilesSender.class);
        eventsStrategy = new TestEnabledEventsStrategy(RuntimeEnvironment.application, mockExecutor,
                mockFilesManager, mockFilesSender);
    }

    @Test
    public void testScheduleTimeBasedRollOverIfNeeded() throws Exception {
        final int rollover = 10;
        eventsStrategy.rolloverIntervalSeconds = rollover;
        eventsStrategy.scheduleTimeBasedRollOverIfNeeded();
        verifyExecutorScheduled(rollover, rollover);
    }

    @Test
    public void testScheduleTimeBasedRollOverIfNeeded_noRollOverInterval() throws Exception {
        eventsStrategy.scheduleTimeBasedRollOverIfNeeded();
        verifyZeroInteractions(mockExecutor);
    }

    @Test
    public void testCancelTimeBasedFileRollOver() throws Exception {
        final ScheduledFuture mockFuture = mock(ScheduledFuture.class);
        eventsStrategy.scheduledRolloverFutureRef.set(mockFuture);

        eventsStrategy.cancelTimeBasedFileRollOver();

        verify(mockFuture).cancel(false);
        assertNull(eventsStrategy.scheduledRolloverFutureRef.get());
    }

    @Test
    public void testDeleteAllEvents() throws Exception {
        eventsStrategy.deleteAllEvents();
        verify(mockFilesManager).deleteAllEventsFiles();
    }

    @Test
    public void testRecordEvent() throws Exception {
        final int rollover = 10;
        final TestEvent testEvent = mock(TestEvent.class);
        eventsStrategy.rolloverIntervalSeconds = rollover;
        eventsStrategy.recordEvent(testEvent);
        verify(mockFilesManager).writeEvent(testEvent);
        verifyExecutorScheduled(rollover, rollover);
    }

    @Test
    public void testRollFileOver() throws Exception {
        doReturn(true).when(mockFilesManager).rollFileOver();
        assertTrue(eventsStrategy.rollFileOver());
    }

    @Test
    public void testRollFileOver_failure() throws Exception {
        doReturn(false).when(mockFilesManager).rollFileOver();
        assertFalse(eventsStrategy.rollFileOver());
    }

    @Test
    public void testRollFileOver_exception() throws Exception {
        doThrow(new IOException()).when(mockFilesManager).rollFileOver();
        assertFalse(eventsStrategy.rollFileOver());
    }

    @Test
    public void testConfigureRollover() throws Exception {
        final int rollover = 10;
        eventsStrategy.configureRollover(rollover);
        assertEquals(rollover, eventsStrategy.rolloverIntervalSeconds);
        verifyExecutorScheduled(0, rollover);
    }

    @Test
    public void testScheduleTimeBasedFileRollOver() throws Exception {
        final long initialDelay = 10L;
        final long frequency = 20L;
        final ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);

        doReturn(mockFuture).when(mockExecutor).scheduleAtFixedRate(
                any(TimeBasedFileRollOverRunnable.class), eq(initialDelay), eq(frequency),
                eq(TimeUnit.SECONDS));

        eventsStrategy.scheduleTimeBasedFileRollOver(initialDelay, frequency);

        assertEquals(mockFuture, eventsStrategy.scheduledRolloverFutureRef.get());
    }

    @Test
    public void testScheduleTimeBasedRollOver_rollOverScheduled() throws Exception {
        final long initialDelay = 10L;
        final long frequency = 20L;
        final ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);

        eventsStrategy.scheduledRolloverFutureRef.set(mockFuture);
        eventsStrategy.scheduleTimeBasedFileRollOver(initialDelay, frequency);
        verifyZeroInteractions(mockExecutor);
    }

    @Test
    public void testSendAndCleanUpIfSuccess() throws Exception {
        final List<File> fileList = new ArrayList<>();
        fileList.add(new File("file-1"));
        fileList.add(new File("file-2"));

        doReturn(fileList).doReturn(Collections.emptyList()).when(mockFilesManager)
                .getBatchOfFilesToSend();
        doReturn(true).when(mockFilesSender).send(fileList);

        eventsStrategy.sendAndCleanUpIfSuccess();

        verify(mockFilesManager, times(2)).getBatchOfFilesToSend();
        verify(mockFilesManager).deleteSentFiles(fileList);
        verifyNoMoreInteractions(mockFilesManager);
    }

    @Test
    public void testSendAndCleanUpIfSuccess_doNothing() throws Exception {
        eventsStrategy.filesSender = null;
        eventsStrategy.sendAndCleanUpIfSuccess();
        verifyZeroInteractions(mockFilesManager);
    }

    @Test
    public void testSendAndCleanUpIfSuccess_noFilesToSend() throws Exception {
        doReturn(Collections.emptyList()).when(mockFilesManager).getBatchOfFilesToSend();
        eventsStrategy.sendAndCleanUpIfSuccess();
        verify(mockFilesManager).getBatchOfFilesToSend();
        verify(mockFilesManager).deleteOldestInRollOverIfOverMax();
        verifyNoMoreInteractions(mockFilesManager);
    }

    @Test
    public void testSendAndCleanUpIfSuccess_failedToSendFirstFile() throws Exception {
        final List<File> fileList = new ArrayList<>();
        fileList.add(new File("file-1"));
        fileList.add(new File("file-2"));

        doReturn(fileList).when(mockFilesManager).getBatchOfFilesToSend();
        doReturn(false).when(mockFilesSender).send(fileList);

        eventsStrategy.sendAndCleanUpIfSuccess();

        verify(mockFilesManager).getBatchOfFilesToSend();
        verify(mockFilesManager).deleteOldestInRollOverIfOverMax();
        verifyNoMoreInteractions(mockFilesManager);
    }

    @Test
    public void testSendAndCleanUpIfSuccess_failedToSendFile() throws Exception {
        final List<File> fileList1 = new ArrayList<>();
        fileList1.add(new File("file-1"));
        fileList1.add(new File("file-2"));
        final List<File> fileList2 = new ArrayList<>();
        fileList2.add(new File("file-3"));

        doReturn(fileList1).doReturn(fileList2).when(mockFilesManager).getBatchOfFilesToSend();
        doReturn(true).when(mockFilesSender).send(fileList1);
        doReturn(false).when(mockFilesSender).send(fileList2);

        eventsStrategy.sendAndCleanUpIfSuccess();

        verify(mockFilesManager, times(2)).getBatchOfFilesToSend();
        verify(mockFilesManager).deleteSentFiles(fileList1);
        verifyNoMoreInteractions(mockFilesManager);
    }

    void verifyExecutorScheduled(long initialDelaySecs, long frequencySecs) {
        verify(mockExecutor).scheduleAtFixedRate(any(TimeBasedFileRollOverRunnable.class),
                eq(initialDelaySecs), eq(frequencySecs), eq(TimeUnit.SECONDS));
    }
}
