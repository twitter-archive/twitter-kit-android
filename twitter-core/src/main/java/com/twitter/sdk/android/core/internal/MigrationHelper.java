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

package com.twitter.sdk.android.core.internal;

import android.content.Context;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

public class MigrationHelper {
    private static final String SHARED_PREFS_DIR = "shared_prefs";

    public void migrateSessionStore(Context context, String prefixMatch, String expectedFileName) {
        final File sharedPrefsDir = getSharedPreferencesDir(context);

        // shared_prefs dir has not been created, do nothing
        if (!sharedPrefsDir.exists() || !sharedPrefsDir.isDirectory()) {
            return;
        }

        // if shared prefs already exist, do nothing
        final File expectedSharedPrefsFile = new File(sharedPrefsDir, expectedFileName);
        if (expectedSharedPrefsFile.exists()) {
            return;
        }

        // rename latest
        final File oldPrefsharedPrefsFile = getLatestFile(sharedPrefsDir, prefixMatch);
        if (oldPrefsharedPrefsFile != null) {
            oldPrefsharedPrefsFile.renameTo(expectedSharedPrefsFile);
        }
    }

    File getSharedPreferencesDir(Context context) {
        return new File(context.getApplicationInfo().dataDir, SHARED_PREFS_DIR);
    }

    File getLatestFile(File sharedPrefsDir, String prefix) {
        final File[] files = sharedPrefsDir.listFiles(new PrefixFileNameFilter(prefix));
        Arrays.sort(files, new FileLastModifiedComparator());
        return files.length > 0 ? files[0] : null;
    }

    static class FileLastModifiedComparator implements Comparator<File> {
        @Override
        public int compare(File file1, File file2) {
            return Long.valueOf(file2.lastModified()).compareTo(file1.lastModified());
        }
    }

    static class PrefixFileNameFilter implements FilenameFilter {
        final String prefix;

        public PrefixFileNameFilter(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public boolean accept(File file, String filename) {
            return filename.startsWith(prefix);
        }
    }
}
