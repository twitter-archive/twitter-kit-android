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

import android.view.View;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiException;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.TwitterApiConstants;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetBuilder;

/*
 * FavoriteTweetAction is a click listener for ToggleImageButtons which performs Tweet
 * favorite/unfavorite actions onClick, updates button state, and calls through to the given
 * callback.
 */
class FavoriteTweetAction extends BaseTweetAction implements View.OnClickListener {
    final Tweet tweet;
    TweetRepository tweetRepository;

    public FavoriteTweetAction(Tweet tweet, TweetRepository tweetRepository, Callback<Tweet> cb) {
        super(cb);
        this.tweet = tweet;
        this.tweetRepository = tweetRepository;
    }

    @Override
    public void onClick(View view) {
        if (view instanceof ToggleImageButton) {
            final ToggleImageButton toggleImageButton = (ToggleImageButton) view;
            if (tweet.favorited) {
                tweetRepository.unfavorite(tweet.id,
                        new FavoriteCallback(toggleImageButton, tweet, getActionCallback()));
            } else {
                tweetRepository.favorite(tweet.id,
                        new FavoriteCallback(toggleImageButton, tweet, getActionCallback()));
            }
        }
    }

    /*
     * Toggles favorite button state to handle exceptions. It calls through to the given action
     * callback.
     */
    static class FavoriteCallback extends Callback<Tweet> {
        ToggleImageButton button;
        Tweet tweet;
        Callback<Tweet> cb;

        /*
         * Constructs a new FavoriteCallback.
         * @param button Favorite ToggleImageButton which should reflect Tweet favorited state
         * @param wasFavorited whether the Tweet was favorited or not before the click
         * @param cb the Callback.
         */
        FavoriteCallback(ToggleImageButton button, Tweet tweet, Callback<Tweet> cb) {
            this.button = button;
            this.tweet = tweet;
            this.cb = cb;
        }

        @Override
        public void success(Result<Tweet> result) {
            cb.success(result);
        }

        @Override
        public void failure(TwitterException exception) {
            if (exception instanceof TwitterApiException) {
                final TwitterApiException apiException = (TwitterApiException) exception;
                final int errorCode = apiException.getErrorCode();

                switch (errorCode) {
                    case TwitterApiConstants.Errors.ALREADY_FAVORITED:
                        final Tweet favorited = new TweetBuilder().copy(tweet).setFavorited(true)
                                .build();
                        cb.success(new Result<>(favorited, null));
                        return;
                    case TwitterApiConstants.Errors.ALREADY_UNFAVORITED:
                        final Tweet unfavorited = new TweetBuilder().copy(tweet).setFavorited(false)
                                .build();
                        cb.success(new Result<>(unfavorited, null));
                        return;
                    default:
                        // reset the toggle state back to match the Tweet
                        button.setToggledOn(tweet.favorited);
                        cb.failure(exception);
                        return;
                }
            }
            // reset the toggle state back to match the Tweet
            button.setToggledOn(tweet.favorited);
            cb.failure(exception);
        }
    }
}
