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
import android.test.AndroidTestCase;

import com.twitter.sdk.android.core.TwitterCoreTestUtils;
import com.twitter.sdk.android.core.TwitterTestUtils;
import com.twitter.sdk.android.core.models.Card;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetBuilder;
import com.twitter.sdk.android.core.models.TweetEntities;
import com.twitter.sdk.android.core.models.UserBuilder;

public class TweetUtilsTest extends AndroidTestCase {
    static final String NOT_STARTED_ERROR = "Must initialize Twitter before using getInstance()";
    private static final String A_FULL_PERMALINK =
            "https://twitter.com/jack/status/20?ref_src=twsrc%5Etwitterkit";
    private static final String A_PERMALINK_WITH_NO_SCREEN_NAME
            = "https://twitter.com/twitter_unknown/status/20?ref_src=twsrc%5Etwitterkit";
    private static final String A_VALID_SCREEN_NAME = "jack";
    private static final int A_VALID_TWEET_ID = 20;
    private static final int AN_INVALID_TWEET_ID = 0;

    @Override
    public void tearDown() throws Exception {
        TwitterTestUtils.resetTwitter();
        TwitterCoreTestUtils.resetTwitterCore();
        TweetUiTestUtils.resetTweetUi();

        super.tearDown();
    }

    public void testLoadTweet_beforeKitStart() {
        try {
            TweetUtils.loadTweet(TestFixtures.TEST_TWEET_ID, null);
            fail("IllegalStateException not thrown");
        } catch (IllegalStateException e) {
            assertEquals(NOT_STARTED_ERROR, e.getMessage());
        } catch (Exception ex) {
            fail();
        }
    }

    public void testLoadTweets_beforeKitStart() {
        try {
            TweetUtils.loadTweets(TestFixtures.TWEET_IDS, null);
            fail("IllegalStateException not thrown");
        } catch (IllegalStateException e) {
            assertEquals(NOT_STARTED_ERROR, e.getMessage());
        } catch (Exception ex) {
            fail();
        }
    }

    public void testIsTweetResolvable_nullTweet() {
        assertFalse(TweetUtils.isTweetResolvable(null));
    }

    public void testIsTweetResolvable_hasInvalidIdAndNullUser() {
        final Tweet tweet = new TweetBuilder().build();
        assertNull(tweet.user);
        assertFalse(TweetUtils.isTweetResolvable(tweet));
    }

    public void testIsTweetResolvable_hasValidIdAndNullUser() {
        final Tweet tweet = new TweetBuilder().setId(TestFixtures.TEST_TWEET_ID).build();
        assertNull(tweet.user);
        assertFalse(TweetUtils.isTweetResolvable(tweet));
    }

    public void testIsTweetResolvable_hasInvalidIdAndUserWithNullScreenName() {
        final Tweet tweet = new TweetBuilder()
                .setUser(
                        new UserBuilder()
                                .setId(1)
                                .setName(null)
                                .setScreenName(null)
                                .setVerified(false)
                                .build())
                .build();
        assertFalse(TweetUtils.isTweetResolvable(tweet));
    }

    public void testIsTweetResolvable_hasValidIdAndUserWithNullScreenName() {
        final Tweet tweet = new TweetBuilder()
                .setId(TestFixtures.TEST_TWEET_ID)
                .setUser(
                        new UserBuilder()
                                .setId(1)
                                .setName(null)
                                .setScreenName(null)
                                .setVerified(false)
                                .build()
                ).build();
        assertFalse(TweetUtils.isTweetResolvable(tweet));
    }

    public void testIsTweetResolvable_hasInvalidIdAndUserWithEmptyScreenName() {
        final Tweet tweet = new TweetBuilder()
                .setUser(new UserBuilder()
                        .setId(1)
                        .setName(null)
                        .setScreenName("")
                        .setVerified(false)
                        .build())
                .build();
        assertFalse(TweetUtils.isTweetResolvable(tweet));
    }

    public void testIsTweetResolvable_hasValidIdAndUserWithEmptyScreenName() {
        final Tweet tweet = new TweetBuilder()
                .setId(TestFixtures.TEST_TWEET_ID)
                .setUser(new UserBuilder()
                        .setId(1)
                        .setName(null)
                        .setScreenName("")
                        .setVerified(false)
                        .build())
                .build();
        assertFalse(TweetUtils.isTweetResolvable(tweet));
    }

    public void testIsTweetResolvable_hasUserWithScreenNameAndValidId() {
        assertTrue(TweetUtils.isTweetResolvable(TestFixtures.TEST_TWEET));
    }

    public void testGetPermalink_nullScreenNameValidId() {
        assertEquals(A_PERMALINK_WITH_NO_SCREEN_NAME,
                TweetUtils.getPermalink(null, A_VALID_TWEET_ID).toString());
    }

    public void testGetPermalink_validScreenNameZeroId() {
        assertNull(TweetUtils.getPermalink(A_VALID_SCREEN_NAME, AN_INVALID_TWEET_ID));
    }

    public void testGetPermalink_validScreenNameAndId() {
        assertEquals(A_FULL_PERMALINK,
                TweetUtils.getPermalink(A_VALID_SCREEN_NAME, A_VALID_TWEET_ID).toString());
    }

    public void testGetPermalink_emptyScreenName() {
        final Uri permalink = TweetUtils.getPermalink("", 20);
        assertEquals(A_PERMALINK_WITH_NO_SCREEN_NAME, permalink.toString());
    }

    public void testGetDisplayTweet_nullTweet() {
        assertNull(TweetUtils.getDisplayTweet(null));
    }

    public void testGetDisplayTweet_retweet() {
        assertEquals(TestFixtures.TEST_RETWEET.retweetedStatus,
                TweetUtils.getDisplayTweet(TestFixtures.TEST_RETWEET));
    }

    public void testGetDisplayTweet_nonRetweet() {
        assertEquals(TestFixtures.TEST_TWEET, TweetUtils.getDisplayTweet(TestFixtures.TEST_TWEET));
    }

    public void testShowQuoteTweet() {
        final Tweet tweet = new TweetBuilder()
                .copy(TestFixtures.TEST_TWEET)
                .setQuotedStatus(TestFixtures.TEST_TWEET)
                .build();
        assertTrue(TweetUtils.showQuoteTweet(tweet));
    }

    public void testShowQuoteTweet_withCardAndQuoteTweet() {
        final Tweet tweet = new TweetBuilder()
                .setQuotedStatus(TestFixtures.TEST_TWEET)
                .setCard(new Card(null, "Vine"))
                .setEntities(new TweetEntities(null, null, null, null, null))
                .build();
        assertFalse(TweetUtils.showQuoteTweet(tweet));
    }

    public void testShowQuoteTweet_withMediaAndQuoteTweet() {
        final Tweet tweet = new TweetBuilder()
                .copy(TestFixtures.TEST_PHOTO_TWEET)
                .setQuotedStatus(TestFixtures.TEST_TWEET)
                .build();
        assertFalse(TweetUtils.showQuoteTweet(tweet));
    }

    public void testShowQuoteTweet_nullEntity() {
        final Tweet tweet = new TweetBuilder()
                .copy(TestFixtures.TEST_PHOTO_TWEET)
                .setQuotedStatus(TestFixtures.TEST_TWEET)
                .setEntities(null)
                .build();
        assertTrue(TweetUtils.showQuoteTweet(tweet));
    }
}
