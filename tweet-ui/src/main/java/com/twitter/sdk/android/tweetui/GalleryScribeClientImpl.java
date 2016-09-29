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
import com.twitter.sdk.android.core.internal.scribe.SyndicationClientEvent;

import java.util.ArrayList;
import java.util.List;

public class GalleryScribeClientImpl implements GalleryScribeClient {

    static final String TFW_CLIENT_EVENT_PAGE = "android";
    static final String TFW_CLIENT_EVENT_SECTION = "gallery";

    static final String SCRIBE_SHOW_ACTION = "show";
    static final String SCRIBE_IMPRESSION_ACTION = "impression";
    static final String SCRIBE_NAVIGATE_ACTION = "navigate";
    static final String SCRIBE_DISMISS_ACTION = "dismiss";

    final TweetUi tweetUi;

    public GalleryScribeClientImpl(TweetUi tweetUi) {
        this.tweetUi = tweetUi;
    }

    @Override
    public void show() {
        tweetUi.scribe(getTfwShowNamespace());
    }

    @Override
    public void impression(ScribeItem item) {
        final List<ScribeItem> items = new ArrayList<>();
        items.add(item);

        tweetUi.scribe(getTfwImpressionNamespace(), items);
    }

    @Override
    public void navigate() {
        tweetUi.scribe(getTfwNavigateNamespace());
    }

    @Override
    public void dismiss() {
        tweetUi.scribe(getTfwDimissNamespace());
    }

    static EventNamespace getTfwImpressionNamespace() {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(TFW_CLIENT_EVENT_SECTION)
                .setAction(SCRIBE_IMPRESSION_ACTION)
                .builder();
    }

    static EventNamespace getTfwShowNamespace() {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(TFW_CLIENT_EVENT_SECTION)
                .setAction(SCRIBE_SHOW_ACTION)
                .builder();
    }

    static EventNamespace getTfwNavigateNamespace() {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(TFW_CLIENT_EVENT_SECTION)
                .setAction(SCRIBE_NAVIGATE_ACTION)
                .builder();
    }

    static EventNamespace getTfwDimissNamespace() {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(TFW_CLIENT_EVENT_SECTION)
                .setAction(SCRIBE_DISMISS_ACTION)
                .builder();
    }
}
