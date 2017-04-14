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

package com.twitter.sdk.android.core.models;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.twitter.sdk.android.core.TestResources;
import com.twitter.sdk.android.core.internal.CommonUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
public class MediaEntityTest  {
    private static final int TEST_INDICES_START = 117;
    private static final int TEST_INDICES_END = 139;
    private static final String TEST_URL = "http://t.co/lvYVFjAbzz";
    private static final String TEST_DISPLAY_URL = "pic.twitter.com/lvYVFjAbzz";
    private static final String TEST_EXPANDED_URL = "http://twitter.com/jbulava/status/606528031289655296/video/1";
    private static final long TEST_ID = 606527664086781952L;
    private static final String TEST_ID_STR = "606527664086781952";
    private static final String TEST_MEDIA_URL = "http://pbs.twimg.com/ext_tw_video_thumb/606527664086781952/pu/img/mInvoINHjLcN8Mvk.jpg";
    private static final String TEST_MEDIA_URL_HTTPS = "https://pbs.twimg.com/ext_tw_video_thumb/606527664086781952/pu/img/mInvoINHjLcN8Mvk.jpg";
    private static final MediaEntity.Size TEST_SIZE_THUMB = new MediaEntity.Size(150, 150, "crop");
    private static final MediaEntity.Size TEST_SIZE_SMALL = new MediaEntity.Size(340, 191, "fit");
    private static final MediaEntity.Size TEST_SIZE_MEDIUM = new MediaEntity.Size(600, 338, "fit");
    private static final MediaEntity.Size TEST_SIZE_LARGE = new MediaEntity.Size(1024, 576, "fit");
    private static final long TEST_SOURCE_STATUS_ID = 205282515685081088L;
    private static final String TEST_SOURCE_STATUS_ID_STR = "205282515685081088";
    private static final String TEST_TYPE = "video";
    private static final int TEST_ASPECT_WIDTH = 16;
    private static final int TEST_ASPECT_HEIGHT = 9;
    private static final long TEST_DURATION = 30024;
    private static final int TEST_TOTAL_VARIANTS = 2;
    private static final String TEST_ALT_TEXT = "A Twitter employee";
    private static final String TEST_URL_0 = "https://video.twimg.com/ext_tw_video/606527664086781952/pu/vid/640x360/jdAs88NgP4N3Iqtu.mp4";
    private static final VideoInfo.Variant TEST_VARIANT_0 =
            new VideoInfo.Variant(832000, "video/mp4", TEST_URL_0);
    private static final String TEST_URL_1 = "https://video.twimg.com/ext_tw_video/606527664086781952/pu/vid/640x360/jdAs88NgP4N3Iqtu.webm";
    private static final VideoInfo.Variant TEST_VARIANT_1 =
            new VideoInfo.Variant(832000, "video/webm", TEST_URL_1);

    @Rule
    public final TestResources testResources = new TestResources();
    private Gson gson;

    @Before
    public void setUp() throws Exception {

        gson = new Gson();
    }

    @Test
    public void testDeserialization() throws IOException {
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(testResources
                    .getAsStream("model_media_entity.json")));
            final MediaEntity entity = gson.fromJson(reader, MediaEntity.class);

            assertEquals(TEST_INDICES_START, entity.getStart());
            assertEquals(TEST_INDICES_END, entity.getEnd());
            assertEquals(TEST_URL, entity.url);
            assertEquals(TEST_DISPLAY_URL, entity.displayUrl);
            assertEquals(TEST_EXPANDED_URL, entity.expandedUrl);
            assertEquals(TEST_ID, entity.id);
            assertEquals(TEST_ID_STR, entity.idStr);
            assertEquals(TEST_MEDIA_URL, entity.mediaUrl);
            assertEquals(TEST_MEDIA_URL_HTTPS, entity.mediaUrlHttps);
            assertSizeEquals(TEST_SIZE_THUMB, entity.sizes.thumb);
            assertSizeEquals(TEST_SIZE_SMALL, entity.sizes.small);
            assertSizeEquals(TEST_SIZE_MEDIUM, entity.sizes.medium);
            assertSizeEquals(TEST_SIZE_LARGE, entity.sizes.large);
            assertEquals(TEST_SOURCE_STATUS_ID, entity.sourceStatusId);
            assertEquals(TEST_SOURCE_STATUS_ID_STR, entity.sourceStatusIdStr);
            assertEquals(TEST_TYPE, entity.type);
            assertEquals(TEST_ASPECT_WIDTH, (int) entity.videoInfo.aspectRatio.get(0));
            assertEquals(TEST_ASPECT_HEIGHT, (int) entity.videoInfo.aspectRatio.get(1));
            assertEquals(TEST_DURATION, entity.videoInfo.durationMillis);
            assertEquals(TEST_TOTAL_VARIANTS, entity.videoInfo.variants.size());
            assertVariantEquals(TEST_VARIANT_0, entity.videoInfo.variants.get(0));
            assertVariantEquals(TEST_VARIANT_1, entity.videoInfo.variants.get(1));
            assertEquals(TEST_ALT_TEXT, entity.altText);
        } finally {
            CommonUtils.closeQuietly(reader);
        }
    }

    @Test
    public void testSerializable() throws Exception {
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(testResources
                    .getAsStream("model_media_entity.json")));
            final MediaEntity entity = gson.fromJson(reader, MediaEntity.class);

            new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(entity);
        } catch (NotSerializableException ex) {
            fail("MediaEntity should implement Serializable");
        } finally {
            CommonUtils.closeQuietly(reader);
        }
    }

    public static void assertSizeEquals(MediaEntity.Size expected, MediaEntity.Size actual) {
        assertEquals(expected.h, actual.h);
        assertEquals(expected.w, actual.w);
        assertEquals(expected.resize, actual.resize);
    }

    public static void assertVariantEquals(VideoInfo.Variant expected, VideoInfo.Variant actual) {
        assertEquals(expected.bitrate, actual.bitrate);
        assertEquals(expected.contentType, actual.contentType);
        assertEquals(expected.url, actual.url);
    }
}
