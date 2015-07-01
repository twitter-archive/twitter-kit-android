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
import com.twitter.sdk.android.core.internal.GuestCallback;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

/**
 * Encapsulates Tweet API access. Tweet loads are read through a thread safe LruCache.
 */
class TweetRepository {
    private static final String TAG = TweetUi.LOGTAG;
    private static final String AUTH_ERROR = "Auth could not be obtained.";
    // Cache size units are in number of entries, an average Tweet is roughly 900 bytes in memory
    private static final int DEFAULT_CACHE_SIZE = 20;

    private final Handler mainHandler;
    private final TweetUiAuthRequestQueue guestAuthQueue;
    private final TweetUiAuthRequestQueue userAuthQueue;

    // leave this package accessible for testing
    final LruCache<Long, Tweet> tweetCache;
    final LruCache<Long, FormattedTweetText> formatCache;

    TweetRepository(Handler mainHandler, TweetUiAuthRequestQueue userAuthQueue,
            TweetUiAuthRequestQueue guestAuthQueue) {
        this.mainHandler = mainHandler;
        this.userAuthQueue = userAuthQueue;
        this.guestAuthQueue = guestAuthQueue;
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

    protected void updateCache(final Tweet tweet) {
        tweetCache.put(tweet.id, tweet);
    }

    /**
     * Callable on the main thread.
     * @param tweet Tweet to deliver to the client in a Result
     * @param cb the developer callback
     */
    private void deliverTweet(final Tweet tweet, final Callback<Tweet> cb) {
        if (cb == null) return;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                cb.success(new Result<>(tweet, null));
            }
        });
    }

    void favorite(final long tweetId, final Callback<Tweet> cb) {
        userAuthQueue.addClientRequest(new LoggingCallback<TwitterApiClient>(cb,
                Fabric.getLogger()) {
            @Override
            public void success(Result<TwitterApiClient> result) {
                result.data.getFavoriteService().create(tweetId, true, cb);
            }
        });
    }

    void unfavorite(final long tweetId, final Callback<Tweet> cb) {
        userAuthQueue.addClientRequest(new LoggingCallback<TwitterApiClient>(cb,
                Fabric.getLogger()) {
            @Override
            public void success(Result<TwitterApiClient> result) {
                result.data.getFavoriteService().destroy(tweetId, true, cb);
            }
        });
    }

    void retweet(final long tweetId, final Callback<Tweet> cb) {
        userAuthQueue.addClientRequest(new LoggingCallback<TwitterApiClient>(cb,
                Fabric.getLogger()) {
            @Override
            public void success(Result<TwitterApiClient> result) {
                result.data.getStatusesService().retweet(tweetId, false, cb);
            }
        });
    }

    void unretweet(final long tweetId, final Callback<Tweet> cb) {
        userAuthQueue.addClientRequest(new LoggingCallback<TwitterApiClient>(cb,
                Fabric.getLogger()) {
            @Override
            public void success(Result<TwitterApiClient> result) {
//                result.data.getStatusesService().unretweet(tweetId, false, cb);
            }
        });
    }

    /**
     * Queues and loads a Tweet from the API statuses/show endpoint. Queue ensures a client with
     * at least guest auth is obtained before performing the request. Adds the the Tweet from the
     * response to the cache and provides the Tweet to the callback success method.
     * @param tweetId Tweet id
     * @param cb callback
     */
    void loadTweet(final long tweetId, final Callback<Tweet> cb) {
        final Tweet cachedTweet = tweetCache.get(tweetId);

        if (cachedTweet != null) {
            deliverTweet(cachedTweet, cb);
            return;
        }

        guestAuthQueue.addClientRequest(new Callback<TwitterApiClient>() {
            @Override
            public void success(Result<TwitterApiClient> result) {
                result.data.getStatusesService().show(tweetId, null, null, null,
                        new SingleTweetCallback(cb));
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
     * Queues and loads multiple Tweets from the API lookup endpoint. Queue ensures a client with
     * at least guest auth is obtained before performing the request. Orders the Tweets from the
     * response and provides them to the callback success method.
     * @param tweetIds list of Tweet ids
     * @param cb callback
     */
    void loadTweets(final List<Long> tweetIds, final Callback<List<Tweet>> cb) {
        guestAuthQueue.addClientRequest(new Callback<TwitterApiClient>() {

            @Override
            public void success(Result<TwitterApiClient> result) {
                final String commaSepIds = TextUtils.join(",", tweetIds);
                result.data.getStatusesService().lookup(commaSepIds, null, null, null,
                        new MultiTweetsCallback(tweetIds, cb));
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
     * Callback updates the single Tweet cache before passing to the given callback on success.
     * Handles guest auth expired or failing tokens on failure.
     */
    class SingleTweetCallback extends GuestCallback<Tweet> {

        SingleTweetCallback(Callback<Tweet> cb) {
            super(cb);
        }

        @Override
        public void success(Result<Tweet> result) {
            final Tweet tweet = result.data;
            updateCache(tweet);
            if (cb != null) {
                cb.success(new Result<>(tweet, result.response));
            }
        }
    }

    /**
     * Callback handles sorting Tweets before passing to the given callback on success. Handles
     * guest auto expired or failing tokens on failure.
     */
    class MultiTweetsCallback extends GuestCallback<List<Tweet>> {
        final List<Long> tweetIds;

        MultiTweetsCallback(List<Long> tweetIds, Callback<List<Tweet>> cb) {
            super(cb);
            this.tweetIds = tweetIds;
        }

        @Override
        public void success(Result<List<Tweet>> result) {
            if (cb != null) {
                final List<Tweet> sorted = Utils.orderTweets(tweetIds, result.data);
                cb.success(new Result<>(sorted, result.response));
            }
        }
    }
}
