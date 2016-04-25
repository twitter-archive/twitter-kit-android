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

import android.app.Activity;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.network.NetworkUtils;
import io.fabric.sdk.android.services.persistence.PreferenceStoreImpl;

import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.internal.MigrationHelper;
import com.twitter.sdk.android.core.internal.SessionMonitor;
import com.twitter.sdk.android.core.internal.TwitterApi;
import com.twitter.sdk.android.core.internal.TwitterSessionVerifier;
import com.twitter.sdk.android.core.internal.oauth.OAuth2Service;
import com.twitter.sdk.android.core.internal.scribe.TwitterCoreScribeClientHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLSocketFactory;

/**
 * The TwitterCore Kit provides Login with Twitter and the Twitter API.
 */
public class TwitterCore extends Kit<Boolean> {

    public static final String TAG = "Twitter";

    static final String PREF_KEY_ACTIVE_TWITTER_SESSION = "active_twittersession";
    static final String PREF_KEY_TWITTER_SESSION = "twittersession";
    static final String PREF_KEY_ACTIVE_APP_SESSION = "active_appsession";
    static final String PREF_KEY_APP_SESSION = "appsession";
    static final String SESSION_PREF_FILE_NAME = "session_store";

    SessionManager<TwitterSession> twitterSessionManager;
    SessionManager<AppSession> appSessionManager;
    SessionMonitor<TwitterSession> sessionMonitor;

    private final TwitterAuthConfig authConfig;
    private final ConcurrentHashMap<Session, TwitterApiClient> apiClients;
    private volatile SSLSocketFactory sslSocketFactory;

    public TwitterCore(TwitterAuthConfig authConfig) {
        this.authConfig = authConfig;
        apiClients = new ConcurrentHashMap<>();
    }

    TwitterCore(TwitterAuthConfig authConfig,
                ConcurrentHashMap<Session, TwitterApiClient> apiClients) {
        this.authConfig = authConfig;
        this.apiClients = apiClients;
    }

    public static TwitterCore getInstance() {
        checkInitialized();
        return Fabric.getKit(TwitterCore.class);
    }

    @Override
    public String getVersion() {
        return BuildConfig.VERSION_NAME + "." + BuildConfig.BUILD_NUMBER;
    }

    public TwitterAuthConfig getAuthConfig() {
        return authConfig;
    }

    /**
     *
     * @return the SSLSocketFactory
     *
     * @throws java.lang.IllegalStateException if {@link io.fabric.sdk.android.Fabric}
     *          or {@link TwitterCore} has not been initialized.
     */
    public SSLSocketFactory getSSLSocketFactory() {
        checkInitialized();

        if (sslSocketFactory == null) {
            createSSLSocketFactory();
        }
        return sslSocketFactory;
    }

    private synchronized void createSSLSocketFactory() {
        if (sslSocketFactory == null) {
            try {
                sslSocketFactory = NetworkUtils.getSSLSocketFactory(
                        new TwitterPinningInfoProvider(getContext()));
                Fabric.getLogger().d(TAG, "Custom SSL pinning enabled");
            } catch (Exception e) {
                Fabric.getLogger().e(TAG, "Exception setting up custom SSL pinning", e);
            }
        }
    }

    @Override
    protected boolean onPreExecute() {
        final MigrationHelper migrationHelper = new MigrationHelper();
        migrationHelper.migrateSessionStore(getContext(), getIdentifier(),
                getIdentifier() + ":" + SESSION_PREF_FILE_NAME + ".xml");

        twitterSessionManager = new PersistedSessionManager<>(
                new PreferenceStoreImpl(getContext(), SESSION_PREF_FILE_NAME),
                new TwitterSession.Serializer(), PREF_KEY_ACTIVE_TWITTER_SESSION,
                PREF_KEY_TWITTER_SESSION);

        sessionMonitor = new SessionMonitor<>(twitterSessionManager,
                getFabric().getExecutorService(), new TwitterSessionVerifier());

        appSessionManager = new PersistedSessionManager<>(
                new PreferenceStoreImpl(getContext(), SESSION_PREF_FILE_NAME),
                new AppSession.Serializer(), PREF_KEY_ACTIVE_APP_SESSION, PREF_KEY_APP_SESSION);

        return true;
    }

    @Override
    protected Boolean doInBackground() {
        // Trigger restoration of session
        twitterSessionManager.getActiveSession();
        appSessionManager.getActiveSession();
        getSSLSocketFactory();
        initializeScribeClient();
        // Monitor activity lifecycle after sessions have been restored. Otherwise we would not
        // have any sessions to monitor anyways.
        sessionMonitor.monitorActivityLifecycle(getFabric().getActivityLifecycleManager());
        return true;
    }

