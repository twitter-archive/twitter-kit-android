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

package com.twitter.sdk.android.core.internal.persistence;

import android.content.Context;
import android.os.Environment;

import com.twitter.sdk.android.core.Twitter;

import java.io.File;

public class FileStoreImpl implements FileStore {
    private final Context context;

    public FileStoreImpl(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }

        this.context = context;
    }

    /**
     *
     * @return Directory to store internal cache files.
     */
    @Override
    public File getCacheDir() {
        return prepare(context.getCacheDir());
    }

    /**
     * Requires {@link android.Manifest.permission#WRITE_EXTERNAL_STORAGE}
     *
     * @return Directory to store External Cache files.
     */
    @Override
    public File getExternalCacheDir() {
        if (isExternalStorageAvailable()) {
            return prepare(context.getExternalCacheDir());
        }

        return prepare(null);
    }

    /**
     *
     * @return Directory to store internal files.
     */
    @Override
    public File getFilesDir() {
        return prepare(context.getFilesDir());
    }

    /**
     * Requires {@link android.Manifest.permission#WRITE_EXTERNAL_STORAGE}
     *
     * @return Directory to store External files.
     */
    @Override
    public File getExternalFilesDir() {
        if (isExternalStorageAvailable()) {
            return prepare(context.getExternalFilesDir(null));
        }

        return prepare(null);
    }

    File prepare(File file) {
        if (file != null) {
            if (file.exists() || file.mkdirs()) {
                return file;
            } else {
                Twitter.getLogger().w(Twitter.TAG, "Couldn't create file");
            }
        } else {
            Twitter.getLogger().d(Twitter.TAG, "Null File");
        }
        return null;
    }

    boolean isExternalStorageAvailable() {
        final String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Twitter.getLogger().w(Twitter.TAG,
                    "External Storage is not mounted and/or writable\n" +
                            "Have you declared android.permission.WRITE_EXTERNAL_STORAGE " +
                            "in the manifest?");
            return false;
        }

        return true;
    }
}
