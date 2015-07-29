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

import static org.junit.Assert.assertEquals;

public class ScribeConstantsTest {
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
    static final String REQUIRED_SCRIBE_INITIAL_ELEMENT = "initial";
    static final String REQUIRED_SCRIBE_TIMELINE_SECTION = "timeline";
    static final String REQUIRED_SCRIBE_TIMELINE_PAGE = "timeline";
    static final String REQUIRED_SCRIBE_INITIAL_COMPONENT = "initial";

    static final String TEST_VIEW_NAME = "compact";

    @Test
    public void testGetTfwImpressionNamespace_actionsEnabled() {
        final EventNamespace ns =
                ScribeConstants.getTfwEventImpressionNamespace(TEST_VIEW_NAME, true);
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
                ScribeConstants.getTfwEventImpressionNamespace(TEST_VIEW_NAME, false);
        assertConsistentTfwNamespaceValuesForTweets(ns);
        assertEquals(TEST_VIEW_NAME, ns.component);
        assertEquals(REQUIRED_SCRIBE_IMPRESSION_ACTION, ns.action);
    }

    @Test
    public void testGetSdkImpressionNamespace() {
        final EventNamespace ns =
                ScribeConstants.getSyndicatedSdkImpressionNamespace(TEST_VIEW_NAME);
        assertConsistentSdkNamespaceValuesForTweets(ns);
        assertEquals(TEST_VIEW_NAME, ns.section);
        assertEquals(REQUIRED_SCRIBE_IMPRESSION_ACTION, ns.action);
    }

    @Test
    public void testGetTfwClickNamespace() {
        final EventNamespace ns = ScribeConstants.getTfwEventClickNamespace(TEST_VIEW_NAME);

        assertConsistentTfwNamespaceValuesForTweets(ns);
        assertEquals(TEST_VIEW_NAME, ns.component);
        assertEquals(REQUIRED_SCRIBE_CLICK_ACTION, ns.action);
    }

    @Test
    public void testGetSdkClickNamespace() {
        final EventNamespace ns = ScribeConstants.getSyndicatedSdkClickNamespace(TEST_VIEW_NAME);

        assertConsistentSdkNamespaceValuesForTweets(ns);
        assertEquals(TEST_VIEW_NAME, ns.section);
        assertEquals(REQUIRED_SCRIBE_CLICK_ACTION, ns.action);
    }

    @Test
    public void testGetTfwEventFavoriteNamespace() {
        final EventNamespace ns = ScribeConstants.getTfwEventFavoriteNamespace();
        assertConsistentTfwEventNamespaceForActions(ns);
        assertEquals(REQUIRED_SCRIBE_FAVORITE_ACTION, ns.action);
    }

    @Test
    public void testGetTfwEventUnFavoriteNamespace() {
        final EventNamespace ns = ScribeConstants.getTfwEventUnFavoriteNamespace();
        assertConsistentTfwEventNamespaceForActions(ns);
        assertEquals(REQUIRED_SCRIBE_UNFAVORITE_ACTION, ns.action);
    }

    @Test
    public void testGetTfwEventShareNamespace() {
        final EventNamespace ns = ScribeConstants.getTfwEventShareNamespace();
        assertConsistentTfwEventNamespaceForActions(ns);
        assertEquals(REQUIRED_SCRIBE_SHARE_ACTION, ns.action);
    }


    @Test
    public void testGetSyndicatedSdkTimelineNamespace() {
        final EventNamespace ns = ScribeConstants.getSyndicatedSdkTimelineNamespace(TEST_VIEW_NAME);

        assertEquals(REQUIRED_SDK_SCRIBE_CLIENT, ns.client);
        assertEquals(REQUIRED_SCRIBE_TIMELINE_PAGE, ns.page);
        assertEquals(TEST_VIEW_NAME, ns.section);
        assertEquals(REQUIRED_SCRIBE_INITIAL_COMPONENT, ns.component);
        assertEquals(REQUIRED_SDK_SCRIBE_ELEMENT, ns.element);
        assertEquals(REQUIRED_SCRIBE_IMPRESSION_ACTION, ns.action);
    }

    @Test
    public void testGetTfwClientTimelineNamespace() {
        final EventNamespace ns = ScribeConstants.getTfwClientTimelineNamespace(TEST_VIEW_NAME);

        assertEquals(REQUIRED_TFW_SCRIBE_CLIENT, ns.client);
        assertEquals(REQUIRED_TFW_SCRIBE_PAGE, ns.page);
        assertEquals(REQUIRED_SCRIBE_TIMELINE_SECTION, ns.section);
        assertEquals(TEST_VIEW_NAME, ns.component);
        assertEquals(REQUIRED_SCRIBE_INITIAL_ELEMENT, ns.element);
        assertEquals(REQUIRED_SCRIBE_IMPRESSION_ACTION, ns.action);
    }

    static void assertConsistentTfwEventNamespaceForActions(EventNamespace ns) {
        assertEquals(REQUIRED_TFW_SCRIBE_CLIENT, ns.client);
        assertEquals(REQUIRED_TFW_SCRIBE_PAGE, ns.page);
        assertEquals(REQUIRED_TFW_SCRIBE_SECTION, ns.section);
        assertEquals(REQUIRED_SCRIBE_ACTIONS_ELEMENT, ns.element);
    }

    static void assertConsistentTfwNamespaceValuesForTweets(EventNamespace ns) {
        assertEquals(REQUIRED_TFW_SCRIBE_CLIENT, ns.client);
        assertEquals(REQUIRED_TFW_SCRIBE_PAGE, ns.page);
        assertEquals(REQUIRED_TFW_SCRIBE_SECTION, ns.section);
        assertEquals(REQUIRED_TFW_SCRIBE_ELEMENT, ns.element);
    }

    static void assertConsistentSdkNamespaceValuesForTweets(EventNamespace ns) {
        assertEquals(REQUIRED_SDK_SCRIBE_CLIENT, ns.client);
        assertEquals(REQUIRED_SDK_SCRIBE_PAGE, ns.page);
        assertEquals(REQUIRED_SDK_SCRIBE_COMPONENT, ns.component);
        assertEquals(REQUIRED_SDK_SCRIBE_ELEMENT, ns.element);
    }
}
