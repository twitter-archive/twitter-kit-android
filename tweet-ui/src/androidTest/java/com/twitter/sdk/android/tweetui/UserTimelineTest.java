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
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.GuestCallback;
import com.twitter.sdk.android.core.services.StatusesService;

import static org.mockito.Mockito.*;

public class UserTimelineTest extends TweetUiTestCase {
    private static final String ILLEGAL_TWEET_UI_MESSAGE = "TweetUi instance must not be null";
    private static final Integer REQUIRED_DEFAULT_ITEMS_PER_REQUEST = 30;
    private static final Integer TEST_ITEMS_PER_REQUEST = 100;
    private static final Long TEST_SINCE_ID = 1000L;
    private static final Long TEST_MAX_ID = 1111L;
    private static final String REQUIRED_IMPRESSION_SECTION = "user";

    public void testConstructor() {
        final UserTimeline timeline = new UserTimeline(tweetUi, TestFixtures.TEST_USER.id,
                TestFixtures.TEST_USER.screenName, TEST_ITEMS_PER_REQUEST, true, true);
        assertEquals(tweetUi, timeline.tweetUi);
        assertEquals((Long) TestFixtures.TEST_USER.id, timeline.userId);
        assertEquals(TestFixtures.TEST_USER.screenName, timeline.screenName);
        assertEquals(TEST_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest);
        assertTrue(timeline.includeReplies);
        assertTrue(timeline.includeRetweets);
    }

    public void testConstructor_nullTweetUi() {
        try {
            new UserTimeline(null, null, null, null, null, null);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals(ILLEGAL_TWEET_UI_MESSAGE, e.getMessage());
        }
    }

    // most api arguments should default to Null to allow the backend to determine default behavior
    public void testConstructor_defaults() {
        final UserTimeline timeline = new UserTimeline(tweetUi, null, null, null, null, null);
        assertEquals(tweetUi, timeline.tweetUi);
        assertNull(timeline.userId);
        assertNull(timeline.screenName);
        assertNull(timeline.maxItemsPerRequest);
        assertNull(timeline.includeRetweets);
        // parameters which default to false
        assertFalse(timeline.includeReplies);
    }

    public void testNext_createsCorrectRequest() {
        final UserTimeline timeline = spy(new TestUserTimeline(tweetUi, TestFixtures.TEST_USER.id,
                TestFixtures.TEST_USER.screenName, TEST_ITEMS_PER_REQUEST, null, null));
        timeline.next(TEST_SINCE_ID, mock(Callback.class));
        verify(timeline, times(1)).createUserTimelineRequest(eq(TEST_SINCE_ID),
                isNull(Long.class), any(Callback.class));
        verify(timeline, times(1)).addRequest(any(Callback.class));
    }

    public void testPrevious_createsCorrectRequest() {
        final UserTimeline timeline = spy(new TestUserTimeline(tweetUi, TestFixtures.TEST_USER.id,
                TestFixtures.TEST_USER.screenName, TEST_ITEMS_PER_REQUEST, null, null));
        timeline.previous(TEST_MAX_ID, mock(Callback.class));
        // intentionally decrementing the maxId which is passed through to the request
        verify(timeline, times(1)).createUserTimelineRequest(isNull(Long.class),
                eq(TEST_MAX_ID - 1), any(Callback.class));
        verify(timeline, times(1)).addRequest(any(Callback.class));
    }

    public void testCreateUserTimelineRequest() {
        // build a timeline with test params
        final UserTimeline timeline = new UserTimeline(tweetUi, TestFixtures.TEST_USER.id,
                TestFixtures.TEST_USER.screenName, TEST_ITEMS_PER_REQUEST, null, null);
        // create a request (Callback<TwitterApiClient>) directly
        final Callback<TwitterApiClient> request = timeline.createUserTimelineRequest(TEST_SINCE_ID,
                TEST_MAX_ID, mock(Callback.class));
        final TwitterApiClient mockTwitterApiClient = mock(TwitterApiClient.class);
        final StatusesService mockStatusesService = mock(StatusesService.class);
        when(mockTwitterApiClient.getStatusesService()).thenReturn(mockStatusesService);
        // execute request with mock auth'd TwitterApiClient (auth queue tested separately)
        request.success(new Result<>(mockTwitterApiClient, null));
        // assert statuses service is requested once
        verify(mockTwitterApiClient, times(1)).getStatusesService();
        // assert userTimeline call is made with the correct arguments
        verify(mockStatusesService, times(1)).userTimeline(eq(TestFixtures.TEST_USER.id),
                eq(TestFixtures.TEST_USER.screenName), eq(TEST_ITEMS_PER_REQUEST),
                eq(TEST_SINCE_ID), eq(TEST_MAX_ID), eq(false), eq(true), isNull(Boolean.class),
                isNull(Boolean.class), any(GuestCallback.class));
    }

