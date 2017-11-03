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
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.IntentUtils;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.internal.UserUtils;
import com.twitter.sdk.android.core.internal.VineCardUtils;
import com.twitter.sdk.android.core.internal.scribe.ScribeItem;
import com.twitter.sdk.android.core.models.Card;
import com.twitter.sdk.android.core.models.ImageValue;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.internal.AspectRatioFrameLayout;
import com.twitter.sdk.android.tweetui.internal.MediaBadgeView;
import com.twitter.sdk.android.tweetui.internal.SpanClickHandler;
import com.twitter.sdk.android.tweetui.internal.TweetMediaUtils;
import com.twitter.sdk.android.tweetui.internal.TweetMediaView;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;


abstract class AbstractTweetView extends RelativeLayout{
    static final String TAG = TweetUi.LOGTAG;
    static final int DEFAULT_STYLE = R.style.tw__TweetLightStyle;
    static final String EMPTY_STRING = "";
    static final double DEFAULT_ASPECT_RATIO = 16.0 / 9.0;

    static final double SECONDARY_TEXT_COLOR_LIGHT_OPACITY = 0.4;
    static final double SECONDARY_TEXT_COLOR_DARK_OPACITY = 0.35;
    static final double MEDIA_BG_LIGHT_OPACITY = 0.08;
    static final double MEDIA_BG_DARK_OPACITY = 0.12;

    static final long INVALID_ID = -1L;

    // Dependency Provider
    final DependencyProvider dependencyProvider;

    // attributes
    private LinkClickListener linkClickListener;
    TweetLinkClickListener tweetLinkClickListener;
    TweetMediaClickListener tweetMediaClickListener;
    private Uri permalinkUri;
    Tweet tweet;

    // for testing
    int styleResId;
    boolean tweetActionsEnabled;

    // layout views
    TextView fullNameView;
    TextView screenNameView;
    AspectRatioFrameLayout mediaContainer;
    TweetMediaView tweetMediaView;
    TextView contentView;
    MediaBadgeView mediaBadgeView;

    // color values
    int primaryTextColor;
    int secondaryTextColor;
    int actionColor;
    int actionHighlightColor;
    int mediaBgColor;
    // resource id's
    int photoErrorResId;

    /**
     * Performs inflation from XML and apply a class-specific base style with the given dependency
     * provider.
     * @param context the context of the view
     * @param attrs the attributes of the XML tag that is inflating the TweetView
     * @param defStyle An attribute in the current theme that contains a reference to a style
     *                 resource to apply to this view. If 0, no default style will be applied.
     * @param dependencyProvider the dependency provider
     * @throws IllegalArgumentException if the Tweet id is invalid.
     */
    AbstractTweetView(Context context, AttributeSet attrs, int defStyle,
                  DependencyProvider dependencyProvider) {
        super(context, attrs, defStyle);

        this.dependencyProvider = dependencyProvider;
        inflateView(context);
        findSubviews();
    }

    /**
     * Inflate the TweetView using the layout that has been set.
     * @param context The Context the view is running in.
     */
    private void inflateView(Context context) {
        LayoutInflater.from(context).inflate(getLayout(), this, true);
    }

    /**
     * Checks whether the TweetUi kit is setup and the instance is available.
     * @return true if the instance is available and view creation can continue
     * or false otherwise
     */
    boolean isTweetUiEnabled() {
        // in edit mode, halt view creation
        if (isInEditMode()) return false;
        try {
            dependencyProvider.getTweetUi();
        } catch (IllegalStateException e) {
            Twitter.getLogger().e(TAG, e.getMessage());
            // TweetUi kit instance not available, halt view creation and disable
            setEnabled(false);
            return false;
        }
        return true;
    }

