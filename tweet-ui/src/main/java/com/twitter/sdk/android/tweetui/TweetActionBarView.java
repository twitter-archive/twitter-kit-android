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

import com.twitter.sdk.android.core.models.Tweet;

public class TweetActionBarView extends LinearLayout {
    ToggleImageButton favoriteButton;
    ImageButton shareButton;

    public TweetActionBarView(Context context) {
        this(context, null);
    }

    public TweetActionBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findSubviews();
    }

    void findSubviews() {
        favoriteButton = (ToggleImageButton) findViewById(R.id.tw__tweet_favorite_button);
        shareButton = (ImageButton) findViewById(R.id.tw__tweet_share_button);
    }

    public void setTweet(Tweet tweet) {
        setFavorite(tweet);
        setShare(tweet);
    }

    void setFavorite(Tweet tweet) {
        if (tweet != null) {
            favoriteButton.setToggledOn(tweet.favorited);
            favoriteButton.setOnClickListener(new FavoriteTweetAction(tweet));
        }
    }

    void setShare(Tweet tweet) {
        shareButton.setOnClickListener(new ShareTweetAction(tweet));
    }
}
