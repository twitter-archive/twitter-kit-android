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

import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;

public class CompactTweetView extends BaseTweetView {
    private static final String VIEW_TYPE_NAME = "compact";
    private static final double SQUARE_ASPECT_RATIO = 1.0;
    private static final double MAX_LANDSCAPE_ASPECT_RATIO = 3.0;
    private static final double MIN_LANDSCAPE_ASPECT_RATIO = 4.0 / 3.0;
    private static final double DEFAULT_ASPECT_RATIO_MEDIA_CONTAINER = 16.0 / 10.0;

    public CompactTweetView(Context context, Tweet tweet) {
        super(context, tweet);
    }

    public CompactTweetView(Context context, Tweet tweet, int styleResId) {
        super(context, tweet, styleResId);
    }

    CompactTweetView(Context context, Tweet tweet, int styleResId,
            DependencyProvider dependencyProvider) {
        super(context, tweet, styleResId, dependencyProvider);
    }

    public CompactTweetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompactTweetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int getLayout() {
        return R.layout.tw__tweet_compact;
    }

    @Override
    void render() {
        super.render();

        // Redraw screen name on recycle, because TextView doesn't resize when text length changes
        screenNameView.requestLayout();
    }

    @Override
    protected void applyStyles() {
        super.applyStyles();

        final int paddingTop = getResources()
                .getDimensionPixelSize(R.dimen.tw__compact_tweet_container_padding_top);
        setPadding(0, paddingTop, 0, 0);

        final int mediaViewRadius =
                getResources().getDimensionPixelSize(R.dimen.tw__media_view_radius);
        tweetMediaView.setRoundedCornersRadii(mediaViewRadius, mediaViewRadius,
                mediaViewRadius, mediaViewRadius);
    }

    /**
     * Returns the desired aspect ratio of the Tweet media entity according to "sizes" metadata
     * and the aspect ratio display rules.
     * @param photoEntity the first
     * @return the target image and bitmap width to height aspect ratio
     */
    @Override
    protected double getAspectRatio(MediaEntity photoEntity) {
        final double ratio = super.getAspectRatio(photoEntity);
        if (ratio <= SQUARE_ASPECT_RATIO) {
            // portrait (tall) photos should be cropped to be square aspect ratio
            return SQUARE_ASPECT_RATIO;
        } else if (ratio > MAX_LANDSCAPE_ASPECT_RATIO) {
            // the widest landscape photos allowed are 3:1
            return MAX_LANDSCAPE_ASPECT_RATIO;
        } else if (ratio < MIN_LANDSCAPE_ASPECT_RATIO) {
            // the tallest landscape photos allowed are 4:3
            return MIN_LANDSCAPE_ASPECT_RATIO;
        } else {
            // landscape photos between 3:1 to 4:3 present the original width to height ratio
            return ratio;
        }
    }

    /**
     * Returns the desired aspect ratio for Tweet that contains photo entities
     *
     * @param photoCount total count of photo entities
     * @return the target image and bitmap width to height aspect ratio
     */
    @Override
    protected double getAspectRatioForPhotoEntity(int photoCount) {
        return DEFAULT_ASPECT_RATIO_MEDIA_CONTAINER;
    }

    @Override
    String getViewTypeName() {
        return VIEW_TYPE_NAME;
    }
}
