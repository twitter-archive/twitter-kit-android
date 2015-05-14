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

import com.twitter.sdk.android.core.internal.scribe.EventNamespace;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class CompactTweetViewTest extends BaseTweetViewTest {
    private static final String REQUIRED_TFW_SCRIBE_COMPONENT = "compact";
    private static final String REQUIRED_SDK_SCRIBE_SECTION = "compact";
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
        final CompactTweetView compactView = new CompactTweetView(context, TestFixtures.TEST_TWEET);
        assertEquals(R.layout.tw__tweet_compact, compactView.getLayout());
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

    public void testGetAspectRatio() {
        assertEquals(1.0, CompactTweetView.getAspectRatio(fakeMediaEntity(100, 100)), DELTA);
        assertEquals(1.0, CompactTweetView.getAspectRatio(fakeMediaEntity(300, 400)), DELTA);
        assertEquals(1.0, CompactTweetView.getAspectRatio(fakeMediaEntity(100, 800)), DELTA);
        assertEquals(1.3333, CompactTweetView.getAspectRatio(fakeMediaEntity(400, 300)), DELTA);
        assertEquals(1.6666, CompactTweetView.getAspectRatio(fakeMediaEntity(500, 300)), DELTA);
        assertEquals(2.0, CompactTweetView.getAspectRatio(fakeMediaEntity(600, 300)), DELTA);
        assertEquals(2.3333, CompactTweetView.getAspectRatio(fakeMediaEntity(700, 300)), DELTA);
        assertEquals(2.6666, CompactTweetView.getAspectRatio(fakeMediaEntity(800, 300)), DELTA);
        assertEquals(3.0, CompactTweetView.getAspectRatio(fakeMediaEntity(900, 300)), DELTA);
        assertEquals(3.0, CompactTweetView.getAspectRatio(fakeMediaEntity(1000, 50)), DELTA);
    }

    public void testGetAspectRatio_missingSizeDimension() {
        assertEquals(CompactTweetView.DEFAULT_ASPECT_RATIO,
                CompactTweetView.getAspectRatio(fakeMediaEntity(0, 0)));
        assertEquals(CompactTweetView.DEFAULT_ASPECT_RATIO,
                CompactTweetView.getAspectRatio(fakeMediaEntity(100, 0)));
        assertEquals(CompactTweetView.DEFAULT_ASPECT_RATIO,
                CompactTweetView.getAspectRatio(fakeMediaEntity(0, 100)));
    }

    public void testSetTweetPhoto() {
        final Picasso mockPicasso = mock(Picasso.class);
        final RequestCreator mockRequestCreator = mock(RequestCreator.class);
        MockUtils.mockPicasso(mockPicasso, mockRequestCreator);
        when(mockDependencyProvider.getImageLoader()).thenReturn(mockPicasso);

        final CompactTweetView tv = createViewWithMocks(context, TestFixtures.TEST_PHOTO_TWEET,
                R.style.tw__TweetLightStyle, mockDependencyProvider);
        // assert 2 loads, once for profile photo and once for compact tweet photo
        verify(mockPicasso, times(2)).load(anyString());
        // assert fit is called once when the compact tweet photo is loaded
        verify(mockRequestCreator, times(1)).fit();
    }

    private MediaEntity fakeMediaEntity(int width, int height) {
        final MediaEntity.Size medium = new MediaEntity.Size(width, height, "fit");
        final MediaEntity.Sizes sizes = new MediaEntity.Sizes(null, null, medium, null);
        return new MediaEntity(null, null, null, 0, 0, 0L, null, null, "fake", sizes, 0L, null,
                null);
    }
}
