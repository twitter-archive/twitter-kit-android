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
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import io.fabric.sdk.android.Fabric;

import com.twitter.sdk.android.core.internal.scribe.EventNamespace;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.scribe.SyndicatedSdkImpressionEvent;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetBuilder;
import com.twitter.sdk.android.tweetui.internal.util.AspectRatioImageView;

import java.text.DateFormat;
import java.util.Date;

@SuppressWarnings({"TooManyMethods", "TooManyFields"})
public abstract class BaseTweetView extends LinearLayout {
    private static final String TAG = TweetUi.LOGTAG;
    private static final int DEFAULT_STYLE = R.style.tw__TweetLightStyle;
    private static final String EMPTY_STRING = "";
    static final double DEFAULT_ASPECT_RATIO = 16.0 / 9.0;

    static final double SECONDARY_TEXT_COLOR_LIGHT_OPACITY = 0.4;
    static final double SECONDARY_TEXT_COLOR_DARK_OPACITY = 0.35;
    static final double MEDIA_BG_LIGHT_OPACITY = 0.08;
    static final double MEDIA_BG_DARK_OPACITY = 0.12;

    static final long INVALID_ID = -1L;

    // tfw client event specific names
    private static final String TFW_CLIENT_EVENT_PAGE = "android";
    private static final String TFW_CLIENT_EVENT_SECTION = "tweet";
    private static final String TFW_CLIENT_EVENT_ELEMENT = ""; // intentionally blank

    // syndicated sdk impression specific names
    private static final String SYNDICATED_SDK_IMPRESSION_PAGE = "tweet";
    private static final String SYNDICATED_SDK_IMPRESSION_COMPONENT = "";
    private static final String SYNDICATED_SDK_IMPRESSION_ELEMENT = ""; // intentionally blank

    // general names
    private static final String SCRIBE_CLICK_ACTION = "click";
    private static final String SCRIBE_IMPRESSION_ACTION = "impression";

    // Dependency Provider
    final DependencyProvider dependencyProvider;

    // attributes
    private LinkClickListener linkClickListener;
    private Uri permalinkUri;
    Tweet tweet;

    // layout views
    RelativeLayout containerView;
    ImageView avatarView;
    TextView fullNameView;
    TextView screenNameView;
    ImageView verifiedCheckView;
    AspectRatioImageView mediaPhotoView;
    TextView contentView;
    TextView timestampView;
    ImageView twitterLogoView;
    TextView retweetedByView;

    // color values
    int containerBgColor;
    int primaryTextColor;
    int secondaryTextColor;
    int actionColor;
    int mediaBgColor;
    // resource id's
    int photoErrorResId;
    int birdLogoResId;
    int retweetIconResId;
    // styled drawables for images
    ColorDrawable mediaBg;

    /**
     * Constructs a view from the given Tweet.
     * @param context the context of the view
     * @param tweet a Tweet object
     */
    BaseTweetView(Context context, Tweet tweet) {
        this(context, tweet, DEFAULT_STYLE);
    }

    /**
     * Constructs a view from the given Tweet.
     * @param context the context of the view
     * @param tweet a Tweet object
     * @param styleResId resource id of the Tweet view style
     */
    BaseTweetView(Context context, Tweet tweet, int styleResId) {
        this(context, tweet, styleResId, new DependencyProvider());
    }

    /**
     * Constructs a view from the given Tweet using the given dependency provider
     * @param context the context of the view
     * @param tweet a Tweet object
     * @param styleResId resource id of the Tweet view style
     * @param dependencyProvider the dependency provider
     */
    BaseTweetView(Context context, Tweet tweet, int styleResId,
            DependencyProvider dependencyProvider) {
        super(context, null);

        this.dependencyProvider = dependencyProvider;
        initAttributes(styleResId);
        inflateView(context);
        findSubviews();
        applyStyles();
        if (!isTweetUiEnabled()) return;
        setTweet(tweet);
    }

    /* XML Constructors */

    /**
     * Constructs a view with data retrieved from the API, for the XML defined tweetId.
     * @param context the context of the view
     * @param attrs the attributes of the XML tag that is inflating the TweetView.
     * @throws IllegalArgumentException if the Tweet id is invalid.
     */
    public BaseTweetView(Context context, AttributeSet attrs) {
        this(context, attrs, new DependencyProvider());
    }

    /**
     * Constructs a view from xml with the given dependency provider
     * @param context the context of the view
     * @param attrs the attributes of the XML tag that is inflating the TweetView
     * @param dependencyProvider the dependency provider
     * @throws java.lang.IllegalAccessError if the Tweet id is invalid
     */
    BaseTweetView(Context context, AttributeSet attrs, DependencyProvider dependencyProvider) {
        super(context, attrs);
        this.dependencyProvider = dependencyProvider;
        initXmlAttributes(context, attrs);
        inflateView(context);
    }

