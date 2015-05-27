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

import android.net.Uri;
import android.text.TextUtils;

import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Logger;

/**
 * Convenience methods for loading Tweets from the API without requiring a user
 * sign in flow.
 */
public final class TweetUtils {
    private static final String TAG = TweetUi.LOGTAG;
    private static final String PERMALINK_FORMAT = "https://twitter.com/%s/status/%d";
    private static final String UNKNOWN_SCREEN_NAME = "twitter_unknown";
    static final String LOAD_TWEET_DEBUG = "loadTweet failure for Tweet Id %d.";

    private TweetUtils() {}

    /**
     * Loads a single Tweet by id.
     * @param tweetId Tweet id
     * @param cb callback
     */
    public static void loadTweet(final long tweetId, final LoadCallback<Tweet> cb) {
        TweetUi.getInstance().getTweetRepository()
                .loadTweet(tweetId, new LoggingCallback(tweetId, cb, Fabric.getLogger()));
    }

    /**
     * Loads a List of Tweets by id. Returns Tweets in the order requested.
     * @param tweetIds List of Tweet ids
     * @param cb callback
     */
    public static void loadTweets(final List<Long> tweetIds,
                                  final LoadCallback<List<Tweet>> cb) {
        // purposefully not adding a callback for logging the failure to lookup the Tweets
        TweetUi.getInstance().getTweetRepository().loadTweets(tweetIds, cb);
    }

    /**
     * Determines if an accurate permalink can be constructed for the Tweet
     *
     * @param tweet a Tweet which may be missing fields for resolving its author
     * @return Returns true if tweet has a greater than zero id and a screen name
     */
    static boolean isTweetResolvable(Tweet tweet) {
        return tweet != null && tweet.id > 0 && tweet.user != null
                && !TextUtils.isEmpty(tweet.user.screenName);
    }

    /**
     * Returns the Tweet which should be displayed in a TweetView. If the given Tweet is a retweet,
     * the embedded retweetedStatus Tweet is returned.
     * @param tweet A tweet from the API
     * @return either the tweet argument or the Tweet in the retweetedStatus field
     */
    static Tweet getDisplayTweet(Tweet tweet) {
        if (tweet == null || tweet.retweetedStatus == null) {
            return tweet;
        } else {
            return tweet.retweetedStatus;
        }
    }

    /**
     * Builds a permalink url for the given screen name and Tweet id. If we don't have a
     * screen_name, use the constant UNKNOWN_SCREEN_NAME value and the app or the site will figure
     * out the redirect. The reason for using twitter_unknown is that only twitter official accounts
     * can have twitter in their screen name so we'd never be somehow pointing the user to something
     * potentially inflammatory (see twitter.com/unknown for an example).
     *
     * @param screenName The screen name to build the url with
     * @param tweetId    The id to build the url with
     * @return           Can be null, otherwise a resolvable permalink to a Tweet.
     */
    static Uri getPermalink(String screenName, long tweetId) {
        if (tweetId <= 0) {
            return null;
        }

        String permalink;
        if (TextUtils.isEmpty(screenName)) {
            permalink = String.format(Locale.US, PERMALINK_FORMAT, UNKNOWN_SCREEN_NAME, tweetId);
        } else {
            permalink = String.format(Locale.US, PERMALINK_FORMAT, screenName, tweetId);
        }
        return Uri.parse(permalink);
    }

    /**
     * Callback that logs error messages to the debug logger. Otherwise acts as a pass through to
     * the developer specified callback.
     * @deprecated Use tweet-ui LoggingCallback instead.
     */
    @Deprecated
    static class LoggingCallback implements LoadCallback<Tweet> {
        // TODO: Remove class when LoadCallback is removed.
        private final LoadCallback<Tweet> cb;
        private final long tweetId;
        private final Logger logger;

        LoggingCallback(long tweetId, LoadCallback<Tweet> cb, Logger logger) {
            this.tweetId = tweetId;
            this.cb = cb;
            this.logger = logger;
        }

        @Override
        public void success(Tweet loadedItem) {
            if (cb != null) {
                cb.success(loadedItem);
            }
        }

        @Override
        public void failure(TwitterException exception) {
            logger.d(TAG, String.format(LOAD_TWEET_DEBUG, tweetId));
            if (cb != null) {
                cb.failure(exception);
            }
        }
    }
}
