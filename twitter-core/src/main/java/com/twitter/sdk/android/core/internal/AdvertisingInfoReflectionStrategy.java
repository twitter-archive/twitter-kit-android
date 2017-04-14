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

import com.twitter.sdk.android.core.Twitter;

import java.lang.reflect.Method;

class AdvertisingInfoReflectionStrategy implements AdvertisingInfoStrategy {
    private static final int GOOGLE_PLAY_SERVICES_SUCCESS_CODE = 0;
    private static final String CLASS_NAME_GOOGLE_PLAY_SERVICES_UTILS
            = "com.google.android.gms.common.GooglePlayServicesUtil";
    private static final String METHOD_NAME_IS_GOOGLE_PLAY_SERVICES_AVAILABLE
            = "isGooglePlayServicesAvailable";

    private static final String CLASS_NAME_ADVERTISING_ID_CLIENT
            = "com.google.android.gms.ads.identifier.AdvertisingIdClient";
    private static final String CLASS_NAME_ADVERTISING_ID_CLIENT_INFO
            = CLASS_NAME_ADVERTISING_ID_CLIENT + "$Info";
    private static final String METHOD_NAME_GET_ADVERTISING_ID_INFO = "getAdvertisingIdInfo";
    private static final String METHOD_NAME_GET_ID = "getId";
    private static final String METHOD_NAME_IS_LIMITED_AD_TRACKING_ENABLED
            = "isLimitAdTrackingEnabled";

    private final Context context;

    AdvertisingInfoReflectionStrategy(Context context) {
        this.context = context.getApplicationContext();
    }

    boolean isGooglePlayServiceAvailable(final Context context) {
        try {
            final Method method = Class.forName(CLASS_NAME_GOOGLE_PLAY_SERVICES_UTILS)
                    .getMethod(METHOD_NAME_IS_GOOGLE_PLAY_SERVICES_AVAILABLE, Context.class);
            final Integer result = (Integer) method.invoke(null, context);
            return result.intValue() == GOOGLE_PLAY_SERVICES_SUCCESS_CODE;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * Returns AdvertisingInfo from the given context, using reflection to access the Google Play
     * Services APIs. This Provider requires that an app is compiled against Google Play Services.
     * May be <code>null</code>
     */
    public AdvertisingInfo getAdvertisingInfo() {
        if (isGooglePlayServiceAvailable(context)) {
            return new AdvertisingInfo(getAdvertisingId(), isLimitAdTrackingEnabled());
        }
        return null;
    }

    private String getAdvertisingId() {
        try {
            final Method method = Class.forName(CLASS_NAME_ADVERTISING_ID_CLIENT_INFO)
                    .getMethod(METHOD_NAME_GET_ID);

            return (String) method.invoke(getInfo());

        } catch (Exception e) {
            Twitter.getLogger().w(Twitter.TAG, "Could not call " + METHOD_NAME_GET_ID
                    + " on " + CLASS_NAME_ADVERTISING_ID_CLIENT_INFO);
        }

        return null;
    }

    private boolean isLimitAdTrackingEnabled() {
        try {
            final Method method = Class.forName(CLASS_NAME_ADVERTISING_ID_CLIENT_INFO)
                    .getMethod(METHOD_NAME_IS_LIMITED_AD_TRACKING_ENABLED);

            return (Boolean) method.invoke(getInfo());

        } catch (Exception e) {
            Twitter.getLogger().w(Twitter.TAG, "Could not call "
                    + METHOD_NAME_IS_LIMITED_AD_TRACKING_ENABLED + " on "
                    + CLASS_NAME_ADVERTISING_ID_CLIENT_INFO);
        }

        return false;
    }

    private Object getInfo() {
        try {
            final Method method = Class.forName(CLASS_NAME_ADVERTISING_ID_CLIENT)
                    .getMethod(METHOD_NAME_GET_ADVERTISING_ID_INFO, Context.class);
            return method.invoke(null, context);

        } catch (Exception e) {
            Twitter.getLogger()
                    .w(Twitter.TAG, "Could not call " + METHOD_NAME_GET_ADVERTISING_ID_INFO
                    + " on " + CLASS_NAME_ADVERTISING_ID_CLIENT);
        }

        return null;
    }
}
