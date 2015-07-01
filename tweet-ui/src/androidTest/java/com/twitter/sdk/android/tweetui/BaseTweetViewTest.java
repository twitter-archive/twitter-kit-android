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
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.FabricTestUtils;
import io.fabric.sdk.android.KitStub;
import io.fabric.sdk.android.Logger;

import com.twitter.sdk.android.core.internal.scribe.EventNamespace;
import com.twitter.sdk.android.core.internal.scribe.ScribeEvent;
import com.twitter.sdk.android.core.internal.scribe.SyndicatedSdkImpressionEvent;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetBuilder;

import org.mockito.ArgumentCaptor;

import java.util.Locale;

import static org.mockito.Mockito.*;

/**
 * Tests the state of BaseTweetViews created via constructors.
 */
public abstract class BaseTweetViewTest extends TweetUiTestCase {
    private static final String REQUIRED_SCRIBE_CLICK_ACTION = "click";
    private static final String REQUIRED_SCRIBE_IMPRESSION_ACTION = "impression";
    private static final String ANY_ADVERTISING_ID = "ANY_ID";
    private static final String REQUIRED_RETWEETED_BY_TEXT = "Retweeted by Mr Retweets";
    protected static final double DELTA = 0.001f;

    protected Context context;
    private Resources resources;
    private Locale defaultLocale;
    protected BaseTweetView.DependencyProvider mockDependencyProvider;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = Fabric.getKit(TweetUi.class).getContext();
        resources = context.getResources();
        defaultLocale = TestUtils.setLocale(getContext(), Locale.ENGLISH);
        setUpMockDependencyProvider();
    }

    @Override
    protected void tearDown() throws Exception {
        TestUtils.setLocale(getContext(), defaultLocale);
        scrubClass(BaseTweetViewTest.class);
        super.tearDown();
    }

    public Resources getResources() {
        return resources;
    }

    // constructor factories

    abstract BaseTweetView createView(Context context, Tweet tweet);

    abstract BaseTweetView createView(Context context, Tweet tweet, int styleResId);

    abstract BaseTweetView createViewInEditMode(Context context, Tweet tweet);

    abstract BaseTweetView createViewWithMocks(Context context, Tweet tweet);

    abstract BaseTweetView createViewWithMocks(Context context, Tweet tweet, int styleResId,
            BaseTweetView.DependencyProvider dependencyProvider);

    private void setUpMockDependencyProvider() {
        mockDependencyProvider = mock(TestDependencyProvider.class);
        when(mockDependencyProvider.getImageLoader())
                .thenReturn(TweetUi.getInstance().getImageLoader());
        when(mockDependencyProvider.getTweetUi()).thenReturn(TweetUi.getInstance());
    }

    // initialization

    public void testInit() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET);
        final long tweetId = TestFixtures.TEST_TWEET.id;
        assertEquals(tweetId, view.getTweetId());
        assertEquals(TestFixtures.TEST_NAME, view.fullNameView.getText().toString());
        assertEquals(TestFixtures.TEST_FORMATTED_SCREEN_NAME, view.screenNameView.getText());
        assertEquals(TestFixtures.TEST_STATUS, view.contentView.getText().toString());
        assertEquals(TestFixtures.TIMESTAMP_RENDERED, view.timestampView.getText().toString());
    }

    public void testInit_withEmptyTweet() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET);
        // recycle so we're not relying on first time defaults, fields should clear
        view.setTweet(TestFixtures.EMPTY_TWEET);
        assertEquals(TestFixtures.EMPTY_TWEET.id, view.getTweetId());
        assertEquals(TestFixtures.EMPTY_STRING, view.fullNameView.getText().toString());
        assertEquals(TestFixtures.EMPTY_STRING, view.screenNameView.getText().toString());
        assertEquals(TestFixtures.EMPTY_STRING, view.contentView.getText().toString());
        assertEquals(TestFixtures.EMPTY_STRING, view.timestampView.getText().toString());
    }

    public void testInit_withNullTweet() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET);
        // recycle so we're not relying on first time defaults, fields should clear
        view.setTweet(null);
        assertEquals(TestFixtures.EMPTY_TWEET.id, view.getTweetId());
        assertEquals(TestFixtures.EMPTY_STRING, view.fullNameView.getText().toString());
        assertEquals(TestFixtures.EMPTY_STRING, view.screenNameView.getText().toString());
        assertEquals(TestFixtures.EMPTY_STRING, view.contentView.getText().toString());
        assertEquals(TestFixtures.EMPTY_STRING, view.timestampView.getText().toString());
    }

    // setTweet with a Tweet with an invalid timestamp makes timestamp view show an empty string
    public void testInit_withInvalidTimestamp() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET);
        // recycle so we're not relying on first time defaults, timestamp should clear
        view.setTweet(TestFixtures.INVALID_TIMESTAMP_TWEET);
        assertEquals(TestFixtures.INVALID_TIMESTAMP_TWEET.id, view.getTweetId());
        assertEquals(TestFixtures.EMPTY_STRING, view.timestampView.getText().toString());
    }

    public void testInit_inEditMode() {
        FabricTestUtils.resetFabric();
        try {
            final Fabric fabric = new Fabric.Builder(getContext())
                    .debuggable(true)
                    .kits(new KitStub())
                    .build();
            FabricTestUtils.with(fabric);
            final BaseTweetView view = createViewInEditMode(context, TestFixtures.TEST_TWEET);
            assertTrue(view.isInEditMode());
            assertTrue(view.isEnabled());
        } catch (Exception e) {
            fail("Must start TweetUi... IllegalStateException should be caught");
        } finally {
            FabricTestUtils.resetFabric();
        }
    }

    public void testInit_tweetUiNotStarted() {
        FabricTestUtils.resetFabric();
        try {
            final Fabric fabric = new Fabric.Builder(getContext())
                    .debuggable(true)
                    .kits(new KitStub())
                    .logger(mock(Logger.class))
                    .build();
            FabricTestUtils.with(fabric);
            final BaseTweetView view = createView(getContext(), TestFixtures.TEST_TWEET);
            // asserts that view is disabled and "Must start TweetUi..." logged as an error
            assertFalse(view.isEnabled());
            final Logger logger = Fabric.getLogger();
            verify(logger).e(eq(TweetUi.LOGTAG), eq(TweetUi.NOT_STARTED_ERROR));
        } catch (Exception e) {
            fail("Must start TweetUi... IllegalStateException should be caught");
        } finally {
            FabricTestUtils.resetFabric();
        }
    }

    public void testIsTweetUiEnabled_withEditMode() {
        final BaseTweetView view = createView(getContext(), TestFixtures.TEST_TWEET);
        assertTrue(view.isTweetUiEnabled());
    }

    public void testIsTweetUiEnabled_inEditMode() {
        final BaseTweetView view = createViewInEditMode(getContext(), TestFixtures.TEST_TWEET);
        assertFalse(view.isTweetUiEnabled());
    }

    public void testIsTweetUiEnabled_tweetUiStarted() {
        final BaseTweetView view = new TweetView(getContext(), TestFixtures.TEST_TWEET);
        assertTrue(view.isTweetUiEnabled());
        assertTrue(view.isEnabled());
    }

    // Tests Date formatting reliant string, manually sets english and restores original locale
    public void testGetContentDescription_emptyTweet() {
        final Locale originalLocale = TestUtils.setLocale(getContext(), Locale.ENGLISH);
        final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET);
        view.setTweet(TestFixtures.EMPTY_TWEET);
        assertEquals(getResources().getString(R.string.tw__loading_tweet),
                view.getContentDescription());
        TestUtils.setLocale(getContext(), originalLocale);
    }

    // Tests Date formatting reliant string, manually sets english and restores original locale
    public void testGetContentDescription_fullTweet() {
        final Locale originalLocale = TestUtils.setLocale(getContext(), Locale.ENGLISH);

        final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET);
        assertTrue(TweetUtils.isTweetResolvable(view.tweet));
        assertEquals(TestFixtures.TEST_CONTENT_DESCRIPTION, view.getContentDescription());

        TestUtils.setLocale(getContext(), originalLocale);
    }

    // Permalink click

    // TODO: test scribePermalinkClick is called, without exposing scribePermalinkClick as protected

    public void testSetTweet_permalink() {
        final BaseTweetView view = createView(context, null);
        view.setTweet(TestFixtures.TEST_TWEET);
        assertEquals(TestFixtures.TEST_PERMALINK_ONE, view.getPermalinkUri().toString());
    }

    // permalinkUri should be null so the permalink launcher will be a NoOp
    public void testSetTweet_nullTweetPermalink() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET);
        view.setTweet(null);
        assertNull(view.getPermalinkUri());
    }

    public void testSetTweet_updatePermalink() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET);
        assertEquals(TestFixtures.TEST_PERMALINK_ONE, view.getPermalinkUri().toString());
        view.setTweet(TestFixtures.TEST_PHOTO_TWEET);
        assertEquals(TestFixtures.TEST_PERMALINK_TWO, view.getPermalinkUri().toString());
    }

    // Styling
    // light style (default)

    public void testStaticColorsDefault() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET);
        TweetAsserts.assertDefaultColors(view, getResources());
    }

    public void testSecondaryColorsDefault() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET);
        final int primaryTextColor = getResources().getColor(
                R.color.tw__tweet_light_primary_text_color);
        final int color = ColorUtils.calculateOpacityTransform(
                BaseTweetView.SECONDARY_TEXT_COLOR_LIGHT_OPACITY, Color.WHITE, primaryTextColor);
        assertEquals(color, view.secondaryTextColor);
        assertEquals(color, view.timestampView.getCurrentTextColor());
        assertEquals(color, view.screenNameView.getCurrentTextColor());
        assertEquals(color, view.retweetedByView.getCurrentTextColor());
    }

    public void testAvatarDefault() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET);
            final int containerColor = getResources().getColor(
                    R.color.tw__tweet_light_container_bg_color);
            final int color = ColorUtils.calculateOpacityTransform(
                    BaseTweetView.MEDIA_BG_LIGHT_OPACITY, Color.BLACK, containerColor);
            assertEquals(color, TestUtils.getDrawableColor(view.avatarView));
        }
    }

    public void testPhotoDefault() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final BaseTweetView view = createView(context, TestFixtures.TEST_PHOTO_TWEET);
            final int containerColor = getResources().getColor(
                    R.color.tw__tweet_light_container_bg_color);
            final int color = ColorUtils.calculateOpacityTransform(
                    BaseTweetView.MEDIA_BG_LIGHT_OPACITY, Color.BLACK, containerColor);
            assertEquals(color, TestUtils.getDrawableColor(view.mediaPhotoView));
        }
    }

    public void testTweetPhotoErrorDefault() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_PHOTO_TWEET);
        assertEquals(R.drawable.tw__ic_tweet_photo_error_light, view.photoErrorResId);
    }

    public void testRetweetIconDefault() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_RETWEET);
        assertEquals(R.drawable.tw__ic_retweet_light, view.retweetIconResId);
    }

    // dark style

    public void testStaticColorsDark() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET,
                R.style.tw__TweetDarkStyle);
        TweetAsserts.assertDarkColors(view, getResources());
    }

    public void testSecondaryColorsDark() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET,
                R.style.tw__TweetDarkStyle);
        final int primaryTextColor = getResources().getColor(
                R.color.tw__tweet_dark_primary_text_color);
        final int color = ColorUtils.calculateOpacityTransform(
                BaseTweetView.SECONDARY_TEXT_COLOR_DARK_OPACITY, Color.BLACK, primaryTextColor);
        assertEquals(color, view.secondaryTextColor);
        assertEquals(color, view.timestampView.getCurrentTextColor());
        assertEquals(color, view.screenNameView.getCurrentTextColor());
        assertEquals(color, view.retweetedByView.getCurrentTextColor());
    }

    public void testAvatarDark() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET,
                    R.style.tw__TweetDarkStyle);
            final int containerColor = getResources().getColor(
                    R.color.tw__tweet_dark_container_bg_color);
            final int color = ColorUtils.calculateOpacityTransform(
                    BaseTweetView.MEDIA_BG_DARK_OPACITY, Color.WHITE, containerColor);
            assertEquals(color, TestUtils.getDrawableColor(view.avatarView));
        }
    }

    public void testPhotoDark() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final BaseTweetView view = createView(context, TestFixtures.TEST_PHOTO_TWEET,
                    R.style.tw__TweetDarkStyle);
            final int containerColor = getResources().getColor(
                    R.color.tw__tweet_dark_container_bg_color);
            final int color = ColorUtils.calculateOpacityTransform(
                    BaseTweetView.MEDIA_BG_DARK_OPACITY, Color.WHITE, containerColor);
            assertEquals(color, TestUtils.getDrawableColor(view.mediaPhotoView));
        }
    }

    public void testTweetPhotoErrorDark() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET,
                R.style.tw__TweetDarkStyle);
        assertEquals(R.drawable.tw__ic_tweet_photo_error_dark, view.photoErrorResId);
    }

    public void testRetweetIconDark() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_RETWEET,
               R.style.tw__TweetDarkStyle);
        assertEquals(R.drawable.tw__ic_retweet_dark, view.retweetIconResId);
    }

    public void testTweetActionsEnabled() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_RETWEET,
                R.style.tw__TweetActionsEnabled);
        assertTrue(view.tweetActionsEnabled);
    }

    public void testTweetActionsDisabled() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_RETWEET,
                R.style.tw__TweetActionsDisabled);
        assertFalse(view.tweetActionsEnabled);
    }

    public void testGetAspectRatio_withNullMediaEntity() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET);

        assertEquals(BaseTweetView.DEFAULT_ASPECT_RATIO, view.getAspectRatio(null));
    }

    public void testGetAspectRatio_mediaEntityWithNullSizes() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET);
        final MediaEntity mediaEntity = TestFixtures.createMediaEntityWithSizes(null);

        assertEquals(BaseTweetView.DEFAULT_ASPECT_RATIO, view.getAspectRatio(mediaEntity));
    }

    public void testGetAspectRatio_mediaEntityWithEmptySizes() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET);
        final MediaEntity.Sizes sizes = new MediaEntity.Sizes(null, null, null, null);
        final MediaEntity mediaEntity = TestFixtures.createMediaEntityWithSizes(sizes);

        assertEquals(BaseTweetView.DEFAULT_ASPECT_RATIO, view.getAspectRatio(mediaEntity));
    }

    public void testGetAspectRatio_mediaEntityWithZeroDimension() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET);

        assertEquals(BaseTweetView.DEFAULT_ASPECT_RATIO,
                view.getAspectRatio(TestFixtures.createMediaEntityWithSizes(0, 0)));
        assertEquals(BaseTweetView.DEFAULT_ASPECT_RATIO,
                view.getAspectRatio(TestFixtures.createMediaEntityWithSizes(100, 0)));
        assertEquals(BaseTweetView.DEFAULT_ASPECT_RATIO,
                view.getAspectRatio(TestFixtures.createMediaEntityWithSizes(0, 100)));
    }

    // Scribing

    public void testSetTweet_scribesBothEventTypes() {
        final BaseTweetView view = setUpScribeTest();
        view.setTweet(TestFixtures.TEST_TWEET);
        assertBothScribeTypes();
    }

    private BaseTweetView setUpScribeTest() {
        TweetUi.getInstance().advertisingId = ANY_ADVERTISING_ID;
        TweetUi.getInstance().scribeClient = scribeClient;
        final Tweet tweet = new TweetBuilder().build();
        final BaseTweetView view = createView(context, tweet);
        reset(scribeClient);
        return view;
    }

    private void assertBothScribeTypes() {
        final ArgumentCaptor<ScribeEvent> eventCaptor
                = ArgumentCaptor.forClass(ScribeEvent.class);
        verify(scribeClient, times(2)).scribe(eventCaptor.capture());
        final SyndicationClientEvent capturedClientEvent =
                ((SyndicationClientEvent) eventCaptor.getAllValues().get(0));
        assertEquals(ANY_ADVERTISING_ID, capturedClientEvent.externalIds.adId);

        final SyndicatedSdkImpressionEvent capturedImpression =
                ((SyndicatedSdkImpressionEvent) eventCaptor.getAllValues().get(1));
        assertEquals(ANY_ADVERTISING_ID, capturedImpression.externalIds.adId);
    }

    public void testScribePermalinkClick_scribesBothEventTypes() {
        final BaseTweetView view = setUpScribeTest();

        view.scribePermalinkClick();

        assertBothScribeTypes();
    }

    public void testGetTfwImpressionNamespace() {
        final EventNamespace ns = createView(context, TestFixtures.TEST_TWEET)
                .getTfwEventImpressionNamespace();
        TweetAsserts.assertConsistentTfwNamespaceValues(ns);
        assertTfwScribeComponent(ns);
        assertEquals(REQUIRED_SCRIBE_IMPRESSION_ACTION, ns.action);
    }

    abstract void assertTfwScribeComponent(EventNamespace ns);

    public void testGetTfwClickNamespace() {
        final EventNamespace ns = createView(context, TestFixtures.TEST_TWEET)
                .getTfwEventClickNamespace();
        TweetAsserts.assertConsistentTfwNamespaceValues(ns);
        assertTfwScribeComponent(ns);
        assertEquals(REQUIRED_SCRIBE_CLICK_ACTION, ns.action);
    }

    public void testGetSdkImpressionNamespace() {
        final EventNamespace ns = createView(context, TestFixtures.TEST_TWEET)
                .getSyndicatedSdkImpressionNamespace();
        TweetAsserts.assertConsistentSdkNamespaceValues(ns);
        assertSdkScribeSection(ns);
        assertEquals(REQUIRED_SCRIBE_IMPRESSION_ACTION, ns.action);
    }

    abstract void assertSdkScribeSection(EventNamespace ns);

    public void testGetSdkClickNamespace() {
        final EventNamespace ns = createView(context, TestFixtures.TEST_TWEET)
                .getSyndicatedSdkClickNamespace();
        TweetAsserts.assertConsistentSdkNamespaceValues(ns);
        assertSdkScribeSection(ns);
        assertEquals(REQUIRED_SCRIBE_CLICK_ACTION, ns.action);
    }

    public void testSetErrorImage_handlesNullPicasso() {
        when(mockDependencyProvider.getImageLoader()).thenReturn(null);
        final BaseTweetView tweetView = createViewWithMocks(context, TestFixtures.TEST_TWEET,
                R.style.tw__TweetDarkStyle, mockDependencyProvider);

        try {
            tweetView.setErrorImage();
        } catch (NullPointerException e) {
            fail("Should have handled null error image");
        }
    }

    public void testSetProfilePhotoView_handlesNullPicasso() {
        when(mockDependencyProvider.getImageLoader()).thenReturn(null);

        final BaseTweetView tweetView = createViewWithMocks(context, TestFixtures.TEST_TWEET,
                R.style.tw__TweetDarkStyle, mockDependencyProvider);

        try {
            tweetView.setProfilePhotoView(TestFixtures.TEST_TWEET);
        } catch (NullPointerException e) {
            fail("Should have handled null error image");
        }
    }

    public void testSetTweetPhoto_handlesNullPicasso() {
        when(mockDependencyProvider.getImageLoader()).thenReturn(null);

        final BaseTweetView tweetView = createViewWithMocks(context, TestFixtures.TEST_TWEET,
                R.style.tw__TweetDarkStyle, mockDependencyProvider);

        try {
            tweetView.setTweetPhoto(mock(MediaEntity.class));
        } catch (NullPointerException e) {
            fail("Should have handled null error image");
        }
    }

    public void testRender_rendersRetweetedStatus() {
        final BaseTweetView tweetView = createViewWithMocks(context, null);
        tweetView.setTweet(TestFixtures.TEST_RETWEET);
        assertEquals(REQUIRED_RETWEETED_BY_TEXT, tweetView.retweetedByView.getText());
        assertEquals(TestFixtures.TEST_NAME, tweetView.fullNameView.getText());
        assertEquals(TestFixtures.TEST_FORMATTED_SCREEN_NAME, tweetView.screenNameView.getText());
        assertEquals(TestFixtures.TEST_STATUS, tweetView.contentView.getText().toString());
    }

    public void testSetRetweetedBy_nullTweet() {
        final BaseTweetView tweetView = createViewWithMocks(context, null);
        tweetView.setTweet(null);
        assertEquals(View.GONE, tweetView.retweetedByView.getVisibility());
    }

    public void testSetRetweetedBy_nonRetweet() {
        final BaseTweetView tweetView = createViewWithMocks(context, null);
        tweetView.setTweet(TestFixtures.TEST_TWEET);
        assertEquals(View.GONE, tweetView.retweetedByView.getVisibility());
    }

    public void testSetRetweetedBy_retweet() {
        final BaseTweetView tweetView = createViewWithMocks(context, null);
        tweetView.setTweet(TestFixtures.TEST_RETWEET);
        assertEquals(View.VISIBLE, tweetView.retweetedByView.getVisibility());
        assertEquals(REQUIRED_RETWEETED_BY_TEXT, tweetView.retweetedByView.getText());
    }
}
