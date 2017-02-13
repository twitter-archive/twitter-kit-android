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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class TwitterCollectionTest {
    private static final int EXPECTED_NUM_USERS = 2;
    private static final int EXPECTED_NUM_TWEETS = 3;
    private static final Long EXPECTED_TWEET_ID_FIRST = 504032379045179393L;
    private static final Long EXPECTED_TWEET_ID_SECOND = 532654992071852032L;
    private static final Long EXPECTED_USER_ID_FIRST = 2244994945L;
    private static final String EXPECTED_USER_SCREEN_NAME_FIRST = "TwitterDev";

    private static final String EXPECTED_TIMELINE_ID = "custom-539487832448843776";
    private static final Long EXPECTED_MAX_POSITION = 371578415352947200L;
    private static final Long EXPECTED_MIN_POSITION = 371578380871797248L;

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
                    .getAsStream("model_twitter_collection.json")));
            final TwitterCollection twitterCollection
                    = gson.fromJson(reader, TwitterCollection.class);

            // check collection decomposed object maps in objects field
            assertEquals(EXPECTED_NUM_TWEETS, twitterCollection.contents.tweetMap.size());
            assertEquals(EXPECTED_NUM_USERS, twitterCollection.contents.userMap.size());
            assertTrue(twitterCollection.contents.tweetMap.containsKey(EXPECTED_TWEET_ID_FIRST));
            assertEquals((long) EXPECTED_TWEET_ID_FIRST,
                    twitterCollection.contents.tweetMap.get(EXPECTED_TWEET_ID_FIRST).id);
            assertTrue(twitterCollection.contents.tweetMap.containsKey(EXPECTED_TWEET_ID_SECOND));
            assertTrue(twitterCollection.contents.userMap.containsKey(EXPECTED_USER_ID_FIRST));
            assertEquals(EXPECTED_USER_SCREEN_NAME_FIRST,
                    twitterCollection.contents.userMap.get(EXPECTED_USER_ID_FIRST).screenName);

            // check object references and contextual info in response field
            assertEquals(EXPECTED_TIMELINE_ID, twitterCollection.metadata.timelineId);
            assertEquals(EXPECTED_MAX_POSITION, twitterCollection.metadata.position.maxPosition);
            assertEquals(EXPECTED_MIN_POSITION, twitterCollection.metadata.position.minPosition);
            assertEquals(EXPECTED_NUM_TWEETS, twitterCollection.metadata.timelineItems.size());
            assertEquals(EXPECTED_TWEET_ID_FIRST,
                    twitterCollection.metadata.timelineItems.get(0).tweetItem.id);
            assertEquals(EXPECTED_TWEET_ID_SECOND,
                    twitterCollection.metadata.timelineItems.get(1).tweetItem.id);
        } finally {
            CommonUtils.closeQuietly(reader);
        }
    }
}
