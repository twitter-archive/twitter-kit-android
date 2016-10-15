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
import android.view.View;

import com.example.app.R;
import com.example.app.tweetui.TweetActivity;
import com.squareup.spoon.Spoon;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

/**
 * UI/integration tests of Tweet views added to the layout via code (requires network connectivity).
 */
public class TweetActivityTest extends ActivityInstrumentationTestCase2<TweetActivity> {
    private static final String TAG = "TweetActivityTest";
    private static final String EXPECTED_TIMESTAMP = "• 03/21/06";
    private static final String EXPECTED_TEXT = "just setting up my twttr";
    private TweetActivity activity;

    public TweetActivityTest() {
        super(TweetActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        activity = getActivity();
        final View root = activity.findViewById(R.id.tweet_region);
        final AddViewIdlingResource jackRes = new AddViewIdlingResource(R.id.jack_regular_tweet,
                root);
        final AddViewIdlingResource jackCompactRes = new AddViewIdlingResource(
                R.id.jack_compact_tweet, root);
        final AddViewIdlingResource bikeRes = new AddViewIdlingResource(R.id.bike_regular_tweet,
                root);
        final AddViewIdlingResource bikeCompactRes = new AddViewIdlingResource(
                R.id.bike_compact_tweet, root);
        registerIdlingResources(jackRes, jackCompactRes, bikeRes, bikeCompactRes);
    }

    public void testTweetView() throws Exception {
        onView(withId(R.id.jack_regular_tweet)).perform(scrollTo());
        TweetAsserts.assertTweetText(R.id.jack_regular_tweet, EXPECTED_TEXT);
        TweetAsserts.assertTweetTimestamp(R.id.jack_regular_tweet, EXPECTED_TIMESTAMP);
        TweetAsserts.assertVerifiedUser(R.id.jack_regular_tweet);
        TweetAsserts.assertActionsEnabled(R.id.jack_regular_tweet);

        onView(withId(R.id.bike_regular_tweet)).perform(scrollTo());
        TweetAsserts.assertNonVerifiedUser(R.id.bike_regular_tweet);

        Spoon.screenshot(activity, TAG);
    }

    public void testCompactTweetView() throws Exception {
        onView(withId(R.id.jack_compact_tweet)).perform(scrollTo());
        TweetAsserts.assertTweetText(R.id.jack_compact_tweet, EXPECTED_TEXT);
        TweetAsserts.assertTweetTimestamp(R.id.jack_compact_tweet, EXPECTED_TIMESTAMP);
        TweetAsserts.assertNonVerifiedUser(R.id.jack_compact_tweet);
        TweetAsserts.assertActionsEnabled(R.id.jack_compact_tweet);

        Spoon.screenshot(activity, TAG);
    }
}
