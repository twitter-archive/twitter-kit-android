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

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
public class TweetEntitiesTest {

    private static final int EXPECTED_URLS_SIZE = 1;
    private static final int EXPECTED_USER_MENTIONS_SIZE = 1;
    private static final int EXPECTED_MEDIA_SIZE = 1;
    private static final int EXPECTED_HASHTAGS_SIZE = 1;
    private static final int EXPECTED_SYMBOLS_SIZE = 1;

    @Rule
    public final TestResources testResources = new TestResources();

    private Gson gson;

    @Before
    public void setUp() throws Exception {
        gson = new Gson();
    }

    @Test
    public void testConstructor_nullParameters() {
        try {
            final TweetEntities entities = new TweetEntities(null, null, null, null, null);
            assertEquals(Collections.EMPTY_LIST, entities.urls);
            assertEquals(Collections.EMPTY_LIST, entities.userMentions);
            assertEquals(Collections.EMPTY_LIST, entities.media);
            assertEquals(Collections.EMPTY_LIST, entities.hashtags);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testDeserialization() throws IOException {
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(testResources
                    .getAsStream("model_tweetentities.json")));
            final TweetEntities tweetEntities = gson.fromJson(reader, TweetEntities.class);
            // We simply assert that we parsed it successfully and rely on our other unit tests to
            // verify parsing of the individual objects.
            assertEquals(EXPECTED_URLS_SIZE, tweetEntities.urls.size());
            assertEquals(EXPECTED_USER_MENTIONS_SIZE, tweetEntities.userMentions.size());
            assertEquals(EXPECTED_MEDIA_SIZE, tweetEntities.media.size());
            assertEquals(EXPECTED_HASHTAGS_SIZE, tweetEntities.hashtags.size());
            assertEquals(EXPECTED_SYMBOLS_SIZE, tweetEntities.symbols.size());
        } finally {
            CommonUtils.closeQuietly(reader);
        }
    }

    @Test
    public void testDeserialization_noEntities() throws IOException {
        final TweetEntities tweetEntities = gson.fromJson("{\"urls\":[]}", TweetEntities.class);
        // We simply assert that we parsed it successfully and rely on our other unit tests to
        // verify parsing of the individual objects.
        assertNotNull(tweetEntities.urls);
        assertEquals(0, tweetEntities.urls.size());
        assertNotNull(tweetEntities.userMentions);
        assertEquals(0, tweetEntities.userMentions.size());
        assertNotNull(tweetEntities.media);
        assertEquals(0, tweetEntities.media.size());
        assertNotNull(tweetEntities.hashtags);
        assertEquals(0, tweetEntities.hashtags.size());
        assertNotNull(tweetEntities.symbols);
        assertEquals(0, tweetEntities.symbols.size());
    }
}
