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

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class ConfigurationTest {
    @Rule
    public final TestResources testResources = new TestResources();

    private static final int TEST_DM_CHAR_LIMIT = 10000;
    private static final int TEST_SHORT_URL_LENGTH = 23;
    private static final int TEST_NUN_NON_USER_NAME = 85;
    private static final long TEST_PHOTO_SIZE_LIMIT = 3145728;
    private static final MediaEntity.Size TEST_SIZE_THUMB = new MediaEntity.Size(150, 150, "crop");
    private static final MediaEntity.Size TEST_SIZE_SMALL = new MediaEntity.Size(340, 480, "fit");
    private static final MediaEntity.Size TEST_SIZE_MEDIUM = new MediaEntity.Size(600, 1200, "fit");
    private static final MediaEntity.Size TEST_SIZE_LARGE = new MediaEntity.Size(1024, 2048, "fit");

    @Test
    public void testDeserialization() throws IOException {
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(testResources
                    .getAsStream("model_configuration.json")));
            final Configuration configuration = new Gson().fromJson(reader, Configuration.class);
            assertEquals(TEST_DM_CHAR_LIMIT, configuration.dmTextCharacterLimit);
            assertNotNull(configuration.nonUsernamePaths);
            assertEquals(TEST_NUN_NON_USER_NAME, configuration.nonUsernamePaths.size());
            assertEquals(TEST_PHOTO_SIZE_LIMIT, configuration.photoSizeLimit);
            assertNotNull(configuration.photoSizes);
            MediaEntityTest.assertSizeEquals(TEST_SIZE_THUMB, configuration.photoSizes.thumb);
            MediaEntityTest.assertSizeEquals(TEST_SIZE_SMALL, configuration.photoSizes.small);
            MediaEntityTest.assertSizeEquals(TEST_SIZE_MEDIUM, configuration.photoSizes.medium);
            MediaEntityTest.assertSizeEquals(TEST_SIZE_LARGE, configuration.photoSizes.large);
            assertEquals(TEST_SHORT_URL_LENGTH, configuration.shortUrlLengthHttps);
        } finally {
            CommonUtils.closeQuietly(reader);
        }
    }
}
