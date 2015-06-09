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

import io.fabric.sdk.android.services.common.CommonUtils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.twitter.sdk.android.core.BuildConfig;
import com.twitter.sdk.android.core.TestResources;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class TweetTest {

    private static final String EXPECTED_CREATED_AT = "Wed Jun 06 20:07:10 +0000 2012";
    private static final long EXPECTED_ID = 210462857140252672L;
    private static final String EXPECTED_TEXT = "Along with our new #Twitterbird, we've also updated our Display Guidelines: https://t.co/Ed4omjYs  ^JC";

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
                    .getAsStream("model_tweet.json")));
            final Tweet tweet = gson.fromJson(reader, Tweet.class);
            // We simply assert that we parsed it successfully and rely on our other unit tests to
            // verify parsing of the individual objects.
            assertEquals(EXPECTED_CREATED_AT, tweet.createdAt);
            assertNotNull(tweet.entities);
            assertEquals(EXPECTED_ID, tweet.id);
            assertEquals(EXPECTED_ID, tweet.getId());
            assertEquals(EXPECTED_TEXT, tweet.text);
            assertNotNull(tweet.user);
        } finally {
            CommonUtils.closeQuietly(reader);
        }
    }
}
