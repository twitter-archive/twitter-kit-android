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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.twitter.sdk.android.core.models.Tweet;

public class TweetView extends BaseTweetView {
    private static final String VIEW_TYPE_NAME = "default";

    // layout views
    Button shareButton;

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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public TweetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int getLayout() {
        return R.layout.tw__tweet;
    }

    @Override
    void findSubviews() {
        super.findSubviews();
        shareButton = (Button) findViewById(R.id.tw__tweet_share);
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
        setShareButtonVisibility(tweet);
    }

    @Override
    protected void applyStyles() {
        super.applyStyles();
        shareButton.setTextColor(actionColor);
    }

    private void setShareButtonVisibility(Tweet tweet) {
        if (TweetUtils.isTweetResolvable(tweet)) {
            setShareButtonVisible();
            shareButton.setOnClickListener(new OnShareButtonClickListener(tweet));
        } else {
            setShareButtonGone();
            shareButton.setOnClickListener(null);
        }
    }

    private void setShareButtonVisible() {
        shareButton.setVisibility(VISIBLE);

        containerView.setPadding(containerView.getPaddingLeft(), containerView.getPaddingTop(),
                containerView.getPaddingRight(), 0);

        final int leftMarginPx
                = (int) getResources().getDimension(R.dimen.tw__tweet_share_margin_left);
        final RelativeLayout.LayoutParams layoutParams
                = new RelativeLayout.LayoutParams(shareButton.getLayoutParams());
        layoutParams.addRule(RelativeLayout.BELOW, R.id.tw__tweet_text);

        final int extraBottomMarginPx = (int) getResources().getDimension(
                R.dimen.tw__tweet_share_extra_bottom_margin);

        // If there is no text in the view (if it's only a pic.twitter link) we want to add some
        // extra margin in order to fix the visual spacing.
        if (TextUtils.isEmpty(contentView.getText())) {
            // set more padding on share button
            final int extraTopMarginPx
                    = (int) getResources().getDimension(R.dimen.tw__tweet_share_extra_top_margin);
            layoutParams.setMargins(leftMarginPx, extraTopMarginPx, 0, extraBottomMarginPx);
            shareButton.setLayoutParams(layoutParams);
        } else {
            layoutParams.setMargins(leftMarginPx, 0, 0, extraBottomMarginPx);
            shareButton.setLayoutParams(layoutParams);
        }
        shareButton.requestLayout();
    }

    private void setShareButtonGone() {
        shareButton.setVisibility(GONE);

        // restore original container bottom padding
        final int bottomPaddingPx
                = (int) getResources().getDimension(R.dimen.tw__tweet_container_padding_bottom);
        containerView.setPadding(containerView.getPaddingLeft(), containerView.getPaddingTop(),
                containerView.getPaddingRight(), bottomPaddingPx);
    }

    /**
     * Sets the verified check if the User is verified. If the User is not verified or if the
     * verification data is unavailable, sets the check visibility to gone.
     */
    private void setVerifiedCheck(Tweet tweet) {
        if (tweet != null && tweet.user != null && tweet.user.verified) {
            verifiedCheckView.setVisibility(ImageView.VISIBLE);
        } else {
            verifiedCheckView.setVisibility(ImageView.GONE);
        }
    }

    @Override
    String getViewTypeName() {
        return VIEW_TYPE_NAME;
    }
}