    @Override
    public String getIdentifier() {
        return BuildConfig.GROUP + ":" + BuildConfig.ARTIFACT_ID;
    }

    private static void checkInitialized() {
        if (Fabric.getKit(TwitterCore.class) == null) {
            throw new IllegalStateException("Must start Twitter Kit with Fabric.with() first");
        }
    }

    private void initializeScribeClient() {
        final List<SessionManager<? extends Session>> sessionManagers = new ArrayList<>();
        sessionManagers.add(twitterSessionManager);
        sessionManagers.add(appSessionManager);
        TwitterCoreScribeClientHolder.initialize(this, sessionManagers, getIdManager());
    }

    /**********************************************************************************************
     * BEGIN PUBLIC API METHODS                                                                   *
     **********************************************************************************************/

    /**
     * Performs log in on behalf of a user.
     *
     * @param activity The {@link android.app.Activity} context to use for the login flow.
     * @param callback The callback interface to invoke when login completes.
     *
     * @throws java.lang.IllegalStateException if {@link io.fabric.sdk.android.Fabric}
     *          or {@link TwitterCore} has not been initialized.
     */
    public void logIn(Activity activity, Callback<TwitterSession> callback) {
        checkInitialized();
        new TwitterAuthClient().authorize(activity, callback);
    }

    /**
     * Performs guest login.
     *
     * @param callback The callback interface to invoke when guest login completes.
     *
     * @throws java.lang.IllegalStateException if {@link io.fabric.sdk.android.Fabric}
     *          or {@link TwitterCore} has not been initialized.
     */
    public void logInGuest(final Callback<AppSession> callback) {
        checkInitialized();
        final OAuth2Service service =
                new OAuth2Service(this, getSSLSocketFactory(), new TwitterApi());
        new GuestAuthClient(service).authorize(appSessionManager, callback);
    }

    /**
     * Logs out the user, clearing user session. This will not make a network request to invalidate
     * the session.
     *
     * @throws java.lang.IllegalStateException if {@link io.fabric.sdk.android.Fabric}
     *          or {@link TwitterCore} has not been initialized.
     */
    public void logOut() {
        checkInitialized();
        final SessionManager<TwitterSession> sessionManager = getSessionManager();
        if (sessionManager != null) {
            sessionManager.clearActiveSession();
        }
    }

    /**
     * @return the {@link com.twitter.sdk.android.core.SessionManager} for user sessions.
     *
     * @throws java.lang.IllegalStateException if {@link io.fabric.sdk.android.Fabric}
     *          or {@link TwitterCore} has not been initialized.
     */
    public SessionManager<TwitterSession> getSessionManager() {
        checkInitialized();
        return twitterSessionManager;
    }

    /**
     * @return the {@link com.twitter.sdk.android.core.SessionManager} for app sessions.
     *
     * @throws java.lang.IllegalStateException if {@link io.fabric.sdk.android.Fabric}
     *          or {@link TwitterCore} has not been initialized.
     */
    public SessionManager<AppSession> getAppSessionManager() {
        checkInitialized();
        return appSessionManager;
    }

    private Session getActiveSession() {
        // Prefer user session over app session.
        Session session = twitterSessionManager.getActiveSession();
        if (session == null) {
            session = appSessionManager.getActiveSession();
        }
        return session;
    }

    /**
     * Creates {@link com.twitter.sdk.android.core.TwitterApiClient} from default
     * {@link com.twitter.sdk.android.core.Session} retrieved from {@link com.twitter.sdk.android.core.SessionManager}.
     *
     * Caches internally for efficient access.
     *
     * @throws java.lang.IllegalStateException if {@link io.fabric.sdk.android.Fabric}
     *          or {@link TwitterCore} has not been initialized.
     */
    public TwitterApiClient getApiClient() {
        checkInitialized();
        final Session session = getActiveSession();
        if (session == null) {
            throw new IllegalStateException("Must have valid session."
                    + " Did you authenticate with Twitter?");
        }

        return getApiClient(session);
    }

    /**
     * Creates {@link com.twitter.sdk.android.core.TwitterApiClient} from authenticated
     * {@link com.twitter.sdk.android.core.Session} provided.
     *
     * Caches internally for efficient access.
     * @param session the session
     *
     * @throws java.lang.IllegalStateException if {@link io.fabric.sdk.android.Fabric}
     *          or {@link TwitterCore} has not been initialized.
     */
    public TwitterApiClient getApiClient(Session session) {
        checkInitialized();
        if (!apiClients.containsKey(session)) {
            apiClients.putIfAbsent(session, new TwitterApiClient(session));
        }
        return apiClients.get(session);
    }
}
