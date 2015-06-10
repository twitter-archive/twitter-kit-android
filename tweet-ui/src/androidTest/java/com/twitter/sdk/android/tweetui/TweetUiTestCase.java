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

import io.fabric.sdk.android.FabricAndroidTestCase;
import io.fabric.sdk.android.FabricTestUtils;

import com.squareup.picasso.RequestCreator;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterTestUtils;
import com.twitter.sdk.android.core.internal.scribe.DefaultScribeClient;
import com.twitter.sdk.android.core.services.StatusesService;

import com.squareup.picasso.Picasso;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.mock;

public class TweetUiTestCase extends FabricAndroidTestCase {

    protected TweetUi tweetUi;

    // mocks
    protected AuthRequestQueue queue;
    protected Picasso picasso;
    private StatusesService statusesService;
    protected DefaultScribeClient scribeClient;
    protected Handler mainHandler;
    protected ExecutorService executorService;
    private TwitterApiClient apiClient;
    private ConcurrentHashMap<Session, TwitterApiClient> clients;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createMocks();

        FabricTestUtils.resetFabric();
        final TwitterCore twitterCore = TwitterTestUtils.createTwitter(
                new TwitterAuthConfig("", ""), clients);
        FabricTestUtils.with(getContext(), twitterCore, new TweetUi());

        tweetUi = TweetUi.getInstance();
        final TweetRepository tweetRepository
                = new TweetRepository(tweetUi, executorService, mainHandler, queue);
        tweetUi.setTweetRepository(tweetRepository);
        tweetUi.setImageLoader(picasso);
    }

    @Override
    protected void tearDown() throws Exception {
        FabricTestUtils.resetFabric();
        scrubClass(TweetUiTestCase.class);
        super.tearDown();
    }

    public void testClearSession() {
        final AppSession session = mock(AppSession.class);
        TwitterCore.getInstance().getAppSessionManager().setActiveSession(session);
        TweetUi.getInstance().clearAppSession(session.getId());
        assertEquals(null, TwitterCore.getInstance().getAppSessionManager().getActiveSession());
    }

    // Mocks

    private void createMocks() {
        mainHandler = mock(Handler.class);
        queue = mock(TestAuthRequestQueue.class);
        picasso = MockUtils.mockPicasso(mock(Picasso.class), mock(RequestCreator.class));

        statusesService = mock(StatusesService.class);

        scribeClient = mock(DefaultScribeClient.class);

        executorService = mock(ExecutorService.class);
        MockUtils.mockExecutorService(executorService);

        apiClient = mock(TwitterApiClient.class);
        MockUtils.mockStatusesServiceClient(apiClient, statusesService);

        clients = mock(ConcurrentHashMap.class);
        MockUtils.mockClients(clients, apiClient);
    }
}
