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

import java.lang.SuppressWarnings;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

public abstract class TweetAsserts {
    @SuppressWarnings("PrivateResource")
    public static void assertTweetText(int tweetResId, String expected) throws Exception {
        onView(allOf(withId(R.id.tw__tweet_text),
                isDescendantOfA(withId(tweetResId))))
                .check(matches(isDisplayed()))
                .check(matches(withText(expected)));
    }

    @SuppressWarnings("PrivateResource")
    public static void assertTweetTimestamp(int tweetResId, String expected) throws Exception {
        onView(allOf(withId(R.id.tw__tweet_timestamp),
                isDescendantOfA(withId(tweetResId))))
                .check(matches(isDisplayed()))
                .check(matches(withText(expected)));
    }

    @SuppressWarnings("PrivateResource")
    public static void assertVerifiedUser(int tweetResId) {
        onView(allOf(withId(R.id.tw__tweet_author_verified),
                isDescendantOfA(withId(tweetResId))))
                .check(matches(isDisplayed()));
    }

    @SuppressWarnings("PrivateResource")
    public static void assertNonVerifiedUser(int tweetResId) {
        onView(allOf(withId(R.id.tw__tweet_author_verified),
                isDescendantOfA(withId(tweetResId))))
                .check(matches(not(isDisplayed())));
    }

    @SuppressWarnings("PrivateResource")
    public static void assertNoVerifiedBadge(int tweetResId) {
        onView(allOf(withId(R.id.tw__tweet_author_verified),
                isDescendantOfA(withId(tweetResId))))
                .check(doesNotExist());
    }

    @SuppressWarnings("PrivateResource")
    public static void assertActionsEnabled(int tweetResId) {
        // tweet actions enabled
        onView(allOf(withId(R.id.tw__tweet_action_bar),
                isDescendantOfA(withId(tweetResId))))
                .check(matches(isDisplayed()));
        // share tweet button
        onView(allOf(withId(R.id.tw__tweet_share_button),
                isDescendantOfA(withId(tweetResId))))
                .check(matches(isDisplayed()))
                .check(matches(hasContentDescription()))
                .check(matches(isFocusable()))
                .check(matches(isClickable()));
        // favorite tweet button
        onView(allOf(withId(R.id.tw__tweet_like_button),
                isDescendantOfA(withId(tweetResId))))
                .check(matches(isDisplayed()))
                .check(matches(hasContentDescription()))
                .check(matches(isFocusable()))
                .check(matches(isClickable()));
    }

    @SuppressWarnings("PrivateResource")
    public static void assertActionsDisabled(int tweetResId) {
        onView(allOf(withId(R.id.tw__tweet_action_bar),
                isDescendantOfA(withId(tweetResId))))
                .check(matches(not(isDisplayed())));
    }
}
