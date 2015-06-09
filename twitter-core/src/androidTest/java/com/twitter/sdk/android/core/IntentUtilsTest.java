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

import android.content.Intent;

import io.fabric.sdk.android.FabricAndroidTestCase;

public class IntentUtilsTest extends FabricAndroidTestCase {
    public void testIsActivityAvailable_noActivitiesAvailable() {
        final Intent intent = new Intent("io.fabric.is.awesome");
        assertFalse(IntentUtils.isActivityAvailable(getContext(), intent));
    }

    public void testIsActivityAvailable_activitiesAvailable() {
        final Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        assertTrue(IntentUtils.isActivityAvailable(getContext(), intent));
    }

    public void testSafeStartActivity() {
        final Intent intent = new Intent("io.fabric.is.awesome");
        assertFalse(IntentUtils.safeStartActivity(getContext(), intent));
    }
}
