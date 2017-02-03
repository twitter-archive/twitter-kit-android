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

package com.twitter.sdk.android.core.internal.scribe;

import com.twitter.sdk.android.core.TestFixtures;
import com.twitter.sdk.android.core.models.Card;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetBuilder;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.models.UserBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class ScribeItemTest {
    static final long TEST_ID = 123;
    static final long TEST_MEDIA_ID = 586671909L;
    static final String TEST_MESSAGE = "test message";
    static final ScribeItem.CardEvent TEST_CARD_EVENT = new ScribeItem.CardEvent(1);
    static final ScribeItem.MediaDetails TEST_MEDIA_DETAILS = new ScribeItem.MediaDetails(1, 2, 3);

    static final String TEST_TYPE_ANIMATED_GIF = "animated_gif";
    static final String TEST_TYPE_CONSUMER = "video";
    static final int TEST_TYPE_CONSUMER_ID = 1;
    static final int TEST_TYPE_ANIMATED_GIF_ID = 3;
    static final int TEST_TYPE_VINE_ID = 4;

    @Test
    public void testFromTweet() {
        final Tweet tweet = new TweetBuilder().setId(TEST_ID).build();
        final ScribeItem item = ScribeItem.fromTweet(tweet);

        assertEquals(Long.valueOf(TEST_ID), item.id);
        assertEquals(Integer.valueOf(ScribeItem.TYPE_TWEET), item.itemType);
        assertNull(item.description);
    }

    @Test
    public void testFromUser() {
        final User user = new UserBuilder().setId(TEST_ID).build();
        final ScribeItem item = ScribeItem.fromUser(user);

        assertEquals(Long.valueOf(TEST_ID), item.id);
        assertEquals(Integer.valueOf(ScribeItem.TYPE_USER), item.itemType);
        assertNull(item.description);
    }

    @Test
    public void testFromMediaEntity_withAnimatedGif() {
        final MediaEntity animatedGif = createTestEntity(TEST_TYPE_ANIMATED_GIF);
        final ScribeItem scribeItem = ScribeItem.fromMediaEntity(TEST_ID, animatedGif);

        assertEquals(Long.valueOf(TEST_ID), scribeItem.id);
        assertEquals(Integer.valueOf(ScribeItem.TYPE_TWEET), scribeItem.itemType);
        assertMediaDetails(scribeItem.mediaDetails, TEST_TYPE_ANIMATED_GIF_ID);
    }

    @Test
    public void testFromMediaEntity_withConsumerVideo() {
        final MediaEntity videoEntity = createTestEntity(TEST_TYPE_CONSUMER);
        final ScribeItem scribeItem = ScribeItem.fromMediaEntity(TEST_ID, videoEntity);

        assertEquals(Long.valueOf(TEST_ID), scribeItem.id);
        assertEquals(Integer.valueOf(ScribeItem.TYPE_TWEET), scribeItem.itemType);
        assertMediaDetails(scribeItem.mediaDetails, TEST_TYPE_CONSUMER_ID);
    }

    @Test
    public void testFromTweetCard() {
        final long tweetId = TEST_ID;
        final Card vineCard = TestFixtures.sampleValidVineCard();
        final ScribeItem scribeItem = ScribeItem.fromTweetCard(tweetId, vineCard);

        assertEquals(Long.valueOf(TEST_ID), scribeItem.id);
        assertEquals(Integer.valueOf(ScribeItem.TYPE_TWEET), scribeItem.itemType);
        assertMediaDetails(scribeItem.mediaDetails, TEST_TYPE_VINE_ID);
    }

    @Test
    public void testFromMessage() {
        final ScribeItem item = ScribeItem.fromMessage(TEST_MESSAGE);

        assertNull(item.id);
        assertEquals(Integer.valueOf(ScribeItem.TYPE_MESSAGE), item.itemType);
        assertEquals(TEST_MESSAGE, item.description);
    }

    @Test
    public void testBuilder() {
        final ScribeItem item = new ScribeItem.Builder()
                .setId(TEST_ID)
                .setItemType(ScribeItem.TYPE_MESSAGE)
                .setDescription(TEST_MESSAGE)
                .setCardEvent(TEST_CARD_EVENT)
                .setMediaDetails(TEST_MEDIA_DETAILS)
                .build();

        assertEquals(Long.valueOf(TEST_ID), item.id);
        assertEquals(Integer.valueOf(ScribeItem.TYPE_MESSAGE), item.itemType);
        assertEquals(TEST_MESSAGE, item.description);
        assertEquals(TEST_CARD_EVENT, item.cardEvent);
        assertEquals(TEST_MEDIA_DETAILS, item.mediaDetails);
    }

    @Test
    public void testBuilder_empty() {
        final ScribeItem item = new ScribeItem.Builder().build();

        assertNull(item.id);
        assertNull(item.itemType);
        assertNull(item.description);
        assertNull(item.cardEvent);
        assertNull(item.mediaDetails);
    }


    static void assertMediaDetails(ScribeItem.MediaDetails mediaDetails, int type) {
        assertNotNull(mediaDetails);
        assertEquals(TEST_ID, mediaDetails.contentId);
        assertEquals(type, mediaDetails.mediaType);
        assertEquals(TEST_MEDIA_ID, mediaDetails.publisherId);
    }

    private MediaEntity createTestEntity(String type) {
        return new MediaEntity(null, null, null, 0, 0, TEST_MEDIA_ID, null, null, null, null, 0,
                null, type, null, "");
    }
}
