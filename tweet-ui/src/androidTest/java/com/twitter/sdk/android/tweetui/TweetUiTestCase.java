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

import android.os.Handler;
import android.test.AndroidTestCase;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterCoreTestUtils;
import com.twitter.sdk.android.core.TwitterTestUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

import static org.mockito.Mockito.mock;

public class TweetUiTestCase extends AndroidTestCase {

    protected TweetUi tweetUi;

    // mocks
    protected Picasso picasso;
    protected TweetScribeClient scribeClient;
    protected Handler mainHandler;
    private TwitterApiClient apiClient;
    private ConcurrentHashMap<Session, TwitterApiClient> clients;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createMocks();

        // Initialize Fabric with mock executor so that kit#doInBackground() will not be called
        // during kit initialization.
        final TwitterConfig config = new TwitterConfig.Builder(getContext())
                .logger(new DefaultLogger(Log.DEBUG))
                .executorService(mock(ThreadPoolExecutor.class))
                .build();

        Twitter.initialize(config);

        final TwitterCore twitterCore = TwitterCoreTestUtils.createTwitterCore(
                new TwitterAuthConfig("", ""), clients, apiClient);

        tweetUi = TweetUi.getInstance();
        final TweetRepository tweetRepository = new TweetRepository(mainHandler,
                mock(SessionManager.class), twitterCore);
        tweetUi.setTweetRepository(tweetRepository);
        tweetUi.setImageLoader(picasso);
    }

    @Override
    protected void tearDown() throws Exception {
        TwitterTestUtils.resetTwitter();
        TwitterCoreTestUtils.resetTwitterCore();
        TweetUiTestUtils.resetTweetUi();

        scrubClass(TweetUiTestCase.class);
        super.tearDown();
    }

    private void createMocks() {
        mainHandler = mock(Handler.class);
        picasso = MockUtils.mockPicasso(mock(Picasso.class), mock(RequestCreator.class));

        scribeClient = mock(TweetScribeClient.class);

        apiClient = mock(TwitterApiClient.class);
        MockUtils.mockApiClient(apiClient);

        clients = mock(ConcurrentHashMap.class);
        MockUtils.mockClients(clients, apiClient);
    }
}
