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

import com.twitter.sdk.android.core.TwitterCoreTest;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Comparator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MigrationHelperTests extends TwitterCoreTest {
    private static final String SHARED_PREFS_DIR = "shared_prefs";
    private static final String KIT_IDENTIFIER = "com.foo.test:test";
    private static final String EXPECTED_PREFERENCE = KIT_IDENTIFIER + ":test.xml";
    private static final String TEST_PREFERENCE = KIT_IDENTIFIER + ":a.b.c.xml";

    MigrationHelper migrationHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        migrationHelper = new MigrationHelper();
    }

    public void testMigrateSessionStore_emptyDirectory() throws Exception {
        final File sharedPrefsDir = getSharedPreferencesDir(getContext());
        clearSharePrefs(sharedPrefsDir);
        createSharedPrefsFolder(sharedPrefsDir);

        migrationHelper.migrateSessionStore(getContext(), KIT_IDENTIFIER, EXPECTED_PREFERENCE);

        assertEquals(0, sharedPrefsDir.listFiles().length);
    }

    public void testMigrateSessionStore_noSharedPrefDirectory() throws Exception {
        final File sharedPrefsDir = getSharedPreferencesDir(getContext());
        clearSharePrefs(sharedPrefsDir);
        deleteSharedPrefsFolder(sharedPrefsDir);

        migrationHelper.migrateSessionStore(getContext(), KIT_IDENTIFIER, EXPECTED_PREFERENCE);

        assertFalse(sharedPrefsDir.exists());
    }

    public void testMigrateSessionStore_notMigrated() throws Exception {
        final File sharedPrefsDir = getSharedPreferencesDir(getContext());
        clearSharePrefs(sharedPrefsDir);
        createSharedPrefsFolder(sharedPrefsDir);
        createFile(sharedPrefsDir, TEST_PREFERENCE);

        migrationHelper.migrateSessionStore(getContext(), KIT_IDENTIFIER, EXPECTED_PREFERENCE);

        final File expected = new File(sharedPrefsDir, EXPECTED_PREFERENCE);
        assertTrue(expected.exists());

        final File oldPrefFile = new File(sharedPrefsDir, TEST_PREFERENCE);
        assertFalse(oldPrefFile.exists());
    }

    public void testMigrateSessionStore_alreadyMigrated() throws Exception {
        final File sharedPrefsDir = getSharedPreferencesDir(getContext());
        clearSharePrefs(sharedPrefsDir);
        createSharedPrefsFolder(sharedPrefsDir);
        createFile(sharedPrefsDir, TEST_PREFERENCE);
        createFile(sharedPrefsDir, EXPECTED_PREFERENCE);

        migrationHelper.migrateSessionStore(getContext(), KIT_IDENTIFIER, EXPECTED_PREFERENCE);

        final File expected = new File(sharedPrefsDir, EXPECTED_PREFERENCE);
        assertTrue(expected.exists());

        final File oldPrefFile = new File(sharedPrefsDir, TEST_PREFERENCE);
        assertTrue(oldPrefFile.exists());
    }

    public void testPrefixFileNameFilter() throws Exception {
        final FilenameFilter filter = new MigrationHelper.PrefixFileNameFilter(KIT_IDENTIFIER);

        assertFalse(filter.accept(null, "foo.xml"));
        assertTrue(filter.accept(null, KIT_IDENTIFIER + "foo.xml"));
    }

    public void testFileLastModifiedComparator() {
        final Comparator<File> comparator = new MigrationHelper.FileLastModifiedComparator();

        final File file1 = mock(File.class);
        when(file1.lastModified()).thenReturn(100L);

        final File file2 = mock(File.class);
        when(file2.lastModified()).thenReturn(200L);

        assertEquals(1, comparator.compare(file1, file2));
        assertEquals(-1, comparator.compare(file2, file1));
    }

    private void clearSharePrefs(File sharedPrefsFolder) {
        final File[] files = sharedPrefsFolder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            file.delete();
        }
    }

    private void createSharedPrefsFolder(File sharedPrefsFolder) {
        if (!sharedPrefsFolder.exists()) {
            sharedPrefsFolder.mkdir();
        }
    }

    private void deleteSharedPrefsFolder(File sharedPrefsFolder) {
        if (sharedPrefsFolder.exists()) {
            sharedPrefsFolder.delete();
        }
    }

    private File getSharedPreferencesDir(Context context) {
        return new File(context.getApplicationInfo().dataDir, SHARED_PREFS_DIR);
    }

    private File createFile(File sharedPrefsFolder, String name) {
        final File result = new File(sharedPrefsFolder, name);
        try {
            result.createNewFile();
        } catch (IOException e) {
            // Ignore
        }

        return result;
    }
}
