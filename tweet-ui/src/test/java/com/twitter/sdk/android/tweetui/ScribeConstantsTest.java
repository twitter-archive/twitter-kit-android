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

    static final String REQUIRED_SDK_SCRIBE_CLIENT = "android";
    static final String REQUIRED_SDK_SCRIBE_ELEMENT = "";
    static final String REQUIRED_SCRIBE_IMPRESSION_ACTION = "impression";
    static final String REQUIRED_SCRIBE_INITIAL_ELEMENT = "initial";
    static final String REQUIRED_SCRIBE_TIMELINE_SECTION = "timeline";
    static final String REQUIRED_SCRIBE_TIMELINE_PAGE = "timeline";
    static final String REQUIRED_SCRIBE_INITIAL_COMPONENT = "initial";

    static final String TEST_VIEW_NAME = "compact";

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
}
