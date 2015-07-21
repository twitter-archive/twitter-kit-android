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
import com.twitter.sdk.android.core.models.TweetBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests the TweetViewAdapter which can accept Tweet objects and acts as a data provider to
 * ListViews.
 */
public class TweetViewAdapterTest extends TweetUiTestCase {
    private static final int TWEET_COUNT = 2;
    private static final long[] TWEET_IDS = {20L, 30L};
    private final List<Tweet> tweets = new ArrayList<>();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        for (long tweetId: TWEET_IDS) {
            final Tweet tweet = new TweetBuilder().setId(tweetId).build();
            tweets.add(tweet);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        scrubClass(TweetViewAdapterTest.class);
        super.tearDown();
    }

    public void testAdapter_construction() {
        final TweetViewAdapter<CompactTweetView> adapter = new TweetViewAdapter<>(getContext());
        assertEquals(0, adapter.getCount());
        assertNotNull(adapter.getTweets());
        assertTrue(adapter.getTweets().isEmpty());
    }

    public void testAdapter_constructionWithTweets() {
        final TweetViewAdapter<CompactTweetView> adapter = new TweetViewAdapter<>(getContext(),
                tweets);
        BaseTweetView tweetView = (BaseTweetView) adapter.getView(0, null, null);
        assertEquals(tweets.get(0).id, tweetView.getTweetId());
        tweetView = (BaseTweetView) adapter.getView(1, null, null);
        assertEquals(tweets.get(1).id, tweetView.getTweetId());
        assertEquals(TWEET_COUNT, adapter.getCount());
        assertNotNull(adapter.getTweets());
        assertEquals(TWEET_COUNT, adapter.getTweets().size());
    }

    public void testAdapter_setTweets() {
        final TweetViewAdapter<CompactTweetView> adapter = new TweetViewAdapter<>(getContext());
        assertEquals(0, adapter.getCount());
        adapter.setTweets(tweets);
        assertEquals(TWEET_COUNT, adapter.getCount());
        BaseTweetView tweetView = (BaseTweetView) adapter.getView(0, null, null);
        assertEquals(tweets.get(0).id, tweetView.getTweetId());
        tweetView = (BaseTweetView) adapter.getView(1, null, null);
        assertEquals(tweets.get(1).id, tweetView.getTweetId());
    }

    public void testAdapter_setTweetsNull() {
        final TweetViewAdapter<CompactTweetView> adapter = new TweetViewAdapter<>(getContext());
        adapter.setTweets(null);
        assertNotNull(adapter.getTweets());
        assertEquals(0, adapter.getCount());
    }

    public void testAdapter_getTweets() {
        final TweetViewAdapter<CompactTweetView> adapter = new TweetViewAdapter<>(getContext(),
                tweets);
        final List<Tweet> foundTweets = adapter.getTweets();
        assertEquals(TWEET_COUNT, foundTweets.size());
        for (int i = 0; i < foundTweets.size(); i++) {
            assertEquals(TWEET_IDS[i], foundTweets.get(i).id);
        }
    }

    public void testSetTweetById() {
        final TweetViewAdapter<CompactTweetView> adapter = new TweetViewAdapter<>(getContext(),
                tweets);
        final Tweet tweet = adapter.getItem(0);
        final Tweet differentTweetSameId = new TweetBuilder().setId(tweet.getId()).build();
        adapter.setTweetById(differentTweetSameId);
        assertEquals(tweet.getId(), adapter.getItem(0).getId());
        assertNotSame(tweet, adapter.getItem(0));
    }
}
