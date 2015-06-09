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
