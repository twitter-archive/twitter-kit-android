package com.twitter.sdk.android.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

public class IntentUtils {
    /*
     * Determine if activity is available to handle provided intent.
     * @param context The context through which {@link android.content.pm.PackageManager} can be accessed.
     * @param intent The intent of the activity to start.
     * @returns true if activity is found, otherwise false.
     */
    public static boolean isActivityAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        return !activities.isEmpty();
    }

    /*
     * Determine if activity is available to handle provided intent before calling startActivity.
     * @param context The context through which activity can be started.
     * @param intent The intent of the activity to start.
     * @returns true if activity is found and startActivity called, otherwise false.
     */
    public static boolean safeStartActivity(Context context, Intent intent) {
        if (isActivityAvailable(context, intent)) {
            context.startActivity(intent);
            return true;
        }

        return false;
    }
}
