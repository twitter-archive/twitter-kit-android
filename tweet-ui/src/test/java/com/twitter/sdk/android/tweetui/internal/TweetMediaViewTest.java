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

package com.twitter.sdk.android.tweetui.internal;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.twitter.sdk.android.core.models.Card;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.R;
import com.twitter.sdk.android.tweetui.TestFixtures;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class TweetMediaViewTest {
    static final String TEST_ALT_TEXT = "alt text";
    static final int TEST_ERROR_RES_ID = 123456789;

    private TweetMediaView tweetMediaView;
    private CharSequence contentDefaultDescription;

    @Before
    public void setUp() throws Exception {
        tweetMediaView = new TweetMediaView(RuntimeEnvironment.application, null,
                mock(TweetMediaView.DependencyProvider.class));
        contentDefaultDescription = RuntimeEnvironment.application.getResources()
                .getString(R.string.tw__tweet_media);
    }

    @Test
    public void testInitialViewState() {
        for (int index = 0; index < TweetMediaView.MAX_IMAGE_VIEW_COUNT; index++) {
            final ImageView imageView = (ImageView) tweetMediaView.getChildAt(index);
            assertNull(imageView);
        }

        assertArrayEquals(new float[]{0, 0, 0, 0, 0, 0, 0, 0}, tweetMediaView.radii, 0);
    }

    public void testSetMediaBgColor() {
        tweetMediaView.setMediaBgColor(Color.BLUE);
        assertEquals(Color.BLUE, tweetMediaView.mediaBgColor);
    }

    public void testSetPhotoErrorResId() {
        tweetMediaView.setPhotoErrorResId(TEST_ERROR_RES_ID);
        assertEquals(TEST_ERROR_RES_ID, tweetMediaView.photoErrorResId);
    }

    @Test
    public void testSetTweetMediaEntities_withEmptyList() {
        final List<MediaEntity> emptyMediaEntities = Collections.EMPTY_LIST;
        tweetMediaView.setTweetMediaEntities(TestFixtures.TEST_TWEET, emptyMediaEntities);

        for (int index = 0; index < TweetMediaView.MAX_IMAGE_VIEW_COUNT; index++) {
            final ImageView imageView = (ImageView) tweetMediaView.getChildAt(index);
            assertNull(imageView);
        }
    }

    @Test
    public void testSetTweetMediaEntities_withSingleEntity() {
        final MediaEntity entity = TestFixtures.createMediaEntityWithPhoto(100, 100);
        final List<MediaEntity> mediaEntities = new ArrayList<>();
        mediaEntities.add(entity);
        tweetMediaView.setTweetMediaEntities(TestFixtures.TEST_TWEET, mediaEntities);

        final ImageView imageView = (ImageView) tweetMediaView.getChildAt(0);
        assertEquals(View.VISIBLE, imageView.getVisibility());
        assertNull(tweetMediaView.getChildAt(1));
        assertNull(tweetMediaView.getChildAt(2));
        assertNull(tweetMediaView.getChildAt(3));
    }

    @Test
    public void testSetTweetMediaEntities_withMultipleEntities() {
        final List<MediaEntity> mediaEntities = TestFixtures.createMultipleMediaEntitiesWithPhoto
                (TweetMediaView.MAX_IMAGE_VIEW_COUNT, 100, 100);
        tweetMediaView.setTweetMediaEntities(TestFixtures.TEST_TWEET, mediaEntities);

        for (int index = 0; index < TweetMediaView.MAX_IMAGE_VIEW_COUNT; index++) {
            final ImageView imageView = (ImageView) tweetMediaView.getChildAt(index);
            assertEquals(View.VISIBLE, imageView.getVisibility());
            assertEquals(index, imageView.getTag(R.id.tw__entity_index));
            assertEquals(contentDefaultDescription, imageView.getContentDescription());
        }
    }

    @Test
    public void testSetTweetMediaEntities_withVine() {
        final Card sampleVineCard = TestFixtures.sampleValidVineCard();
        final Tweet tweetWithVineCard = TestFixtures.createTweetWithVineCard(
                TestFixtures.TEST_TWEET_ID, TestFixtures.TEST_USER,
                TestFixtures.TEST_STATUS, sampleVineCard);

        tweetMediaView.setVineCard(tweetWithVineCard);

        final OverlayImageView imageView = (OverlayImageView) tweetMediaView.getChildAt(0);
        assertEquals(View.VISIBLE, imageView.getVisibility());
        assertNotNull(imageView.overlay.drawable);
        assertNull(tweetMediaView.getChildAt(1));
        assertNull(tweetMediaView.getChildAt(2));
        assertNull(tweetMediaView.getChildAt(3));
    }

    @Test
    public void testSetRoundedCornersRadii() {
        tweetMediaView.setRoundedCornersRadii(1, 2, 3, 4);
        assertArrayEquals(new float[]{1, 1, 2, 2, 3, 3, 4, 4}, tweetMediaView.radii, 0);
    }

    @Test
    public void testSetAltText_withEmptyString() {
        final OverlayImageView imageView = mock(OverlayImageView.class);
        tweetMediaView.setAltText(imageView, "");

        verify(imageView).setContentDescription(contentDefaultDescription);
    }

    @Test
    public void testSetAltText_withAtlText() {
        final OverlayImageView imageView = mock(OverlayImageView.class);
        tweetMediaView.setAltText(imageView, TEST_ALT_TEXT);

        verify(imageView).setContentDescription(TEST_ALT_TEXT);
    }

    @Test
    public void testSetOverlayImage_isVideoTrue() {
        final OverlayImageView imageView = mock(OverlayImageView.class);
        tweetMediaView.setOverlayImage(imageView, true);

        verify(imageView).setOverlayDrawable(any(Drawable.class));
    }

    @Test
    public void testSetOverlayImage_isVideoFalse() {
        final OverlayImageView imageView = mock(OverlayImageView.class);
        tweetMediaView.setOverlayImage(imageView, false);

        verify(imageView).setOverlayDrawable(isNull(Drawable.class));
    }

    @Test
    public void testClearImageViews() {
        final List<MediaEntity> mediaEntities = TestFixtures.createMultipleMediaEntitiesWithPhoto
                (TweetMediaView.MAX_IMAGE_VIEW_COUNT, 100, 100);
        tweetMediaView.setTweetMediaEntities(TestFixtures.TEST_TWEET, mediaEntities);
        tweetMediaView.clearImageViews();
        for (int index = 0; index < TweetMediaView.MAX_IMAGE_VIEW_COUNT; index++) {
            final ImageView imageView = (ImageView) tweetMediaView.getChildAt(index);
            assertEquals(View.GONE, imageView.getVisibility());
        }
    }
}
