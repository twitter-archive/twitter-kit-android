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

import android.view.View;

import com.example.app.R;
import com.example.app.tweetui.TweetActivity;
import com.squareup.spoon.Spoon;

import static android.support.test.espresso.Espresso.registerIdlingResources;

/**
 * UI/integration tests of Tweet views added to the layout via code (requires network connectivity).
 */
public class TweetActivityTest extends BaseTweetViewActivityTest<TweetActivity> {
    private static final String TAG = "TweetActivityTest";
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
