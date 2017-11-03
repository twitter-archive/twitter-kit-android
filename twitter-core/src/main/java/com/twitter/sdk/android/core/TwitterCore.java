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

import android.annotation.SuppressLint;
import android.content.Context;

import com.twitter.sdk.android.core.internal.SessionMonitor;
import com.twitter.sdk.android.core.internal.TwitterApi;
import com.twitter.sdk.android.core.internal.TwitterSessionVerifier;
import com.twitter.sdk.android.core.internal.oauth.OAuth2Service;
import com.twitter.sdk.android.core.internal.persistence.PreferenceStoreImpl;
import com.twitter.sdk.android.core.internal.scribe.TwitterCoreScribeClientHolder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * The TwitterCore Kit provides Login with Twitter and the Twitter API.
 */
public class TwitterCore {
    @SuppressLint("StaticFieldLeak")
    static volatile TwitterCore instance;
    public static final String TAG = "Twitter";

    static final String PREF_KEY_ACTIVE_TWITTER_SESSION = "active_twittersession";
    static final String PREF_KEY_TWITTER_SESSION = "twittersession";
    static final String PREF_KEY_ACTIVE_GUEST_SESSION = "active_guestsession";
    static final String PREF_KEY_GUEST_SESSION = "guestsession";
    static final String SESSION_PREF_FILE_NAME = "session_store";
    static final String KIT_SCRIBE_NAME = "TwitterCore";

    SessionManager<TwitterSession> twitterSessionManager;
    SessionManager<GuestSession> guestSessionManager;
    SessionMonitor<TwitterSession> sessionMonitor;

    private final TwitterAuthConfig authConfig;
    private final ConcurrentHashMap<Session, TwitterApiClient> apiClients;
    private final Context context;
    private volatile TwitterApiClient guestClient;
    private volatile GuestSessionProvider guestSessionProvider;

    TwitterCore(TwitterAuthConfig authConfig) {
        this(authConfig, new ConcurrentHashMap<Session, TwitterApiClient>(), null);
    }

    // Testing only
    TwitterCore(TwitterAuthConfig authConfig,
                ConcurrentHashMap<Session, TwitterApiClient> apiClients,
                TwitterApiClient guestClient) {
        this.authConfig = authConfig;
        this.apiClients = apiClients;
        this.guestClient = guestClient;
        context = Twitter.getInstance().getContext(getIdentifier());

        twitterSessionManager = new PersistedSessionManager<>(
                new PreferenceStoreImpl(context, SESSION_PREF_FILE_NAME),
                new TwitterSession.Serializer(), PREF_KEY_ACTIVE_TWITTER_SESSION,
                PREF_KEY_TWITTER_SESSION);

        guestSessionManager = new PersistedSessionManager<>(
                new PreferenceStoreImpl(context, SESSION_PREF_FILE_NAME),
                new GuestSession.Serializer(), PREF_KEY_ACTIVE_GUEST_SESSION,
                PREF_KEY_GUEST_SESSION);

        sessionMonitor = new SessionMonitor<>(twitterSessionManager,
                Twitter.getInstance().getExecutorService(), new TwitterSessionVerifier());
    }

    public static TwitterCore getInstance() {
        if (instance == null) {
            synchronized (TwitterCore.class) {
                if (instance == null) {
                    instance = new TwitterCore(Twitter.getInstance().getTwitterAuthConfig());
                    Twitter.getInstance().getExecutorService().execute(() -> instance.doInBackground());
                }
            }
        }
        return instance;
    }

    public String getVersion() {
        return BuildConfig.VERSION_NAME + "." + BuildConfig.BUILD_NUMBER;
    }

    public TwitterAuthConfig getAuthConfig() {
        return authConfig;
    }

    void doInBackground() {
        // Trigger restoration of session
        twitterSessionManager.getActiveSession();
        guestSessionManager.getActiveSession();
        getGuestSessionProvider();
        setUpScribeClient();
        // Monitor activity lifecycle after sessions have been restored. Otherwise we would not
        // have any sessions to monitor anyways.

        sessionMonitor.monitorActivityLifecycle(
                Twitter.getInstance().getActivityLifecycleManager());
    }

