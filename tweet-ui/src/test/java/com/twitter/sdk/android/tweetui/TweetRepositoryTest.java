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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import org.junit.Before;

import java.util.List;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TweetRepositoryTest {
    private static final Long anyId = 1L;
    private static final List<Long> anyIds = new ArrayList<Long>();
    private TweetUiAuthRequestQueue mockUserAuthQueue;
    private TweetUiAuthRequestQueue mockGuestAuthQueue;
    private TweetRepository tweetRepository;

    @Before
    public void setUp() throws Exception {
        anyIds.add(anyId);
        mockUserAuthQueue = mock(TweetUiAuthRequestQueue.class);
        mockGuestAuthQueue = mock(TweetUiAuthRequestQueue.class);
        tweetRepository = new TweetRepository(mock(Handler.class), mockUserAuthQueue,
                mockGuestAuthQueue);
    }

    @Test
    public void testFavoriteDelegation() {
        tweetRepository.favorite(anyId, mock(Callback.class));
        verify(mockUserAuthQueue, times(1)).addClientRequest(any(Callback.class));
        verifyZeroInteractions(mockGuestAuthQueue);
    }

    @Test
    public void testUnfavoriteDelegation() {
        tweetRepository.unfavorite(anyId, mock(Callback.class));
        verify(mockUserAuthQueue, times(1)).addClientRequest(any(Callback.class));
        verifyZeroInteractions(mockGuestAuthQueue);
    }

    @Test
    public void testRetweetDelegation() {
        tweetRepository.retweet(anyId, mock(Callback.class));
        verify(mockUserAuthQueue, times(1)).addClientRequest(any(Callback.class));
        verifyZeroInteractions(mockGuestAuthQueue);
    }

    @Test
    public void testUnretweetDelegation() {
        tweetRepository.unretweet(anyId, mock(Callback.class));
        verify(mockUserAuthQueue, times(1)).addClientRequest(any(Callback.class));
        verifyZeroInteractions(mockGuestAuthQueue);
    }

    @Test
    public void testLoadTweetDelegation() {
        tweetRepository.loadTweet(anyId, mock(Callback.class));
        verifyZeroInteractions(mockUserAuthQueue);
        verify(mockGuestAuthQueue, times(1)).addClientRequest(any(Callback.class));
    }

    @Test
    public void testLoadTweetsDelegation() {
        tweetRepository.loadTweets(anyIds, mock(Callback.class));
        verifyZeroInteractions(mockUserAuthQueue);
        verify(mockGuestAuthQueue, times(1)).addClientRequest(any(Callback.class));
    }
}
