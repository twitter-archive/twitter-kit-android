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

import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetBuilder;
import com.twitter.sdk.android.core.models.TweetEntities;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TweetMediaUtilsTest {
    private static final int TEST_INDICES_START = 0;
    private static final int TEST_INDICES_END = 13;
    private static final String TEST_MEDIA_TYPE_PHOTO = "photo";
    private static final String TEST_MEDIA_TYPE_VIDEO = "video";
    private static final String TEST_MEDIA_TYPE_ANIMATED_GIF = "animated_gif";

    @Test
    public void testGetPhotoEntity_nullEntities() {
        final Tweet tweet = new TweetBuilder().setEntities(null).build();
        assertNull(TweetMediaUtils.getPhotoEntity(tweet));
    }

    @Test
    public void testGetPhotoEntity_nullMedia() {
        final TweetEntities entities = new TweetEntities(null, null, null, null);
        final Tweet tweet = new TweetBuilder().setEntities(entities).build();
        assertNull(TweetMediaUtils.getPhotoEntity(tweet));
    }

    @Test
    public void testGetPhotoEntity_emptyMedia() {
        final TweetEntities entities = new TweetEntities(null, null, new ArrayList<MediaEntity>(),
                null);
        final Tweet tweet = new TweetBuilder().setEntities(entities).build();
        assertNull(TweetMediaUtils.getPhotoEntity(tweet));
    }

    @Test
    public void testGetPhotoEntity_hasFinalPhotoEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_PHOTO);
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null);
        final Tweet tweet = new TweetBuilder().setEntities(entities).build();

        assertEquals(entity, TweetMediaUtils.getPhotoEntity(tweet));
    }

    @Test
    public void testGetPhotoEntity_nonPhotoMedia() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_VIDEO);
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null);
        final Tweet tweet = new TweetBuilder().setEntities(entities).build();

        assertNull(TweetMediaUtils.getPhotoEntity(tweet));
    }

    @Test
    public void testHasPhoto_hasPhotoEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_PHOTO);
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null);
        final Tweet tweet = new TweetBuilder().setEntities(entities).build();

        assertTrue(TweetMediaUtils.hasPhoto(tweet));
    }

    @Test
    public void testHasPhoto_noPhotoEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_VIDEO);
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null);
        final Tweet tweet = new TweetBuilder().setEntities(entities).build();

        assertFalse(TweetMediaUtils.hasPhoto(tweet));
    }

    @Test
    public void testHasPhoto_uninitializedMediaEntities() {
        final TweetEntities entities = new TweetEntities(null, null, null, null);
        final Tweet tweet = new TweetBuilder().setEntities(entities).build();
        assertFalse(TweetMediaUtils.hasPhoto(tweet));
    }

    @Test
    public void testHasPhoto_nullEntities() {
        final Tweet tweet = new TweetBuilder().setEntities(null).build();
        assertFalse(TweetMediaUtils.hasPhoto(tweet));
    }

    @Test
    public void testGetVideoEntity_nullEntities() {
        final Tweet tweet = new TweetBuilder().setExtendedEntities(null).build();
        assertNull(TweetMediaUtils.getVideoEntity(tweet));
    }

    @Test
    public void testGetVideoEntity_nullMedia() {
        final TweetEntities entities = new TweetEntities(null, null, null, null);
        final Tweet tweet = new TweetBuilder().setExtendedEntities(entities).build();
        assertNull(TweetMediaUtils.getVideoEntity(tweet));
    }

    @Test
    public void testGetVideoEntity_emptyMedia() {
        final TweetEntities entities = new TweetEntities(null, null, new ArrayList<MediaEntity>(),
                null);
        final Tweet tweet = new TweetBuilder().setExtendedEntities(entities).build();
        assertNull(TweetMediaUtils.getVideoEntity(tweet));
    }

    @Test
    public void testGetVideoEntity_hasVideoEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_VIDEO);
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null);
        final Tweet tweet = new TweetBuilder().setExtendedEntities(entities).build();

        assertEquals(entity, TweetMediaUtils.getVideoEntity(tweet));
    }

    @Test
    public void testGetVideoEntity_nonVideoMedia() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_PHOTO);
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null);
        final Tweet tweet = new TweetBuilder().setExtendedEntities(entities).build();

        assertNull(TweetMediaUtils.getVideoEntity(tweet));
    }

    @Test
    public void testHasVideo_hasVideoEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_VIDEO);
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null);
        final Tweet tweet = new TweetBuilder().setExtendedEntities(entities).build();

        assertTrue(TweetMediaUtils.hasVideo(tweet));
    }

    @Test
    public void testHasVideo_noVideoEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_PHOTO);
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null);
        final Tweet tweet = new TweetBuilder().setExtendedEntities(entities).build();

        assertFalse(TweetMediaUtils.hasVideo(tweet));
    }

    @Test
    public void testHasVideo_uninitializedMediaEntities() {
        final TweetEntities entities = new TweetEntities(null, null, null, null);
        final Tweet tweet = new TweetBuilder().setExtendedEntities(entities).build();
        assertFalse(TweetMediaUtils.hasVideo(tweet));
    }

    @Test
    public void testHasVideo_nullEntities() {
        final Tweet tweet = new TweetBuilder().setExtendedEntities(null).build();
        assertFalse(TweetMediaUtils.hasVideo(tweet));
    }

    @Test
    public void testIsPhotoType_photoEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_PHOTO);
        assertTrue(TweetMediaUtils.isPhotoType(entity));
    }

    @Test
    public void testIsPhotoType_videoEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_VIDEO);
        assertFalse(TweetMediaUtils.isPhotoType(entity));
    }

    @Test
    public void testIsPhotoType_animatedGifEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_ANIMATED_GIF);
        assertFalse(TweetMediaUtils.isPhotoType(entity));
    }

    @Test
    public void testIsVideoType_photoEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_PHOTO);
        assertFalse(TweetMediaUtils.isVideoType(entity));
    }

    @Test
    public void testIsVideoType_videoEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_VIDEO);
        assertTrue(TweetMediaUtils.isVideoType(entity));
    }

    @Test
    public void testIsVideoType_animatedGifEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_ANIMATED_GIF);
        assertTrue(TweetMediaUtils.isVideoType(entity));
    }
}
