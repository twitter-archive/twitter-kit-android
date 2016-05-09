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

package com.twitter.sdk.android.tweetui;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.concurrency.DependsOn;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.internal.scribe.DefaultScribeClient;
import com.twitter.sdk.android.core.internal.scribe.EventNamespace;
import com.twitter.sdk.android.core.internal.scribe.ScribeItem;
import com.twitter.sdk.android.tweetui.internal.GuestSessionProvider;
import com.twitter.sdk.android.tweetui.internal.UserSessionProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * The TweetUi Kit provides views to render Tweets.
 */
@DependsOn(TwitterCore.class)
public class TweetUi extends Kit<Boolean> {
    static final String LOGTAG = "TweetUi";
    static final String NOT_STARTED_ERROR = "Must start TweetUi Kit in Fabric.with().";

    private static final String KIT_SCRIBE_NAME = "TweetUi";

    List<SessionManager<? extends Session>> userSessionManagers;
    List<SessionManager<? extends Session>> guestSessionManagers;
    UserSessionProvider userSessionProvider;
    GuestSessionProvider guestSessionProvider;
    DefaultScribeClient scribeClient;

    private TweetRepository tweetRepository;
    private TweetUiAuthRequestQueue userAuthQueue;
    private TweetUiAuthRequestQueue guestAuthQueue;
    private Picasso imageLoader;

    /**
     * @return the TweetUi singleton.
     * @throws IllegalStateException if the kit has not been started.
     */
    public static TweetUi getInstance() {
        checkInitialized();
        return Fabric.getKit(TweetUi.class);
    }

    @Override
    public String getIdentifier() {
        return BuildConfig.GROUP + ":" + BuildConfig.ARTIFACT_ID;
    }

    @Override
    public String getVersion() {
        return BuildConfig.VERSION_NAME + "." + BuildConfig.BUILD_NUMBER;
    }

    @Override
    protected boolean onPreExecute() {
        super.onPreExecute();
        final TwitterCore twitterCore = TwitterCore.getInstance();
        userSessionManagers = new ArrayList<>(1);
        userSessionManagers.add(twitterCore.getSessionManager());
        userSessionProvider = new UserSessionProvider(userSessionManagers);
        userAuthQueue = new TweetUiAuthRequestQueue(twitterCore, userSessionProvider);

        guestSessionManagers = new ArrayList<>(2);
        guestSessionManagers.add(twitterCore.getSessionManager());
        guestSessionManagers.add(twitterCore.getGuestSessionManager());
        guestSessionProvider = new GuestSessionProvider(twitterCore, guestSessionManagers);
        guestAuthQueue = new TweetUiAuthRequestQueue(twitterCore, guestSessionProvider);

        tweetRepository = new TweetRepository(getFabric().getMainHandler(), userAuthQueue,
                guestAuthQueue);
        return true;
    }

    @Override
    protected Boolean doInBackground() {
        /*
         * Picasso creation was moved to doInBackground because in the presence of okHttp there
         * ends up being strict mode violations if it is initialized on the main thread.
         */
        imageLoader = Picasso.with(getContext());

        // restore active sessions to user and guest auth queues
        userAuthQueue.sessionRestored(userSessionProvider.getActiveSession());
        guestAuthQueue.sessionRestored(guestSessionProvider.getActiveSession());

        setUpScribeClient();

        return true;
    }

    /**
     * Checks that the TweetUi kit has a singleton instance available.
     * @throws IllegalStateException if the kit instance is null.
     */
    private static void checkInitialized() {
        if (Fabric.getKit(TweetUi.class) == null) {
            throw new IllegalStateException(NOT_STARTED_ERROR);
        }
    }

    private void setUpScribeClient() {
        scribeClient = new DefaultScribeClient(this, KIT_SCRIBE_NAME, guestSessionManagers,
                getIdManager());
    }

    void scribe(EventNamespace... namespaces) {
        if (scribeClient == null) return;

        for (EventNamespace ns : namespaces) {
            scribeClient.scribe(ns);
        }
    }

    void scribe(EventNamespace ns, List<ScribeItem> items) {
        if (scribeClient == null) return;

        scribeClient.scribe(ns, items);
    }

    TweetRepository getTweetRepository() {
        return tweetRepository;
    }

    TweetUiAuthRequestQueue getGuestAuthQueue() {
        return guestAuthQueue;
    }

    // Testing purposes only
    void setTweetRepository(TweetRepository tweetRepository) {
        this.tweetRepository = tweetRepository;
    }

    Picasso getImageLoader() {
        return imageLoader;
    }

    // Testing purposes only
    void setImageLoader(Picasso imageLoader) {
        this.imageLoader = imageLoader;
    }
}
