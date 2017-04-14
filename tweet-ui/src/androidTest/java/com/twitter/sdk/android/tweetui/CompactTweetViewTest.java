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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.twitter.sdk.android.core.models.Tweet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CompactTweetViewTest extends BaseTweetViewTest {
    private static final float DELTA = 0.001f;

    @Override
    CompactTweetView createView(Context context, Tweet tweet) {
        return new CompactTweetView(context, tweet);
    }

    @Override
    CompactTweetView createView(Context context, Tweet tweet, int styleResId) {
        return new CompactTweetView(context, tweet, styleResId);
    }

    @Override
    CompactTweetView createViewInEditMode(Context context, Tweet tweet) {
        return new CompactTweetView(context, tweet) {
            @Override
            public boolean isInEditMode() {
                return true;
            }
        };
    }

    @Override
    CompactTweetView createViewWithMocks(Context context, Tweet tweet) {
        return new CompactTweetView(context, tweet);
    }

    @Override
    CompactTweetView createViewWithMocks(Context context, Tweet tweet, int styleResId,
            BaseTweetView.DependencyProvider dependencyProvider) {
        return new CompactTweetView(context, tweet, styleResId, dependencyProvider);
    }

    // Layout
    public void testLayout() {
        final CompactTweetView compactView = createView(context, TestFixtures.TEST_TWEET);
        assertEquals(R.layout.tw__tweet_compact, compactView.getLayout());
    }

    public void testGetAspectRatio() {
        final CompactTweetView compactView = createView(context, TestFixtures.TEST_TWEET);

        assertEquals(1.0, compactView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(100, 100)), DELTA);
        assertEquals(1.0, compactView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(300, 400)), DELTA);
        assertEquals(1.0, compactView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(100, 800)), DELTA);
        assertEquals(1.3333, compactView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(400, 300)), DELTA);
        assertEquals(1.6666, compactView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(500, 300)), DELTA);
        assertEquals(2.0, compactView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(600, 300)), DELTA);
        assertEquals(2.3333, compactView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(700, 300)), DELTA);
        assertEquals(2.6666, compactView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(800, 300)), DELTA);
        assertEquals(3.0, compactView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(900, 300)), DELTA);
        assertEquals(3.0, compactView.getAspectRatio(
                TestFixtures.createMediaEntityWithPhoto(1000, 50)), DELTA);
    }

    public void testSetTweetPhoto() {
        final Picasso mockPicasso = mock(Picasso.class);
        final RequestCreator mockRequestCreator = mock(RequestCreator.class);
        MockUtils.mockPicasso(mockPicasso, mockRequestCreator);
        when(mockDependencyProvider.getImageLoader()).thenReturn(mockPicasso);

        final CompactTweetView tv = createViewWithMocks(context, TestFixtures.TEST_PHOTO_TWEET,
                R.style.tw__TweetLightStyle, mockDependencyProvider);
        // assert 1 load for profile photo, tweet photo loaded in TweetMediaView
        verify(mockPicasso, times(1)).load(TestFixtures.TEST_PROFILE_IMAGE_URL);
    }

    public void testGetAspectRatioForPhotoEntity() {
        final CompactTweetView compactView = createView(context, TestFixtures.TEST_PHOTO_TWEET);
        assertEquals(1.6, compactView.getAspectRatioForPhotoEntity(1));
        assertEquals(1.6, compactView.getAspectRatioForPhotoEntity(2));
        assertEquals(1.6, compactView.getAspectRatioForPhotoEntity(3));
        assertEquals(1.6, compactView.getAspectRatioForPhotoEntity(4));
    }
}
