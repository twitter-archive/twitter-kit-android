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

import android.os.Handler;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import io.fabric.sdk.android.Fabric;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Encapsulates Tweet API access as a read through cache. The LruCache implementation we use handles
 * thread safe access.
 */
class TweetRepository extends Repository {
    private static final String TAG = TweetUi.LOGTAG;
    private static final String AUTH_ERROR = "Auth could not be obtained.";

    // Cache size units are in number of entries, an average Tweet is roughly 900 bytes in memory
    private static final int DEFAULT_CACHE_SIZE = 20;

    // leave this package accessible for testing
    final LruCache<Long, Tweet> tweetCache;
    final LruCache<Long, FormattedTweetText> formatCache;

    TweetRepository(TweetUi tweetUiKit, ExecutorService executorService,
            Handler mainHandler, AuthRequestQueue queue) {
        super(tweetUiKit, executorService, mainHandler, queue);

        tweetCache = new LruCache<>(DEFAULT_CACHE_SIZE);
        formatCache = new LruCache<>(DEFAULT_CACHE_SIZE);
    }

    /**
     * This method will cache formatted tweet values to ensure we don't slow down rendering
     *
     * @param tweet the Tweet that will be formatted
     * @return      the formatted values suitable for display, can be null
     */
    FormattedTweetText formatTweetText(final Tweet tweet) {
        if (tweet == null) return null;

        final FormattedTweetText cached = formatCache.get(tweet.id);

        if (cached != null) return cached;

        final FormattedTweetText formattedTweetText = TweetTextUtils.formatTweetText(tweet);
        if (formattedTweetText != null && !TextUtils.isEmpty(formattedTweetText.text)) {
            formatCache.put(tweet.id, formattedTweetText);
        }

        return formattedTweetText;
    }

    /**
     * Queues and loads a Tweet from the API statuses/show endpoint. Queue ensures a guest or app
     * auth token is obtained before performing the request. Adds the the Tweet from the response
     * to the cache and provides the Tweet to the repository callback success method.
     * @param tweetId Tweet id
     * @param cb repository callback
     */
    void loadTweet(final long tweetId, final LoadCallback<Tweet> cb) {
        final Tweet cachedTweet = tweetCache.get(tweetId);

        if (cachedTweet != null) {
            deliverTweet(cachedTweet, cb);
            return;
        }

        queue.addRequest(new Callback<TwitterApiClient>() {
            @Override
            public void success(Result<TwitterApiClient> result) {
                result.data.getStatusesService().show(tweetId, null, null, null,
                        new TweetApiCallback(cb));
            }

            @Override
            public void failure(TwitterException exception) {
                Fabric.getLogger().e(TAG, AUTH_ERROR, exception);
                if (cb != null) {
                    cb.failure(exception);
                }
            }
        });
    }

    /**
     * Queues and loads multiple Tweets from the API lookup endpoint. Queue ensures a guest or app
     * auth token is obtained before performing the request. Orders the Tweets from the response
     * and provides them to the repository callback success method.
     * @param tweetIds list of Tweet ids
     * @param cb repository callback
     */
    void loadTweets(final List<Long> tweetIds, final LoadCallback<List<Tweet>> cb) {
        queue.addRequest(new Callback<TwitterApiClient>() {
            @Override
            public void success(Result<TwitterApiClient> result) {
                final String commaSepIds = TextUtils.join(",", tweetIds);
                result.data.getStatusesService().lookup(commaSepIds, null, null, null,
                        new TweetsApiCallback(tweetIds, cb));
            }

            @Override
            public void failure(TwitterException exception) {
                Fabric.getLogger().e(TAG, AUTH_ERROR, exception);
                if (cb != null) {
                    cb.failure(exception);
                }
            }
        });
    }

    protected void updateCache(final Tweet tweet) {
        tweetCache.put(tweet.id, tweet);
    }

    /*
     * callable on main thread, but not necessary
     */
    private void deliverTweet(final Tweet tweet, final LoadCallback<Tweet> cb) {
        if (cb == null) return;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                cb.success(tweet);
            }
        });
    }

    class TweetApiCallback extends ApiCallback<Tweet> {

        TweetApiCallback(LoadCallback<Tweet> cb) {
            super(cb);
        }

        @Override
        public void success(Result<Tweet> result) {
            /*
             * The tweet at this point is parsed directly from the api, we need to fix up the
             * text and entities now so it will display correctly. The mutations caused by
             * TweetUtils.format are preserved through serializing since we call toJson on the tweet
             * object itself and store the result.
             */
            final Tweet tweet = result.data;
            updateCache(tweet);
            if (cb != null) {
                cb.success(tweet);
            }
        }
    }

    class TweetsApiCallback extends ApiCallback<List<Tweet>> {
        final List<Long> tweetIds;

        TweetsApiCallback(List<Long> tweetIds, LoadCallback<List<Tweet>> cb) {
            super(cb);
            this.tweetIds = tweetIds;
        }

        @Override
        public void success(Result<List<Tweet>> result) {
            if (cb != null) {
                final List<Tweet> sorted = Utils.orderTweets(tweetIds, result.data);
                cb.success(sorted);
            }
        }
    }
}
