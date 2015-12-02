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

import com.twitter.sdk.android.core.internal.scribe.EventNamespace;
import com.twitter.sdk.android.core.internal.scribe.ScribeItem;
import com.twitter.sdk.android.core.internal.scribe.SyndicationClientEvent;
import com.twitter.sdk.android.core.models.MediaEntity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

public class PlayerScribeClientImplTest {
    static final long TEST_MEDIA_ID = 123456789L;
    static final String TEST_TYPE_CONSUMER = "video";
    static final int TEST_TYPE_CONSUMER_ID = 1;
    static final String TEST_TYPE_ANIMATED_GIF = "animated_gif";
    static final int TEST_TYPE_ANIMATED_GIF_ID = 3;
    static final String TEST_TFW_CLIENT_EVENT_PAGE = "android";
    static final String TEST_TFW_CLIENT_EVENT_SECTION = "video";
    static final String TEST_SCRIBE_IMPRESSION_ACTION = "impression";

    private PlayerScribeClientImpl scribeClient;
    @Mock
    private TweetUi tweetUi;
    @Captor
    private ArgumentCaptor<List<ScribeItem>> itemsArgumentCaptor;
    @Captor
    private ArgumentCaptor<EventNamespace> namespaceArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        scribeClient = new PlayerScribeClientImpl(tweetUi);
    }

    @Test
    public void testImpression() {
        scribeClient.impression(TestFixtures.TEST_TWEET_ID,
                createTestEntity(TEST_TYPE_ANIMATED_GIF));

        verify(tweetUi).scribe(namespaceArgumentCaptor.capture(), itemsArgumentCaptor.capture());

        final EventNamespace ns = namespaceArgumentCaptor.getValue();
        assertEquals(SyndicationClientEvent.CLIENT_NAME, ns.client);
        assertEquals(TEST_TFW_CLIENT_EVENT_PAGE, ns.page);
        assertEquals(TEST_TFW_CLIENT_EVENT_SECTION, ns.section);
        assertNull(ns.element);
        assertNull(ns.component);
        assertEquals(TEST_SCRIBE_IMPRESSION_ACTION, ns.action);

        final List<ScribeItem> items = itemsArgumentCaptor.getValue();
        assertItems(items);
    }

    @Test
    public void testCreateScribeItem_withAnimatedGif() {
        final ScribeItem.MediaDetails mediaDetails = scribeClient.createMediaDetails(
                TestFixtures.TEST_TWEET_ID, createTestEntity(TEST_TYPE_ANIMATED_GIF));

        assertMediaDetails(mediaDetails, TEST_TYPE_ANIMATED_GIF_ID);
    }

    @Test
    public void testCreateScribeItem_withConsumerVideo() {
        final ScribeItem.MediaDetails mediaDetails = scribeClient.createMediaDetails(
                TestFixtures.TEST_TWEET_ID, createTestEntity(TEST_TYPE_CONSUMER));

        assertMediaDetails(mediaDetails, TEST_TYPE_CONSUMER_ID);
    }

    static void assertItems(List<ScribeItem> items) {
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(TestFixtures.TEST_TWEET_ID, items.get(0).id.longValue());
        assertEquals(ScribeItem.TYPE_TWEET, items.get(0).itemType.intValue());

        assertMediaDetails(items.get(0).mediaDetails, TEST_TYPE_ANIMATED_GIF_ID);
    }

    static void assertMediaDetails(ScribeItem.MediaDetails mediaDetails, int type) {
        assertNotNull(mediaDetails);
        assertEquals(TestFixtures.TEST_TWEET_ID, mediaDetails.contentId);
        assertEquals(type, mediaDetails.mediaType);
        assertEquals(TEST_MEDIA_ID, mediaDetails.publisherId);
    }

    private MediaEntity createTestEntity(String type) {
        return new MediaEntity(null, null, null, 0, 0, TEST_MEDIA_ID, null, null, null, null, 0,
                null, type, null);
    }
}
