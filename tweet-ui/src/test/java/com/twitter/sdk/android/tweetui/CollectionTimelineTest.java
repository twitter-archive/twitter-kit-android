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

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetBuilder;
import com.twitter.sdk.android.core.models.TwitterCollection;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.models.UserBuilder;
import com.twitter.sdk.android.core.services.CollectionService;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CollectionTimelineTest {
    private static final String ILLEGAL_TWEET_UI_MESSAGE = "TweetUi instance must not be null";
    private static final Long TEST_COLLECTION_ID = 393773266801659904L;
    private static final Integer TEST_ITEMS_PER_REQUEST = 100;
    private static final Integer REQUIRED_DEFAULT_ITEMS_PER_REQUEST = 30;
    private static final String REQUIRED_IMPRESSION_SECTION = "collection";
    private static final Long TEST_MAX_POSITION = 1111L;
    private static final Long TEST_MIN_POSITION = 1000L;
    private static final TwitterCollection.Metadata.Position TEST_POSITION
            = new TwitterCollection.Metadata.Position(TEST_MAX_POSITION, TEST_MIN_POSITION);
    private static final User TEST_USER_1 = new UserBuilder().setName("foo").setId(40L).build();
    private static final User TEST_USER_2 = new UserBuilder().setName("bar").setId(41L).build();
    private static final Tweet TEST_TWEET_1
            = new TweetBuilder().setId(5858L).setUser(TEST_USER_1).build();
    private static final Tweet TEST_TWEET_2
            = new TweetBuilder().setId(8585L).setUser(TEST_USER_1).build();
    private static final Tweet TEST_TWEET_QUOTE
            = new TweetBuilder().setId(858909L).setUser(TEST_USER_1).setQuotedStatus(TEST_TWEET_2)
            .build();

    private Map<Long, Tweet> testTweetMap = new HashMap<>();
    private Map<Long, User> testUserMap = new HashMap<>();
    private List<TwitterCollection.TimelineItem> testItems = new ArrayList<>();
    private List<TwitterCollection.TimelineItem> testItemsRev = new ArrayList<>();

    TwitterCore twitterCore;
    TwitterApiClient apiClient;
    CollectionService collectionService;

    @Before
    public void setUp() throws Exception {
        testUserMap.put(TEST_USER_1.id, TEST_USER_1);
        testUserMap.put(TEST_USER_2.id, TEST_USER_2);
        testTweetMap.put(TEST_TWEET_1.id, TEST_TWEET_1);
        testTweetMap.put(TEST_TWEET_2.id, TEST_TWEET_2);
        testTweetMap.put(TEST_TWEET_QUOTE.id, TEST_TWEET_QUOTE);
        // testItems order Test Tweet 1, then 2
        testItems.add(new TwitterCollection.TimelineItem(
                new TwitterCollection.TimelineItem.TweetItem(5858L)));
        testItems.add(new TwitterCollection.TimelineItem(
                new TwitterCollection.TimelineItem.TweetItem(8585L)));
        testItems.add(new TwitterCollection.TimelineItem(
                new TwitterCollection.TimelineItem.TweetItem(858909L)));
        // testItemsRev orders Test Tweet 2, then 1
        testItemsRev.add(new TwitterCollection.TimelineItem(
                new TwitterCollection.TimelineItem.TweetItem(858909L)));
        testItemsRev.add(new TwitterCollection.TimelineItem(
                new TwitterCollection.TimelineItem.TweetItem(8585L)));
        testItemsRev.add(new TwitterCollection.TimelineItem(
                new TwitterCollection.TimelineItem.TweetItem(5858L)));

        twitterCore = mock(TwitterCore.class);
        apiClient = mock(TwitterApiClient.class);
        collectionService = mock(CollectionService.class, new MockCallAnswer());

        when(apiClient.getCollectionService()).thenReturn(collectionService);
        when(twitterCore.getApiClient()).thenReturn(apiClient);
    }

    @Test
    public void testConstructor() {
        final CollectionTimeline timeline = new CollectionTimeline(twitterCore, TEST_COLLECTION_ID,
                TEST_ITEMS_PER_REQUEST);
        assertEquals(CollectionTimeline.COLLECTION_PREFIX + TEST_COLLECTION_ID,
                timeline.collectionIdentifier);
    }

    @Test
    public void testNext_createsCorrectRequest() {
        final CollectionTimeline timeline = spy(new CollectionTimeline(twitterCore,
                TEST_COLLECTION_ID, TEST_ITEMS_PER_REQUEST));
        timeline.next(TEST_MIN_POSITION, mock(Callback.class));
        verify(timeline).createCollectionRequest(eq(TEST_MIN_POSITION), isNull(Long.class));
    }

    @Test
    public void testPrevious_createsCorrectRequest() {
        final CollectionTimeline timeline = spy(new CollectionTimeline(twitterCore,
                TEST_COLLECTION_ID, TEST_ITEMS_PER_REQUEST));
        timeline.next(TEST_MAX_POSITION, mock(Callback.class));
        verify(timeline).createCollectionRequest(eq(TEST_MAX_POSITION), isNull(Long.class));
    }

    @Test
    public void testCreateCollectionRequest() {
        // build a timeline with test params
        final CollectionTimeline timeline = new CollectionTimeline(twitterCore,
                TEST_COLLECTION_ID, TEST_ITEMS_PER_REQUEST);

        // create a request directly
        timeline.createCollectionRequest(TEST_MIN_POSITION, TEST_MAX_POSITION);

        // assert collection call is made with the correct arguments
        verify(twitterCore.getApiClient().getCollectionService()).collection(
                eq(CollectionTimeline.COLLECTION_PREFIX + TEST_COLLECTION_ID),
                eq(TEST_ITEMS_PER_REQUEST), eq(TEST_MAX_POSITION), eq(TEST_MIN_POSITION));
    }

    @Test
    public void testGetScribeSection() {
        final CollectionTimeline timeline = new CollectionTimeline.Builder(twitterCore)
                .id(TEST_COLLECTION_ID)
                .build();
        assertEquals(REQUIRED_IMPRESSION_SECTION, timeline.getTimelineType());
    }

    @Test
    public void testGetOrderedTweets() {
        final TwitterCollection.Content contents
                = new TwitterCollection.Content(testTweetMap, testUserMap);
        final TwitterCollection.Metadata metadata
                = new TwitterCollection.Metadata("", TEST_POSITION, testItems);
        final List<Tweet> tweets = CollectionTimeline.getOrderedTweets(
                new TwitterCollection(contents, metadata));
        assertEquals(3, tweets.size());
        assertEquals(TEST_TWEET_1, tweets.get(0));
        assertEquals(TEST_TWEET_2, tweets.get(1));
        assertEquals(TEST_TWEET_QUOTE, tweets.get(2));
    }

    @Test
    public void testGetOrderedTweets_respectsTimelineItemsOrder() {
        final TwitterCollection.Content contents = new TwitterCollection.Content(testTweetMap,
                testUserMap);
        final TwitterCollection.Metadata metadata = new TwitterCollection.Metadata("",
                TEST_POSITION, testItemsRev);
        final List<Tweet> tweets = CollectionTimeline.getOrderedTweets(
                new TwitterCollection(contents, metadata));
        assertEquals(3, tweets.size());
        assertEquals(TEST_TWEET_QUOTE, tweets.get(0));
        assertEquals(TEST_TWEET_2, tweets.get(1));
        assertEquals(TEST_TWEET_1, tweets.get(2));
    }

    @Test
    public void testGetOrderedTweets_handlesNull() {
        TwitterCollection collection = new TwitterCollection(
            new TwitterCollection.Content(null, testUserMap),
            new TwitterCollection.Metadata("", TEST_POSITION, testItems));
        List<Tweet> tweets = CollectionTimeline.getOrderedTweets(collection);
        assertTrue(tweets.isEmpty());
        collection = new TwitterCollection(new TwitterCollection.Content(testTweetMap, null),
                new TwitterCollection.Metadata("", TEST_POSITION, testItems));
        tweets = CollectionTimeline.getOrderedTweets(collection);
        assertTrue(tweets.isEmpty());
        collection = new TwitterCollection(new TwitterCollection.Content(testTweetMap, testUserMap),
                new TwitterCollection.Metadata("", null, testItems));
        tweets = CollectionTimeline.getOrderedTweets(collection);
        assertTrue(tweets.isEmpty());
        collection = new TwitterCollection(new TwitterCollection.Content(testTweetMap, testUserMap),
                new TwitterCollection.Metadata("", TEST_POSITION, null));
        tweets = CollectionTimeline.getOrderedTweets(collection);
        assertTrue(tweets.isEmpty());
        collection = new TwitterCollection(new TwitterCollection.Content(testTweetMap, testUserMap),
                null);
        tweets = CollectionTimeline.getOrderedTweets(collection);
        assertTrue(tweets.isEmpty());
        collection = new TwitterCollection(null, new TwitterCollection.Metadata("", TEST_POSITION,
                testItems));
        tweets = CollectionTimeline.getOrderedTweets(collection);
        assertTrue(tweets.isEmpty());
        collection = new TwitterCollection(null, null);
        tweets = CollectionTimeline.getOrderedTweets(collection);
        assertTrue(tweets.isEmpty());
        tweets = CollectionTimeline.getOrderedTweets(null);
        assertTrue(tweets.isEmpty());
    }

    @Test
    public void testGetTimelineCursor() {
        final TwitterCollection.Content contents
                = new TwitterCollection.Content(testTweetMap, testUserMap);
        final TwitterCollection.Metadata metadata
                = new TwitterCollection.Metadata("", TEST_POSITION, testItems);
        final TimelineCursor cursor = CollectionTimeline.getTimelineCursor(
                new TwitterCollection(contents, metadata));
        assertEquals(TEST_MAX_POSITION, cursor.maxPosition);
        assertEquals(TEST_MIN_POSITION, cursor.minPosition);
    }

    @Test
    public void testGetTimelineCursor_handlesNull() {
        TwitterCollection collection = new TwitterCollection(new TwitterCollection.Content(null,
                testUserMap), new TwitterCollection.Metadata("", null, testItems));
        TimelineCursor timelineCursor = CollectionTimeline.getTimelineCursor(collection);
        assertNull(timelineCursor);
        collection = new TwitterCollection(new TwitterCollection.Content(null, testUserMap), null);
        timelineCursor = CollectionTimeline.getTimelineCursor(collection);
        assertNull(timelineCursor);
        timelineCursor = CollectionTimeline.getTimelineCursor(null);
        assertNull(timelineCursor);
    }

    /* Builder */
    @Test
    public void testBuilder() {
        final CollectionTimeline timeline = new CollectionTimeline.Builder(twitterCore)
                .id(TEST_COLLECTION_ID)
                .maxItemsPerRequest(TEST_ITEMS_PER_REQUEST)
                .build();
        assertEquals(CollectionTimeline.COLLECTION_PREFIX + TEST_COLLECTION_ID,
                timeline.collectionIdentifier);
        assertEquals(TEST_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest);
    }

    @Test
    public void testBuilder_defaults() {
        final CollectionTimeline timeline = new CollectionTimeline.Builder(twitterCore)
                .id(TEST_COLLECTION_ID)
                .build();
        assertEquals(CollectionTimeline.COLLECTION_PREFIX + TEST_COLLECTION_ID,
                timeline.collectionIdentifier);
        assertEquals(REQUIRED_DEFAULT_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest);
    }

    @Test
    public void testBuilder_id() {
        final CollectionTimeline timeline = new CollectionTimeline.Builder(twitterCore)
                .id(TEST_COLLECTION_ID)
                .build();
        assertEquals(CollectionTimeline.COLLECTION_PREFIX + TEST_COLLECTION_ID,
                timeline.collectionIdentifier);
    }

    @Test
    public void testBuilder_idNull() {
        try {
            new CollectionTimeline.Builder(twitterCore).id(null).build();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("collection id must not be null", e.getMessage());
        }
    }

    @Test
    public void testBuilder_maxItemsPerRequest() {
        final CollectionTimeline timeline = new CollectionTimeline.Builder(twitterCore)
                .id(TEST_COLLECTION_ID)
                .maxItemsPerRequest(TEST_ITEMS_PER_REQUEST)
                .build();
        assertEquals(TEST_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest);
    }
}
