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

import android.os.Environment;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.FabricAndroidTestCase;
import io.fabric.sdk.android.FabricTestUtils;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.KitStub;

import java.io.File;

public class FileStoreImplTest extends FabricAndroidTestCase {
    FileStoreImpl fileStore;
    Kit kit;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        FabricTestUtils.resetFabric();
        FabricTestUtils.with(getContext(), new KitStub(), new PersistenceTestKit());
        kit = Fabric.getKit(KitStub.class);
        fileStore = new FileStoreImpl(kit);
    }

    public void testConstructor() {
        try {
            new FileStoreImpl(new KitStub());
            fail();
        } catch (IllegalStateException ex) {}
    }

    public void testGetCacheDir() {
        verifyFile(fileStore.getCacheDir());
    }
    public void testGetFilesDir() {
        verifyFile(fileStore.getFilesDir());
    }

    public void testGetExternalCacheDir() {
        verifyFile(fileStore.getExternalCacheDir());
    }
    public void testGetExternalFilesDir() {
        verifyFile(fileStore.getExternalFilesDir());
    }

    public void testPrepare() {
        verifyFile(fileStore.prepare(new File(getContext().getFilesDir(), "FileStoreImplTest/")));
    }

    public void testisExternalStorageAvailable() {
        final String state = Environment.getExternalStorageState();
        assertEquals(Environment.MEDIA_MOUNTED.equals(state),
                fileStore.isExternalStorageAvailable());
    }

    public void testNamespace() {
        final FileStoreImpl secondFileStore =
                new FileStoreImpl(Fabric.getKit(PersistenceTestKit.class));

        assertNotSame(fileStore.getFilesDir().getPath(), secondFileStore.getFilesDir().getPath());
        assertNotSame(fileStore.getCacheDir().getPath(), secondFileStore.getCacheDir().getPath());
        assertNotSame(fileStore.getExternalFilesDir().getPath(),
                secondFileStore.getExternalFilesDir().getPath());
        assertNotSame(fileStore.getExternalCacheDir().getPath(),
                secondFileStore.getExternalCacheDir().getPath());
    }

    private void verifyFile(File file) {
        assertNotNull(file);
        assertTrue(file.exists());
    }
}
