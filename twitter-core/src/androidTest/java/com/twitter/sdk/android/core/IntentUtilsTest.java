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
