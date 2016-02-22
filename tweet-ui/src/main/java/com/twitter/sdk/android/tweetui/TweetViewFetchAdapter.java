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

import android.content.Context;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

/**
 * Adapter to provide a collection of TweetViews to AdapterViews (such as ListView) which
 * allows Tweets to be specified by id and handles fetching them from the API.
 * @deprecated Load Tweets by id with TweetUtils.loadTweets and use a FixedTweetTimeline with the
 * TweetTimelineListAdapter.
 */
@Deprecated
public class TweetViewFetchAdapter<T extends BaseTweetView> extends TweetViewAdapter<T> {

    /**
     * Constructs a TweetViewFetchAdapter.
     * @param context the context of the views
     */
    public TweetViewFetchAdapter(Context context) {
        super(context);
    }

    /**
     * Constructs a TweetViewFetchAdapter with a collection of ids for Tweets to fetch.
     * @param context the context of the views
     * @param tweetIds Tweet ids
     */
    public TweetViewFetchAdapter(Context context, List<Long> tweetIds) {
        this(context, tweetIds, null);
    }

    /**
     * Constructs a TweetViewFetchAdapter with a collection of ids for Tweets to fetch.
     * @param context the context of the views
     * @param tweetIds Tweet ids
     * @param cb callback
     */
    public TweetViewFetchAdapter(Context context, List<Long> tweetIds,
            LoadCallback<List<Tweet>> cb) {
        super(context);
        setTweetIds(tweetIds, cb);
    }

    /**
     * Fetches the requested Tweet ids and sets the collection of Tweets in the adapter.
     * @param tweetIds Tweet ids
     */
    public void setTweetIds(final List<Long> tweetIds) {
        setTweetIds(tweetIds, (Callback) null);
    }

    /**
     * Fetches the requested Tweet ids and sets the collection of Tweets in the adapter. Calls
     * the given callback's success or failure.
     * @param tweetIds Tweet ids
     * @param cb callback
     */
    public void setTweetIds(final List<Long> tweetIds, final Callback<List<Tweet>> cb) {
        final Callback<List<Tweet>> repoCallback = new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                setTweets(result.data);
                if (cb != null) {
                    cb.success(result);
                }
            }

            @Override
            public void failure(TwitterException exception) {
                // purposefully not logging the failure to lookup the Tweets
                if (cb != null) {
                    cb.failure(exception);
                }
            }
        };
        TweetUi.getInstance().getTweetRepository().loadTweets(tweetIds, repoCallback);
    }

    /**
     * Fetches the requested Tweet ids and sets the collection of Tweets in the adapter. Calls
     * the given callback's success or failure.
     * @param tweetIds Tweet ids
     * @param loadCallback callback
     * @deprecated Use {@link #setTweetIds(List, Callback)} instead.
     */
    @Deprecated
    public void setTweetIds(final List<Long> tweetIds,
            final LoadCallback<List<Tweet>> loadCallback) {
        final Callback<List<Tweet>> cb = new TweetUtils.CallbackAdapter<>(loadCallback);
        setTweetIds(tweetIds, cb);
    }
}