    /**
     * Performs inflation from XML and apply a class-specific base style.
     * @param context the context of the view
     * @param attrs the attributes of the XML tag that is inflating the TweetView.
     * @param defStyle An attribute in the current theme that contains a reference to a style
     *                 resource to apply to this view. If 0, no default style will be applied.
     * @throws IllegalArgumentException if the Tweet id is invalid.
     */
    public BaseTweetView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, new DependencyProvider());
    }

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
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    BaseTweetView(Context context, AttributeSet attrs, int defStyle,
            DependencyProvider dependencyProvider) {
        super(context, attrs, defStyle);
        this.dependencyProvider = dependencyProvider;
        initXmlAttributes(context, attrs);
        inflateView(context);
    }

    /**
     * Initializes attributes needed before view inflation. This initializer should be called by the
     * programmatic constructors. For programmatic Tweet views, the style is passed as a constructor
     * argument.
     */
    private void initAttributes(int styleResId) {
        final TypedArray a = getContext().getTheme().obtainStyledAttributes(styleResId,
                R.styleable.tw__TweetView);
        try {
            setStyleAttributes(a);
        } finally {
            a.recycle();
        }
    }

    /**
     * Initializes XML attributes needed before view inflation. This initializer should be called
     * by the XML constructor. For XML Tweet views, the style is obtained from XML attrs.
     * @param context the context of the view
     * @param attrs set of raw XML attributes associated with the view's XML tag
     * @throws IllegalArgumentException if the tw__tweet_id XML attribute is invalid
     */
    private void initXmlAttributes(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        // parse the xml attributes by resolving resource references
        final TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.tw__TweetView, 0, 0);
        try {
            setXmlDataAttributes(a);
            setStyleAttributes(a);
        } finally {
            a.recycle();
        }
    }

    /**
     * Parses and sets the Tweet data XML attributes. Must be called before view inflation.
     * @param a A TypedArray holding the attribute values obtained from the XML attributes
     * @throws IllegalArgumentException if the tw__tweet_id XML attribute is invalid
     */
    private void setXmlDataAttributes(TypedArray a) {
        final long tweetId = Utils.numberOrDefault(
                a.getString(R.styleable.tw__TweetView_tw__tweet_id), INVALID_ID);
        if (tweetId <= 0) {
            throw new IllegalArgumentException("Invalid tw__tweet_id");
        }
        // XML special case. The screen_name is not known yet. A permalink can be constructed and
        // followed. Permalink should be updated once the loadTweet call receives the Tweet.
        setPermalinkUri(null, tweetId);
        this.tweet = new TweetBuilder().setId(tweetId).build();
    }

    /**
     * Parses and sets style attributes. Must be called before view inflation. Defaults style
     * attributes to the light style values.
     * @param a A TypedArray holding style-related attribute values.
     */
    private void setStyleAttributes(TypedArray a) {
        // Styled via attributes
        containerBgColor = a.getColor(R.styleable.tw__TweetView_tw__container_bg_color,
                getResources().getColor(R.color.tw__tweet_light_container_bg_color));
        primaryTextColor = a.getColor(R.styleable.tw__TweetView_tw__primary_text_color,
                getResources().getColor(R.color.tw__tweet_light_primary_text_color));
        actionColor = a.getColor(
                R.styleable.tw__TweetView_tw__action_color,
                getResources().getColor(R.color.tw__tweet_action_color));

        // Calculated colors
        final boolean isLightBg = ColorUtils.isLightColor(containerBgColor);

        if (isLightBg) {
            photoErrorResId = R.drawable.tw__ic_tweet_photo_error_light;
            birdLogoResId = R.drawable.tw__ic_logo_blue;
            retweetIconResId = R.drawable.tw__ic_retweet_light;
        } else {
            photoErrorResId = R.drawable.tw__ic_tweet_photo_error_dark;
            birdLogoResId = R.drawable.tw__ic_logo_white;
            retweetIconResId = R.drawable.tw__ic_retweet_dark;
        }

        // offset from white when background is light
        secondaryTextColor = ColorUtils.calculateOpacityTransform(
                isLightBg ? SECONDARY_TEXT_COLOR_LIGHT_OPACITY : SECONDARY_TEXT_COLOR_DARK_OPACITY,
                isLightBg ? Color.WHITE : Color.BLACK,
                primaryTextColor
        );

        // offset from black when background is light
        mediaBgColor = ColorUtils.calculateOpacityTransform(
                isLightBg ? MEDIA_BG_LIGHT_OPACITY : MEDIA_BG_DARK_OPACITY,
                isLightBg ? Color.BLACK : Color.WHITE,
                containerBgColor
        );

        mediaBg = new ColorDrawable(mediaBgColor);
    }

    /**
     * Inflate the TweetView using the layout that has been set.
     * @param context
     */
    private void inflateView(Context context) {
        final LayoutInflater localInflater = LayoutInflater.from(context);
        final View v = localInflater.inflate(getLayout(), null, false);
        // work around a bug(?) in Android that makes it so that our inflated view doesn't
        // pick up layout params correctly from its style
        final LayoutParams layoutParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);
        this.addView(v);
    }

    /**
     * Finalize inflating a view from XML.
     * @throws IllegalArgumentException
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isTweetUiEnabled()) return;
        findSubviews();
        applyStyles();
        loadTweet();
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
            Fabric.getLogger().e(TAG, e.getMessage());
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
        containerView = (RelativeLayout) findViewById(R.id.tw__tweet_view);
        avatarView = (ImageView) findViewById(R.id.tw__tweet_author_avatar);
        fullNameView = (TextView) findViewById(R.id.tw__tweet_author_full_name);
        screenNameView = (TextView) findViewById(R.id.tw__tweet_author_screen_name);
        verifiedCheckView = (ImageView) findViewById(R.id.tw__tweet_author_verified);
        mediaPhotoView = (AspectRatioImageView) findViewById(R.id.tw__tweet_media);
        contentView = (TextView) findViewById(R.id.tw__tweet_text);
        timestampView = (TextView) findViewById(R.id.tw__tweet_timestamp);
        twitterLogoView = (ImageView) findViewById(R.id.tw__twitter_logo);
        retweetedByView = (TextView) findViewById(R.id.tw__tweet_retweeted_by);
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
     * @throws IllegalArgumentException
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
     * Render the Tweet by updating the subviews. For any data that is missing from the Tweet,
     * invalidate the subview value (e.g. text views set to empty string) for view recycling.
     * Do not call with render true until inflation has completed.
     * @throws IllegalArgumentException
     */
    void render() {
        final Tweet displayTweet = TweetUtils.getDisplayTweet(tweet);
        setProfilePhotoView(displayTweet);
        setName(displayTweet);
        setScreenName(displayTweet);
        setTimestamp(displayTweet);
        setTweetPhoto(displayTweet);
        setText(displayTweet);
        setContentDescription(displayTweet);
        showRetweetedBy(tweet);

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

    /**
     * LoadTweet will trigger a request to the Twitter API and hydrate the view with the result.
     * In the event of an error it will call the listener that was provided to setOnTwitterApiError.
     * @throws java.lang.IllegalStateException If there is no auth configured for the Twitter API
     */
    private void loadTweet() {
        final long tweetId = getTweetId();
        // create a callback to setTweet on the view or log a failure to load the Tweet
        final LoadCallback<Tweet> repoCb = new LoadCallback<Tweet>() {
            @Override
            public void success(Tweet tweet) {
                setTweet(tweet);
            }

            @Override
            public void failure(TwitterException e) {
                Fabric.getLogger().d(TAG, String.format(TweetUtils.LOAD_TWEET_DEBUG, tweetId));
            }
        };
        dependencyProvider.getTweetUi().getTweetRepository().loadTweet(getTweetId(), repoCb);
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
        // Set the listener on the text so just plain text will still launch the permalink
        contentView.setOnClickListener(listener);
    }

    /**
     * Toggles display of "Retweeted by" text based on status from the API.
     * @param tweet The status from the API, if it is a retweet show the "retweeted by" text
     */
    void showRetweetedBy(Tweet tweet) {
        if (tweet == null || tweet.retweetedStatus == null) {
            retweetedByView.setVisibility(GONE);
        } else {
            retweetedByView.setText(
                    getResources().getString(R.string.tw__retweeted_by_format, tweet.user.name));
            retweetedByView.setVisibility(VISIBLE);
        }
    }

    void launchPermalink() {
        final Intent intent = new Intent(Intent.ACTION_VIEW, getPermalinkUri());
        getContext().startActivity(intent);
    }

    void scribeImpression() {
        dependencyProvider.getTweetUi().scribe(getTfwEventImpressionNamespace(),
                getSyndicatedSdkImpressionNamespace());
    }

    EventNamespace getTfwEventImpressionNamespace() {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(TFW_CLIENT_EVENT_SECTION)
                .setComponent(getViewTypeName())
                .setElement(TFW_CLIENT_EVENT_ELEMENT)
                .setAction(SCRIBE_IMPRESSION_ACTION)
                .builder();
    }

    EventNamespace getSyndicatedSdkImpressionNamespace() {
        return new EventNamespace.Builder()
                .setClient(SyndicatedSdkImpressionEvent.CLIENT_NAME)
                .setPage(SYNDICATED_SDK_IMPRESSION_PAGE)
                .setSection(getViewTypeName())
                .setComponent(SYNDICATED_SDK_IMPRESSION_COMPONENT)
                .setElement(SYNDICATED_SDK_IMPRESSION_ELEMENT)
                .setAction(SCRIBE_IMPRESSION_ACTION)
                .builder();
    }

    void scribePermalinkClick() {
        dependencyProvider.getTweetUi()
                .scribe(getTfwEventClickNamespace(), getSyndicatedSdkClickNamespace());
    }

    EventNamespace getTfwEventClickNamespace() {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(TFW_CLIENT_EVENT_SECTION)
                .setComponent(getViewTypeName())
                .setElement(TFW_CLIENT_EVENT_ELEMENT)
                .setAction(SCRIBE_CLICK_ACTION)
                .builder();
    }

    EventNamespace getSyndicatedSdkClickNamespace() {
        return new EventNamespace.Builder()
                .setClient(SyndicatedSdkImpressionEvent.CLIENT_NAME)
                .setPage(SYNDICATED_SDK_IMPRESSION_PAGE)
                .setSection(getViewTypeName())
                .setComponent(SYNDICATED_SDK_IMPRESSION_COMPONENT)
                .setElement(SYNDICATED_SDK_IMPRESSION_ELEMENT)
                .setAction(SCRIBE_CLICK_ACTION)
                .builder();
    }

    /**
     * Apply the style attributes to the Tweet subviews. Must be called after view inflation and
     * findSubviews.
     */
    protected void applyStyles() {
        containerView.setBackgroundColor(containerBgColor);
        avatarView.setImageDrawable(mediaBg);
        mediaPhotoView.setImageDrawable(mediaBg);
        fullNameView.setTextColor(primaryTextColor);
        screenNameView.setTextColor(secondaryTextColor);
        contentView.setTextColor(primaryTextColor);
        timestampView.setTextColor(secondaryTextColor);
        twitterLogoView.setImageResource(birdLogoResId);
        retweetedByView.setTextColor(secondaryTextColor);
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
        contentView.setMovementMethod(LinkMovementMethod.getInstance());
        // We need to reset the focus flags on the text view here since setting the movement
        // method has set the flags to an undesirable state
        contentView.setFocusable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            contentView.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        }
        final CharSequence tweetText = Utils.charSeqOrEmpty(getLinkifiedText(displayTweet));
        if (!TextUtils.isEmpty(tweetText)) {
            contentView.setText(tweetText);
            contentView.setVisibility(VISIBLE);
        } else {
            contentView.setText(EMPTY_STRING);
            contentView.setVisibility(GONE);
        }
    }

    /**
     * Set the timestamp if data from the Tweet is available. If timestamp cannot be determined,
     * set the timestamp to an empty string to handle view recycling.
     */
    private void setTimestamp(Tweet displayTweet) {
        final String formattedTimestamp;
        if (displayTweet != null && displayTweet.createdAt != null &&
                TweetDateUtils.isValidTimestamp(displayTweet.createdAt)) {
            final Long createdAtTimestamp
                    = TweetDateUtils.apiTimeToLong(displayTweet.createdAt);
            final String timestamp = TweetDateUtils.getRelativeTimeString(getResources(),
                    System.currentTimeMillis(),
                    createdAtTimestamp);
            formattedTimestamp = TweetDateUtils.dotPrefix(timestamp);
        } else {
            formattedTimestamp = EMPTY_STRING;
        }

        timestampView.setText(formattedTimestamp);
    }

    /**
     * Sets the profile photo. If the profile photo url is available from the Tweet, sets the the
     * default avatar background and attempts to load the image. If the url is not available, just
     * sets the default avatar background. Setting the default background upfront handles view
     * recycling.
     */
    void setProfilePhotoView(Tweet displayTweet) {
        final Picasso imageLoader = dependencyProvider.getImageLoader();

        if (imageLoader == null) return;

        final String url;
        if (displayTweet == null || displayTweet.user == null) {
            url = null;
        } else {
            url = UserUtils.getProfileImageUrlHttps(displayTweet.user,
                    UserUtils.AvatarSize.REASONABLY_SMALL);
        }

        imageLoader.load(url).placeholder(mediaBg).into(avatarView);
    }

    /**
     * Sets the Tweet photo. If the media url is available from the Tweet, sets the photo background
     * and attempts to load the image. If the load fails, the styled photo error image is set. If
     * the url is not available, sets the Tweet photo visibility to gone.
     */
    final void setTweetPhoto(Tweet displayTweet) {
        clearMediaBackground();

        if (displayTweet != null && TweetTextUtils.hasPhotoUrl(displayTweet.entities)) {
            final MediaEntity photoEntity
                    = TweetTextUtils.getLastPhotoEntity(displayTweet.entities);
            // set the image view to visible before setting via picasso placeholders into so
            // measurements are done correctly, fixes a bug where the placeholder was a small square
            // in the corner of the view
            mediaPhotoView.setVisibility(ImageView.VISIBLE);
            setTweetPhoto(photoEntity);
        } else {
            mediaPhotoView.setVisibility(ImageView.GONE);
        }
    }

    void setTweetPhoto(MediaEntity photoEntity) {
        final Picasso imageLoader = dependencyProvider.getImageLoader();

        if (imageLoader == null) return;

        // Picasso fit is a deferred call to resize(w,h) which waits until the target has a
        // non-zero width or height and resizes the bitmap to the target's width and height.
        // For recycled targets, which already have a width and (stale) height, reset the size
        // target to zero so Picasso fit works correctly.
        mediaPhotoView.resetSize();
        mediaPhotoView.setAspectRatio(getAspectRatio(photoEntity));
        imageLoader.load(photoEntity.mediaUrlHttps)
                .placeholder(mediaBg)
                .fit()
                .centerCrop()
                .into(mediaPhotoView, new PicassoCallback());
    }

    protected double getAspectRatio(MediaEntity photoEntity) {
        if (photoEntity == null || photoEntity.sizes == null || photoEntity.sizes.medium == null ||
                photoEntity.sizes.medium.w == 0 || photoEntity.sizes.medium.h == 0) {
            return DEFAULT_ASPECT_RATIO;
        }

        return (double) photoEntity.sizes.medium.w / photoEntity.sizes.medium.h;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void clearMediaBackground() {
        // Clear out the background behind any potential error images that we had
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mediaPhotoView.setBackground(null);
        } else {
            mediaPhotoView.setBackgroundDrawable(null);
        }
    }

    /**
     * Picasso Callback which asynchronously sets the error bitmap onError.
     */
    class PicassoCallback implements Callback {
        @Override
        public void onSuccess() { /* intentionally blank */ }

        @Override
        public void onError() {
            setErrorImage();
        }
    }

    protected void setErrorImage() {
        // async load the error image and set the proper background color behind it once it's loaded
        // this does incur the necessity of clearing the background on each load of an image however
        final Picasso imageLoader = dependencyProvider.getImageLoader();

        if (imageLoader == null) return;

        imageLoader.load(photoErrorResId)
                .into(mediaPhotoView, new Callback() {
                    @Override
                    public void onSuccess() {
                        mediaPhotoView.setBackgroundColor(mediaBgColor);
                    }

                    @Override
                    public void onError() { /* intentionally blank */ }
                });
    }

    /**
     * @param displayTweet The unformatted Tweet
     * @return The linkified text with display url's subbed for t.co links
     */
    protected CharSequence getLinkifiedText(Tweet displayTweet) {
        final FormattedTweetText formattedText = dependencyProvider.getTweetUi()
                .getTweetRepository().formatTweetText(displayTweet);

        if (formattedText == null) return null;

        final boolean stripPhotoEntity = TweetTextUtils.hasPhotoUrl(displayTweet.entities);

        return TweetTextLinkifier.linkifyUrls(formattedText, getLinkClickListener(),
                stripPhotoEntity, actionColor);
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
            linkClickListener = new LinkClickListener() {
                @Override
                public void onUrlClicked(String url) {
                    if (TextUtils.isEmpty(url)) return;

                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getContext().startActivity(intent);
                }

                @Override
                public void onPhotoClicked(MediaEntity mediaEntity) {
                    // Does nothing
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
        /**
         * Can be null in edit mode
         */
        TweetUi getTweetUi() {
            return TweetUi.getInstance();
        }

        /**
         * Can be null if run before TweetUi#doInBackground completes
         */
        Picasso getImageLoader() {
            return TweetUi.getInstance().getImageLoader();
        }

        /*
         * TODO: Reimplement getTweetRepository, currently unable to implement because of class
         * visibility issues with mocking on the Dalvik and ART
         */
    }
}
