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

import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetBuilder;
import com.twitter.sdk.android.core.models.TweetEntities;
import com.twitter.sdk.android.core.models.VideoInfo;
import com.twitter.sdk.android.tweetui.TestFixtures;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class TweetMediaUtilsTest {
    private static final int TEST_INDICES_START = 0;
    private static final int TEST_INDICES_END = 13;
    private static final String TEST_MEDIA_TYPE_PHOTO = "photo";
    private static final String TEST_MEDIA_TYPE_VIDEO = "video";
    private static final String TEST_MEDIA_TYPE_ANIMATED_GIF = "animated_gif";
    private static final String TEST_CONTENT_TYPE_MP4 = "video/mp4";
    private static final String TEST_CONTENT_TYPE_HLS = "application/x-mpegURL";
    private static final String TEST_CONTENT_TYPE_DASH = "video/dash+xml";

    @Test
    public void testGetPhotoEntity_nullEntities() {
        final Tweet tweet = new TweetBuilder().setEntities(null).build();
        assertNull(TweetMediaUtils.getPhotoEntity(tweet));
    }

    @Test
    public void testGetPhotoEntity_nullMedia() {
        final TweetEntities entities = new TweetEntities(null, null, null, null, null);
        final Tweet tweet = new TweetBuilder().setEntities(entities).build();
        assertNull(TweetMediaUtils.getPhotoEntity(tweet));
    }

    @Test
    public void testGetPhotoEntity_emptyMedia() {
        final TweetEntities entities = new TweetEntities(null, null, new ArrayList<MediaEntity>(),
                null, null);
        final Tweet tweet = new TweetBuilder().setEntities(entities).build();
        assertNull(TweetMediaUtils.getPhotoEntity(tweet));
    }

    @Test
    public void testGetPhotoEntity_hasFinalPhotoEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_PHOTO);
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null, null);
        final Tweet tweet = new TweetBuilder().setEntities(entities).build();

        assertEquals(entity, TweetMediaUtils.getPhotoEntity(tweet));
    }

    @Test
    public void testGetPhotoEntity_nonPhotoMedia() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_VIDEO);
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null, null);
        final Tweet tweet = new TweetBuilder().setEntities(entities).build();

        assertNull(TweetMediaUtils.getPhotoEntity(tweet));
    }

    @Test
    public void testHasPhoto_hasPhotoEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_PHOTO);
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null, null);
        final Tweet tweet = new TweetBuilder().setEntities(entities).build();

        assertTrue(TweetMediaUtils.hasPhoto(tweet));
    }

    @Test
    public void testHasPhoto_noPhotoEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_VIDEO);
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null, null);
        final Tweet tweet = new TweetBuilder().setEntities(entities).build();

        assertFalse(TweetMediaUtils.hasPhoto(tweet));
    }

    @Test
    public void testHasPhoto_uninitializedMediaEntities() {
        final TweetEntities entities = new TweetEntities(null, null, null, null, null);
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
        final TweetEntities entities = new TweetEntities(null, null, null, null, null);
        final Tweet tweet = new TweetBuilder().setExtendedEntities(entities).build();
        assertNull(TweetMediaUtils.getVideoEntity(tweet));
    }

    @Test
    public void testGetVideoEntity_emptyMedia() {
        final TweetEntities entities = new TweetEntities(null, null, new ArrayList<MediaEntity>(),
                null, null);
        final Tweet tweet = new TweetBuilder().setExtendedEntities(entities).build();
        assertNull(TweetMediaUtils.getVideoEntity(tweet));
    }

    @Test
    public void testGetVideoEntity_hasVideoEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_VIDEO);
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null, null);
        final Tweet tweet = new TweetBuilder().setExtendedEntities(entities).build();

        assertEquals(entity, TweetMediaUtils.getVideoEntity(tweet));
    }

    @Test
    public void testGetVideoEntity_nonVideoMedia() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_PHOTO);
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null, null);
        final Tweet tweet = new TweetBuilder().setExtendedEntities(entities).build();

        assertNull(TweetMediaUtils.getVideoEntity(tweet));
    }

    @Test
    public void testHasSupportedVideo_hasUnsupportedVideoEntity() {
        final VideoInfo.Variant variant = new VideoInfo.Variant(0, TEST_CONTENT_TYPE_DASH, null);
        final VideoInfo videoInfo = TestFixtures.createVideoInfoWithVariant(variant);
        final MediaEntity entity = TestFixtures.createEntityWithVideo(videoInfo);

        final TweetEntities entities = new TweetEntities(null, null, Arrays.asList(entity), null,
                null);
        final Tweet tweet = new TweetBuilder().setExtendedEntities(entities).build();

        assertFalse(TweetMediaUtils.hasSupportedVideo(tweet));
    }

    @Test
    public void testHasSupportedVideo_hasSupportedVideoEntity() {
        final VideoInfo.Variant variant = new VideoInfo.Variant(0, TEST_CONTENT_TYPE_MP4, null);
        final VideoInfo videoInfo = TestFixtures.createVideoInfoWithVariant(variant);
        final MediaEntity entity = TestFixtures.createEntityWithVideo(videoInfo);

        final TweetEntities entities = new TweetEntities(null, null, Arrays.asList(entity), null,
                null);
        final Tweet tweet = new TweetBuilder().setExtendedEntities(entities).build();

        assertTrue(TweetMediaUtils.hasSupportedVideo(tweet));
    }

    @Test
    public void testHasSupportedVideo_noVideoEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_PHOTO);
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null, null);
        final Tweet tweet = new TweetBuilder().setExtendedEntities(entities).build();

        assertFalse(TweetMediaUtils.hasSupportedVideo(tweet));
    }

    @Test
    public void testHasSupportedVideo_uninitializedMediaEntities() {
        final TweetEntities entities = new TweetEntities(null, null, null, null, null);
        final Tweet tweet = new TweetBuilder().setExtendedEntities(entities).build();
        assertFalse(TweetMediaUtils.hasSupportedVideo(tweet));
    }

    @Test
    public void testHasSupportedVideo_nullEntities() {
        final Tweet tweet = new TweetBuilder().setExtendedEntities(null).build();
        assertFalse(TweetMediaUtils.hasSupportedVideo(tweet));
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

    @Test
    public void testGetSupportedVariant() {
        final VideoInfo.Variant variant = new VideoInfo.Variant(0, TEST_CONTENT_TYPE_MP4, null);
        final VideoInfo videoInfo = TestFixtures.createVideoInfoWithVariant(variant);
        final MediaEntity entity = TestFixtures.createEntityWithVideo(videoInfo);

        assertNotNull(TweetMediaUtils.getSupportedVariant(entity));
        assertEquals(variant, TweetMediaUtils.getSupportedVariant(entity));
    }

    @Test
    public void testGetSupportedVariant_unsupportedContentType() {
        final VideoInfo.Variant variant = new VideoInfo.Variant(0, TEST_CONTENT_TYPE_DASH, null);
        final VideoInfo videoInfo = TestFixtures.createVideoInfoWithVariant(variant);
        final MediaEntity entity = TestFixtures.createEntityWithVideo(videoInfo);

        assertNull(TweetMediaUtils.getSupportedVariant(entity));
    }

    @Test
    public void testIsVariantSupported_withMP4() {
        final VideoInfo.Variant variant = new VideoInfo.Variant(0, TEST_CONTENT_TYPE_MP4, null);
        assertTrue(TweetMediaUtils.isVariantSupported(variant));
    }

    @Test
    public void testIsVariantSupported_withHLS() {
        final VideoInfo.Variant variant = new VideoInfo.Variant(0, TEST_CONTENT_TYPE_HLS, null);
        assertTrue(TweetMediaUtils.isVariantSupported(variant));
    }

    @Test
    public void testIsVariantSupported_withNullContentType() {
        final VideoInfo.Variant variant = new VideoInfo.Variant(0, null, null);
        assertFalse(TweetMediaUtils.isVariantSupported(variant));
    }

    @Test
    public void testIsVariantSupported_withUnsupportedContentType() {
        final VideoInfo.Variant variant = new VideoInfo.Variant(0, TEST_CONTENT_TYPE_DASH, null);
        assertFalse(TweetMediaUtils.isVariantSupported(variant));
    }

    @Test
    public void testIsLooping_withVideoOverSevenSeconds() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_VIDEO, 7000);
        assertFalse(TweetMediaUtils.isLooping(entity));
    }

    @Test
    public void testIsLooping_withVideoUnderSevenSeconds() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_VIDEO, 6000);
        assertTrue(TweetMediaUtils.isLooping(entity));
    }

    @Test
    public void testIsLooping_withAnimatedGif() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_ANIMATED_GIF);
        assertTrue(TweetMediaUtils.isLooping(entity));
    }

    @Test
    public void showVideoControlsWithVideo() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_VIDEO);
        assertTrue(TweetMediaUtils.showVideoControls(entity));
    }

    @Test
    public void showVideoControlsWithAnimatedGif() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_ANIMATED_GIF);
        assertFalse(TweetMediaUtils.showVideoControls(entity));
    }
}
