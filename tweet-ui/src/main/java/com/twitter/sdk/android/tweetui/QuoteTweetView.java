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

import com.twitter.sdk.android.core.models.MediaEntity;

public class QuoteTweetView extends AbstractTweetView {
    private static final String VIEW_TYPE_NAME = "quote";
    private static final double SQUARE_ASPECT_RATIO = 1.0;
    private static final double MAX_LANDSCAPE_ASPECT_RATIO = 3.0;
    private static final double MIN_LANDSCAPE_ASPECT_RATIO = 4.0 / 3.0;
    private static final double DEFAULT_ASPECT_RATIO_MEDIA_CONTAINER = 16.0 / 10.0;

    public QuoteTweetView(Context context) {
        this(context, new DependencyProvider());
    }

    QuoteTweetView(Context context, DependencyProvider dependencyProvider) {
        super(context, null, 0, dependencyProvider);
    }

    public void setStyle(int primaryTextColor, int secondaryTextColor, int actionColor,
                         int actionHighlightColor, int mediaBgColor, int photoErrorResId) {
        this.primaryTextColor = primaryTextColor;
        this.secondaryTextColor = secondaryTextColor;
        this.actionColor = actionColor;
        this.actionHighlightColor = actionHighlightColor;
        this.mediaBgColor = mediaBgColor;
        this.photoErrorResId = photoErrorResId;

        applyStyles();
    }

    @Override
    protected int getLayout() {
        return R.layout.tw__tweet_quote;
    }

    @Override
    void render() {
        super.render();
        // Redraw screen name on recycle
        screenNameView.requestLayout();
    }

    protected void applyStyles() {
        final int mediaViewRadius =
                getResources().getDimensionPixelSize(R.dimen.tw__media_view_radius);
        tweetMediaView.setRoundedCornersRadii(0, 0, mediaViewRadius, mediaViewRadius);

        setBackgroundResource(R.drawable.tw__quote_tweet_border);
        fullNameView.setTextColor(primaryTextColor);
        screenNameView.setTextColor(secondaryTextColor);
        contentView.setTextColor(primaryTextColor);
        tweetMediaView.setMediaBgColor(mediaBgColor);
        tweetMediaView.setPhotoErrorResId(photoErrorResId);
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
