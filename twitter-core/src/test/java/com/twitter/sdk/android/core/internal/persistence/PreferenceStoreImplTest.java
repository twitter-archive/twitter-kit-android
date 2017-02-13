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

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class PreferenceStoreImplTest {
    PreferenceStoreImpl preferenceStore;

    @Before
    public void setUp() {
        preferenceStore = new PreferenceStoreImpl(RuntimeEnvironment.application, "Stub");
    }

    @Test
    @SuppressLint("CommitPrefEdits")
    public void testGet() {
        assertNotNull(preferenceStore.get());
        assertTrue(preferenceStore.get() instanceof SharedPreferences);
    }

    @Test
    @SuppressLint("CommitPrefEdits")
    public void testEdit() {
        assertNotNull(preferenceStore.edit());
        assertTrue(preferenceStore.edit() instanceof SharedPreferences.Editor);
    }

    @Test
    @SuppressLint("CommitPrefEdits")
    public void testSave() {
        final String key = "Test Key";
        final String value = "Test Value";
        final SharedPreferences.Editor editor = preferenceStore.edit();
        editor.putString(key, value);
        assertTrue(preferenceStore.save(editor));

        final String result = preferenceStore.get().getString(key, null);

        assertNotNull(result);
        assertEquals(value, result);
    }

    @Test
    @SuppressLint("CommitPrefEdits")
    public void testNamespace() {
        final String key = "Test namespace key";
        final String value = "Test namespace value";

        final PreferenceStoreImpl secondPrefStore =
                new PreferenceStoreImpl(RuntimeEnvironment.application, "PersistenceTest");

        assertNotSame(preferenceStore.get(), secondPrefStore.get());

        preferenceStore.save(preferenceStore.edit().putString(key, value));

        assertNull(secondPrefStore.get().getString(key, null));

    }
}
