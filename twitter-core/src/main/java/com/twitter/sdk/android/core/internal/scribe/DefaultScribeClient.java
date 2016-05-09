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

package com.twitter.sdk.android.core.internal.scribe;

import android.os.Build;
import android.text.TextUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twitter.sdk.android.core.BuildConfig;
import com.twitter.sdk.android.core.GuestSession;
import com.twitter.sdk.android.core.GuestSessionProvider;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.ExecutorUtils;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.settings.Settings;
import io.fabric.sdk.android.services.settings.SettingsData;

/**
 * Instances of this class should always be created on a background thread.
 */
public class DefaultScribeClient extends ScribeClient {
    /*
     * We are using the syndication backend for all scribing until there is a separate schema and
     * category for other Fabric events.
     */
    private static final String SCRIBE_URL = "https://syndication.twitter.com";
    private static final String SCRIBE_PATH_VERSION = "i";
    private static final String SCRIBE_PATH_TYPE = "sdk";

    private static final String DEBUG_BUILD = "debug";

    private static volatile ScheduledExecutorService executor;

    private final Kit kit;
    private final SessionManager<? extends Session<TwitterAuthToken>> sessionManager;
    private final String advertisingId;

    public DefaultScribeClient(Kit kit, String kitName,
            SessionManager<? extends Session<TwitterAuthToken>> sessionManager,
            GuestSessionProvider guestSessionProvider, IdManager idManager) {
        this(kit, kitName, getGson(), sessionManager, guestSessionProvider, idManager);
    }

    DefaultScribeClient(Kit kit, String kitName, Gson gson,
            SessionManager<? extends Session<TwitterAuthToken>> sessionManager,
            GuestSessionProvider guestSessionProvider, IdManager idManager) {
        super(kit, getExecutor(), getScribeConfig(Settings.getInstance().awaitSettingsData(),
                getUserAgent(kitName, kit)), new ScribeEvent.Transform(gson),
                TwitterCore.getInstance().getAuthConfig(), sessionManager, guestSessionProvider,
                TwitterCore.getInstance().getSSLSocketFactory(), idManager);

        this.sessionManager = sessionManager;
        this.kit = kit;
        this.advertisingId = idManager.getAdvertisingId();
    }

    public void scribe(EventNamespace... namespaces) {
        for (EventNamespace ns : namespaces) {
            scribe(ns, Collections.<ScribeItem>emptyList());
        }
    }

    public void scribe(EventNamespace namespace, List<ScribeItem> items) {
        final String language = getLanguageFromKit();
        final long timestamp = System.currentTimeMillis();
        /*
         * The advertising ID may be null if this method is called before doInBackground completes.
         * It also may be null depending on the users preferences and if Google Play Services has
         * been installed on the device.
         */
        scribe(ScribeEventFactory.newScribeEvent(namespace, "", timestamp, language, advertisingId,
                items));
    }

    public void scribe(ScribeEvent event) {
        super.scribe(event, getScribeSessionId(getActiveSession()));
    }

    public void scribe(EventNamespace namespace, String eventInfo) {
        final String language = getLanguageFromKit();
        final long timestamp = System.currentTimeMillis();
        /*
         * The advertising ID may be null if this method is called before doInBackground completes.
         * It also may be null depending on the users preferences and if Google Play Services has
         * been installed on the device.
         */
        scribe(ScribeEventFactory.newScribeEvent(namespace, eventInfo, timestamp, language,
                advertisingId, Collections.<ScribeItem>emptyList()));
    }

    // visible for tests
    Session getActiveSession() {
        return sessionManager.getActiveSession();
    }

    // visible for tests
    long getScribeSessionId(Session activeSession) {
        final long scribeSessionId;
        if (activeSession != null) {
            scribeSessionId = activeSession.getId();
        } else {
            // It's possible that we're attempting to load a tweet before we have a valid
            // session. Store the scribe event locally with the logged out user id so that we can
            // send it up at a later time with the logged out session.
            scribeSessionId = GuestSession.LOGGED_OUT_USER_ID;
        }
        return scribeSessionId;
    }

    private String getLanguageFromKit(){
        final String language;
        if (kit.getContext() != null) {
            language = kit.getContext().getResources().getConfiguration().locale.getLanguage();
        } else {
            language = "";
        }
        return language;
    }

    private static Gson getGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    private static ScheduledExecutorService getExecutor() {
        if (executor == null) {
            synchronized (DefaultScribeClient.class) {
                if (executor == null) {
                    executor = ExecutorUtils.buildSingleThreadScheduledExecutorService("scribe");
                }
            }
        }
        return executor;
    }

    static ScribeConfig getScribeConfig(SettingsData settingsData, String userAgent) {
        // Get scribe configuration using analytics settings, which is used by crashlytics for
        // configuring Answers. This is temporary until we have can get our scribe settings from the
        // backend. If analytics settings are not available, fallback to defaults.
        final int maxFilesToKeep;
        final int sendIntervalSeconds;
        if (settingsData != null && settingsData.analyticsSettingsData != null) {
            maxFilesToKeep = settingsData.analyticsSettingsData.maxPendingSendFileCount;
            sendIntervalSeconds = settingsData.analyticsSettingsData.flushIntervalSeconds;
        } else {
            maxFilesToKeep = ScribeConfig.DEFAULT_MAX_FILES_TO_KEEP;
            sendIntervalSeconds = ScribeConfig.DEFAULT_SEND_INTERVAL_SECONDS;
        }

        final String scribeUrl = getScribeUrl(SCRIBE_URL, BuildConfig.SCRIBE_ENDPOINT_OVERRIDE);
        return new ScribeConfig(isEnabled(), scribeUrl, SCRIBE_PATH_VERSION,
                SCRIBE_PATH_TYPE, BuildConfig.SCRIBE_SEQUENCE, userAgent, maxFilesToKeep,
                sendIntervalSeconds);
    }

    /*
     * This method serves to disable the scribe strategy in testing, it causes massive memory leaks
     * that are not easily cleaned up unless we have a teardown method added to the kit class
     * interface.
     */
    private static boolean isEnabled() {
        return !BuildConfig.BUILD_TYPE.equals(DEBUG_BUILD);
    }

    static String getUserAgent(String kitName, Kit kit) {
        return new StringBuilder()
                .append("Fabric/")
                .append(kit.getFabric().getVersion())
                .append(" (Android ")
                .append(Build.VERSION.SDK_INT)
                .append(") ")
                .append(kitName)
                .append("/")
                .append(kit.getVersion())
                .toString();
    }

    // visible for tests
    static String getScribeUrl(String defaultUrl, String overrideUrl) {
        if (!TextUtils.isEmpty(overrideUrl)) {
            return overrideUrl;
        } else {
            return defaultUrl;
        }
    }
}
