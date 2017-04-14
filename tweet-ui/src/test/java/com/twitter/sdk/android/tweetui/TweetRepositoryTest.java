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

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.FavoriteService;
import com.twitter.sdk.android.core.services.StatusesService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class TweetRepositoryTest {
    private static final Long anyId = 123L;
    private static final List<Long> anyIds = new ArrayList<>();
    private TwitterCore mockTwitterCore;
    private TwitterApiClient mockApiClient;
    private FavoriteService mockFavoriteService;
    private StatusesService mockStatusesService;
    private SessionManager<TwitterSession> mockSessionManager;
    private Handler mockHandler;
    private TweetRepository tweetRepository;

    @Before
    public void setUp() throws Exception {
        anyIds.add(anyId);
        mockTwitterCore = mock(TwitterCore.class);
        mockApiClient = mock(TwitterApiClient.class);
        mockStatusesService = mock(StatusesService.class, Mockito.RETURNS_MOCKS);
        when(mockApiClient.getStatusesService()).thenReturn(mockStatusesService);
        mockFavoriteService = mock(FavoriteService.class, Mockito.RETURNS_MOCKS);
        when(mockApiClient.getFavoriteService()).thenReturn(mockFavoriteService);
        when(mockTwitterCore.getApiClient(any(TwitterSession.class))).thenReturn(mockApiClient);
        when(mockTwitterCore.getApiClient()).thenReturn(mockApiClient);
        mockSessionManager = mock(SessionManager.class);
        when(mockSessionManager.getActiveSession()).thenReturn(mock(TwitterSession.class));
        mockHandler = mock(Handler.class);
        tweetRepository = new TweetRepository(mockHandler, mockSessionManager, mockTwitterCore);
    }

    @Test
    public void testFavoriteDelegation() {
        tweetRepository.favorite(anyId, mock(Callback.class));
        verify(mockFavoriteService).create(anyId, false);
    }

    @Test
    public void testUnfavoriteDelegation() {
        tweetRepository.unfavorite(anyId, mock(Callback.class));
        verify(mockFavoriteService).destroy(anyId, false);
    }

    @Test
    public void testRetweetDelegation() {
        tweetRepository.retweet(anyId, mock(Callback.class));
        verify(mockStatusesService).retweet(anyId, false);
    }

    @Test
    public void testUnretweetDelegation() {
        tweetRepository.unretweet(anyId, mock(Callback.class));
        verify(mockStatusesService).unretweet(anyId, false);
    }

    @Test
    public void testLoadTweetDelegation() {
        tweetRepository.loadTweet(anyId, mock(Callback.class));
        verify(mockStatusesService).show(anyId, null, null, null);
    }

    @Test
    public void testLoadTweetsDelegation() {
        tweetRepository.loadTweets(anyIds, mock(Callback.class));
        verify(mockStatusesService).lookup(anyId.toString(), null, null, null);
    }

    @Test
    public void testGetUserSession_withActiveUserSession() {
        final Callback<TwitterSession> cb = mock(Callback.class);
        tweetRepository.getUserSession(cb);

        verify(cb).success(any(Result.class));
    }

    @Test
    public void testGetUserSession_withNoActiveUserSession() {
        final Callback<TwitterSession> cb = mock(Callback.class);
        when(mockSessionManager.getActiveSession()).thenReturn(null);
        tweetRepository.getUserSession(cb);

        verify(cb).failure(any(TwitterAuthException.class));
    }

    @Test
    public void testSingleTweetCallback_callsUpdateCache() {
        final TweetRepository mockRepo = mock(TweetRepository.class);
        final TweetRepository.SingleTweetCallback callback
                = mockRepo.new SingleTweetCallback(null);
        callback.success(new Result<>(mock(Tweet.class), null));
        verify(mockRepo, times(1)).updateCache(any(Tweet.class));
    }
}
