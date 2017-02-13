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

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Stores events in a single file in the working directory and on-demand
 * rolls that file over to the specified target in the roll-over directory.
 * Once rolled over, a new working file is created.
 */
public interface EventsStorage {

    /**
     * Add an event represented as a byte array to the working file
     */
    void add(byte[] data)  throws IOException;

    /**
     * @return size in bytes of the current working file
     */
    int getWorkingFileUsedSizeInBytes();

    /**
     * @return true if the working file has not had any events written to it
     */
    boolean isWorkingFileEmpty();

    /**
     * roll the current working file over to a file in the roll-over directory
     * with the given name.  A new working file is created
     */
    void rollOver(String targetName) throws IOException;

    /**
     * @return working directory
     */
    File getWorkingDirectory();

    /**
     * @return roll-over directory
     */
    File getRollOverDirectory();

    /**
     * @param maxBatchSize maximum number of files to include in batch
     * @return a list of files that are ready for being sent to the server
     */
    List<File> getBatchOfFilesToSend(int maxBatchSize);

    /**
     * @return all of the files currently in the roll over directory
     */
    List<File> getAllFilesInRollOverDirectory();

    /**
     * Clear out given set of files in roll-over directory
     */
    void deleteFilesInRollOverDirectory(List<File> files);

    /**
     * Clear out active working file
     */
    void deleteWorkingFile();

    /**
     * @param newEventSizeInBytes the size in bytes of the event we would like to write to the working file
     * @param maxByteSizePerFile The size in bytes which we will enforce as the maximum for the working file
     * @return <code>true</code> if the new event size will fit in the working file, <code>false</code> if not
     */
    boolean canWorkingFileStore(int newEventSizeInBytes, int maxByteSizePerFile);
}
