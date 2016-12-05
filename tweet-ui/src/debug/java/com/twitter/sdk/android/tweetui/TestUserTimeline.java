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

import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import retrofit2.Call;

public class TestUserTimeline extends UserTimeline {

    TestUserTimeline(Long userId, String screenName, Integer count,
                     Boolean excludeReplies, Boolean includeRetweets) {
        super(userId, screenName, count, excludeReplies, includeRetweets);
    }

    @Override
    public Call<List<Tweet>> createUserTimelineRequest(Long sinceId, Long maxId) {
        return super.createUserTimelineRequest(sinceId, maxId);
    }
}
