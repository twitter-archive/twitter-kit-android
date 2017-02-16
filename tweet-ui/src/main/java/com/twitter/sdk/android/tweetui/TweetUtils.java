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

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

/**
 * Convenience methods for loading Tweets from the API without requiring a user
 * sign in flow.
 */
public final class TweetUtils {
    private static final String PERMALINK_FORMAT = "https://twitter.com/%s/status/%d?ref_src=twsrc%%5Etwitterkit";
    private static final String UNKNOWN_SCREEN_NAME = "twitter_unknown";
    static final String LOAD_TWEET_DEBUG = "loadTweet failure for Tweet Id %d.";

    private TweetUtils() {}

    /**
     * Loads a single Tweet by id.
     * @param tweetId Tweet id
     * @param cb callback
     */
    public static void loadTweet(final long tweetId, final Callback<Tweet> cb) {
        TweetUi.getInstance().getTweetRepository().loadTweet(tweetId,
                new com.twitter.sdk.android.tweetui.LoggingCallback<Tweet>(cb, Fabric.getLogger()) {
                    @Override
                    public void success(Result<Tweet> result) {
                        if (cb != null) {
                            cb.success(result);
                        }
                    }
                });
    }

    /**
     * Loads a List of Tweets by id. Returns Tweets in the order requested.
     * @param tweetIds List of Tweet ids
     * @param cb callback
     */
    public static void loadTweets(final List<Long> tweetIds, final Callback<List<Tweet>> cb) {
        TweetUi.getInstance().getTweetRepository().loadTweets(tweetIds,
                new com.twitter.sdk.android.tweetui.LoggingCallback<List<Tweet>>(cb,
                        Fabric.getLogger()) {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        if (cb != null) {
                            cb.success(result);
                        }
                    }
                });
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

    static boolean showQuoteTweet(Tweet tweet) {
        return tweet.quotedStatus != null &&
                tweet.card == null &&
                (tweet.entities.media == null || tweet.entities.media.isEmpty());
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
}
