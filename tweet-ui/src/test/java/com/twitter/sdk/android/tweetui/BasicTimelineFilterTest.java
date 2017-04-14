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

import android.annotation.SuppressLint;

import com.twitter.sdk.android.core.models.HashtagEntity;
import com.twitter.sdk.android.core.models.MentionEntity;
import com.twitter.sdk.android.core.models.SymbolEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetBuilder;
import com.twitter.sdk.android.core.models.TweetEntities;
import com.twitter.sdk.android.core.models.UrlEntity;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.models.UserBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class BasicTimelineFilterTest {
    static final Tweet TEST_TWEET_1 = new TweetBuilder()
            .setText("Lorem Ipsum is simply dummy text of the printing and typesetting industry.")
            .setLang("en")
            .build();
    static final Tweet TEST_TWEET_2 = new TweetBuilder()
            .setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
            .setLang("en")
            .build();
    static final Tweet TEST_TWEET_3 = new TweetBuilder()
            .setText("Donec sodales imperdiet nisi non ullamcorper. Maecenas in turpis ex.")
            .setLang("en")
            .build();

    BasicTimelineFilter basicTimelineFilter;

    @Before
    @SuppressLint("NewApi")
    public void setUp() throws Exception {
        final List<String> keywords = Arrays.asList("dummy", "darn", "foobar");
        final List<String> hashtags = Arrays.asList("cookies", "CookiesAreAwesome");
        final List<String> handles = Arrays.asList("benward", "vam_si", "ericfrohnhoefer");
        final List<String> urls = Arrays.asList("Cookiesareawesome.com", "beevil.net");
        final FilterValues filterValues = new FilterValues(keywords, hashtags, handles, urls);

        basicTimelineFilter = new BasicTimelineFilter(filterValues);
    }

    @Test
    public void testFilter() {
        final List<Tweet> tweets = new ArrayList<>();
        tweets.add(TEST_TWEET_1);
        tweets.add(TEST_TWEET_2);
        tweets.add(TEST_TWEET_3);

        final List<Tweet> filteredTweets = basicTimelineFilter.filter(tweets);

        assertNotNull(filteredTweets);
        assertEquals(2, filteredTweets.size());
        assertEquals(TEST_TWEET_2, filteredTweets.get(0));
        assertEquals(TEST_TWEET_3, filteredTweets.get(0));
    }

    @Test
    public void testShouldFilterTweet_withNoMatch() {
        assertFalse(basicTimelineFilter.shouldFilterTweet(TEST_TWEET_2));
    }

    @Test
    public void testShouldFilterTweet_withTextMatch() {
        assertTrue(basicTimelineFilter.shouldFilterTweet(TEST_TWEET_1));
    }

    @Test
    public void testShouldFilterTweet_withEntityMatch() {
        final UrlEntity entity =
                new UrlEntity("beevil.net", "http://beevil.net", "beevil.net", 0, 0);
        final TweetEntities entities =
                new TweetEntities(Collections.singletonList(entity), null, null, null, null);
        final Tweet tweet = new TweetBuilder().setText("").setEntities(entities).build();

        assertTrue(basicTimelineFilter.shouldFilterTweet(tweet));
    }

    @Test
    public void testShouldFilterTweet_withUserMatch() {
        final User user = new UserBuilder().setScreenName("EricFrohnhoefer").build();
        final Tweet tweet = new TweetBuilder().setText("").setUser(user).build();

        assertTrue(basicTimelineFilter.shouldFilterTweet(tweet));
    }

    @Test
    public void testContainsMatchingText_withNoMatch() {
        assertFalse(basicTimelineFilter.containsMatchingText(TEST_TWEET_2));
    }

    @Test
    public void testContainsMatchingText_withMatch() {
        assertTrue(basicTimelineFilter.containsMatchingText(TEST_TWEET_1));
    }

    @Test
    public void testContainsMatchingUrl_withNoMatch() {
        final UrlEntity entity =
                new UrlEntity("foobar.com", "http://foobar.com", "foobar.com", 0, 0);

        assertFalse(basicTimelineFilter.containsMatchingUrl(Collections.singletonList(entity)));
    }

    @Test
    public void testContainsMatchingUrl_withMatch() {
        final UrlEntity entity = new UrlEntity("Cookiesareawesome.com",
                "http://Cookiesareawesome.com", "Cookiesareawesome.com", 0, 0);

        assertTrue(basicTimelineFilter.containsMatchingUrl(Collections.singletonList(entity)));
    }

    @Test
    public void testContainsMatchingHashtag_withNoMatch() {
        final HashtagEntity entity = new HashtagEntity("foobar", 0, 0);

        assertFalse(basicTimelineFilter.containsMatchingHashtag(Collections.singletonList(entity)));
    }

    @Test
    public void testContainsMatchingHashtag_withMatch() {
        final HashtagEntity entity = new HashtagEntity("cookies", 0, 0);

        assertTrue(basicTimelineFilter.containsMatchingHashtag(Collections.singletonList(entity)));
    }

    @Test
    public void testContainsMatchingSymbol_withNoMatch() {
        final SymbolEntity entity = new SymbolEntity("foobar", 0, 0);

        assertFalse(basicTimelineFilter.containsMatchingSymbol(Collections.singletonList(entity)));
    }

    @Test
    public void testContainsMatchingSymbol_withMatch() {
        final SymbolEntity entity = new SymbolEntity("cookies", 0, 0);

        assertTrue(basicTimelineFilter.containsMatchingSymbol(Collections.singletonList(entity)));
    }

    @Test
    public void testContainsMatchingMention_withNoMatch() {
        final MentionEntity entity =
                new MentionEntity(0, "0", "Foo Bar", "FooBar", 0, 0);

        assertFalse(basicTimelineFilter.containsMatchingMention(Collections.singletonList(entity)));
    }

    @Test
    public void testContainsMatchingMention_withMatch() {
        final MentionEntity entity =
                new MentionEntity(0, "0", "Eric Frohnhoefer", "EricFrohnhoefer", 0, 0);

        assertTrue(basicTimelineFilter.containsMatchingMention(Collections.singletonList(entity)));
    }

    @Test
    public void testContainsMatchingScreenName_withNoMatch() {
        assertFalse(basicTimelineFilter.containsMatchingScreenName("FooBar"));
    }

    @Test
    public void testContainsMatchingScreenName_withMatch() {
        assertTrue(basicTimelineFilter.containsMatchingScreenName("EricFrohnhoefer"));
    }

    @Test
    public void testNormalizeHandle() {
        String twitterHandle = "@twitter";
        String normalizedHandle = BasicTimelineFilter.normalizeHandle(twitterHandle);
        assertEquals("twitter", normalizedHandle);

        twitterHandle = "＠twitter";
        normalizedHandle = BasicTimelineFilter.normalizeHandle(twitterHandle);
        assertEquals("twitter", normalizedHandle);
    }

    @Test
    public void testNormalizeHandleWithoutAtSign() {
        final String twitterHandle = "twiTTer";
        final String normalizedHandle = BasicTimelineFilter.normalizeHandle(twitterHandle);
        assertEquals("twitter", normalizedHandle);
    }

    @Test
    public void testNormalizeHashtag() {
        String hashtag = "#twitter";
        String normalizedHashtag = BasicTimelineFilter.normalizeHashtag(hashtag);
        assertEquals("twitter", normalizedHashtag);

        hashtag = "＃twitter";
        normalizedHashtag = BasicTimelineFilter.normalizeHashtag(hashtag);
        assertEquals("twitter", normalizedHashtag);

        hashtag = "$TWTR";
        normalizedHashtag = BasicTimelineFilter.normalizeHashtag(hashtag);
        assertEquals("TWTR", normalizedHashtag);
    }

    @Test
    public void testNormalizeHashtagWithoutHashtag() {
        final String hashtag = "TWTR";
        final String normalizedHashtag = BasicTimelineFilter.normalizeHashtag(hashtag);
        assertEquals(hashtag, normalizedHashtag);
    }

    @Test
    public void testNormalizeUrl() {
        String url = "twitter.com";
        String normalizedUrl = BasicTimelineFilter.normalizeUrl(url);
        assertEquals("twitter.com", normalizedUrl);

        url = "dev.twitter.com";
        normalizedUrl = BasicTimelineFilter.normalizeUrl(url);
        assertEquals("dev.twitter.com", normalizedUrl);

        url = "http://twitter.com";
        normalizedUrl = BasicTimelineFilter.normalizeUrl(url);
        assertEquals("twitter.com", normalizedUrl);

        url = "http://TwiTTer.com";
        normalizedUrl = BasicTimelineFilter.normalizeUrl(url);
        assertEquals("twitter.com", normalizedUrl);

        url = "https://twitter.com/test";
        normalizedUrl = BasicTimelineFilter.normalizeUrl(url);
        assertEquals("twitter.com", normalizedUrl);

        url = "транспорт.com";
        normalizedUrl = BasicTimelineFilter.normalizeUrl(url);
        assertEquals("xn--80a0addceeeh.com", normalizedUrl);

        url = "https://транспорт.com/test";
        normalizedUrl = BasicTimelineFilter.normalizeUrl(url);
        assertEquals("xn--80a0addceeeh.com", normalizedUrl);
    }

    @Test
    public void testNormalizeUrl_withProhibitedCodePoint() {
        final String url = "twitter\u180E.com";
        final String normalizedUrl = BasicTimelineFilter.normalizeUrl(url);
        assertEquals(url, normalizedUrl);
    }
}
