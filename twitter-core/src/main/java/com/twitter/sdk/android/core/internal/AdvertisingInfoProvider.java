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

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.internal.persistence.PreferenceStore;

class AdvertisingInfoProvider {
    private static final String PREFKEY_LIMIT_AD_TRACKING = "limit_ad_tracking_enabled";
    private static final String PREFKEY_ADVERTISING_ID = "advertising_id";
    private final Context context;
    private final PreferenceStore preferenceStore;

    AdvertisingInfoProvider(Context context, PreferenceStore preferenceStore) {
        this.context = context.getApplicationContext();
        this.preferenceStore = preferenceStore;
    }

    /**
     * Returns an AdvertisingInfo using various Providers with different attempts to gain this data
     *
     * This method should not be called on the UI thread as it always does some kind of IO
     * (reading from Shared Preferences) and other slow tasks like binding to a service or using
     * reflection.
     */
    AdvertisingInfo getAdvertisingInfo() {
        AdvertisingInfo infoToReturn;

        infoToReturn = getInfoFromPreferences();
        if (isInfoValid(infoToReturn)) {
            Twitter.getLogger().d(Twitter.TAG, "Using AdvertisingInfo from Preference Store");
            refreshInfoIfNeededAsync(infoToReturn);
            return infoToReturn;
        }

        infoToReturn =  getAdvertisingInfoFromStrategies();
        storeInfoToPreferences(infoToReturn);
        return infoToReturn;
    }

    /**
     * Asynchronously updates the advertising info stored in shared preferences (if it is different
     * than the current info) so subsequent calls to {@link #getInfoFromPreferences()} are up to
     * date.
     */
    private void refreshInfoIfNeededAsync(final AdvertisingInfo advertisingInfo) {
        new Thread(() -> {
            final AdvertisingInfo infoToStore = getAdvertisingInfoFromStrategies();
            if (!advertisingInfo.equals(infoToStore)) {
                Twitter.getLogger()
                        .d(Twitter.TAG, "Asychronously getting Advertising Info and " +
                        "storing it to preferences");
                storeInfoToPreferences(infoToStore);
            }
        }).start();
    }

    @SuppressLint("CommitPrefEdits")
    private void storeInfoToPreferences(AdvertisingInfo infoToReturn) {
        if (isInfoValid(infoToReturn)) {
            preferenceStore.save(preferenceStore.edit()
                    .putString(PREFKEY_ADVERTISING_ID, infoToReturn.advertisingId)
                    .putBoolean(PREFKEY_LIMIT_AD_TRACKING, infoToReturn.limitAdTrackingEnabled));
        } else {
            // if we get an invalid advertising info, clear out the previous value since it isn't
            // valid now
            preferenceStore.save(preferenceStore.edit()
                    .remove(PREFKEY_ADVERTISING_ID)
                    .remove(PREFKEY_LIMIT_AD_TRACKING));

        }
    }

    private AdvertisingInfo getInfoFromPreferences() {
        final String advertisingId = preferenceStore.get().getString(PREFKEY_ADVERTISING_ID, "");
        final boolean limitAd = preferenceStore.get().getBoolean(PREFKEY_LIMIT_AD_TRACKING, false);
        return new AdvertisingInfo(advertisingId, limitAd);
    }

    private AdvertisingInfoStrategy getReflectionStrategy() {
        return new AdvertisingInfoReflectionStrategy(context);
    }

    private boolean isInfoValid(AdvertisingInfo advertisingInfo) {
        return advertisingInfo != null && !TextUtils.isEmpty(advertisingInfo.advertisingId);
    }

    private AdvertisingInfo getAdvertisingInfoFromStrategies() {
        final AdvertisingInfo infoToReturn;

        final AdvertisingInfoStrategy adInfoStrategy = getReflectionStrategy();
        infoToReturn = adInfoStrategy.getAdvertisingInfo();

        if (!isInfoValid(infoToReturn)) {
            Twitter.getLogger().d(Twitter.TAG, "AdvertisingInfo not present");
        } else {
            Twitter.getLogger().d(Twitter.TAG, "Using AdvertisingInfo from Reflection Provider");
        }

        return infoToReturn;
    }
}
