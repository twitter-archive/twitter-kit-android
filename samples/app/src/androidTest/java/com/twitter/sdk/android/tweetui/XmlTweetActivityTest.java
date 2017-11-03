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

import android.test.ActivityInstrumentationTestCase2;

import com.example.app.R;
import com.example.app.tweetui.XmlTweetActivity;
import com.squareup.spoon.Spoon;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

/**
 * UI/integration tests of XML Tweet views (requires network connectivity).
 */
public class XmlTweetActivityTest extends ActivityInstrumentationTestCase2<XmlTweetActivity> {
    private static final String TAG = "XmlTweetActivityTest";
    private static final String EXPECTED_TIMESTAMP = "• 03/21/06";
    private static final String EXPECTED_TEXT = "just setting up my twttr";
    private XmlTweetActivity activity;
    // activity views to test
    TweetView jackTweet;
    TweetView bikeTweet;
    TweetView simpsonsRetweet;
    CompactTweetView jackCompactTweet;
    CompactTweetView bikeCompactTweet;
    CompactTweetView simpsonsCompactRetweet;

    public XmlTweetActivityTest() {
        super(XmlTweetActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        activity = getActivity();
        findViews();
        final XmlTweetViewIdlingResource jackRes = new XmlTweetViewIdlingResource(jackTweet,
                "jack_regular_tweet");
        final XmlTweetViewIdlingResource jackCompactRes = new XmlTweetViewIdlingResource(
                jackCompactTweet, "jack_compact_tweet");
        final XmlTweetViewIdlingResource bikeRes = new XmlTweetViewIdlingResource(bikeTweet,
                "bike_regular_tweet");
        final XmlTweetViewIdlingResource bikeCompactRes = new XmlTweetViewIdlingResource(
                bikeCompactTweet, "bike_compact_tweet");
        final XmlTweetViewIdlingResource simpsonsRes = new XmlTweetViewIdlingResource(
                simpsonsRetweet, "retweet_simpsons_tweet");
        final XmlTweetViewIdlingResource simpsonsCompactRes = new XmlTweetViewIdlingResource(
                simpsonsCompactRetweet, "retweet_simpsons_tweet_compact");
        registerIdlingResources(jackRes, jackCompactRes, bikeRes, bikeCompactRes,
                simpsonsRes, simpsonsCompactRes);
    }

    private void findViews() {
        jackTweet = activity.findViewById(R.id.jack_regular_tweet);
        jackCompactTweet = activity.findViewById(R.id.jack_compact_tweet);
        bikeTweet = activity.findViewById(R.id.bike_regular_tweet);
        bikeCompactTweet = activity.findViewById(R.id.bike_compact_tweet);
        simpsonsRetweet = activity.findViewById(R.id.retweet_simpsons_tweet);
        simpsonsCompactRetweet
                = activity.findViewById(R.id.retweet_simpsons_compact_tweet);
    }

    public void testTweetView() throws Exception {
        onView(withId(R.id.jack_regular_tweet)).perform(scrollTo());
        TweetAsserts.assertTweetText(R.id.jack_regular_tweet, EXPECTED_TEXT);
        TweetAsserts.assertTweetTimestamp(R.id.jack_regular_tweet, EXPECTED_TIMESTAMP);
        TweetAsserts.assertVerifiedUser(R.id.jack_regular_tweet);
        TweetAsserts.assertActionsDisabled(R.id.jack_regular_tweet);

        onView(withId(R.id.bike_regular_tweet)).perform(scrollTo());
        TweetAsserts.assertNonVerifiedUser(R.id.bike_regular_tweet);

        Spoon.screenshot(activity, TAG);
    }

    public void testCompactTweetView() throws Exception {
        onView(withId(R.id.jack_compact_tweet)).perform(scrollTo());
        TweetAsserts.assertTweetText(R.id.jack_compact_tweet, EXPECTED_TEXT);
        TweetAsserts.assertTweetTimestamp(R.id.jack_compact_tweet, EXPECTED_TIMESTAMP);
        TweetAsserts.assertNonVerifiedUser(R.id.jack_compact_tweet);
        TweetAsserts.assertActionsDisabled(R.id.jack_compact_tweet);

        Spoon.screenshot(activity, TAG);
    }
}
