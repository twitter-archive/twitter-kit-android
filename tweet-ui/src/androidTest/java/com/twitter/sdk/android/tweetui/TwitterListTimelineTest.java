package com.twitter.sdk.android.tweetui;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.internal.GuestCallback;
import com.twitter.sdk.android.core.services.ListService;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class TwitterListTimelineTest extends TweetUiTestCase {
    private static final String ILLEGAL_TWEET_UI_MESSAGE = "TweetUi instance must not be null";
    private static final Integer REQUIRED_DEFAULT_ITEMS_PER_REQUEST = 30;
    private static final Long TEST_LIST_ID = 128271137L;
    private static final String TEST_SLUG = "cool-accounts";
    private static final Long TEST_OWNER_ID =  623265148L;
    private static final String TEST_OWNER_SCREEN_NAME = "dghubble";
    private static final Integer TEST_ITEMS_PER_REQUEST = 100;
    private static final Long TEST_SINCE_ID = 1000L;
    private static final Long TEST_MAX_ID = 1111L;
    private static final String REQUIRED_IMPRESSION_SECTION = "list";

    public void testConstructor() {
        final TwitterListTimeline timeline = new TwitterListTimeline(tweetUi, TEST_LIST_ID,
                TEST_SLUG, TEST_OWNER_ID, TEST_OWNER_SCREEN_NAME, TEST_ITEMS_PER_REQUEST, true);
        assertEquals(tweetUi, timeline.tweetUi);
        assertEquals(TEST_LIST_ID, timeline.listId);
        assertEquals(TEST_SLUG, timeline.slug);
        assertEquals(TEST_OWNER_ID, timeline.ownerId);
        assertEquals(TEST_OWNER_SCREEN_NAME, timeline.ownerScreenName);
        assertEquals(TEST_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest);
        assertTrue(timeline.includeRetweets);
    }

    public void testConstructor_nullTweetUi() {
        try {
            new TwitterListTimeline(null, null, null, null, null, null, null);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals(ILLEGAL_TWEET_UI_MESSAGE, e.getMessage());
        }
    }

    // most api arguments should default to Null to allow the backend to determine default behavior
    public void testConstructor_defaults() {
        final TwitterListTimeline timeline = new TwitterListTimeline(tweetUi, TEST_LIST_ID, null,
                null, null, null, null);
        assertEquals(tweetUi, timeline.tweetUi);
        assertEquals(TEST_LIST_ID, timeline.listId);
        assertNull(timeline.slug);
        assertNull(timeline.ownerId);
        assertNull(timeline.ownerScreenName);
        assertNull(timeline.maxItemsPerRequest);
        assertNull(timeline.includeRetweets);
    }

    public void testCreateListTimelineRequest() {
        // build a timeline with test params
        final TwitterListTimeline timeline = new TwitterListTimeline(tweetUi, TEST_LIST_ID,
                TEST_SLUG, TEST_OWNER_ID, TEST_OWNER_SCREEN_NAME, TEST_ITEMS_PER_REQUEST, true);
        // create a request (Callback<TwitterApiClient>) directly
        final Callback<TwitterApiClient> request = timeline.createListTimelineRequest(TEST_SINCE_ID,
                TEST_MAX_ID, mock(Callback.class));
        final TwitterApiClient mockTwitterApiClient = mock(TwitterApiClient.class);
        final ListService mockListService = mock(ListService.class);
        when(mockTwitterApiClient.getListService()).thenReturn(mockListService);
        // execute request with mock auth'd TwitterApiClient (auth queue tested separately)
        request.success(new Result<>(mockTwitterApiClient, null));
        // assert list service is requested once
        verify(mockTwitterApiClient).getListService();
        // assert twitterListTimeline call is made with the correct arguments
        verify(mockListService).statuses(eq(TEST_LIST_ID), eq(TEST_SLUG),
                eq(TEST_OWNER_SCREEN_NAME), eq(TEST_OWNER_ID), eq(TEST_SINCE_ID), eq(TEST_MAX_ID),
                eq(TEST_ITEMS_PER_REQUEST), eq(true), eq(true), any(GuestCallback.class));
    }

    public void testGetScribeSection() {
        final TwitterListTimeline timeline = new TwitterListTimeline.Builder().id(TEST_LIST_ID)
                .build();
        assertEquals(REQUIRED_IMPRESSION_SECTION, timeline.getTimelineType());
    }

    /* Builder */

    public void testBuilder_viaLlistId() {
        final TwitterListTimeline timeline = new TwitterListTimeline.Builder(tweetUi)
                .id(TEST_LIST_ID)
                .maxItemsPerRequest(TEST_ITEMS_PER_REQUEST)
                .includeRetweets(true)
                .build();
        assertEquals(tweetUi, timeline.tweetUi);
        assertEquals(TEST_LIST_ID, timeline.listId);
        assertEquals(TEST_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest);
        assertTrue(timeline.includeRetweets);
    }

    public void testBuilder_viaSlugOwnerId() {
        final TwitterListTimeline timeline = new TwitterListTimeline.Builder(tweetUi)
                .slugWithOwnerId(TEST_SLUG, TEST_OWNER_ID)
                .maxItemsPerRequest(TEST_ITEMS_PER_REQUEST)
                .includeRetweets(true)
                .build();
        assertEquals(tweetUi, timeline.tweetUi);
        assertEquals(TEST_SLUG, timeline.slug);
        assertEquals(TEST_OWNER_ID, timeline.ownerId);
        assertEquals(TEST_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest);
        assertTrue(timeline.includeRetweets);
    }

    public void testBuilder_viaSlugOwnerScreenName() {
        final TwitterListTimeline timeline = new TwitterListTimeline.Builder(tweetUi)
                .slugWithOwnerScreenName(TEST_SLUG, TEST_OWNER_SCREEN_NAME)
                .maxItemsPerRequest(TEST_ITEMS_PER_REQUEST)
                .includeRetweets(true)
                .build();
        assertEquals(tweetUi, timeline.tweetUi);
        assertEquals(TEST_SLUG, timeline.slug);
        assertEquals(TEST_OWNER_SCREEN_NAME, timeline.ownerScreenName);
        assertEquals(TEST_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest);
        assertTrue(timeline.includeRetweets);
    }

    public void testBuilder_nullTweetUi() {
        try {
            new TwitterListTimeline.Builder(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(ILLEGAL_TWEET_UI_MESSAGE, e.getMessage());
        }
    }

    // api arguments should default to Null to allow the backend to determine default behavior
    public void testBuilder_defaults() {
        final TwitterListTimeline timeline = new TwitterListTimeline.Builder(tweetUi)
                .id(TEST_LIST_ID)
                .build();
        assertEquals(tweetUi, timeline.tweetUi);
        assertEquals(REQUIRED_DEFAULT_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest);
        assertNull(timeline.includeRetweets);
    }

    public void testBuilder_listId() {
        final TwitterListTimeline timeline = new TwitterListTimeline.Builder(tweetUi)
                .id(TEST_LIST_ID)
                .build();
        assertEquals(TEST_LIST_ID, timeline.listId);
    }

    public void testBuilder_slugWithOwnerId() {
        final TwitterListTimeline timeline = new TwitterListTimeline.Builder(tweetUi)
                .slugWithOwnerId(TEST_SLUG, TEST_OWNER_ID)
                .build();
        assertEquals(TEST_SLUG, timeline.slug);
        assertEquals(TEST_OWNER_ID, timeline.ownerId);
    }

    public void testBuilder_slugWithOwnerScreenName() {
        final TwitterListTimeline timeline = new TwitterListTimeline.Builder(tweetUi)
                .slugWithOwnerScreenName(TEST_SLUG, TEST_OWNER_SCREEN_NAME)
                .build();
        assertEquals(TEST_SLUG, timeline.slug);
        assertEquals(TEST_OWNER_SCREEN_NAME, timeline.ownerScreenName);
    }

    public void testBuilder_maxItemsPerRequest() {
        final TwitterListTimeline timeline = new TwitterListTimeline.Builder(tweetUi)
                .id(TEST_LIST_ID)
                .maxItemsPerRequest(TEST_ITEMS_PER_REQUEST)
                .build();
        assertEquals(TEST_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest);
    }

    public void testBuilder_includeRetweets() {
        TwitterListTimeline timeline = new TwitterListTimeline.Builder(tweetUi).id(TEST_LIST_ID)
                .build();
        assertNull(timeline.includeRetweets);
        timeline = new TwitterListTimeline.Builder(tweetUi).id(TEST_LIST_ID).includeRetweets(true)
            .build();
        assertTrue(timeline.includeRetweets);
        timeline = new TwitterListTimeline.Builder(tweetUi).id(TEST_LIST_ID).includeRetweets(false)
                .build();
        assertFalse(timeline.includeRetweets);
    }

    public void testBuilder_noIdOrSlugOwnerPair() {
        try {
            new TwitterListTimeline.Builder(tweetUi).build();
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("must specify either a list id or slug/owner pair", e.getMessage());
        }
    }

    public void testBuilder_bothIdAndSlugOwnerPair() {
        try {
            new TwitterListTimeline.Builder(tweetUi)
                    .id(TEST_LIST_ID)
                    .slugWithOwnerId(TEST_SLUG, TEST_OWNER_ID)
                    .build();
        } catch (IllegalStateException e) {
            assertEquals("must specify either a list id or slug/owner pair", e.getMessage());
        }
    }
}
