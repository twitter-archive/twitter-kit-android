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
import android.content.SharedPreferences;

import com.twitter.sdk.android.core.internal.persistence.PreferenceStore;
import com.twitter.sdk.android.core.internal.persistence.PreferenceStoreImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static com.twitter.sdk.android.core.internal.IdManager.ADVERTISING_PREFERENCES;
import static com.twitter.sdk.android.core.internal.IdManager.PREFKEY_INSTALLATION_UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class IdManagerTest {
    static final String TEST_PACKAGE = "com.twitter.sdk.android.core";
    static final String TEST_VERSION = "5.0.0_r2/0";
    static final String TEST_SECURE_ID = "abc123456789";
    static final String TEST_AD_ID = "123456789";
    static final String TEST_UUID = "d460e2b9-d298-4c24-9bbe-a407f5012876";

    @Mock
    AdvertisingInfoProvider mockAdvertisingInfoProvider;
    PreferenceStore mockPreferenceStore;
    AdvertisingInfo advertisingInfo;
    IdManager idManager;
    Context context;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        advertisingInfo = new AdvertisingInfo(TEST_AD_ID, true);
        when(mockAdvertisingInfoProvider.getAdvertisingInfo()).thenReturn(advertisingInfo);

        context = RuntimeEnvironment.application;

        mockPreferenceStore = new PreferenceStoreImpl(context, ADVERTISING_PREFERENCES);

        idManager = new IdManager(context, mockPreferenceStore, mockAdvertisingInfoProvider);
    }

    @Test
    public void testGetAppIdentifier() {
        assertEquals(TEST_PACKAGE, idManager.getAppIdentifier());
    }

    @Test
    public void testGetOsVersionString() {
        assertEquals(TEST_VERSION, idManager.getOsVersionString());
    }

    @Test
    public void testGetAdvertisingId() {
        assertEquals(TEST_AD_ID, idManager.getAdvertisingId());
        assertTrue(idManager.fetchedAdvertisingInfo);
    }

    @Test
    public void testIsLimitAdTrackingEnabled() {
        assertTrue(idManager.isLimitAdTrackingEnabled());
        assertTrue(idManager.fetchedAdvertisingInfo);
    }

    @Test
    public void testGetDeviceUUID_shouldReturnUUID() {
        final String uuid = idManager.getDeviceUUID();
        assertNotNull(TEST_SECURE_ID, uuid);

        final SharedPreferences prefs = context
                .getSharedPreferences(ADVERTISING_PREFERENCES, Context.MODE_PRIVATE);
        assertEquals(uuid, prefs.getString(PREFKEY_INSTALLATION_UUID, ""));
    }

    @Test
    public void testGetDeviceUUID_shouldReturnSavedUUID() {
        final SharedPreferences prefs = context
                .getSharedPreferences(ADVERTISING_PREFERENCES, Context.MODE_PRIVATE);
        prefs.edit().putString(PREFKEY_INSTALLATION_UUID, TEST_UUID).apply();

        final String uuid = idManager.getDeviceUUID();
        assertNotNull(TEST_UUID, uuid);
    }
}
