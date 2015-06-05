package com.twitter.sdk.android.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.fabric.sdk.android.FabricAndroidTestCase;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class IntentUtilsTest extends FabricAndroidTestCase {
    Intent intent;
    Context context;
    PackageManager pm;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        context = mock(Context.class);
        pm = mock(PackageManager.class);
        intent = mock(Intent.class);
    }

    public void testIsActivityAvailable_noActivitiesAvailable() {
        when(pm.queryIntentActivities(any(Intent.class), anyInt()))
                .thenReturn(Collections.EMPTY_LIST);
        when(context.getPackageManager()).thenReturn(pm);

        assertFalse(IntentUtils.isActivityAvailable(context, intent));
        verify(context).getPackageManager();
        verify(pm).queryIntentActivities(intent, 0);
    }

    public void testIsActivityAvailable_activityAvailable() {
        final List<ResolveInfo> activities = new ArrayList<>();
        activities.add(mock(ResolveInfo.class));

        when(pm.queryIntentActivities(any(Intent.class), anyInt())).thenReturn(activities);
        when(context.getPackageManager()).thenReturn(pm);

        assertTrue(IntentUtils.isActivityAvailable(context, intent));
        verify(context).getPackageManager();
        verify(pm).queryIntentActivities(intent, 0);
    }

    public void testSafeStartActivity_noActivitiesAvailable() {
        when(pm.queryIntentActivities(any(Intent.class), anyInt()))
                .thenReturn(Collections.EMPTY_LIST);
        when(context.getPackageManager()).thenReturn(pm);

        IntentUtils.safeStartActivity(context, intent);

        verify(context).getPackageManager();
        verifyNoMoreInteractions(context);
    }

    public void testSafeStartActivity_activityAvailable() {
        final List<ResolveInfo> activities = new ArrayList<>();
        activities.add(mock(ResolveInfo.class));

        when(pm.queryIntentActivities(any(Intent.class), anyInt())).thenReturn(activities);
        when(context.getPackageManager()).thenReturn(pm);

        IntentUtils.safeStartActivity(context, intent);

        verify(context).startActivity(intent);
    }
}
