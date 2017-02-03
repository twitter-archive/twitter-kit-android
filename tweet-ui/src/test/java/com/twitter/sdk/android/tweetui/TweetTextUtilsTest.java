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
import com.twitter.sdk.android.core.models.UrlEntity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class TweetTextUtilsTest {
    private static final String UNESCAPED_TWEET_TEXT = ">Hello there <\"What is a?\" &;";
    private static final String ESCAPED_TWEET_TEXT
            = "&gt;Hello there &lt;&quot;What is a?&quot; &;";

    // test ported from:
    // twitter-android/app/src/androidTest/java/com/twitter/library/util/EntitiesTests.java
    // tests fixing up entity indices after unescaping html characters in tweet text
    @Test
    public void testFormat_singleEscaping() {
        final FormattedTweetText formattedTweetText = setupAdjustedTweet();
        final Tweet tweet = setupTweetToBeFormatted();
        TweetTextUtils.format(formattedTweetText, tweet);

        assertEquals(UNESCAPED_TWEET_TEXT, formattedTweetText.text);
        assertEquals("Hello", 1, formattedTweetText.urlEntities.get(0).start);
        assertEquals("Hello", 5, formattedTweetText.urlEntities.get(0).end);
        assertEquals("There", 7, formattedTweetText.urlEntities.get(1).start);
        assertEquals("There", 11, formattedTweetText.urlEntities.get(1).end);

        assertEquals("What", 15, formattedTweetText.urlEntities.get(2).start);
        assertEquals("What", 18, formattedTweetText.urlEntities.get(2).end);

        assertEquals("is", 20, formattedTweetText.urlEntities.get(3).start);
        assertEquals("is", 21, formattedTweetText.urlEntities.get(3).end);

        assertEquals("a", 23, formattedTweetText.urlEntities.get(4).start);
        assertEquals("a", 23, formattedTweetText.urlEntities.get(4).end);
    }

    @Test
    public void testFormat_htmlEntityEdgeCases() {
        final FormattedTweetText formattedTweetText = new FormattedTweetText();

        Tweet tweet = new TweetBuilder().setText("&amp;").build();
        TweetTextUtils.format(formattedTweetText, tweet);
        assertEquals("&", formattedTweetText.text);

        tweet = new TweetBuilder().setText("&#;").build();
        TweetTextUtils.format(formattedTweetText, tweet);
        assertEquals("&#;", formattedTweetText.text);

        tweet = new TweetBuilder().setText("&#34;").build();
        TweetTextUtils.format(formattedTweetText, tweet);
        assertEquals("\"", formattedTweetText.text);

        tweet = new TweetBuilder().setText("&#x22;").build();
        TweetTextUtils.format(formattedTweetText, tweet);
        assertEquals("\"", formattedTweetText.text);

        tweet = new TweetBuilder().setText("&lt; & Larry &gt; &").build();
        TweetTextUtils.format(formattedTweetText, tweet);
        assertEquals("< & Larry > &", formattedTweetText.text);

        tweet = new TweetBuilder().setText("&&amp;").build();
        TweetTextUtils.format(formattedTweetText, tweet);
        assertEquals("&&", formattedTweetText.text);

        tweet = new TweetBuilder().setText("&&&&&&&&amp;").build();
        TweetTextUtils.format(formattedTweetText, tweet);
        assertEquals("&&&&&&&&", formattedTweetText.text);

        tweet = new TweetBuilder().setText("&&&&gt&&lt&&amplt;").build();
        TweetTextUtils.format(formattedTweetText, tweet);
        assertEquals("&&&&gt&&lt&&amplt;", formattedTweetText.text);
    }

    private Tweet setupTweetToBeFormatted() {
        return new TweetBuilder().setText(ESCAPED_TWEET_TEXT).build();
    }

    private FormattedTweetText setupAdjustedTweet() {
        final FormattedTweetText formattedTweetText = new FormattedTweetText();

        UrlEntity url = TestFixtures.newUrlEntity(4, 8);
        // Hello
        formattedTweetText.urlEntities.add(new FormattedUrlEntity(url));

        // There
        url = TestFixtures.newUrlEntity(10, 14);
        formattedTweetText.urlEntities.add(new FormattedUrlEntity(url));

        // What
        url = TestFixtures.newUrlEntity(26, 29);
        formattedTweetText.urlEntities.add(new FormattedUrlEntity(url));

        // is
        url = TestFixtures.newUrlEntity(31, 32);
        formattedTweetText.urlEntities.add(new FormattedUrlEntity(url));

        // a
        url = TestFixtures.newUrlEntity(34, 34);
        formattedTweetText.urlEntities.add(new FormattedUrlEntity(url));

        return formattedTweetText;
    }
}
