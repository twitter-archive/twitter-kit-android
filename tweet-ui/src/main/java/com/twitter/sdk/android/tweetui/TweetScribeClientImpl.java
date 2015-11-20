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
import com.twitter.sdk.android.core.internal.scribe.SyndicatedSdkImpressionEvent;
import com.twitter.sdk.android.core.internal.scribe.SyndicationClientEvent;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.List;

class TweetScribeClientImpl implements TweetScribeClient {
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

    final TweetUi tweetUi;

    TweetScribeClientImpl(TweetUi tweetUi) {
        this.tweetUi = tweetUi;
    }

    @Override
    public void impression(Tweet tweet, String viewName, boolean actionEnabled) {
        final List<ScribeItem> items = new ArrayList<>();
        items.add(ScribeItem.fromTweet(tweet));

        tweetUi.scribe(getTfwImpressionNamespace(viewName, actionEnabled), items);
        tweetUi.scribe(getSyndicatedImpressionNamespace(viewName), items);
    }

    @Override
    public void share(Tweet tweet) {
        final List<ScribeItem> items = new ArrayList<>();
        items.add(ScribeItem.fromTweet(tweet));

        tweetUi.scribe(getTfwShareNamespace(), items);
    }

    @Override
    public void favorite(Tweet tweet) {
        final List<ScribeItem> items = new ArrayList<>();
        items.add(ScribeItem.fromTweet(tweet));

        tweetUi.scribe(getTfwFavoriteNamespace(), items);
    }

    @Override
    public void unfavorite(Tweet tweet) {
        final List<ScribeItem> items = new ArrayList<>();
        items.add(ScribeItem.fromTweet(tweet));

        tweetUi.scribe(getTfwUnfavoriteNamespace(), items);
    }

    @Override
    public void click(Tweet tweet, String viewName) {
        final List<ScribeItem> items = new ArrayList<>();
        items.add(ScribeItem.fromTweet(tweet));

        tweetUi.scribe(getTfwClickNamespace(viewName), items);
    }

    static EventNamespace getTfwImpressionNamespace(String viewName, boolean actionEnabled) {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(TFW_CLIENT_EVENT_SECTION)
                .setComponent(viewName)
                .setElement(actionEnabled ? SCRIBE_ACTIONS_ELEMENT : TFW_CLIENT_EVENT_ELEMENT)
                .setAction(SCRIBE_IMPRESSION_ACTION)
                .builder();
    }

    static EventNamespace getTfwUnfavoriteNamespace() {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(TFW_CLIENT_EVENT_SECTION)
                .setElement(SCRIBE_ACTIONS_ELEMENT)
                .setAction(SCRIBE_UNFAVORITE_ACTION)
                .builder();
    }

    static EventNamespace getTfwFavoriteNamespace() {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(TFW_CLIENT_EVENT_SECTION)
                .setElement(SCRIBE_ACTIONS_ELEMENT)
                .setAction(SCRIBE_FAVORITE_ACTION)
                .builder();
    }

    static EventNamespace getTfwShareNamespace() {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(TFW_CLIENT_EVENT_SECTION)
                .setElement(SCRIBE_ACTIONS_ELEMENT)
                .setAction(SCRIBE_SHARE_ACTION)
                .builder();
    }

    static EventNamespace getTfwClickNamespace(String viewName) {
        return new EventNamespace.Builder()
                .setClient(SyndicationClientEvent.CLIENT_NAME)
                .setPage(TFW_CLIENT_EVENT_PAGE)
                .setSection(TFW_CLIENT_EVENT_SECTION)
                .setComponent(viewName)
                .setElement(TFW_CLIENT_EVENT_ELEMENT)
                .setAction(SCRIBE_CLICK_ACTION)
                .builder();
    }

    static EventNamespace getSyndicatedImpressionNamespace(String viewName) {
        return new EventNamespace.Builder()
                .setClient(SyndicatedSdkImpressionEvent.CLIENT_NAME)
                .setPage(SYNDICATED_SDK_IMPRESSION_PAGE)
                .setSection(viewName)
                .setComponent(SYNDICATED_SDK_IMPRESSION_COMPONENT)
                .setElement(SYNDICATED_SDK_IMPRESSION_ELEMENT)
                .setAction(SCRIBE_IMPRESSION_ACTION)
                .builder();
    }
}
