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
import android.os.Build;
import android.provider.Settings.Secure;

import com.twitter.sdk.android.core.Twitter;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public class IdManager {
    static final String COLLECT_DEVICE_IDENTIFIERS = "com.twitter.sdk.android.CollectDeviceIdentifiers";
    static final String PREFKEY_INSTALLATION_UUID = "crashlytics.installation.id";

    /** Regex for stripping all non-alphnumeric characters from ALL the identifier fields. */
    private static final Pattern ID_PATTERN = Pattern.compile("[^\\p{Alnum}]");

    // http://groups.google.com/group/android-developers/browse_thread/thread/53898e508fab44f6/84e54feb28272384?lnk=raot
    private static final String BAD_ANDROID_ID = "9774d56d682e549c";

    private static final String FORWARD_SLASH_REGEX = Pattern.quote("/");

    private final ReentrantLock installationIdLock = new ReentrantLock();
    private final boolean collectHardwareIds;
    private final Context appContext;
    private final String appIdentifier;

    AdvertisingInfoProvider advertisingInfoProvider;
    AdvertisingInfo advertisingInfo;
    boolean fetchedAdvertisingInfo;

    /**
     * @param appContext Application {@link Context}
     * @throws IllegalArgumentException if {@link Context} is null, or <code>appPackageName</code>
     * is null
     */
    public IdManager(Context appContext) {
        this(appContext, new AdvertisingInfoProvider(appContext));
    }

    IdManager(Context appContext, AdvertisingInfoProvider advertisingInfoProvider) {
        if (appContext == null) {
            throw new IllegalArgumentException("appContext must not be null");
        }

        this.appContext = appContext;
        this.appIdentifier = appContext.getPackageName();
        this.advertisingInfoProvider = advertisingInfoProvider;

        collectHardwareIds = CommonUtils.getBooleanResourceValue(appContext,
                COLLECT_DEVICE_IDENTIFIERS, true);
        if (!collectHardwareIds) {
            Twitter.getLogger().d(Twitter.TAG, "Device ID collection disabled for "
                    + appContext.getPackageName());
        }
    }

    /**
     * Apply consistent formatting and stripping of special characters. Null input is allowed, will return
     * null.
     */
    private String formatId(String id) {
        return  (id == null) ? null : ID_PATTERN.matcher(id).replaceAll("").toLowerCase(Locale.US);
    }

    /**
     * @return the package name that identifies this App.
     */
    public String getAppIdentifier() {
        return appIdentifier;
    }

    /**
     * @return {@link String} identifying the version of Android OS that the device is running. Includes the
     *         public version number, and an incremental build number, like "4.2.2/573038"
     */
    public String getOsVersionString() {
        return getOsDisplayVersionString() + "/" + getOsBuildVersionString();
    }

    /**
     * @return {@link String} identifying the display version of the Android OS that the device is
     * running, e.g. "4.2.2". Any forward slashes in the system returned value will be removed.
     */
    public String getOsDisplayVersionString() {
        return removeForwardSlashesIn(Build.VERSION.RELEASE);
    }

    /**
     * @return {@link String} identifying the build version of the Android OS that the device is
     * running, e.g. "573038". Any forward slashes in the system returned value will be removed.
     */
    public String getOsBuildVersionString() {
        return removeForwardSlashesIn(Build.VERSION.INCREMENTAL);
    }

    /**
     * @return {@link String} identifying the model of this device. Includes the manufacturer and model names.
     */
    public String getModelName() {
        return String.format(Locale.US, "%s/%s", removeForwardSlashesIn(Build.MANUFACTURER),
                removeForwardSlashesIn(Build.MODEL));
    }

    private String removeForwardSlashesIn(String s) {
        return s.replaceAll(FORWARD_SLASH_REGEX, "");
    }

    /**
     * If hardware ID collection is off, returns an empty String. Otherwise returns the AndroidId for this
     * device, or the Crashlytics installation UUID if the Android id is not valid.
     *
     * Always returns either an empty string or a hex string of at least 16 characters.
     **/
    public String getDeviceUUID() {
        String toReturn = "";

        if (collectHardwareIds) {
            toReturn = getAndroidId();

            if (toReturn == null) {
                final SharedPreferences prefs = CommonUtils.getSharedPrefs(appContext);
                toReturn = prefs.getString(PREFKEY_INSTALLATION_UUID, null);

                if (toReturn == null) {
                    toReturn = createInstallationUUID(prefs);
                }
            }
        }

        return toReturn;
    }

    /**
     * Creates the Application Installation ID and stores it in shared prefs. This method is thread safe: if
     * the ID already exists when the lock is acquired, that ID will be returned instead of a newly-created
     * one.
     **/
    private String createInstallationUUID(SharedPreferences prefs) {
        installationIdLock.lock();
        try {
            String uuid = prefs.getString(PREFKEY_INSTALLATION_UUID, null);

            if (uuid == null) {
                uuid = formatId(UUID.randomUUID().toString());
                prefs.edit().putString(PREFKEY_INSTALLATION_UUID, uuid).apply();
            }

            return uuid;
        } finally {
            installationIdLock.unlock();
        }
    }

    synchronized AdvertisingInfo getAdvertisingInfo() {
        if (!fetchedAdvertisingInfo) {
            advertisingInfo = advertisingInfoProvider.getAdvertisingInfo();
            fetchedAdvertisingInfo = true;
        }
        return advertisingInfo;
    }

    public Boolean isLimitAdTrackingEnabled() {
        Boolean toReturn = null;

        if (collectHardwareIds) {
            final AdvertisingInfo advertisingInfo = getAdvertisingInfo();
            if (advertisingInfo != null) {
                toReturn = advertisingInfo.limitAdTrackingEnabled;
            }
        }

        return toReturn;
    }

    public String getAdvertisingId() {
        String toReturn = null;

        if (collectHardwareIds) {
            final AdvertisingInfo advertisingInfo = getAdvertisingInfo();
            if (advertisingInfo != null) {
                toReturn = advertisingInfo.advertisingId;
            }
        }

        return toReturn;
    }

    String getAndroidId() {
        String toReturn = null;

        if (collectHardwareIds) {
            final String androidId = Secure.getString(appContext.getContentResolver(),
                    Secure.ANDROID_ID);

            if (!BAD_ANDROID_ID.equals(androidId)) {
                toReturn = formatId(androidId);
            }
        }

        return toReturn;
    }
}
