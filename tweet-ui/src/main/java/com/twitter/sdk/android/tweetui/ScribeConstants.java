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

public class ScribeConstants {
    private ScribeConstants() {}

    // tfw client event specific names
    static final String TFW_CLIENT_EVENT_PAGE = "android";
    static final String TFW_CLIENT_EVENT_SECTION = "tweet";
    static final String TFW_CLIENT_EVENT_ELEMENT = ""; // intentionally blank

    // syndicated sdk impression specific names
    static final String SYNDICATED_SDK_IMPRESSION_PAGE = "tweet";
    static final String SYNDICATED_SDK_IMPRESSION_COMPONENT = "";
    static final String SYNDICATED_SDK_IMPRESSION_ELEMENT = ""; // intentionally blank

    // general names
    static final String SCRIBE_CLICK_ACTION = "click";
    static final String SCRIBE_IMPRESSION_ACTION = "impression";
    static final String SCRIBE_FAVORITE_ACTION = "favorite";
    static final String SCRIBE_UNFAVORITE_ACTION = "unfavorite";
    static final String SCRIBE_SHARE_ACTION = "share";
    static final String SCRIBE_ACTIONS_ELEMENT = "actions";
    static final String SCRIBE_INITIAL_ELEMENT = "initial";
    static final String SCRIBE_TIMELINE_SECTION = "timeline";
    static final String SCRIBE_TIMELINE_PAGE = "timeline";
    static final String SCRIBE_INITIAL_COMPONENT = "initial";

    static EventNamespace getTfwEventUnFavoriteNamespace() {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(TFW_CLIENT_EVENT_SECTION)
                .setElement(SCRIBE_ACTIONS_ELEMENT)
                .setAction(SCRIBE_UNFAVORITE_ACTION)
                .builder();
    }

    static EventNamespace getTfwEventFavoriteNamespace() {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(TFW_CLIENT_EVENT_SECTION)
                .setElement(SCRIBE_ACTIONS_ELEMENT)
                .setAction(SCRIBE_FAVORITE_ACTION)
                .builder();
    }

    static EventNamespace getTfwEventShareNamespace() {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(TFW_CLIENT_EVENT_SECTION)
                .setElement(SCRIBE_ACTIONS_ELEMENT)
                .setAction(SCRIBE_SHARE_ACTION)
                .builder();
    }


    static EventNamespace getTfwEventClickNamespace(String viewName) {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(TFW_CLIENT_EVENT_SECTION)
                .setComponent(viewName)
                .setElement(TFW_CLIENT_EVENT_ELEMENT)
                .setAction(SCRIBE_CLICK_ACTION)
                .builder();
    }

    static EventNamespace getSyndicatedSdkClickNamespace(String viewName) {
        return new EventNamespace.Builder()
                .setClient(SyndicatedSdkImpressionEvent.CLIENT_NAME)
                .setPage(SYNDICATED_SDK_IMPRESSION_PAGE)
                .setSection(viewName)
                .setComponent(SYNDICATED_SDK_IMPRESSION_COMPONENT)
                .setElement(SYNDICATED_SDK_IMPRESSION_ELEMENT)
                .setAction(SCRIBE_CLICK_ACTION)
                .builder();
    }

    static EventNamespace getTfwEventImpressionNamespace(String viewName, boolean actionEnabled) {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(TFW_CLIENT_EVENT_SECTION)
                .setComponent(viewName)
                .setElement(actionEnabled ? SCRIBE_ACTIONS_ELEMENT : TFW_CLIENT_EVENT_ELEMENT)
                .setAction(SCRIBE_IMPRESSION_ACTION)
                .builder();
    }

    static EventNamespace getSyndicatedSdkImpressionNamespace(String viewName) {
        return new EventNamespace.Builder()
                .setClient(SyndicatedSdkImpressionEvent.CLIENT_NAME)
                .setPage(SYNDICATED_SDK_IMPRESSION_PAGE)
                .setSection(viewName)
                .setComponent(SYNDICATED_SDK_IMPRESSION_COMPONENT)
                .setElement(SYNDICATED_SDK_IMPRESSION_ELEMENT)
                .setAction(SCRIBE_IMPRESSION_ACTION)
                .builder();
    }

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
}
