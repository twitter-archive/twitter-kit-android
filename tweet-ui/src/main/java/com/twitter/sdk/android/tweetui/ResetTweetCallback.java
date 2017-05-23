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
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

/**
 * Handles Tweet successes and failures by setting the new Tweet on the given TweetView and
 * clearing the single Tweet cache. Calls through to the given Callback.
 */
class ResetTweetCallback extends Callback<Tweet> {
    final BaseTweetView baseTweetView;
    final TweetRepository tweetRepository;
    final Callback<Tweet> cb;

    ResetTweetCallback(BaseTweetView baseTweetView, TweetRepository tweetRepository,
                        Callback<Tweet> cb) {
        this.baseTweetView = baseTweetView;
        this.tweetRepository = tweetRepository;
        this.cb = cb;
    }

    @Override
    public void success(Result<Tweet> result) {
        tweetRepository.updateCache(result.data);
        baseTweetView.setTweet(result.data);
        if (cb != null) {
            cb.success(result);
        }
    }

    @Override
    public void failure(TwitterException exception) {
        if (cb != null) {
            cb.failure(exception);
        }
    }
}