    public String getIdentifier() {
        return BuildConfig.GROUP + ":" + BuildConfig.ARTIFACT_ID;
    }

    private void setUpScribeClient() {
        TwitterCoreScribeClientHolder.initialize(context, getSessionManager(),
                getGuestSessionProvider(), Twitter.getInstance().getIdManager(), KIT_SCRIBE_NAME,
                getVersion());
    }

    /**********************************************************************************************
     * BEGIN PUBLIC API METHODS                                                                   *
     **********************************************************************************************/

    /**
     * @return the {@link com.twitter.sdk.android.core.SessionManager} for user sessions.
     */
    public SessionManager<TwitterSession> getSessionManager() {
        return twitterSessionManager;
    }

    public GuestSessionProvider getGuestSessionProvider() {
        if (guestSessionProvider == null) {
            createGuestSessionProvider();
        }
        return guestSessionProvider;
    }

    private synchronized void createGuestSessionProvider() {
        if (guestSessionProvider == null) {
            final OAuth2Service service =
                    new OAuth2Service(this, new TwitterApi());
            guestSessionProvider = new GuestSessionProvider(service, guestSessionManager);
        }
    }

    /**
     * Creates {@link com.twitter.sdk.android.core.TwitterApiClient} from default
     * {@link com.twitter.sdk.android.core.Session}.
     *
     * Caches internally for efficient access.
     */
    public TwitterApiClient getApiClient() {
        final TwitterSession session = twitterSessionManager.getActiveSession();
        if (session == null) {
            return getGuestApiClient();
        }

        return getApiClient(session);
    }

    /**
     * Creates {@link com.twitter.sdk.android.core.TwitterApiClient} from authenticated
     * {@link com.twitter.sdk.android.core.Session} provided.
     *
     * Caches internally for efficient access.
     * @param session the session
     */
    public TwitterApiClient getApiClient(TwitterSession session) {
        if (!apiClients.containsKey(session)) {
            apiClients.putIfAbsent(session, new TwitterApiClient(session));
        }
        return apiClients.get(session);
    }

    /**
     * Add custom {@link com.twitter.sdk.android.core.TwitterApiClient} for guest auth access.
     *
     * Only adds guest auth client if it's not already defined. Caches internally for efficient
     * access and storing it in TwitterCore's singleton.
     *
     * @param customTwitterApiClient the custom twitter api client
     */
    public void addGuestApiClient(TwitterApiClient customTwitterApiClient) {
        if (guestClient == null) {
            createGuestClient(customTwitterApiClient);
        }
    }

    /**
     * Add custom {@link com.twitter.sdk.android.core.TwitterApiClient} for authenticated
     * {@link com.twitter.sdk.android.core.Session} access.
     *
     * Only adds session auth client if it's not already defined. Caches internally for efficient
     * access and storing it in TwitterCore's singleton.
     *
     * @param session the session
     * @param customTwitterApiClient the custom twitter api client
     */
    public void addApiClient(TwitterSession session, TwitterApiClient customTwitterApiClient) {
        if (!apiClients.containsKey(session)) {
            apiClients.putIfAbsent(session, customTwitterApiClient);
        }
    }

    /**
     * Creates {@link com.twitter.sdk.android.core.TwitterApiClient} using guest authentication.
     *
     * Caches internally for efficient access.
     */
    public TwitterApiClient getGuestApiClient() {
        if (guestClient == null) {
            createGuestClient();
        }

        return guestClient;
    }

    private synchronized void createGuestClient() {
        if (guestClient == null) {
            guestClient = new TwitterApiClient();
        }
    }

    private synchronized void createGuestClient(TwitterApiClient twitterApiClient) {
        if (guestClient == null) {
            guestClient = twitterApiClient;
        }
    }
}
