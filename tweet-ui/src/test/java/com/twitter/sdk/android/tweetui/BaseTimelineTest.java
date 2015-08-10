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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class BaseTimelineTest {
    private static final String ILLEGAL_TWEET_UI_MESSAGE = "TweetUi instance must not be null";
    private static final Long TEST_ID = 200L;
    private static final String TEST_SCRIBE_SECTION = "test";
    private static final String REQUIRED_SDK_IMPRESSION_CLIENT = "android";
    private static final String REQUIRED_SDK_IMPRESSION_PAGE = "timeline";
    private static final String REQUIRED_SDK_IMPRESSION_COMPONENT = "initial";
    private static final String REQUIRED_SDK_IMPRESSION_ELEMENT = "";
    private static final String REQUIRED_TFW_CLIENT = "tfw";
    private static final String REQUIRED_TFW_PAGE = "android";
    private static final String REQUIRED_TFW_SECTION = "timeline";
    private static final String REQUIRED_TFW_ELEMENT = "initial";
    private static final String REQUIRED_IMPRESSION_ACTION = "impression";

    @Test
    public void testConstructor() {
        final TweetUi tweetUi = mock(TweetUi.class);
        final TestBaseTimeline baseTimeline = new TestBaseTimeline(tweetUi);
        assertEquals(tweetUi, baseTimeline.tweetUi);
    }

    @Test
    public void testConstructor_nullTweetUi() {
        try {
            new TestBaseTimeline(null);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals(ILLEGAL_TWEET_UI_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testConstructor_scribesImpression() {
        final TweetUi tweetUi = mock(TestTweetUi.class);
        final ArgumentCaptor<EventNamespace> sdkNamespaceCaptor
                = ArgumentCaptor.forClass(EventNamespace.class);
        final ArgumentCaptor<EventNamespace> tfwNamespaceCaptor
                = ArgumentCaptor.forClass(EventNamespace.class);
        new TestBaseTimeline(tweetUi);
        verify(tweetUi).scribe(sdkNamespaceCaptor.capture(), tfwNamespaceCaptor.capture());

        final EventNamespace sdkNs = sdkNamespaceCaptor.getValue();

        assertEquals(REQUIRED_SDK_IMPRESSION_CLIENT, sdkNs.client);
        assertEquals(REQUIRED_SDK_IMPRESSION_PAGE, sdkNs.page);
        assertEquals(TEST_SCRIBE_SECTION, sdkNs.section);
        assertEquals(REQUIRED_SDK_IMPRESSION_COMPONENT, sdkNs.component);
        assertEquals(REQUIRED_SDK_IMPRESSION_ELEMENT, sdkNs.element);
        assertEquals(REQUIRED_IMPRESSION_ACTION, sdkNs.action);

        final EventNamespace tfwNs = tfwNamespaceCaptor.getValue();

        assertEquals(REQUIRED_TFW_CLIENT, tfwNs.client);
        assertEquals(REQUIRED_TFW_PAGE, tfwNs.page);
        assertEquals(REQUIRED_TFW_SECTION, tfwNs.section);
        assertEquals(TEST_SCRIBE_SECTION, tfwNs.component);
        assertEquals(REQUIRED_TFW_ELEMENT, tfwNs.element);
        assertEquals(REQUIRED_IMPRESSION_ACTION, tfwNs.action);
    }

    @Test
    public void testDecrementMaxId_positive() {
        final Long correctedId = BaseTimeline.decrementMaxId(TEST_ID);
        assertEquals((Long) (TEST_ID - 1L), correctedId);
    }

    @Test
    public void testDecrementMaxId_nullId() {
        assertNull(BaseTimeline.decrementMaxId(null));
    }

    /* Extends abstract BaseTimeline for testing */
    public class TestBaseTimeline extends BaseTimeline {
        TestBaseTimeline(TweetUi tweetUi) {
            super(tweetUi);
        }

        @Override
        String getTimelineType() {
            return TEST_SCRIBE_SECTION;
        }
    }
}
