package com.twitter.sdk.android.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class IntentUtilsTest {
    Intent intent;
    Context context;
    PackageManager pm;

    @Before
    public void setUp() throws Exception {

        context = mock(Context.class);
        pm = mock(PackageManager.class);
        intent = mock(Intent.class);
    }

    @Test
    public void testIsActivityAvailable_noActivitiesAvailable() {
        when(pm.queryIntentActivities(any(Intent.class), anyInt()))
                .thenReturn(Collections.EMPTY_LIST);
        when(context.getPackageManager()).thenReturn(pm);

        assertFalse(IntentUtils.isActivityAvailable(context, intent));
        verify(context).getPackageManager();
        verify(pm).queryIntentActivities(intent, 0);
    }

    @Test
    public void testIsActivityAvailable_activityAvailable() {
        final List<ResolveInfo> activities = new ArrayList<>();
        activities.add(mock(ResolveInfo.class));

        when(pm.queryIntentActivities(any(Intent.class), anyInt())).thenReturn(activities);
        when(context.getPackageManager()).thenReturn(pm);

        assertTrue(IntentUtils.isActivityAvailable(context, intent));
        verify(context).getPackageManager();
        verify(pm).queryIntentActivities(intent, 0);
    }

    @Test
    public void testSafeStartActivity_noActivitiesAvailable() {
        when(pm.queryIntentActivities(any(Intent.class), anyInt()))
                .thenReturn(Collections.EMPTY_LIST);
        when(context.getPackageManager()).thenReturn(pm);

        IntentUtils.safeStartActivity(context, intent);

        verify(context).getPackageManager();
        verifyNoMoreInteractions(context);
    }

    @Test
    public void testSafeStartActivity_activityAvailable() {
        final List<ResolveInfo> activities = new ArrayList<>();
        activities.add(mock(ResolveInfo.class));

        when(pm.queryIntentActivities(any(Intent.class), anyInt())).thenReturn(activities);
        when(context.getPackageManager()).thenReturn(pm);

        IntentUtils.safeStartActivity(context, intent);

        verify(context).startActivity(intent);
    }
}
