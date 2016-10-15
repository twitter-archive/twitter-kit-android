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

import com.twitter.sdk.android.core.models.Tweet;

public class TweetView extends BaseTweetView {
    private static final String VIEW_TYPE_NAME = "default";
    private static final double SQUARE_ASPECT_RATIO = 1.0;
    private static final double DEFAULT_ASPECT_RATIO_MEDIA_CONTAINER = 3.0 / 2.0;

    public TweetView(Context context, Tweet tweet) {
        super(context, tweet);
    }

    public TweetView(Context context, Tweet tweet, int styleResId) {
        super(context, tweet, styleResId);
    }

    TweetView(Context context, Tweet tweet, int styleResId, DependencyProvider dependencyProvider) {
        super(context, tweet, styleResId, dependencyProvider);
    }

    public TweetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TweetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int getLayout() {
        return R.layout.tw__tweet;
    }

    /**
     * Render the Tweet by updating the subviews. For any data that is missing from the Tweet,
     * invalidate the subview value (e.g. text views set to empty string) for view recycling.
     * Do not call with render true until inflation has completed.
     * @throws IllegalArgumentException
     */
    @Override
    void render() {
        super.render();
        setVerifiedCheck(tweet);
    }

    /**
     * Returns the desired aspect ratio for Tweet that contains photo entities
     *
     * @param photoCount total count of photo entities
     * @return the target image and bitmap width to height aspect ratio
     */
    @Override
    protected double getAspectRatioForPhotoEntity(int photoCount) {
        if (photoCount == 4) {
            return SQUARE_ASPECT_RATIO;
        } else {
            return DEFAULT_ASPECT_RATIO_MEDIA_CONTAINER;
        }
    }

    /**
     * Sets the verified check if the User is verified. If the User is not verified or if the
     * verification data is unavailable, remove the check.
     */
    private void setVerifiedCheck(Tweet tweet) {
        if (tweet != null && tweet.user != null && tweet.user.verified) {
            fullNameView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.tw__ic_tweet_verified, 0);
        } else {
            fullNameView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    @Override
    String getViewTypeName() {
        return VIEW_TYPE_NAME;
    }
}
