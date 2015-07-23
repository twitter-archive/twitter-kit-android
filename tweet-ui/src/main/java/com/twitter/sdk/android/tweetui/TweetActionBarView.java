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

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.Tweet;

public class TweetActionBarView extends LinearLayout {
    final DependencyProvider dependencyProvider;
    ToggleImageButton favoriteButton;
    ImageButton shareButton;
    Callback<Tweet> actionCallback;

    public TweetActionBarView(Context context) {
        this(context, null, new DependencyProvider());
    }

    public TweetActionBarView(Context context, AttributeSet attrs) {
        this(context, attrs, new DependencyProvider());
    }

    TweetActionBarView(Context context, AttributeSet attrs, DependencyProvider dependencyProvider) {
        super(context, attrs);
        this.dependencyProvider = dependencyProvider;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findSubviews();
    }

    /*
     * Sets the callback to call when a Tweet Action (favorite, unfavorite) is performed.
     */
    void setOnActionCallback(Callback<Tweet> actionCallback) {
        this.actionCallback = actionCallback;
    }

    void findSubviews() {
        favoriteButton = (ToggleImageButton) findViewById(R.id.tw__tweet_favorite_button);
        shareButton = (ImageButton) findViewById(R.id.tw__tweet_share_button);
    }

    /*
     * Setup action bar buttons with Tweet and action performer.
     * @param tweet Tweet source for whether an action has been performed (e.g. isFavorited?)
     */
    void setTweet(Tweet tweet) {
        setFavorite(tweet);
        setShare(tweet);
    }

    void setFavorite(Tweet tweet) {
        final TweetRepository tweetRepository = dependencyProvider.getTweetRepository();
        if (tweet != null && tweetRepository != null) {
            favoriteButton.setToggledOn(tweet.favorited);
            final FavoriteTweetAction favoriteTweetAction = new FavoriteTweetAction(tweet,
                    tweetRepository, actionCallback);
            favoriteButton.setOnClickListener(favoriteTweetAction);
        }
    }

    void setShare(Tweet tweet) {
        shareButton.setOnClickListener(new ShareTweetAction(tweet));
    }

    /**
     * This is a mockable class that extracts our tight coupling with the TweetUi singleton.
     */
    static class DependencyProvider {
        /**
         * Return TweetRepository
         */
        TweetRepository getTweetRepository() {
            return TweetUi.getInstance().getTweetRepository();
        }
    }
}
