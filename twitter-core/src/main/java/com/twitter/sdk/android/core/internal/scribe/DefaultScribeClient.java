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

import android.content.Context;
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
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.internal.ExecutorUtils;
import com.twitter.sdk.android.core.internal.IdManager;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Instances of this class should always be created on a background thread.
 */
public class DefaultScribeClient extends ScribeClient {
    /*
     * We are using the syndication backend for all scribing until there is a separate schema and
     * category for other events.
     */
    private static final String SCRIBE_URL = "https://syndication.twitter.com";
    private static final String SCRIBE_PATH_VERSION = "i";
    private static final String SCRIBE_PATH_TYPE = "sdk";

    private static final String DEBUG_BUILD = "debug";

    private static volatile ScheduledExecutorService executor;

    private final SessionManager<? extends Session<TwitterAuthToken>> sessionManager;
    private final String advertisingId;
    private final Context context;

    public DefaultScribeClient(Context context,
                               SessionManager<? extends Session<TwitterAuthToken>> sessionManager,
                               GuestSessionProvider guestSessionProvider, IdManager idManager,
                               ScribeConfig scribeConfig) {
        this(context, TwitterCore.getInstance().getAuthConfig(), sessionManager,
                guestSessionProvider, idManager, scribeConfig);
    }

    DefaultScribeClient(Context context, TwitterAuthConfig authConfig,
                        SessionManager<? extends Session<TwitterAuthToken>> sessionManager,
                        GuestSessionProvider guestSessionProvider, IdManager idManager,
                        ScribeConfig scribeConfig) {
        super(context, getExecutor(), scribeConfig, new ScribeEvent.Transform(getGson()),
                authConfig, sessionManager, guestSessionProvider, idManager);

        this.context = context;
        this.sessionManager = sessionManager;
        this.advertisingId = idManager.getAdvertisingId();
    }

    public void scribe(EventNamespace... namespaces) {
        for (EventNamespace ns : namespaces) {
            scribe(ns, Collections.emptyList());
        }
    }

    public void scribe(EventNamespace namespace, List<ScribeItem> items) {
        final String language = getLanguage();
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
        final String language = getLanguage();
        final long timestamp = System.currentTimeMillis();
        /*
         * The advertising ID may be null if this method is called before doInBackground completes.
         * It also may be null depending on the users preferences and if Google Play Services has
         * been installed on the device.
         */
        scribe(ScribeEventFactory.newScribeEvent(namespace, eventInfo, timestamp, language,
                advertisingId, Collections.emptyList()));
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

    private String getLanguage(){
        return context.getResources().getConfiguration().locale.getLanguage();
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

    public static ScribeConfig getScribeConfig(String kitName, String kitVersion) {
        final String scribeUrl = getScribeUrl(SCRIBE_URL, BuildConfig.SCRIBE_ENDPOINT_OVERRIDE);
        return new ScribeConfig(isEnabled(), scribeUrl, SCRIBE_PATH_VERSION,
                SCRIBE_PATH_TYPE, BuildConfig.SCRIBE_SEQUENCE, getUserAgent(kitName, kitVersion),
                ScribeConfig.DEFAULT_MAX_FILES_TO_KEEP, ScribeConfig.DEFAULT_SEND_INTERVAL_SECONDS);
    }

    /*
     * This method serves to disable the scribe strategy in testing, it causes massive memory leaks
     * that are not easily cleaned up unless we have a teardown method added to the kit class
     * interface.
     */
    private static boolean isEnabled() {
        return !BuildConfig.BUILD_TYPE.equals(DEBUG_BUILD);
    }

    static String getUserAgent(String kitName, String kitVersion) {
        return new StringBuilder()
                .append("TwitterKit/")
                .append("3.0")
                .append(" (Android ")
                .append(Build.VERSION.SDK_INT)
                .append(") ")
                .append(kitName)
                .append("/")
                .append(kitVersion)
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
