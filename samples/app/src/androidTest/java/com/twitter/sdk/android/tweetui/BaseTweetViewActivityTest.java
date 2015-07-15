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

import android.app.Activity;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;

import com.example.app.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Common UI/integration tests for XML and code Tweet views.
 * @param <T> activity containing the view(s) to test
 */
public abstract class BaseTweetViewActivityTest<T extends Activity> extends
        ActivityInstrumentationTestCase2<T> {
    private static final String EXPECTED_TIMESTAMP = "• 03/21/06";
    private static final String EXPECTED_TEXT = "just setting up my twttr";

    public BaseTweetViewActivityTest(Class<T> activityClass) {
        super(activityClass);
    }

    public void testTweetView() throws Exception {
        // timestamp
        onView(allOf(withId(R.id.tw__tweet_timestamp),
                isDescendantOfA(withId(R.id.jack_regular_tweet))))
                .check(matches(withText(EXPECTED_TIMESTAMP)));
        // text
        onView(allOf(withId(R.id.tw__tweet_text), isDescendantOfA(withId(R.id.jack_regular_tweet))))
                .check(matches(withText(EXPECTED_TEXT)));
        // verified badge for verified user
        onView(allOf(withId(R.id.tw__tweet_author_verified),
                isDescendantOfA(withId(R.id.jack_regular_tweet)))).check(matches(isDisplayed()));
        // no verified badge for unverified user
        onView(allOf(withId(R.id.tw__tweet_author_verified),
                isDescendantOfA(withId(R.id.bike_regular_tweet))))
                .check(matches(not(isDisplayed())));
        // share tweet button
        onView(allOf(withId(R.id.tw__tweet_share),
                isDescendantOfA(withId(R.id.jack_regular_tweet)))).check(matches(
                withText(R.string.tw__share_tweet)));
        onView(allOf(withId(R.id.tw__tweet_share),
                isDescendantOfA(withId(R.id.jack_regular_tweet)))).check(matches(isFocusable()));
        onView(allOf(withId(R.id.tw__tweet_share),
                isDescendantOfA(withId(R.id.jack_regular_tweet)))).check(matches(isClickable()));
        // click share tweet button to open share dialog / share slider (API 21+)
        onView(allOf(withId(R.id.tw__tweet_share),
                isDescendantOfA(withId(R.id.jack_regular_tweet)))).perform(click());
        // espresso can check views in dialog non-default windows, cannot check share slider views
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // check that share dialog shows Share Tweet heading
            onView(withText("Share Tweet"))
                    .inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView()))))
                    .check(matches(isDisplayed()));
        }
    }

    // CompactTweetView

    public void testCompactTweetView() throws Exception {
        // timestamp
        onView(allOf(withId(R.id.tw__tweet_timestamp),
                isDescendantOfA(withId(R.id.jack_compact_tweet))))
                .check(matches(withText(EXPECTED_TIMESTAMP)));
        // text
        onView(allOf(withId(R.id.tw__tweet_text), isDescendantOfA(withId(R.id.jack_compact_tweet))))
                .check(matches(withText(EXPECTED_TEXT)));
        // no verified badge
        onView(allOf(withId(R.id.tw__tweet_author_verified),
                isDescendantOfA(withId(R.id.jack_compact_tweet))))
                .check(doesNotExist());
        // no share tweet button
        onView(allOf(withId(R.id.tw__tweet_share),
                isDescendantOfA(withId(R.id.jack_compact_tweet))))
                .check(doesNotExist());

    }
}
