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
import android.widget.ImageView;

import com.twitter.sdk.android.core.internal.scribe.EventNamespace;
import com.twitter.sdk.android.core.models.Tweet;

public class TweetViewTest extends BaseTweetViewTest {
    private static final String REQUIRED_TFW_SCRIBE_COMPONENT = "default";
    private static final String REQUIRED_SDK_SCRIBE_SECTION = "default";

    @Override
    TweetView createView(Context context, Tweet tweet) {
        return new TweetView(context, tweet);
    }

    @Override
    TweetView createView(Context context, Tweet tweet, int styleResId) {
        return new TweetView(context, tweet, styleResId);
    }

    @Override
    TweetView createViewInEditMode(Context context, Tweet tweet) {
        return new TweetView(context, tweet) {
            @Override
            public boolean isInEditMode() {
                return true;
            }
        };
    }

    @Override
    TweetView createViewWithMocks(Context context, Tweet tweet) {
        return new TweetView(context, tweet);
    }

    @Override
    TweetView createViewWithMocks(Context context, Tweet tweet, int styleResId,
            BaseTweetView.DependencyProvider dependencyProvider) {
        return new TweetView(context, tweet, styleResId, dependencyProvider);
    }

    // Initialization

    @Override
    public void testInit() {
        super.testInit();
        final TweetView view = createView(context, TestFixtures.TEST_TWEET);
        assertEquals(ImageView.GONE, view.mediaPhotoView.getVisibility());
    }

    @Override
    public void testInit_withEmptyTweet() {
        super.testInit();
        final TweetView view = createView(context, TestFixtures.TEST_TWEET);
        assertEquals(ImageView.GONE, view.mediaPhotoView.getVisibility());
    }

    public void testInit_withPhotoTweet() {
        final TweetView view = createView(context, TestFixtures.TEST_PHOTO_TWEET);
        assertEquals(ImageView.VISIBLE, view.mediaPhotoView.getVisibility());
    }

    // Layout

    public void testLayout() {
        final TweetView tweetView = new TweetView(context, TestFixtures.TEST_TWEET);
        assertEquals(R.layout.tw__tweet, tweetView.getLayout());
    }

    public void testActionColorDefault_withTweet() {
        final TweetView view = createView(context, TestFixtures.TEST_TWEET);
        final int color = getResources().getColor(R.color.tw__tweet_action_color);
        assertEquals(color, view.shareButton.getCurrentTextColor());
    }

    public void testGetAspectRatio() {
        final BaseTweetView view = createView(context, TestFixtures.TEST_TWEET);

        assertEquals(1, view.getAspectRatio(
                TestFixtures.createMediaEntityWithSizes(100, 100)), DELTA);
        assertEquals(.5, view.getAspectRatio(
                TestFixtures.createMediaEntityWithSizes(100, 200)), DELTA);
        assertEquals(2, view.getAspectRatio(
                TestFixtures.createMediaEntityWithSizes(200, 100)), DELTA);
    }

    // Scribing
    @Override
    void assertSdkScribeSection(EventNamespace ns) {
        assertEquals(REQUIRED_SDK_SCRIBE_SECTION, ns.section);
    }

    @Override
    void assertTfwScribeComponent(EventNamespace ns) {
        assertEquals(REQUIRED_TFW_SCRIBE_COMPONENT, ns.component);
    }
}
