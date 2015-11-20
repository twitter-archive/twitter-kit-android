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

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TweetScribeClientImplTest {
    static final String REQUIRED_TFW_SCRIBE_CLIENT = "tfw";
    static final String REQUIRED_TFW_SCRIBE_PAGE = "android";
    static final String REQUIRED_TFW_SCRIBE_SECTION = "tweet";
    static final String REQUIRED_TFW_SCRIBE_ELEMENT = "";

    static final String REQUIRED_SDK_SCRIBE_CLIENT = "android";
    static final String REQUIRED_SDK_SCRIBE_PAGE = "tweet";
    static final String REQUIRED_SDK_SCRIBE_COMPONENT = "";
    static final String REQUIRED_SDK_SCRIBE_ELEMENT = "";
    static final String REQUIRED_SCRIBE_CLICK_ACTION = "click";
    static final String REQUIRED_SCRIBE_IMPRESSION_ACTION = "impression";
    static final String REQUIRED_SCRIBE_FAVORITE_ACTION = "favorite";
    static final String REQUIRED_SCRIBE_UNFAVORITE_ACTION = "unfavorite";
    static final String REQUIRED_SCRIBE_SHARE_ACTION = "share";
    static final String REQUIRED_SCRIBE_ACTIONS_ELEMENT = "actions";

    static final String TEST_VIEW_NAME = "compact";

    private TweetScribeClientImpl scribeClient;
    @Mock
    private TweetUi tweetUi;
    @Captor
    private ArgumentCaptor<List<ScribeItem>> itemsArgumentCaptor;
    @Captor
    private ArgumentCaptor<EventNamespace> namespaceArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        scribeClient = new TweetScribeClientImpl(tweetUi);
    }

    @Test
    public void testImpression() {
        scribeClient.impression(TestFixtures.TEST_TWEET, TEST_VIEW_NAME, false);

        verify(tweetUi, times(2))
                .scribe(namespaceArgumentCaptor.capture(), itemsArgumentCaptor.capture());

        EventNamespace ns = namespaceArgumentCaptor.getAllValues().get(0);
        assertTfwNamespaceValuesForTweets(ns);
        assertEquals(TEST_VIEW_NAME, ns.component);
        assertEquals(REQUIRED_SCRIBE_IMPRESSION_ACTION, ns.action);
        ns = namespaceArgumentCaptor.getAllValues().get(1);
        assertSyndicatedNamespaceValuesForTweets(ns);
        assertEquals(TEST_VIEW_NAME, ns.section);
        assertEquals(REQUIRED_SCRIBE_IMPRESSION_ACTION, ns.action);

        List<ScribeItem> items = itemsArgumentCaptor.getAllValues().get(0);
        assertItems(items);
        items = itemsArgumentCaptor.getAllValues().get(1);
        assertItems(items);
    }

    @Test
    public void testShare() {
        scribeClient.share(TestFixtures.TEST_TWEET);

        verify(tweetUi).scribe(namespaceArgumentCaptor.capture(), itemsArgumentCaptor.capture());

        final EventNamespace ns = namespaceArgumentCaptor.getValue();
        assertTfwNamespaceForActions(ns);
        assertEquals(REQUIRED_SCRIBE_SHARE_ACTION, ns.action);
        assertItems(itemsArgumentCaptor.getValue());
    }

    @Test
    public void testFavorite() {
        scribeClient.favorite(TestFixtures.TEST_TWEET);

        verify(tweetUi).scribe(namespaceArgumentCaptor.capture(), itemsArgumentCaptor.capture());
        final EventNamespace ns = namespaceArgumentCaptor.getValue();
        assertTfwNamespaceForActions(ns);
        assertEquals(REQUIRED_SCRIBE_FAVORITE_ACTION, ns.action);
        assertItems(itemsArgumentCaptor.getValue());
    }

    @Test
    public void testUnfavorite() {
        scribeClient.unfavorite(TestFixtures.TEST_TWEET);

        verify(tweetUi).scribe(namespaceArgumentCaptor.capture(), itemsArgumentCaptor.capture());
        final EventNamespace ns = namespaceArgumentCaptor.getValue();
        assertTfwNamespaceForActions(ns);
        assertEquals(REQUIRED_SCRIBE_UNFAVORITE_ACTION, ns.action);
        assertItems(itemsArgumentCaptor.getValue());
    }

    @Test
    public void testClick() {
        scribeClient.click(TestFixtures.TEST_TWEET, TEST_VIEW_NAME);

        verify(tweetUi).scribe(namespaceArgumentCaptor.capture(), itemsArgumentCaptor.capture());
        final EventNamespace ns = namespaceArgumentCaptor.getValue();
        assertTfwNamespaceValuesForTweets(ns);
        assertEquals(TEST_VIEW_NAME, ns.component);
        assertEquals(REQUIRED_SCRIBE_CLICK_ACTION, ns.action);
        assertItems(itemsArgumentCaptor.getValue());
    }

    @Test
    public void testGetTfwImpressionNamespace_actionsEnabled() {
        final EventNamespace ns =
                TweetScribeClientImpl.getTfwImpressionNamespace(TEST_VIEW_NAME, true);
        assertEquals(REQUIRED_TFW_SCRIBE_CLIENT, ns.client);
        assertEquals(REQUIRED_TFW_SCRIBE_PAGE, ns.page);
        assertEquals(REQUIRED_TFW_SCRIBE_SECTION, ns.section);
        assertEquals(REQUIRED_SCRIBE_ACTIONS_ELEMENT, ns.element);
        assertEquals(TEST_VIEW_NAME, ns.component);
        assertEquals(REQUIRED_SCRIBE_IMPRESSION_ACTION, ns.action);
    }

    @Test
    public void testGetTfwImpressionNamespace_actionsDisabled() {
        final EventNamespace ns =
                TweetScribeClientImpl.getTfwImpressionNamespace(TEST_VIEW_NAME, false);
        assertTfwNamespaceValuesForTweets(ns);
        assertEquals(TEST_VIEW_NAME, ns.component);
        assertEquals(REQUIRED_SCRIBE_IMPRESSION_ACTION, ns.action);
    }

    @Test
    public void testGetSyndicatedImpressionNamespace() {
        final EventNamespace ns =
                TweetScribeClientImpl.getSyndicatedImpressionNamespace(TEST_VIEW_NAME);
        assertSyndicatedNamespaceValuesForTweets(ns);
        assertEquals(TEST_VIEW_NAME, ns.section);
        assertEquals(REQUIRED_SCRIBE_IMPRESSION_ACTION, ns.action);
    }

    @Test
    public void testGetTfwClickNamespace() {
        final EventNamespace ns = TweetScribeClientImpl.getTfwClickNamespace(TEST_VIEW_NAME);

        assertTfwNamespaceValuesForTweets(ns);
        assertEquals(TEST_VIEW_NAME, ns.component);
        assertEquals(REQUIRED_SCRIBE_CLICK_ACTION, ns.action);
    }

    @Test
    public void testGetTfwFavoriteNamespace() {
        final EventNamespace ns = TweetScribeClientImpl.getTfwFavoriteNamespace();
        assertTfwNamespaceForActions(ns);
        assertEquals(REQUIRED_SCRIBE_FAVORITE_ACTION, ns.action);
    }

    @Test
    public void testGetTfwUnfavoriteNamespace() {
        final EventNamespace ns = TweetScribeClientImpl.getTfwUnfavoriteNamespace();
        assertTfwNamespaceForActions(ns);
        assertEquals(REQUIRED_SCRIBE_UNFAVORITE_ACTION, ns.action);
    }

    @Test
    public void testGetTfwShareNamespace() {
        final EventNamespace ns = TweetScribeClientImpl.getTfwShareNamespace();
        assertTfwNamespaceForActions(ns);
        assertEquals(REQUIRED_SCRIBE_SHARE_ACTION, ns.action);
    }

    static void assertItems(List<ScribeItem> items) {
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(TestFixtures.TEST_TWEET.id, items.get(0).id.longValue());
        assertEquals(ScribeItem.TYPE_TWEET, items.get(0).itemType.intValue());
    }

    static void assertTfwNamespaceForActions(EventNamespace ns) {
        assertEquals(REQUIRED_TFW_SCRIBE_CLIENT, ns.client);
        assertEquals(REQUIRED_TFW_SCRIBE_PAGE, ns.page);
        assertEquals(REQUIRED_TFW_SCRIBE_SECTION, ns.section);
        assertEquals(REQUIRED_SCRIBE_ACTIONS_ELEMENT, ns.element);
    }

    static void assertTfwNamespaceValuesForTweets(EventNamespace ns) {
        assertEquals(REQUIRED_TFW_SCRIBE_CLIENT, ns.client);
        assertEquals(REQUIRED_TFW_SCRIBE_PAGE, ns.page);
        assertEquals(REQUIRED_TFW_SCRIBE_SECTION, ns.section);
        assertEquals(REQUIRED_TFW_SCRIBE_ELEMENT, ns.element);
    }

    static void assertSyndicatedNamespaceValuesForTweets(EventNamespace ns) {
        assertEquals(REQUIRED_SDK_SCRIBE_CLIENT, ns.client);
        assertEquals(REQUIRED_SDK_SCRIBE_PAGE, ns.page);
        assertEquals(REQUIRED_SDK_SCRIBE_COMPONENT, ns.component);
        assertEquals(REQUIRED_SDK_SCRIBE_ELEMENT, ns.element);
    }
}
