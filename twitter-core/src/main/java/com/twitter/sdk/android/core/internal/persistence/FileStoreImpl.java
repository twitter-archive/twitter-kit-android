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


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;

import java.io.File;

public class FileStoreImpl implements FileStore {
    private final Context context;
    private final String contentPath;
    private final String legacySupport;


    public FileStoreImpl(Kit kit) {
        if (kit.getContext() == null) {
            throw new IllegalStateException("Cannot get directory before context has been set. " +
                    "Call Fabric.with() first");
        }

        context = kit.getContext();
        contentPath = kit.getPath();
        legacySupport = "Android/" + context.getPackageName();
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
        File file = null;
        if (isExternalStorageAvailable()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                file = context.getExternalCacheDir();
            } else {
                file = new File(Environment.getExternalStorageDirectory(),
                        legacySupport + "/cache/" + contentPath);
            }
        }
        return prepare(file);
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
    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    public File getExternalFilesDir() {
        File file = null;
        if (isExternalStorageAvailable()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                file = context.getExternalFilesDir(null);
            } else {
                file = new File(Environment.getExternalStorageDirectory(),
                        legacySupport + "/files/" + contentPath);
            }
        }
        return prepare(file);
    }

    File prepare(File file) {
        if (file != null) {
            if (file.exists() || file.mkdirs()) {
                return file;
            } else {
                Fabric.getLogger().w(Fabric.TAG, "Couldn't create file");
            }
        } else {
            Fabric.getLogger().d(Fabric.TAG, "Null File");
        }
        return null;
    }

    boolean isExternalStorageAvailable() {
        final String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Fabric.getLogger().w(Fabric.TAG,
                    "External Storage is not mounted and/or writable\n" +
                            "Have you declared android.permission.WRITE_EXTERNAL_STORAGE " +
                            "in the manifest?");
            return false;
        }

        return true;
    }
}
