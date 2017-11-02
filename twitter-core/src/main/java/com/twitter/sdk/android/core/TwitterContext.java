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
package com.twitter.sdk.android.core;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Wraps Context to provide sub directories for Kits
 */
class TwitterContext extends ContextWrapper {
    private final String componentPath;
    private final String componentName;

    TwitterContext(Context base, String componentName, String componentPath) {
        super(base);
        this.componentName = componentName;
        this.componentPath = componentPath;
    }

    @Override
    public File getDatabasePath(String name) {

        final File dir = new File(super.getDatabasePath(name).getParentFile(),
                componentPath);
        dir.mkdirs();

        return new File(dir, name);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
            SQLiteDatabase.CursorFactory factory) {
        return android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(
                getDatabasePath(name), factory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
            SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(
                getDatabasePath(name).getPath(), factory, errorHandler);
    }

    @Override
    public File getFilesDir() {
        return new File(super.getFilesDir(), componentPath);
    }

    @Override
    public File getExternalFilesDir(String type) {
        return new File(super.getExternalFilesDir(type), componentPath);
    }

    @Override
    public File getCacheDir() {
        return new File(super.getCacheDir(), componentPath);
    }

    @Override
    public File getExternalCacheDir() {
        return new File(super.getExternalCacheDir(), componentPath);
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return super.getSharedPreferences(componentName + ":" + name, mode);
    }
}
