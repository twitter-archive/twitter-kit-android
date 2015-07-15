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

import com.example.app.R;
import com.example.app.tweetui.XmlTweetActivity;
import com.squareup.spoon.Spoon;

import static android.support.test.espresso.Espresso.registerIdlingResources;

/**
 * UI/integration tests of XML Tweet views (requires network connectivity).
 */
public class XmlTweetActivityTest extends BaseTweetViewActivityTest<XmlTweetActivity> {
    private static final String TAG = "XmlTweetActivityTest";
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
        jackTweet = (TweetView) activity.findViewById(R.id.jack_regular_tweet);
        jackCompactTweet = (CompactTweetView) activity.findViewById(R.id.jack_compact_tweet);
        bikeTweet = (TweetView) activity.findViewById(R.id.bike_regular_tweet);
        bikeCompactTweet = (CompactTweetView) activity.findViewById(R.id.bike_compact_tweet);
        simpsonsRetweet = (TweetView) activity.findViewById(R.id.retweet_simpsons_tweet);
        simpsonsCompactRetweet
                = (CompactTweetView) activity.findViewById(R.id.retweet_simpsons_compact_tweet);
    }

    @Override
    public void testTweetView() throws Exception {
        super.testTweetView();
        Spoon.screenshot(activity, TAG);
    }

    @Override
    public void testCompactTweetView() throws Exception {
        super.testCompactTweetView();
        Spoon.screenshot(activity, TAG);
    }
}
