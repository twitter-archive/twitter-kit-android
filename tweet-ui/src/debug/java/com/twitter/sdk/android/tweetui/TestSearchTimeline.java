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

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.models.Tweet;

public class TestSearchTimeline extends SearchTimeline {

    TestSearchTimeline(TweetUi tweetUi, String query, String lang, Integer count) {
        super(tweetUi, query, lang, count);
    }

    @Override
    public void addRequest(Callback<TwitterApiClient> cb) {
        super.addRequest(cb);
    }

    @Override
    public Callback<TwitterApiClient> createSearchRequest(Long sinceId, Long maxId,
            Callback<TimelineResult<Tweet>> cb) {
        return super.createSearchRequest(sinceId, maxId, cb);
    }
}
