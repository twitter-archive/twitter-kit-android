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
import com.twitter.sdk.android.core.models.TweetEntities;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TweetEntityUtilsTest {
    private static final int TEST_INDICES_START = 0;
    private static final int TEST_INDICES_END = 13;
    private static final String TEST_MEDIA_TYPE_PHOTO = "photo";

    @Test
    public void testGetLastPhotoEntity_nullEntities() {
        assertNull(TweetEntityUtils.getLastPhotoEntity(null));
    }

    @Test
    public void testGetLastPhotoEntity_nullMedia() {
        final TweetEntities entities = new TweetEntities(null, null, null, null);
        assertNull(TweetEntityUtils.getLastPhotoEntity(entities));
    }

    @Test
    public void testGetLastPhotoEntity_emptyMedia() {
        final TweetEntities entities = new TweetEntities(null, null, new ArrayList<MediaEntity>(),
                null);
        assertNull(TweetEntityUtils.getLastPhotoEntity(entities));
    }

    @Test
    public void testGetLastPhotoEntity_hasFinalPhotoEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_PHOTO);
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null);

        assertEquals(entity, TweetEntityUtils.getLastPhotoEntity(entities));
    }

    @Test
    public void testGetLastPhotoEntity_nonPhotoMedia() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                "imaginary");
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null);

        assertNull(TweetEntityUtils.getLastPhotoEntity(entities));
    }

    @Test
    public void testHasPhotoUrl_hasPhotoEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                TEST_MEDIA_TYPE_PHOTO);
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null);

        assertTrue(TweetEntityUtils.hasPhotoUrl(entities));
    }

    @Test
    public void testHasPhotoUrl_noPhotoEntity() {
        final MediaEntity entity = TestFixtures.newMediaEntity(TEST_INDICES_START, TEST_INDICES_END,
                "imaginary");
        final ArrayList<MediaEntity> media = new ArrayList<>();
        media.add(entity);
        final TweetEntities entities = new TweetEntities(null, null, media, null);

        assertFalse(TweetEntityUtils.hasPhotoUrl(entities));
    }

    @Test
    public void testHasPhotoUrl_uninitializedMediaEntities() {
        assertFalse(TweetEntityUtils.hasPhotoUrl(new TweetEntities(null, null, null, null)));
    }

    @Test
    public void testHasPhotoUrl_nullEntities() {
        assertFalse(TweetEntityUtils.hasPhotoUrl(null));
    }
}
