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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

final class Utils {
    private Utils() {}

    static Long numberOrDefault(String candidate, long defaultLong) {
        try {
            return Long.parseLong(candidate);
        } catch (NumberFormatException e) {
            return defaultLong;
        }
    }

    static String stringOrEmpty(String candidate) {
        return stringOrDefault(candidate, "");
    }

    static String stringOrDefault(String candidate, String defaultString) {
        return (candidate != null) ? candidate : defaultString;
    }

    static CharSequence charSeqOrEmpty(CharSequence candidate) {
        return charSeqOrDefault(candidate, "");
    }

    static CharSequence charSeqOrDefault(CharSequence candidate, CharSequence defaultSequence) {
        return (candidate != null) ? candidate : defaultSequence;
    }

    /**
     * Orders tweets by the tweetIds order. If tweetIds contains duplicates, the result Tweet list
     * will duplicate Tweets accordingly.
     * @param tweetIds ordered list of Tweet ids
     * @param tweets unordered list of Tweet results
     */
    static List<Tweet> orderTweets(List<Long> tweetIds, List<Tweet> tweets) {
        final HashMap<Long, Tweet> idToTweet = new HashMap<>();
        final ArrayList<Tweet> ordered = new ArrayList<>();
        for (Tweet tweet: tweets) {
            idToTweet.put(tweet.id, tweet);
        }
        for (Long id: tweetIds) {
            if (idToTweet.containsKey(id)) {
                ordered.add(idToTweet.get(id));
            }
        }
        return ordered;
    }
}