    /**
     * Find and hold subview references for quick lookup.
     */
    void findSubviews() {
        // Tweet attribution (avatar, name, screen name, etc.)
        fullNameView = findViewById(R.id.tw__tweet_author_full_name);
        screenNameView = findViewById(R.id.tw__tweet_author_screen_name);
        mediaContainer =
                findViewById(R.id.tw__aspect_ratio_media_container);
        tweetMediaView = findViewById(R.id.tweet_media_view);
        contentView = findViewById(R.id.tw__tweet_text);
        mediaBadgeView = findViewById(R.id.tw__tweet_media_badge);
    }

    /*
     * It's up to the extending class to determine what layout id to use
     */
    abstract int getLayout();

    /*
     * Gets the scribe namespace
     */
    abstract String getViewTypeName();

    /**
     * @return id of the Tweet of the TweetView.
     */
    public long getTweetId() {
        if (tweet == null) {
            return INVALID_ID;
        }
        return tweet.id;
    }

    /**
     * Set the Tweet to be displayed and update the subviews. For any data that is missing from
     * the Tweet, invalidate the subview value (e.g. text views set to empty string) for view
     * recycling. Cannot be called before inflation has completed.
     * @param tweet Tweet data
     */
    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
        render();
    }

    /**
     * @return the Tweet of the TweetView
     */
    public Tweet getTweet() {
        return tweet;
    }

    /**
     * Override the default action when media is clicked.
     * @param tweetMediaClickListener called when media is clicked.
     */
    public void setTweetMediaClickListener(TweetMediaClickListener tweetMediaClickListener) {
        this.tweetMediaClickListener = tweetMediaClickListener;
        tweetMediaView.setTweetMediaClickListener(tweetMediaClickListener);
    }

    /**
     * Override the default action when any link or entity is clicked.
     * @param tweetLinkClickListener called when any link or entity is clicked.
     */
    public void setTweetLinkClickListener(TweetLinkClickListener tweetLinkClickListener) {
        this.tweetLinkClickListener = tweetLinkClickListener;
    }

    /**
     * Render the Tweet by updating the subviews. For any data that is missing from the Tweet,
     * invalidate the subview value (e.g. text views set to empty string) for view recycling.
     * Do not call with render true until inflation has completed.
     */
    void render() {
        final Tweet displayTweet = TweetUtils.getDisplayTweet(tweet);
        setName(displayTweet);
        setScreenName(displayTweet);
        setTweetMedia(displayTweet);
        setText(displayTweet);
        setContentDescription(displayTweet);

        // set permalink if tweet id and screen name are available
        if (TweetUtils.isTweetResolvable(tweet)) {
            setPermalinkUri(tweet.user.screenName, getTweetId());
        } else {
            permalinkUri = null;
        }

        // set or update the permalink launcher with the current permalinkUri
        setPermalinkLauncher();
        scribeImpression();
    }

    Uri getPermalinkUri() {
        return permalinkUri;
    }

    void setPermalinkUri(String screenName, Long tweetId) {
        if (tweetId <= 0) return;
        permalinkUri = TweetUtils.getPermalink(screenName, tweetId);
    }

    private void setPermalinkLauncher() {
        final OnClickListener listener = new PermalinkClickListener();

        this.setOnClickListener(listener);
    }

    void launchPermalink() {
        final Intent intent = new Intent(Intent.ACTION_VIEW, getPermalinkUri());
        if (!IntentUtils.safeStartActivity(getContext(), intent)) {
            Twitter.getLogger().e(TweetUi.LOGTAG, "Activity cannot be found to open permalink URI");
        }
    }

    void scribeImpression() {
        if (tweet != null) {
            dependencyProvider.getTweetScribeClient().impression(tweet, getViewTypeName(),
                    tweetActionsEnabled);
        }
    }

    void scribePermalinkClick() {
        if (tweet != null) {
            dependencyProvider.getTweetScribeClient().click(tweet, getViewTypeName());
        }
    }

    void scribeCardImpression(Long tweetId, Card card) {
        final ScribeItem scribeItem = ScribeItem.fromTweetCard(tweetId, card);
        dependencyProvider.getVideoScribeClient().impression(scribeItem);
    }

    void scribeMediaEntityImpression(long tweetId, MediaEntity mediaEntity) {
        final ScribeItem scribeItem = ScribeItem.fromMediaEntity(tweetId, mediaEntity);
        dependencyProvider.getVideoScribeClient().impression(scribeItem);
    }

    /**
     * Sets the Tweet author name. If author name is unavailable, resets to empty string.
     */
    private void setName(Tweet displayTweet) {
        if (displayTweet != null && displayTweet.user != null) {
            fullNameView.setText(Utils.stringOrEmpty(displayTweet.user.name));
        } else {
            fullNameView.setText(EMPTY_STRING);
        }
    }

    /**
     * Sets the Tweet author screen name. If screen name is unavailable, resets to empty string.
     */
    private void setScreenName(Tweet displayTweet) {
        if (displayTweet != null && displayTweet.user != null) {
            screenNameView.setText(UserUtils.formatScreenName(
                    Utils.stringOrEmpty(displayTweet.user.screenName)));
        } else {
            screenNameView.setText(EMPTY_STRING);
        }
    }

    /**
     * Sets the Tweet text. If the Tweet text is unavailable, resets to empty string.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setText(Tweet displayTweet) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            contentView.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        }
        final CharSequence tweetText = Utils.charSeqOrEmpty(getLinkifiedText(displayTweet));
        SpanClickHandler.enableClicksOnSpans(contentView);
        if (!TextUtils.isEmpty(tweetText)) {
            contentView.setText(tweetText);
            contentView.setVisibility(VISIBLE);
        } else {
            contentView.setText(EMPTY_STRING);
            contentView.setVisibility(GONE);
        }
    }

    final void setTweetMedia(Tweet displayTweet) {
        clearTweetMedia();

        if (displayTweet == null) {
            return;
        }

        if (displayTweet.card != null && VineCardUtils.isVine(displayTweet.card)) {
            final Card card = displayTweet.card;
            final ImageValue imageValue = VineCardUtils.getImageValue(card);
            final String playerStreamUrl = VineCardUtils.getStreamUrl(card);
            // Make sure we have required bindings for Vine card
            if (imageValue != null && !TextUtils.isEmpty(playerStreamUrl)) {
                setViewsForMedia(getAspectRatio(imageValue));
                tweetMediaView.setVineCard(displayTweet);
                mediaBadgeView.setVisibility(View.VISIBLE);
                mediaBadgeView.setCard(card);
                scribeCardImpression(displayTweet.id, card);
            }
        } else if (TweetMediaUtils.hasSupportedVideo(displayTweet)) {
            final MediaEntity mediaEntity = TweetMediaUtils.getVideoEntity(displayTweet);
            setViewsForMedia(getAspectRatio(mediaEntity));
            tweetMediaView.setTweetMediaEntities(tweet, Collections.singletonList(mediaEntity));
            mediaBadgeView.setVisibility(View.VISIBLE);
            mediaBadgeView.setMediaEntity(mediaEntity);
            scribeMediaEntityImpression(displayTweet.id, mediaEntity);
        } else if (TweetMediaUtils.hasPhoto(displayTweet)) {
            final List<MediaEntity> mediaEntities = TweetMediaUtils.getPhotoEntities(displayTweet);
            setViewsForMedia(getAspectRatioForPhotoEntity(mediaEntities.size()));
            tweetMediaView.setTweetMediaEntities(displayTweet, mediaEntities);
            mediaBadgeView.setVisibility(View.GONE);
        }
    }

    void setViewsForMedia(double aspectRatio) {
        mediaContainer.setVisibility(ImageView.VISIBLE);
        mediaContainer.setAspectRatio(aspectRatio);
        tweetMediaView.setVisibility(View.VISIBLE);
    }

    protected double getAspectRatio(MediaEntity photoEntity) {
        if (photoEntity == null || photoEntity.sizes == null || photoEntity.sizes.medium == null ||
                photoEntity.sizes.medium.w == 0 || photoEntity.sizes.medium.h == 0) {
            return DEFAULT_ASPECT_RATIO;
        }

        return (double) photoEntity.sizes.medium.w / photoEntity.sizes.medium.h;
    }

    protected double getAspectRatio(ImageValue imageValue) {
        if (imageValue == null || imageValue.width == 0 || imageValue.height == 0) {
            return DEFAULT_ASPECT_RATIO;
        }

        return (double) imageValue.width / imageValue.height;
    }

    protected abstract double getAspectRatioForPhotoEntity(int photoCount);

    protected void clearTweetMedia() {
        mediaContainer.setVisibility(ImageView.GONE);
    }

    /**
     * @param displayTweet The unformatted Tweet
     * @return The linkified text with display url's subbed for t.co links
     */
    protected CharSequence getLinkifiedText(Tweet displayTweet) {
        final FormattedTweetText formattedText = dependencyProvider.getTweetUi()
                .getTweetRepository().formatTweetText(displayTweet);

        if (formattedText == null) return null;

        final boolean stripVineCard = displayTweet.card != null
                && VineCardUtils.isVine(displayTweet.card);

        final boolean stripQuoteTweet = TweetUtils.showQuoteTweet(displayTweet);

        return TweetTextLinkifier.linkifyUrls(formattedText, getLinkClickListener(), actionColor,
                actionHighlightColor, stripQuoteTweet, stripVineCard);
    }

    void setContentDescription(Tweet displayTweet) {
        if (!TweetUtils.isTweetResolvable(displayTweet)) {
            setContentDescription(getResources().getString(R.string.tw__loading_tweet));
            return;
        }

        final FormattedTweetText formattedTweetText = dependencyProvider.getTweetUi()
                .getTweetRepository().formatTweetText(displayTweet);
        String tweetText = null;
        if (formattedTweetText != null) tweetText = formattedTweetText.text;

        final long createdAt = TweetDateUtils.apiTimeToLong(displayTweet.createdAt);
        String timestamp = null;
        if (createdAt != TweetDateUtils.INVALID_DATE) {
            timestamp = DateFormat.getDateInstance().format(new Date(createdAt));
        }

        setContentDescription(getResources().getString(R.string.tw__tweet_content_description,
                Utils.stringOrEmpty(displayTweet.user.name), Utils.stringOrEmpty(tweetText),
                Utils.stringOrEmpty(timestamp)));
    }

    protected LinkClickListener getLinkClickListener() {
        if (linkClickListener == null) {
            linkClickListener = url -> {
                if (TextUtils.isEmpty(url)) return;

                if (tweetLinkClickListener != null) {
                    tweetLinkClickListener.onLinkClick(tweet, url);
                } else {
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    if (!IntentUtils.safeStartActivity(getContext(), intent)) {
                        Twitter.getLogger().e(TweetUi.LOGTAG,
                                "Activity cannot be found to open URL");
                    }
                }
            };
        }
        return linkClickListener;
    }

    class PermalinkClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (getPermalinkUri() == null) return;

            scribePermalinkClick();
            launchPermalink();
        }
    }

    /**
     * This is a mockable class that extracts our tight coupling with the TweetUi singleton.
     */
    static class DependencyProvider {
        TweetScribeClient tweetScribeClient;
        VideoScribeClient videoScribeClient;

        /**
         * Can be null in edit mode
         */
        TweetUi getTweetUi() {
            return TweetUi.getInstance();
        }

        TweetScribeClient getTweetScribeClient() {
            if (tweetScribeClient == null) {
                tweetScribeClient = new TweetScribeClientImpl(getTweetUi());
            }
            return tweetScribeClient;
        }

        VideoScribeClient getVideoScribeClient() {
            if (videoScribeClient == null) {
                videoScribeClient = new VideoScribeClientImpl(getTweetUi());
            }
            return videoScribeClient;
        }

        /**
         * Can be null if run before TweetUi#doInBackground completes
         */
        Picasso getImageLoader() {
            return TweetUi.getInstance().getImageLoader();
        }
    }
}
