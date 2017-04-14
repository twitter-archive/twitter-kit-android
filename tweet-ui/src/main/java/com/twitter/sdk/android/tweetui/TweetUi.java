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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.GuestSessionProvider;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.scribe.DefaultScribeClient;
import com.twitter.sdk.android.core.internal.scribe.EventNamespace;
import com.twitter.sdk.android.core.internal.scribe.ScribeConfig;
import com.twitter.sdk.android.core.internal.scribe.ScribeItem;

import java.util.List;

/**
 * The TweetUi Kit provides views to render Tweets.
 */
public class TweetUi {
    @SuppressLint("StaticFieldLeak")
    static volatile TweetUi instance;
    static final String LOGTAG = "TweetUi";

    private static final String KIT_SCRIBE_NAME = "TweetUi";

    SessionManager<TwitterSession> sessionManager;
    GuestSessionProvider guestSessionProvider;
    DefaultScribeClient scribeClient;
    Context context;

    private TweetRepository tweetRepository;
    private Picasso imageLoader;

    public static TweetUi getInstance() {
        if (instance == null) {
            synchronized (TweetUi.class) {
                if (instance == null) {
                    instance = new TweetUi();
                }
            }
        }
        return instance;
    }

    TweetUi() {
        final TwitterCore twitterCore = TwitterCore.getInstance();

        context = Twitter.getInstance().getContext(getIdentifier());
        sessionManager = twitterCore.getSessionManager();
        guestSessionProvider = twitterCore.getGuestSessionProvider();
        tweetRepository = new TweetRepository(new Handler(Looper.getMainLooper()),
                twitterCore.getSessionManager());
        imageLoader = Picasso.with(Twitter.getInstance().getContext(getIdentifier()));
        setUpScribeClient();
    }

    public String getIdentifier() {
        return BuildConfig.GROUP + ":" + BuildConfig.ARTIFACT_ID;
    }

    public String getVersion() {
        return BuildConfig.VERSION_NAME + "." + BuildConfig.BUILD_NUMBER;
    }

    private void setUpScribeClient() {
        final ScribeConfig config =
                DefaultScribeClient.getScribeConfig(KIT_SCRIBE_NAME, getVersion());
        scribeClient = new DefaultScribeClient(context, sessionManager,
                guestSessionProvider, Twitter.getInstance().getIdManager(), config);
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

    // Testing purposes only
    void setTweetRepository(TweetRepository tweetRepository) {
        this.tweetRepository = tweetRepository;
    }

    public Picasso getImageLoader() {
        return imageLoader;
    }

    // Testing purposes only
    void setImageLoader(Picasso imageLoader) {
        this.imageLoader = imageLoader;
    }
}
