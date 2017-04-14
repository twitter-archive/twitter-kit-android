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

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class ResetTweetCallbackTest {

    BaseTweetView mockTweetView;
    TweetRepository mockTweetRepository;

    @Before
    public void setUp() throws Exception {
        mockTweetRepository = mock(TweetRepository.class);
        mockTweetView = mock(BaseTweetView.class);
    }

    @Test
    public void testSuccess() {
        final Callback<Tweet> developerCallback = mock(Callback.class);
        final ResetTweetCallback resetCallback = new ResetTweetCallback(mockTweetView,
                mockTweetRepository, developerCallback);
        final Result<Tweet> successResult = new Result<>(TestFixtures.TEST_TWEET, null);
        resetCallback.success(successResult);
        verify(mockTweetRepository).updateCache(TestFixtures.TEST_TWEET);
        verify(mockTweetView).setTweet(TestFixtures.TEST_TWEET);
        verify(developerCallback).success(successResult);
    }

    @Test
    public void testSuccess_handlesNullCallback() {
        final Callback<Tweet> developerCallback = mock(Callback.class);
        final ResetTweetCallback resetCallback = new ResetTweetCallback(mockTweetView,
                mockTweetRepository, null);
        final Result<Tweet> successResult = new Result<>(TestFixtures.TEST_TWEET, null);
        try {
            resetCallback.success(successResult);
        } catch (NullPointerException e) {
            fail("Should have handled null callback");
        }
        verify(mockTweetRepository).updateCache(TestFixtures.TEST_TWEET);
        verify(mockTweetView).setTweet(TestFixtures.TEST_TWEET);
    }

    @Test
    public void testFailure() {
        final Callback<Tweet> developerCallback = mock(Callback.class);
        final ResetTweetCallback resetCallback = new ResetTweetCallback(mockTweetView,
                mockTweetRepository, developerCallback);
        final TwitterException exception = mock(TwitterException.class);
        resetCallback.failure(exception);
        verify(developerCallback).failure(exception);
    }

    @Test
    public void testFailure_handlesNullCallback() {
        final Callback<Tweet> developerCallback = mock(Callback.class);
        final ResetTweetCallback resetCallback = new ResetTweetCallback(mockTweetView,
                mockTweetRepository, null);
        final TwitterException exception = mock(TwitterException.class);
        try {
            resetCallback.failure(exception);
        } catch (NullPointerException e) {
            fail("Should have handled null callback");
        }
    }
}
