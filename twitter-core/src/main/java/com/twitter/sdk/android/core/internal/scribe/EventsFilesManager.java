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
import android.util.Log;

import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.internal.CommonUtils;
import com.twitter.sdk.android.core.internal.CurrentTimeProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages files containing events.  This includes, writing events to them
 * and periodically (size or time triggered) rolling them over to a target directory.
 */
public abstract class EventsFilesManager<T> {

    public static final String ROLL_OVER_FILE_NAME_SEPARATOR = "_";
    public static final int MAX_BYTE_SIZE_PER_FILE = 8000;
    public static final int MAX_FILES_IN_BATCH = 1;
    public static final int MAX_FILES_TO_KEEP = 100;

    protected final Context context;
    protected final EventTransform<T> transform;
    protected final CurrentTimeProvider currentTimeProvider;

    protected final EventsStorage eventStorage;
    private final int defaultMaxFilesToKeep;

    protected volatile long lastRollOverTime;

    protected final List<EventsStorageListener> rollOverListeners = new CopyOnWriteArrayList<>();

    /**
     * @param context Context to use for file access
     * @param transform EventTransform used to convert events to bytes for storage
     * @param currentTimeProvider CurrentTimeProvider that defines how we determine time
     * @param eventStorage EventsStorage into which we will store events
     * @param defaultMaxFilesToKeep int defining the maximum number of storage files we will buffer
     *                              on device. This is useful for providing a constant maximum, or
     *                              a default value that varies based on overriding
     *                              {@link #getMaxFilesToKeep()}
     * @throws IOException
     */
    EventsFilesManager(Context context, EventTransform<T> transform,
            CurrentTimeProvider currentTimeProvider, EventsStorage eventStorage,
            int defaultMaxFilesToKeep)
            throws IOException {
        this.context = context.getApplicationContext();
        this.transform = transform;
        this.eventStorage = eventStorage;
        this.currentTimeProvider = currentTimeProvider;

        lastRollOverTime = this.currentTimeProvider.getCurrentTimeMillis();

        this.defaultMaxFilesToKeep = defaultMaxFilesToKeep;
    }

    public void writeEvent(T event) throws IOException {
        final byte[] eventBytes = transform.toBytes(event);
        rollFileOverIfNeeded(eventBytes.length);

        eventStorage.add(eventBytes);
    }

    /**
     * Register a listener for session analytics file roll over events that may get triggered
     * due to the file reaching threshold size.
     */
    public void registerRollOverListener(EventsStorageListener listener){
        if (listener != null) {
            rollOverListeners.add(listener);
        }
    }

    /**
     * Trigger a file roll over. Returns <code>true</code> if events existed and a roll-over file was created.
     * Returns <code>false</code> if no events existed and a roll-over file was not created.
     */
    public boolean rollFileOver() throws IOException {
        boolean fileRolledOver = false;
        String targetFileName = null;

        // if the current active file is empty, don't roll it over, however
        // still trigger listeners as they may be interested in the fact that an event were fired
        if (!eventStorage.isWorkingFileEmpty()){
            targetFileName = generateUniqueRollOverFileName();
            eventStorage.rollOver(targetFileName);

            CommonUtils.logControlled(context,
                    Log.INFO, Twitter.TAG,
                    String.format(Locale.US,
                            "generated new file %s", targetFileName)
            );

            lastRollOverTime = currentTimeProvider.getCurrentTimeMillis();
            fileRolledOver = true;
        }

        triggerRollOverOnListeners(targetFileName);
        return fileRolledOver;
    }

    /**
     * Roll-over active session analytics file if writing new event will put it over our target
     * limit.
     *
     * @param newEventSizeInBytes size of event to be written
     */
    private void rollFileOverIfNeeded(int newEventSizeInBytes) throws IOException{
        if (!eventStorage.canWorkingFileStore(newEventSizeInBytes, getMaxByteSizePerFile())) {
            final String msg = String.format(Locale.US,
                    "session analytics events file is %d bytes," +
                            " new event is %d bytes, this is over flush limit of %d," +
                            " rolling it over",
                    eventStorage.getWorkingFileUsedSizeInBytes(), newEventSizeInBytes,
                    getMaxByteSizePerFile());
            CommonUtils.logControlled(context, Log.INFO, Twitter.TAG, msg);
            rollFileOver();
        }
    }

    protected abstract String generateUniqueRollOverFileName();

    /**
     * This method can be overridden by subclasses to vary the maximum file count value used
     * during file clean-up.
     */
    protected int getMaxFilesToKeep() {
        return defaultMaxFilesToKeep;
    }

    protected int getMaxByteSizePerFile() {
        return MAX_BYTE_SIZE_PER_FILE;
    }

    public long getLastRollOverTime() {
        return lastRollOverTime;
    }

    private void triggerRollOverOnListeners(String rolledOverFile){
        for (EventsStorageListener eventStorageRollOverListener : rollOverListeners){
            try {
                eventStorageRollOverListener.onRollOver(rolledOverFile);
            } catch (Exception e){
                CommonUtils.logControlledError(context,
                        "One of the roll over listeners threw an exception", e);
            }
        }
    }

    public List<File> getBatchOfFilesToSend(){
        return eventStorage.getBatchOfFilesToSend(MAX_FILES_IN_BATCH);
    }

    public void deleteSentFiles(List<File> files){
        eventStorage.deleteFilesInRollOverDirectory(files);
    }

    public void deleteAllEventsFiles(){
        eventStorage.deleteFilesInRollOverDirectory(
                eventStorage.getAllFilesInRollOverDirectory());
        eventStorage.deleteWorkingFile();
    }

    public void deleteOldestInRollOverIfOverMax(){
        final List<File> allFiles = eventStorage.getAllFilesInRollOverDirectory();
        final int maxFiles = getMaxFilesToKeep();

        if (allFiles.size() <= maxFiles) {
            return;
        }

        final int numberOfFilesToDelete = allFiles.size() - maxFiles;

        CommonUtils.logControlled(context,
                String.format(Locale.US, "Found %d files in " +
                        " roll over directory, this is greater than %d, deleting %d oldest files",
                allFiles.size(), maxFiles, numberOfFilesToDelete));

        final TreeSet<FileWithTimestamp> sortedFiles = new TreeSet<>(
                (arg0, arg1) -> (int) (arg0.timestamp - arg1.timestamp));

        for (File file : allFiles){
            final long creationTimestamp = parseCreationTimestampFromFileName(file.getName());
            sortedFiles.add(new FileWithTimestamp(file, creationTimestamp));
        }

        final ArrayList<File> toDelete = new ArrayList<>();
        for (FileWithTimestamp fileWithTimestamp : sortedFiles){
            toDelete.add(fileWithTimestamp.file);

            if (toDelete.size() == numberOfFilesToDelete){
                break;
            }
        }

        eventStorage.deleteFilesInRollOverDirectory(toDelete);
    }

    public long parseCreationTimestampFromFileName(String fileName){
        final String[] fileNameParts = fileName.split(ROLL_OVER_FILE_NAME_SEPARATOR);

        if (fileNameParts.length != 3) {
            return 0;
        }

        try {
            return Long.valueOf(fileNameParts[2]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    static class FileWithTimestamp{
        final File file;
        final long timestamp;

        FileWithTimestamp(File file, long timestamp) {
            this.file = file;
            this.timestamp = timestamp;
        }
    }
}
