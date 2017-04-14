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
import com.twitter.sdk.android.core.internal.scribe.SyndicatedSdkImpressionEvent;
import com.twitter.sdk.android.core.internal.scribe.SyndicationClientEvent;

final class ScribeConstants {
    // tfw client event specific names
    static final String TFW_CLIENT_EVENT_PAGE = "android";

    static final String SYNDICATED_SDK_IMPRESSION_ELEMENT = ""; // intentionally blank

    // general names
    static final String SCRIBE_IMPRESSION_ACTION = "impression";
    static final String SCRIBE_FILTER_ACTION = "filter";
    static final String SCRIBE_INITIAL_ELEMENT = "initial";
    static final String SCRIBE_TIMELINE_SECTION = "timeline";
    static final String SCRIBE_TIMELINE_PAGE = "timeline";
    static final String SCRIBE_INITIAL_COMPONENT = "initial";

    private ScribeConstants() {}

    static EventNamespace getSyndicatedSdkTimelineNamespace(String timelineType) {
        return new EventNamespace.Builder()
                .setClient(SyndicatedSdkImpressionEvent.CLIENT_NAME)
                .setPage(SCRIBE_TIMELINE_PAGE)
                .setSection(timelineType)
                .setComponent(SCRIBE_INITIAL_COMPONENT)
                .setElement(SYNDICATED_SDK_IMPRESSION_ELEMENT)
                .setAction(SCRIBE_IMPRESSION_ACTION)
                .builder();
    }

    static EventNamespace getTfwClientTimelineNamespace(String timelineType) {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(SCRIBE_TIMELINE_SECTION)
                .setComponent(timelineType)
                .setElement(SCRIBE_INITIAL_ELEMENT)
                .setAction(SCRIBE_IMPRESSION_ACTION)
                .builder();
    }

    static EventNamespace getTfwClientFilterTimelineNamespace(String timelineType) {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(SCRIBE_TIMELINE_SECTION)
                .setComponent(timelineType)
                .setElement(SCRIBE_INITIAL_ELEMENT)
                .setAction(SCRIBE_FILTER_ACTION)
                .builder();
    }
}
