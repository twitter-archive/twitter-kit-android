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
import com.twitter.sdk.android.core.TwitterApiException;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.TwitterApiConstants;
import com.twitter.sdk.android.core.models.Tweet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class LikeTweetActionTest {
    TweetUi mockTweetUi;
    TweetRepository mockTweetRepository;
    Callback<Tweet> mockCallback;
    TweetScribeClient mockScribeClient;

    @Before
    public void setUp() throws Exception {
        mockTweetUi = mock(TweetUi.class);
        mockTweetRepository = mock(TweetRepository.class);
        when(mockTweetUi.getTweetRepository()).thenReturn(mockTweetRepository);
        mockScribeClient = mock(TweetScribeClient.class);
        mockCallback = mock(Callback.class);
    }

    @Test
    public void testOnClick_performFavorite() {
        final ArgumentCaptor<LikeTweetAction.LikeCallback> favoriteCbCaptor
                = ArgumentCaptor.forClass(LikeTweetAction.LikeCallback.class);
        final LikeTweetAction likeAction = new LikeTweetAction(TestFixtures.TEST_TWEET,
                mockTweetUi, mockCallback, mockScribeClient);
        final ToggleImageButton mockToggleButton = mock(ToggleImageButton.class);
        // assert that click when tweet is unfavorited
        // - performs a like action which favorites the correct tweet id
        // - passes FavoriteCallback with toggle button and tweet references
        likeAction.onClick(mockToggleButton);
        verify(mockTweetRepository).favorite(eq(TestFixtures.TEST_TWEET.id),
                favoriteCbCaptor.capture());
        assertEquals(mockToggleButton, favoriteCbCaptor.getValue().button);
        assertFalse(favoriteCbCaptor.getValue().tweet.favorited);

        assertFavoriteScribe();
    }

    @Test
    public void testOnClick_performUnfavorite() {
        final ArgumentCaptor<LikeTweetAction.LikeCallback> favoriteCbCaptor
                = ArgumentCaptor.forClass(LikeTweetAction.LikeCallback.class);
        final LikeTweetAction favoriteAction = new LikeTweetAction(
                TestFixtures.TEST_FAVORITED_TWEET, mockTweetUi, mockCallback, mockScribeClient);
        final ToggleImageButton mockToggleButton = mock(ToggleImageButton.class);
        // assert that click when tweet is favorited
        // - performs an unlike action which unfavorites the correct tweet id
        // - passes FavoriteCallback with toggle button and tweet references
        favoriteAction.onClick(mockToggleButton);
        verify(mockTweetRepository).unfavorite(eq(TestFixtures.TEST_TWEET.id),
                favoriteCbCaptor.capture());
        assertEquals(mockToggleButton, favoriteCbCaptor.getValue().button);
        assertTrue(favoriteCbCaptor.getValue().tweet.favorited);

        assertUnfavoriteScribe();
    }

    private void assertFavoriteScribe() {
        final ArgumentCaptor<Tweet> tweetCaptor
                = ArgumentCaptor.forClass(Tweet.class);
        verify(mockScribeClient).favorite(tweetCaptor.capture());
        final Tweet capturedTweet = tweetCaptor.getValue();
        assertEquals(TestFixtures.TEST_TWEET, capturedTweet);
    }

    private void assertUnfavoriteScribe() {
        final ArgumentCaptor<Tweet> tweetCaptor
                = ArgumentCaptor.forClass(Tweet.class);
        verify(mockScribeClient).unfavorite(tweetCaptor.capture());
        final Tweet capturedTweet = tweetCaptor.getValue();
        assertEquals(TestFixtures.TEST_FAVORITED_TWEET, capturedTweet);
    }

    @Test
    public void testFavoriteCallback_successCallsCallback() {
        final ToggleImageButton mockToggleButton = mock(ToggleImageButton.class);
        final Tweet unfavoritedTweet = TestFixtures.TEST_TWEET;
        final LikeTweetAction.LikeCallback callback
                = new LikeTweetAction.LikeCallback(mockToggleButton, unfavoritedTweet,
                mockCallback);
        final Result<Tweet> successResult = new Result<>(TestFixtures.TEST_FAVORITED_TWEET, null);
        callback.success(successResult);
        verify(mockCallback).success(successResult);
    }

    @Test
    public void testFavoriteCallback_failureAlreadyFavorited() {
        final ToggleImageButton mockToggleButton = mock(ToggleImageButton.class);
        // locally unfavorited, but on server the tweet is favorited
        final Tweet tweet = TestFixtures.TEST_TWEET;
        final LikeTweetAction.LikeCallback callback
                = new LikeTweetAction.LikeCallback(mockToggleButton, tweet,
                mockCallback);
        final TwitterApiException alreadyFavoritedException = mock(TwitterApiException.class);
        when(alreadyFavoritedException.getErrorCode()).thenReturn(
                TwitterApiConstants.Errors.ALREADY_FAVORITED);
        // assert that
        // - the failure is treated as a cb success
        // - success result Tweet is marked as favorited
        callback.failure(alreadyFavoritedException);
        final ArgumentCaptor<Result<Tweet>> resultCaptor
                = ArgumentCaptor.forClass(Result.class);
        verify(mockCallback).success(resultCaptor.capture());
        assertEquals(tweet.getId(), resultCaptor.getValue().data.getId());
        assertTrue(resultCaptor.getValue().data.favorited);
    }

    @Test
    public void testFavoriteCallback_failureAlreadyUnfavorited() {
        final ToggleImageButton mockToggleButton = mock(ToggleImageButton.class);
        // locally favorited, but on server the tweet is unfavorited
        final Tweet tweet = TestFixtures.TEST_FAVORITED_TWEET;
        final LikeTweetAction.LikeCallback callback
                = new LikeTweetAction.LikeCallback(mockToggleButton, tweet,
                mockCallback);
        final TwitterApiException alreadyUnfavoritedException = mock(TwitterApiException.class);
        when(alreadyUnfavoritedException.getErrorCode()).thenReturn(
                TwitterApiConstants.Errors.ALREADY_UNFAVORITED);
        // assert that
        // - the failure is treated as a cb success
        // - success result Tweet is marked as unfavorited
        callback.failure(alreadyUnfavoritedException);
        final ArgumentCaptor<Result<Tweet>> resultCaptor
                = ArgumentCaptor.forClass(Result.class);
        verify(mockCallback).success(resultCaptor.capture());
        assertEquals(tweet.getId(), resultCaptor.getValue().data.getId());
        assertFalse(resultCaptor.getValue().data.favorited);
    }

    @Test
    public void testFavoriteCallback_failureOtherTwitterApiException() {
        final ToggleImageButton mockToggleButton = mock(ToggleImageButton.class);
        final Tweet favoritedTweet = TestFixtures.TEST_FAVORITED_TWEET;
        final Callback<Tweet> mockCallback = mock(Callback.class);
        final LikeTweetAction.LikeCallback callback
                = new LikeTweetAction.LikeCallback(mockToggleButton, favoritedTweet,
                mockCallback);
        final TwitterApiException twitterApiException = mock(TwitterApiException.class);
        callback.failure(twitterApiException);
        verify(mockToggleButton).setToggledOn(favoritedTweet.favorited);
        verify(mockCallback).failure(twitterApiException);
    }

    @Test
    public void testFavoriteCallback_failureOtherTwitterException() {
        final ToggleImageButton mockToggleButton = mock(ToggleImageButton.class);
        final Tweet unfavoritedTweet = TestFixtures.TEST_TWEET;
        final Callback<Tweet> mockCallback = mock(Callback.class);
        final LikeTweetAction.LikeCallback callback
                = new LikeTweetAction.LikeCallback(mockToggleButton, unfavoritedTweet,
                mockCallback);
        final TwitterException twitterException = mock(TwitterException.class);
        callback.failure(twitterException);
        verify(mockToggleButton).setToggledOn(unfavoritedTweet.favorited);
        verify(mockCallback).failure(twitterException);
    }
}
