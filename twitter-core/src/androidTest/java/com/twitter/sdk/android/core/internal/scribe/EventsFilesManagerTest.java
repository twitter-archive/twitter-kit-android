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

import com.twitter.sdk.android.core.internal.CommonUtils;
import com.twitter.sdk.android.core.internal.CurrentTimeProvider;
import com.twitter.sdk.android.core.internal.SystemCurrentTimeProvider;

import io.fabric.sdk.android.FabricAndroidTestCase;
import io.fabric.sdk.android.FabricTestUtils;
import io.fabric.sdk.android.KitStub;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventsFilesManagerTest extends FabricAndroidTestCase {

    EventsFilesManager<TestEvent> filesManager;

    CurrentTimeProvider mockCurrentTimeProvider;
    EventsStorage mockEventStorage;

    TestEventTransform transform;
    TestEvent testEvent;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        FabricTestUtils.resetFabric();
        FabricTestUtils.with(getContext(), new KitStub());

        mockCurrentTimeProvider = mock(CurrentTimeProvider.class);
        mockEventStorage = mock(EventsStorage.class);

        transform = new TestEventTransform();

        testEvent = new TestEvent("id", "msg");
    }

    @Override
    protected void tearDown() throws Exception {
        FabricTestUtils.resetFabric();
        super.tearDown();
    }

    public void testNoRollOverNeeded() throws IOException{
        final long startTime = 10000L;

        when(mockCurrentTimeProvider.getCurrentTimeMillis()).thenReturn(startTime);

        filesManager = new TestEventsFilesManager(getContext(), transform,
                mockCurrentTimeProvider, mockEventStorage,
                "testNoRollOverNeeded", EventsFilesManager.MAX_FILES_TO_KEEP);

        when(mockEventStorage.canWorkingFileStore(anyInt(), anyInt()))
                .thenReturn(true);

        filesManager.writeEvent(testEvent);

        verify(mockEventStorage).add(aryEq(transform.toBytes(testEvent)));
        verify(mockEventStorage).canWorkingFileStore(anyInt(), anyInt());
        verify(mockCurrentTimeProvider, times(1)).getCurrentTimeMillis();

        assertEquals("roll over time should NOT have been updated",
                startTime, filesManager.getLastRollOverTime());
    }

    public void testSizeTriggeredRollOver() throws IOException{
        final long startTime = 10000L;
        final long newMostRecentRollOverTime = 11500L;

        when(mockCurrentTimeProvider.getCurrentTimeMillis())
                .thenReturn(startTime)
                .thenReturn(newMostRecentRollOverTime);

        filesManager = new TestEventsFilesManager(getContext(), transform,
                mockCurrentTimeProvider, mockEventStorage,
                "testSizeTriggeredRollOver", EventsFilesManager.MAX_FILES_TO_KEEP);

        when(mockEventStorage.canWorkingFileStore(anyInt(), anyInt()))
                .thenReturn(false);

        // Called during log message
        when(mockEventStorage.getWorkingFileUsedSizeInBytes()).thenReturn(10000);
        when(mockEventStorage.isWorkingFileEmpty()).thenReturn(false);

        filesManager.writeEvent(testEvent);

        verify(mockEventStorage).add(aryEq(transform.toBytes(testEvent)));
        verify(mockEventStorage).canWorkingFileStore(anyInt(), anyInt());
        verify(mockEventStorage).getWorkingFileUsedSizeInBytes();
        verify(mockEventStorage).isWorkingFileEmpty();
        verify(mockEventStorage).rollOver(any(String.class));
        verify(mockCurrentTimeProvider, times(2)).getCurrentTimeMillis();

        assertEquals("roll over time should have been updated", newMostRecentRollOverTime,
                filesManager.getLastRollOverTime());
    }

    public void testParseTimestampFromRolledOverFileName() throws IOException{
        final long startTime = 10000L;

        when(mockCurrentTimeProvider.getCurrentTimeMillis()).thenReturn(startTime);

        filesManager = new TestEventsFilesManager(getContext(), transform,
                mockCurrentTimeProvider, mockEventStorage,
                "testParseTimestampFromRolledOverFileName",
                EventsFilesManager.MAX_FILES_TO_KEEP);

        assertEquals(10, filesManager.parseCreationTimestampFromFileName("sa_hey_10"));
        assertEquals(0, filesManager.parseCreationTimestampFromFileName("unexpected_badname"));
        assertEquals(0, filesManager.parseCreationTimestampFromFileName(
                "unexpected_nonnumeric_time"));
    }

    public void testWriteEvent() throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        try {
            final EventsStorage stubEventStorage = new EventsStorage() {
                @Override
                public void add(byte[] data) throws IOException {
                    bos.write(data);
                    bos.flush();
                }

                @Override
                public int getWorkingFileUsedSizeInBytes() {
                    return 0;
                }

                @Override
                public boolean isWorkingFileEmpty() {
                    return false;
                }

                @Override
                public void rollOver(String targetName) throws IOException {
                    // Does nothing
                }

                @Override
                public File getWorkingDirectory() {
                    return null;
                }

                @Override
                public File getRollOverDirectory() {
                    return null;
                }

                @Override
                public List<File> getBatchOfFilesToSend(int maxBatchSize) {
                    return null;
                }

                @Override
                public List<File> getAllFilesInRollOverDirectory() {
                    return null;
                }

                @Override
                public void deleteFilesInRollOverDirectory(List<File> files) {
                    // Does nothing
                }

                @Override
                public void deleteWorkingFile() {
                    // Does nothing
                }

                @Override
                public boolean canWorkingFileStore(int newEventSizeInBytes,
                        int maxByteSizePerFile) {
                    return false;
                }
            };

            filesManager = new TestEventsFilesManager(getContext(), transform,
                    new SystemCurrentTimeProvider(), stubEventStorage, "testWriteEvent",
                    EventsFilesManager.MAX_FILES_TO_KEEP);

            filesManager.writeEvent(testEvent);

            // Verify the event transform.
            final byte[] writtenBytes = bos.toByteArray();
            assertTrue(Arrays.equals(transform.toBytes(testEvent), writtenBytes));
            assertEquals(testEvent, transform.fromBytes(writtenBytes));
        } finally {
            CommonUtils.closeQuietly(bos);
        }
    }
}
