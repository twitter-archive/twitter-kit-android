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
package com.twitter.sdk.android.tweetcomposer;

import com.twitter.sdk.android.core.internal.scribe.EventNamespace;
import com.twitter.sdk.android.core.internal.scribe.ScribeItem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class ComposerScribeClientTest {
    private ComposerScribeClient composerScribeClient;

    @Mock
    private ScribeClient mockClient;

    @Captor
    private ArgumentCaptor<EventNamespace> eventNamespaceCaptor;

    @Captor
    private ArgumentCaptor<List<ScribeItem>> scribeItemsCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        composerScribeClient = new ComposerScribeClientImpl(mockClient);
    }

    @Test
    public void testConstructor() throws Exception {
        composerScribeClient = new ComposerScribeClientImpl(mockClient);
        assertNotNull(composerScribeClient);
    }

    @Test
    public void testConstructor_null() throws Exception {
        try {
            new ComposerScribeClientImpl(null);
            fail("expected scribeClient NullPointerException");
        } catch (NullPointerException npe) {
            assertEquals("scribeClient must not be null", npe.getMessage());
        }
    }

    @Test
    public void testImpression() throws Exception {
        composerScribeClient.impression();
        verify(mockClient).scribe(eventNamespaceCaptor.capture(), scribeItemsCaptor.capture());
        final EventNamespace eventNamespace = eventNamespaceCaptor.getValue();
        assertEquals(expectedImpression(), eventNamespace);
        assertEquals(Collections.EMPTY_LIST, scribeItemsCaptor.getValue());
    }

    @Test
    public void testTweetClick() throws Exception {
        composerScribeClient.click(ScribeConstants.SCRIBE_TWEET_ELEMENT);
        verify(mockClient).scribe(eventNamespaceCaptor.capture(), scribeItemsCaptor.capture());
        final EventNamespace eventNamespace = eventNamespaceCaptor.getValue();
        assertEquals(expectedTweetClick(), eventNamespace);
        assertEquals(Collections.EMPTY_LIST, scribeItemsCaptor.getValue());
    }

    @Test
    public void testCancelClick() throws Exception {
        composerScribeClient.click(ScribeConstants.SCRIBE_CANCEL_ELEMENT);
        verify(mockClient).scribe(eventNamespaceCaptor.capture(), scribeItemsCaptor.capture());
        final EventNamespace eventNamespace = eventNamespaceCaptor.getValue();
        assertEquals(expectedCancelClick(), eventNamespace);
        assertEquals(Collections.EMPTY_LIST, scribeItemsCaptor.getValue());
    }

    private EventNamespace expectedImpression() {
        return new EventNamespace.Builder()
                .setClient(ScribeConstants.SCRIBE_TFW_CLIENT)
                .setPage(ScribeConstants.SCRIBE_PAGE)
                .setSection(ScribeConstants.SCRIBE_SECTION)
                .setComponent(ScribeConstants.SCRIBE_COMPONENT)
                .setElement(ScribeConstants.SCRIBE_IMPRESSION_ELEMENT)
                .setAction(ScribeConstants.SCRIBE_IMPRESSION_ACTION)
                .builder();
    }

    private EventNamespace expectedTweetClick() {
        return new EventNamespace.Builder()
                .setClient(ScribeConstants.SCRIBE_TFW_CLIENT)
                .setPage(ScribeConstants.SCRIBE_PAGE)
                .setSection(ScribeConstants.SCRIBE_SECTION)
                .setComponent(ScribeConstants.SCRIBE_COMPONENT)
                .setElement(ScribeConstants.SCRIBE_TWEET_ELEMENT)
                .setAction(ScribeConstants.SCRIBE_CLICK_ACTION)
                .builder();
    }

    private EventNamespace expectedCancelClick() {
        return new EventNamespace.Builder()
                .setClient(ScribeConstants.SCRIBE_TFW_CLIENT)
                .setPage(ScribeConstants.SCRIBE_PAGE)
                .setSection(ScribeConstants.SCRIBE_SECTION)
                .setComponent(ScribeConstants.SCRIBE_COMPONENT)
                .setElement(ScribeConstants.SCRIBE_CANCEL_ELEMENT)
                .setAction(ScribeConstants.SCRIBE_CLICK_ACTION)
                .builder();
    }
}


