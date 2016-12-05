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
import com.twitter.sdk.android.core.TwitterCore;

import static org.mockito.Mockito.*;

public class UserTimelineTest extends TweetUiTestCase {
    private static final Integer REQUIRED_DEFAULT_ITEMS_PER_REQUEST = 30;
    private static final Integer TEST_ITEMS_PER_REQUEST = 100;
    private static final Long TEST_SINCE_ID = 1000L;
    private static final Long TEST_MAX_ID = 1111L;
    private static final String REQUIRED_IMPRESSION_SECTION = "user";

    public void testConstructor() {
        final UserTimeline timeline = new UserTimeline(TestFixtures.TEST_USER.id,
                TestFixtures.TEST_USER.screenName, TEST_ITEMS_PER_REQUEST, true, true);
        assertEquals((Long) TestFixtures.TEST_USER.id, timeline.userId);
        assertEquals(TestFixtures.TEST_USER.screenName, timeline.screenName);
        assertEquals(TEST_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest);
        assertTrue(timeline.includeReplies);
        assertTrue(timeline.includeRetweets);
    }

    // most api arguments should default to Null to allow the backend to determine default behavior
    public void testConstructor_defaults() {
        final UserTimeline timeline = new UserTimeline(null, null, null, null, null);
        assertNull(timeline.userId);
        assertNull(timeline.screenName);
        assertNull(timeline.maxItemsPerRequest);
        assertNull(timeline.includeRetweets);
        // parameters which default to false
        assertFalse(timeline.includeReplies);
    }

    public void testNext_createsCorrectRequest() {
        final UserTimeline timeline = spy(new TestUserTimeline(TestFixtures.TEST_USER.id,
                TestFixtures.TEST_USER.screenName, TEST_ITEMS_PER_REQUEST, null, null));
        timeline.next(TEST_SINCE_ID, mock(Callback.class));
        verify(timeline, times(1)).createUserTimelineRequest(eq(TEST_SINCE_ID),
                isNull(Long.class));
    }

    public void testPrevious_createsCorrectRequest() {
        final UserTimeline timeline = spy(new TestUserTimeline(TestFixtures.TEST_USER.id,
                TestFixtures.TEST_USER.screenName, TEST_ITEMS_PER_REQUEST, null, null));
        timeline.previous(TEST_MAX_ID, mock(Callback.class));
        // intentionally decrementing the maxId which is passed through to the request
        verify(timeline, times(1)).createUserTimelineRequest(isNull(Long.class),
                eq(TEST_MAX_ID - 1));
    }

    public void testCreateUserTimelineRequest() {
        // build a timeline with test params
        final UserTimeline timeline = new UserTimeline(TestFixtures.TEST_USER.id,
                TestFixtures.TEST_USER.screenName, TEST_ITEMS_PER_REQUEST, null, null);

        // create a request directly
        timeline.createUserTimelineRequest(TEST_SINCE_ID, TEST_MAX_ID);

        // assert userTimeline call is made with the correct arguments
        verify(TwitterCore.getInstance().getApiClient().getStatusesService())
                .userTimeline(eq(TestFixtures.TEST_USER.id),
                        eq(TestFixtures.TEST_USER.screenName), eq(TEST_ITEMS_PER_REQUEST),
                        eq(TEST_SINCE_ID), eq(TEST_MAX_ID), eq(false), eq(true),
                        isNull(Boolean.class), isNull(Boolean.class));
    }

    public void testGetScribeSection() {
        final UserTimeline timeline = new UserTimeline.Builder().build();
        assertEquals(REQUIRED_IMPRESSION_SECTION, timeline.getTimelineType());
    }

    /* Builder */

    public void testBuilder() {
        final UserTimeline timeline = new UserTimeline.Builder()
                .userId(TestFixtures.TEST_USER.id)
                .screenName(TestFixtures.TEST_USER.screenName)
                .maxItemsPerRequest(TEST_ITEMS_PER_REQUEST)
                .includeReplies(true)
                .includeRetweets(true)
                .build();
        assertEquals((Long) TestFixtures.TEST_USER.id, timeline.userId);
        assertEquals(TestFixtures.TEST_USER.screenName, timeline.screenName);
        assertEquals(TEST_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest);
        assertTrue(timeline.includeReplies);
        assertTrue(timeline.includeRetweets);
    }

    // api arguments should default to Null to allow the backend to determine default behavior
    public void testBuilder_defaults() {
        final UserTimeline timeline = new UserTimeline.Builder().build();
        assertNull(timeline.userId);
        assertNull(timeline.screenName);
        assertEquals(REQUIRED_DEFAULT_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest);
        assertNull(timeline.includeRetweets);
        // parameters which default to false
        assertFalse(timeline.includeReplies);
    }

    public void testBuilder_userId() {
        final Long USER_ID = TestFixtures.TEST_USER.id;
        final UserTimeline timeline = new UserTimeline.Builder()
                .userId(USER_ID)
                .build();
        assertEquals(USER_ID, timeline.userId);
    }

    public void testBuilder_screenName() {
        final UserTimeline timeline = new UserTimeline.Builder()
                .screenName(TestFixtures.TEST_USER.screenName)
                .build();
        assertEquals(TestFixtures.TEST_USER.screenName, timeline.screenName);
    }

    public void testBuilder_maxItemsPerRequest() {
        final UserTimeline timeline = new UserTimeline.Builder()
                .maxItemsPerRequest(TEST_ITEMS_PER_REQUEST)
                .build();
        assertEquals(TEST_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest);
    }

    public void testBuilder_includeReplies() {
        // null includeReplies defaults to false
        UserTimeline timeline = new UserTimeline.Builder().build();
        assertFalse(timeline.includeReplies);
        timeline = new UserTimeline.Builder().includeReplies(true).build();
        assertTrue(timeline.includeReplies);
        timeline = new UserTimeline.Builder().includeReplies(false).build();
        assertFalse(timeline.includeReplies);
    }

    public void testBuilder_includeRetweets() {
        UserTimeline timeline = new UserTimeline.Builder().build();
        assertNull(timeline.includeRetweets);
        timeline = new UserTimeline.Builder().includeRetweets(true).build();
        assertTrue(timeline.includeRetweets);
        timeline = new UserTimeline.Builder().includeRetweets(false).build();
        assertFalse(timeline.includeRetweets);
    }
}
