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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class FavoriteTweetActionTest {

    @Test
    public void testOnClick_tweetFavorited() {
        final TweetRepository mockTweetRepository = mock(TweetRepository.class);
        final ArgumentCaptor<FavoriteTweetAction.FavoriteCallback> favoriteCbCaptor
                = ArgumentCaptor.forClass(FavoriteTweetAction.FavoriteCallback.class);
        final FavoriteTweetAction favoriteAction = new FavoriteTweetAction(TestFixtures.TEST_TWEET,
                mockTweetRepository, mock(Callback.class));
        final ToggleImageButton mockToggleButton = mock(ToggleImageButton.class);

        favoriteAction.onClick(mockToggleButton);
        verify(mockTweetRepository).favorite(eq(TestFixtures.TEST_TWEET.id),
                favoriteCbCaptor.capture());
        assertEquals(mockToggleButton, favoriteCbCaptor.getValue().button);
        assertFalse(favoriteCbCaptor.getValue().wasFavorited);
    }

    public void testOnClick_tweetNotFavorited() {
        final TweetRepository mockTweetRepository = mock(TweetRepository.class);
        final ArgumentCaptor<FavoriteTweetAction.FavoriteCallback> favoriteCbCaptor
                = ArgumentCaptor.forClass(FavoriteTweetAction.FavoriteCallback.class);
        final FavoriteTweetAction favoriteAction = new FavoriteTweetAction(
                TestFixtures.TEST_FAVORITED_TWEET, mockTweetRepository, mock(Callback.class));
        final ToggleImageButton mockToggleButton = mock(ToggleImageButton.class);

        favoriteAction.onClick(mockToggleButton);
        verify(mockTweetRepository).unfavorite(eq(TestFixtures.TEST_TWEET.id),
                favoriteCbCaptor.capture());
        assertEquals(mockToggleButton, favoriteCbCaptor.getValue().button);
        assertTrue(favoriteCbCaptor.getValue().wasFavorited);
    }

    public void testFavoriteCallback_favoriteSuccess() {
        final ToggleImageButton mockToggleButton = mock(ToggleImageButton.class);
        final boolean wasFavorited = false;
        final Callback<Tweet> mockCallback = mock(Callback.class);
        final FavoriteTweetAction.FavoriteCallback callback
                = new FavoriteTweetAction.FavoriteCallback(mockToggleButton, wasFavorited,
                mockCallback);
        final Result<Tweet> successResult = new Result<>(TestFixtures.TEST_FAVORITED_TWEET, null);
        callback.success(successResult);
        verify(mockToggleButton).setToggledOn(true);
        verify(mockCallback).success(successResult);
    }

    public void testFavoriteCallback_unfavoriteSuccess() {
        final ToggleImageButton mockToggleButton = mock(ToggleImageButton.class);
        final boolean wasFavorited = true;
        final Callback<Tweet> mockCallback = mock(Callback.class);
        final FavoriteTweetAction.FavoriteCallback callback
                = new FavoriteTweetAction.FavoriteCallback(mockToggleButton, wasFavorited,
                mockCallback);
        final Result<Tweet> successResult = new Result<>(TestFixtures.TEST_TWEET, null);
        callback.success(successResult);
        verify(mockToggleButton).setToggledOn(false);
        verify(mockCallback).success(successResult);
    }

    public void testFavoriteCallback_failureAlreadyFavorited() {
        final ToggleImageButton mockToggleButton = mock(ToggleImageButton.class);
        final boolean wasFavorited = false;   // value doesn't matter
        final Callback<Tweet> mockCallback = mock(Callback.class);
        final FavoriteTweetAction.FavoriteCallback callback
                = new FavoriteTweetAction.FavoriteCallback(mockToggleButton, wasFavorited,
                mockCallback);
        final TwitterApiException alreadyFavoritedException = mock(TwitterApiException.class);
        when(alreadyFavoritedException.getErrorCode()).thenReturn(
                TwitterApiConstants.Errors.ALREADY_FAVORITED);
        callback.failure(alreadyFavoritedException);
        verify(mockToggleButton).setToggledOn(true);
        verify(mockCallback).failure(alreadyFavoritedException);
    }

    public void testFavoriteCallback_failureNotFound() {
        final ToggleImageButton mockToggleButton = mock(ToggleImageButton.class);
        final boolean wasFavorited = false;   // value doesn't matter
        final Callback<Tweet> mockCallback = mock(Callback.class);
        final FavoriteTweetAction.FavoriteCallback callback
                = new FavoriteTweetAction.FavoriteCallback(mockToggleButton, wasFavorited,
                mockCallback);
        final TwitterApiException notFoundException = mock(TwitterApiException.class);
        when(notFoundException.getErrorCode()).thenReturn(
                TwitterApiConstants.Errors.NOT_FOUND);
        callback.failure(notFoundException);
        verify(mockToggleButton).setToggledOn(false);
        verify(mockCallback).failure(notFoundException);
    }

    public void testFavoriteCallback_failureOtherTwitterApiException() {
        final ToggleImageButton mockToggleButton = mock(ToggleImageButton.class);
        final boolean wasFavorited = false;
        final Callback<Tweet> mockCallback = mock(Callback.class);
        final FavoriteTweetAction.FavoriteCallback callback
                = new FavoriteTweetAction.FavoriteCallback(mockToggleButton, wasFavorited,
                mockCallback);
        final TwitterApiException twitterApiException = mock(TwitterApiException.class);
        callback.failure(twitterApiException);
        verify(mockToggleButton).setToggledOn(wasFavorited);
        verify(mockCallback).failure(twitterApiException);
    }

    public void testFavoriteCallback_failureOtherTwitterException() {
        final ToggleImageButton mockToggleButton = mock(ToggleImageButton.class);
        final boolean wasFavorited = false;
        final Callback<Tweet> mockCallback = mock(Callback.class);
        final FavoriteTweetAction.FavoriteCallback callback
                = new FavoriteTweetAction.FavoriteCallback(mockToggleButton, wasFavorited,
                mockCallback);
        final TwitterException twitterException = mock(TwitterException.class);
        callback.failure(twitterException);
        verify(mockToggleButton).setToggledOn(wasFavorited);
        verify(mockCallback).failure(twitterException);
    }
}
