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

class VideoScribeClientImpl implements VideoScribeClient {

    static final String TFW_CLIENT_EVENT_PAGE = "android";

    static final String TFW_CLIENT_EVENT_SECTION = "video";

    static final String SCRIBE_IMPRESSION_ACTION = "impression";

    static final String SCRIBE_PLAY_ACTION = "play";

    final TweetUi tweetUi;

    VideoScribeClientImpl(TweetUi tweetUi) {
        this.tweetUi = tweetUi;
    }

    @Override
    public void impression(ScribeItem scribeItem) {
        final List<ScribeItem> items = new ArrayList<>();
        items.add(scribeItem);

        tweetUi.scribe(getTfwImpressionNamespace(), items);
    }

    @Override
    public void play(ScribeItem scribeItem) {
        final List<ScribeItem> items = new ArrayList<>();
        items.add(scribeItem);

        tweetUi.scribe(getTfwPlayNamespace(), items);
    }

    static EventNamespace getTfwImpressionNamespace() {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(TFW_CLIENT_EVENT_SECTION)
                .setAction(SCRIBE_IMPRESSION_ACTION)
                .builder();
    }

    static EventNamespace getTfwPlayNamespace() {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(TFW_CLIENT_EVENT_SECTION)
                .setAction(SCRIBE_PLAY_ACTION)
                .builder();
    }
}
