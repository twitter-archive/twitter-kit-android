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

import com.twitter.sdk.android.core.internal.CommonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An implementation of {@link EventsStorage} backed by a {@link QueueFile}.
 *
 * Note that this class is not thread safe and relies on upstream collaborators
 * to ensure thread safe access.
 */
public class QueueFileEventStorage implements EventsStorage {

    private final Context context;
    private final File workingDirectory;
    private final String targetDirectoryName;
    private final File workingFile;

    private QueueFile queueFile;
    private File targetDirectory;

    public QueueFileEventStorage(Context context, File workingDirectory, String workingFileName,
            String targetDirectoryName) throws IOException {
        this.context = context;
        this.workingDirectory = workingDirectory;
        this.targetDirectoryName = targetDirectoryName;

        workingFile = new File(this.workingDirectory, workingFileName);

        queueFile = new QueueFile(workingFile);

        createTargetDirectory();
    }

    private void createTargetDirectory(){
        targetDirectory = new File(workingDirectory, targetDirectoryName);
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }
    }

    @Override
    public void add(byte[] data) throws IOException {
        queueFile.add(data);
    }

    @Override
    public int getWorkingFileUsedSizeInBytes() {
        return queueFile.usedBytes();
    }

    @Override
    public void rollOver(String targetName) throws IOException {
        queueFile.close();

        move(workingFile, new File(targetDirectory, targetName));

        queueFile = new QueueFile(workingFile);
    }

    /**
     * <p>
     * Moves the content of the <code>sourceFile</code> and stores it in the <code>targetFile</code>.
     * The <code>sourceFile</code> is then deleted, completing the semantics of a file "move".
     * </p>
     * <p>
     * The source file will always be deleted, so if there is an error during the process, the data will be
     * lost. This is preferable to leaving the data in place because of the expectations of the calling code.
     * </p>
     */
    private void move(File sourceFile, File targetFile) throws IOException {
        OutputStream fos = null;
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(sourceFile);
            fos = getMoveOutputStream(targetFile);
            CommonUtils.copyStream(fis, fos, new byte[1024]);
        } finally {
            CommonUtils.closeOrLog(fis, "Failed to close file input stream");
            CommonUtils.closeOrLog(fos, "Failed to close output stream");
            sourceFile.delete();
        }
    }

    /**
     * Returns the output stream to be used for moving file contents to the specified {@code targetFile}.
     * Override this method if you need a different output stream, such as one that supports
     * compression.
     */
    public OutputStream getMoveOutputStream(File targetFile) throws IOException {
        return new FileOutputStream(targetFile);
    }

    @Override
    public File getWorkingDirectory() {
        return workingDirectory;
    }

    @Override
    public File getRollOverDirectory() {
        return targetDirectory;
    }

    @Override
    public List<File> getBatchOfFilesToSend(int maxBatchSize) {
        final List<File> batch = new ArrayList<>();

        for (File file : targetDirectory.listFiles()){
            batch.add(file);

            if (batch.size() >= maxBatchSize) {
                break;
            }
        }

        return batch;
    }

    @Override
    public void deleteFilesInRollOverDirectory(List<File> files) {
        for (File file : files){
            CommonUtils.logControlled(context,
                    String.format("deleting sent analytics file %s", file.getName()));
            file.delete();
        }
    }

    @Override
    public List<File> getAllFilesInRollOverDirectory() {
        return Arrays.asList(targetDirectory.listFiles());
    }

    @Override
    public void deleteWorkingFile() {
        try {
            queueFile.close();
        } catch (IOException ignore) {
        }
        workingFile.delete();
    }

    @Override
    public boolean isWorkingFileEmpty() {
        return queueFile.isEmpty();
    }

    @Override
    public boolean canWorkingFileStore(int newEventSizeInBytes, int maxByteSizePerFile) {
        return queueFile.hasSpaceFor(newEventSizeInBytes, maxByteSizePerFile);
    }
}
