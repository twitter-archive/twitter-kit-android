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

import android.view.View;

import com.twitter.sdk.android.core.models.Card;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.VideoInfo;
import com.twitter.sdk.android.tweetui.TestFixtures;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class MediaBadgeViewTest {
    MediaBadgeView view;

    @Before
    public void setUp() throws Exception {
        view = new MediaBadgeView(RuntimeEnvironment.application);
    }

    @Test
    public void testInitialViewState() {
        assertEquals(View.GONE, view.badge.getVisibility());
        assertEquals(View.GONE, view.videoDuration.getVisibility());
    }

    @Test
    public void testSetMediaEntity_withAnimatedGif() {
        final MediaEntity entity = TestFixtures.createEntityWithAnimatedGif(null);
        view.setMediaEntity(entity);

        assertEquals(View.VISIBLE, view.badge.getVisibility());
        assertEquals(View.GONE, view.videoDuration.getVisibility());
    }

    @Test
    public void testSetMediaEntity_withVideo() {
        final VideoInfo videoInfo = new VideoInfo(null, 1000, null);
        final MediaEntity entity = TestFixtures.createEntityWithVideo(videoInfo);
        view.setMediaEntity(entity);

        assertEquals(View.GONE, view.badge.getVisibility());
        assertEquals(View.VISIBLE, view.videoDuration.getVisibility());
        assertEquals("0:01", view.videoDuration.getText());
    }

    @Test
    public void testSetMediaEntity_withNullVideoInfo() {
        final MediaEntity entity = TestFixtures.createEntityWithVideo(null);
        view.setMediaEntity(entity);

        assertEquals(View.GONE, view.badge.getVisibility());
        assertEquals(View.VISIBLE, view.videoDuration.getVisibility());
        assertEquals("0:00", view.videoDuration.getText());
    }

    @Test
    public void testSetMediaEntity_withImage() {
        final MediaEntity entity = TestFixtures.createMediaEntityWithPhoto(null);
        view.setMediaEntity(entity);

        assertEquals(View.GONE, view.badge.getVisibility());
        assertEquals(View.GONE, view.videoDuration.getVisibility());
    }

    @Test
    public void testSetEntity_withVineCard() {
        final Card vineCard = TestFixtures.sampleValidVineCard();
        view.setCard(vineCard);

        assertEquals(View.VISIBLE, view.badge.getVisibility());
        assertEquals(View.GONE, view.videoDuration.getVisibility());
    }

    @Test
    public void testSetEntity_withInvalidVineCard() {
        final Card vineCard = TestFixtures.sampleInvalidVineCard();
        view.setCard(vineCard);

        assertEquals(View.GONE, view.badge.getVisibility());
        assertEquals(View.GONE, view.videoDuration.getVisibility());
    }
}
