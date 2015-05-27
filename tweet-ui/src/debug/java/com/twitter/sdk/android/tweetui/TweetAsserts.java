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

import android.content.res.Resources;

import com.twitter.sdk.android.core.internal.scribe.EventNamespace;

import junit.framework.Assert;

/**
 * Common shared utilities for testing.
 */
public final class TweetAsserts extends Assert {

    private static final String REQUIRED_TFW_SCRIBE_CLIENT = "tfw";
    private static final String REQUIRED_TFW_SCRIBE_PAGE = "android";
    private static final String REQUIRED_TFW_SCRIBE_SECTION = "tweet";
    private static final String REQUIRED_TFW_SCRIBE_ELEMENT = "";

    private static final String REQUIRED_SDK_SCRIBE_CLIENT = "android";
    private static final String REQUIRED_SDK_SCRIBE_PAGE = "tweet";
    private static final String REQUIRED_SDK_SCRIBE_COMPONENT = "";
    private static final String REQUIRED_SDK_SCRIBE_ELEMENT = "";

    private TweetAsserts() {}

    public static void assertDefaultColors(BaseTweetView view, Resources resources) {
        final int containerColor
                = resources.getColor(R.color.tw__tweet_light_container_bg_color);
        final int primaryTextColor
                = resources.getColor(R.color.tw__tweet_light_primary_text_color);

        assertEquals(containerColor, view.containerBgColor);
        assertEquals(primaryTextColor, view.primaryTextColor);
        assertEquals(primaryTextColor, view.contentView.getCurrentTextColor());
        assertEquals(primaryTextColor, view.fullNameView.getCurrentTextColor());
        assertEquals(R.drawable.tw__ic_tweet_photo_error_light, view.photoErrorResId);
        assertEquals(R.drawable.tw__ic_logo_blue, view.birdLogoResId);
    }

    public static void assertDarkColors(BaseTweetView view, Resources resources) {
        final int containerColor
                = resources.getColor(R.color.tw__tweet_dark_container_bg_color);
        final int primaryTextColor
                = resources.getColor(R.color.tw__tweet_dark_primary_text_color);

        assertEquals(containerColor, view.containerBgColor);
        assertEquals(primaryTextColor, view.primaryTextColor);
        assertEquals(primaryTextColor, view.contentView.getCurrentTextColor());
        assertEquals(primaryTextColor, view.fullNameView.getCurrentTextColor());
        assertEquals(R.drawable.tw__ic_tweet_photo_error_dark, view.photoErrorResId);
        assertEquals(R.drawable.tw__ic_logo_white, view.birdLogoResId);
    }

    protected static void assertConsistentTfwNamespaceValues(EventNamespace ns) {
        assertEquals(REQUIRED_TFW_SCRIBE_CLIENT, ns.client);
        assertEquals(REQUIRED_TFW_SCRIBE_PAGE, ns.page);
        assertEquals(REQUIRED_TFW_SCRIBE_SECTION, ns.section);
        assertEquals(REQUIRED_TFW_SCRIBE_ELEMENT, ns.element);
    }

    protected static void assertConsistentSdkNamespaceValues(EventNamespace ns) {
        assertEquals(REQUIRED_SDK_SCRIBE_CLIENT, ns.client);
        assertEquals(REQUIRED_SDK_SCRIBE_PAGE, ns.page);
        assertEquals(REQUIRED_SDK_SCRIBE_COMPONENT, ns.component);
        assertEquals(REQUIRED_SDK_SCRIBE_ELEMENT, ns.element);
    }
}
