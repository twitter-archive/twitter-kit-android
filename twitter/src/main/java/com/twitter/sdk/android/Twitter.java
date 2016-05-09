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

package com.twitter.sdk.android;

import android.app.Activity;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetui.TweetUi;
import io.fabric.sdk.android.KitGroup;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Grouping of Twitter related Kits including {@link com.twitter.sdk.android.core.TwitterCore}
 * and {@link com.twitter.sdk.android.tweetui.TweetUi}.
 *
 * Must be provided to {@link io.fabric.sdk.android.Fabric#with(android.content.Context, io.fabric.sdk.android.Kit[])}
 * to initialize contained kits.
 */
public class Twitter extends Kit implements KitGroup {
    public final TwitterCore core;
    public final TweetUi tweetUi;
    public final TweetComposer tweetComposer;
    public final Collection<? extends Kit> kits;

    public static Twitter getInstance() {
        return Fabric.getKit(Twitter.class);
    }

    private static void checkInitialized() {
        if (getInstance() == null) {
            throw new IllegalStateException("Must start Twitter Kit with Fabric.with() first");
        }
    }

    public Twitter(TwitterAuthConfig config) {
        core = new TwitterCore(config);
        tweetUi = new TweetUi();
        tweetComposer = new TweetComposer();
        kits = Collections.unmodifiableCollection(Arrays.asList(core, tweetUi, tweetComposer));
    }

    @Override
    public String getVersion() {
        return BuildConfig.VERSION_NAME + "." + BuildConfig.BUILD_NUMBER;
    }

    @Override
    public String getIdentifier() {
        return BuildConfig.GROUP + ":" + BuildConfig.ARTIFACT_ID;
    }

    @Override
    public Collection<? extends Kit> getKits() {
        return kits;
    }

    @Override
    protected Object doInBackground() {
        //Nothing to do
        return null;
    }

    /**
     * Performs log in on behalf of a user.
     *
     * @param activity The {@link android.app.Activity} context to use for the login flow.
     * @param callback The callback interface to invoke when login completes.
     *
     * @throws java.lang.IllegalStateException if {@link io.fabric.sdk.android.Fabric}
     *          or {@link Twitter} has not been initialized.
     */
    public static void logIn(Activity activity, Callback<TwitterSession> callback) {
        checkInitialized();
        getInstance().core.logIn(activity, callback);
    }

    /**
     * Logs out the user, clearing user session. This will not make a network request to invalidate
     * the session.
     *
     * @throws java.lang.IllegalStateException if {@link io.fabric.sdk.android.Fabric}
     *          or {@link Twitter} has not been initialized.
     */
    public static void logOut() {
        checkInitialized();
        getInstance().core.logOut();
    }

    /**
     * @return the {@link com.twitter.sdk.android.core.SessionManager} for user sessions.
     *
     * @throws java.lang.IllegalStateException if {@link io.fabric.sdk.android.Fabric}
     *          or {@link Twitter} has not been initialized.
     */
    public static SessionManager<TwitterSession> getSessionManager() {
        checkInitialized();
        return getInstance().core.getSessionManager();
    }

    /**
     * Creates {@link com.twitter.sdk.android.core.TwitterApiClient} from default
     * {@link com.twitter.sdk.android.core.Session} retrieved from {@link com.twitter.sdk.android.core.SessionManager}.
     *
     * Caches internally for efficient access.
     *
     * @throws java.lang.IllegalStateException if {@link io.fabric.sdk.android.Fabric}
     *          or {@link Twitter} has not been initialized.
     */
    public static TwitterApiClient getApiClient() {
        checkInitialized();
        return getInstance().core.getApiClient();
    }

    /**
     * Creates {@link com.twitter.sdk.android.core.TwitterApiClient} from authenticated
     * {@link com.twitter.sdk.android.core.Session} provided.
     *
     * Caches internally for efficient access.
     * @param session the session
     *
     * @throws java.lang.IllegalStateException if {@link io.fabric.sdk.android.Fabric}
     *          or {@link Twitter} has not been initialized.
     */
    public static TwitterApiClient getApiClient(TwitterSession session) {
        checkInitialized();
        return getInstance().core.getApiClient(session);
    }
}
