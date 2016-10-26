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

import com.twitter.sdk.android.core.models.Tweet;

public class QuoteTweetViewTest extends AbstractTweetViewTest {
    protected static final double DELTA = 0.001f;

    @Override
    QuoteTweetView createView(Context context, Tweet tweet) {
        final QuoteTweetView quoteTweetView = new QuoteTweetView(context);
        quoteTweetView.setTweet(tweet);
        return quoteTweetView;
    }

    @Override
    QuoteTweetView createViewInEditMode(Context context, Tweet tweet) {
        final QuoteTweetView quoteTweetView = new QuoteTweetView(context) {
            @Override
            public boolean isInEditMode() {
                return true;
            }
        };

        return quoteTweetView;
    }

    @Override
    QuoteTweetView createViewWithMocks(Context context, Tweet tweet) {
        final QuoteTweetView quoteTweetView = new QuoteTweetView(context);
        quoteTweetView.setTweet(tweet);
        return quoteTweetView;
    }

    @Override
    QuoteTweetView createViewWithMocks(Context context, Tweet tweet,
                                         BaseTweetView.DependencyProvider dependencyProvider) {
        final QuoteTweetView quoteTweetView = new QuoteTweetView(context, dependencyProvider);
        quoteTweetView.setTweet(tweet);
        return quoteTweetView;
    }

    public void testGetAspectRatio() {
        final QuoteTweetView quoteTweetView = createView(context, TestFixtures.TEST_TWEET);

        assertEquals(1.0, quoteTweetView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(100, 100)), DELTA);
        assertEquals(1.0, quoteTweetView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(300, 400)), DELTA);
        assertEquals(1.0, quoteTweetView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(100, 800)), DELTA);
        assertEquals(1.3333, quoteTweetView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(400, 300)), DELTA);
        assertEquals(1.6666, quoteTweetView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(500, 300)), DELTA);
        assertEquals(2.0, quoteTweetView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(600, 300)), DELTA);
        assertEquals(2.3333, quoteTweetView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(700, 300)), DELTA);
        assertEquals(2.6666, quoteTweetView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(800, 300)), DELTA);
        assertEquals(3.0, quoteTweetView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(900, 300)), DELTA);
        assertEquals(3.0, quoteTweetView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(1000, 50)), DELTA);
    }

    public void testGetAspectRatioForPhotoEntity() {
        final QuoteTweetView quoteTweetView = createView(context, TestFixtures.TEST_PHOTO_TWEET);
        assertEquals(1.6, quoteTweetView.getAspectRatioForPhotoEntity(1));
        assertEquals(1.6, quoteTweetView.getAspectRatioForPhotoEntity(2));
        assertEquals(1.6, quoteTweetView.getAspectRatioForPhotoEntity(3));
        assertEquals(1.6, quoteTweetView.getAspectRatioForPhotoEntity(4));
    }
}