    public void testGetScribeSection() {
        final UserTimeline timeline = new UserTimeline.Builder().build();
        assertEquals(REQUIRED_IMPRESSION_SECTION, timeline.getTimelineType());
    }

    /* Builder */

    public void testBuilder() {
        final UserTimeline timeline = new UserTimeline.Builder(tweetUi)
                .userId(TestFixtures.TEST_USER.id)
                .screenName(TestFixtures.TEST_USER.screenName)
                .maxItemsPerRequest(TEST_ITEMS_PER_REQUEST)
                .includeReplies(true)
                .includeRetweets(true)
                .build();
        assertEquals(tweetUi, timeline.tweetUi);
        assertEquals((Long) TestFixtures.TEST_USER.id, timeline.userId);
        assertEquals(TestFixtures.TEST_USER.screenName, timeline.screenName);
        assertEquals(TEST_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest);
        assertTrue(timeline.includeReplies);
        assertTrue(timeline.includeRetweets);
    }

    public void testBuilder_empty() {
        final UserTimeline timeline = new UserTimeline.Builder().build();
        assertNotNull(timeline.tweetUi);
    }

    // api arguments should default to Null to allow the backend to determine default behavior
    public void testBuilder_defaults() {
        final UserTimeline timeline = new UserTimeline.Builder(tweetUi).build();
        assertEquals(tweetUi, timeline.tweetUi);
        assertNull(timeline.userId);
        assertNull(timeline.screenName);
        assertEquals(REQUIRED_DEFAULT_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest);
        assertNull(timeline.includeRetweets);
        // parameters which default to false
        assertFalse(timeline.includeReplies);
    }

    public void testBuilder_nullTweetUi() {
        try {
            new UserTimeline.Builder(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(ILLEGAL_TWEET_UI_MESSAGE, e.getMessage());
        }
    }

    public void testBuilder_userId() {
        final Long USER_ID = TestFixtures.TEST_USER.id;
        final UserTimeline timeline = new UserTimeline.Builder(tweetUi)
                .userId(USER_ID)
                .build();
        assertEquals(USER_ID, timeline.userId);
    }

    public void testBuilder_screenName() {
        final UserTimeline timeline = new UserTimeline.Builder(tweetUi)
                .screenName(TestFixtures.TEST_USER.screenName)
                .build();
        assertEquals(TestFixtures.TEST_USER.screenName, timeline.screenName);
    }

    public void testBuilder_maxItemsPerRequest() {
        final UserTimeline timeline = new UserTimeline.Builder(tweetUi)
                .maxItemsPerRequest(TEST_ITEMS_PER_REQUEST)
                .build();
        assertEquals(TEST_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest);
    }

    public void testBuilder_includeReplies() {
        // null includeReplies defaults to false
        UserTimeline timeline = new UserTimeline.Builder(tweetUi).build();
        assertFalse(timeline.includeReplies);
        timeline = new UserTimeline.Builder(tweetUi).includeReplies(true).build();
        assertTrue(timeline.includeReplies);
        timeline = new UserTimeline.Builder(tweetUi).includeReplies(false).build();
        assertFalse(timeline.includeReplies);
    }

    public void testBuilder_includeRetweets() {
        UserTimeline timeline = new UserTimeline.Builder(tweetUi).build();
        assertNull(timeline.includeRetweets);
        timeline = new UserTimeline.Builder(tweetUi).includeRetweets(true).build();
        assertTrue(timeline.includeRetweets);
        timeline = new UserTimeline.Builder(tweetUi).includeRetweets(false).build();
        assertFalse(timeline.includeRetweets);
    }
}
