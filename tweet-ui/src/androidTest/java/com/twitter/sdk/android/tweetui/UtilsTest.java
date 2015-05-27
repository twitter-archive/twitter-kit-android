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
import java.util.List;

import io.fabric.sdk.android.FabricAndroidTestCase;

public class UtilsTest extends FabricAndroidTestCase {

    public void testNumberOrDefault_validNumber() {
        assertEquals(Long.valueOf(123), Utils.numberOrDefault("123", -1L));
    }

    public void testNumberOrDefault_invalidNumber() {
        assertEquals(Long.valueOf(-1L), Utils.numberOrDefault("abc", -1L));
    }

    public void testStringOrEmpty_validString() {
        assertEquals("string", Utils.stringOrEmpty("string"));
    }

    public void testStringOrEmpty_nullString() {
        assertEquals("", Utils.stringOrEmpty(null));
    }

    public void testStringOrDefault_validString() {
        assertEquals("string", Utils.stringOrDefault("string", "default"));
    }

    public void testStringOrDefault_nullString() {
        assertEquals("default", Utils.stringOrDefault(null, "default"));
    }

    public void testCharSeqOrEmpty_validCharSeq() {
        assertEquals("string", Utils.charSeqOrEmpty("string"));
    }

    public void testCharSeqOrEmpty_nullCharSeq() {
        assertEquals("", Utils.charSeqOrEmpty(null));
    }

    public void testCharSeqOrDefault_validCharSeq() {
        assertEquals("string", Utils.charSeqOrDefault("string", "default"));
    }

    public void testCharSeqOrDefault_nullCharSeq() {
        assertEquals("default", Utils.charSeqOrDefault(null, "default"));
    }

    public void testSortTweets() {
        final List<Long> requestedIds = TestFixtures.TWEET_IDS;
        final List<Tweet> tweets = new ArrayList<>();
        tweets.addAll(TestFixtures.UNORDERED_TWEETS);
        final List<Tweet> ordered = Utils.orderTweets(requestedIds, tweets);
        assertEquals(TestFixtures.ORDERED_TWEETS, ordered);
    }

    // Tweet results will match the requested Tweet ids, duplicate requested ids duplicate Tweets.
    public void testSortTweets_duplicateRequestedIds() {
        final List<Long> requestedIds = TestFixtures.DUPLICATE_TWEET_IDS;
        final List<Tweet> tweets = new ArrayList<>();
        tweets.addAll(TestFixtures.UNORDERED_TWEETS);
        final List<Tweet> ordered = Utils.orderTweets(requestedIds, tweets);
        assertEquals(TestFixtures.ORDERED_DUPLICATE_TWEETS, ordered);
    }

    // Tweet results will match the requested Tweet ids, duplicate results ignored.
    public void testSortTweets_duplicateTweets() {
        final List<Long> requestedIds = TestFixtures.TWEET_IDS;
        final List<Tweet> tweets = new ArrayList<>();
        tweets.addAll(TestFixtures.UNORDERED_DUPLICATE_TWEETS);
        final List<Tweet> ordered = Utils.orderTweets(requestedIds, tweets);
        assertEquals(TestFixtures.ORDERED_TWEETS, ordered);
    }

    public void testSortTweets_missingTweets() {
        final List<Long> requestedIds = TestFixtures.TWEET_IDS;
        final List<Tweet> tweets = new ArrayList<>();
        tweets.addAll(TestFixtures.UNORDERED_MISSING_TWEETS);
        final List<Tweet> ordered = Utils.orderTweets(requestedIds, tweets);
        assertEquals(TestFixtures.ORDERED_MISSING_TWEETS, ordered);
    }

    // Tweet result with an extra, unrequested Tweet, not included in the result.
    public void testSortTweets_extraTweetsFirst() {
        final List<Long> requestedIds = TestFixtures.TWEET_IDS;
        final List<Tweet> tweets = new ArrayList<>();
        tweets.addAll(TestFixtures.UNORDERED_TWEETS);
        tweets.add(TestFixtures.TEST_TWEET);

        final List<Tweet> ordered = Utils.orderTweets(requestedIds, tweets);
        assertEquals(TestFixtures.ORDERED_TWEETS, ordered);
    }
}

