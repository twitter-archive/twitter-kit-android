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

import android.graphics.drawable.Drawable;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.TextView;

import com.example.app.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.lang.SuppressWarnings;
import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isFocusable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

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
        onView(allOf(withId(R.id.tw__tweet_author_full_name),
                isDescendantOfA(withId(tweetResId))))
                .check(matches(hasCompoundDrawable(0, 0, R.drawable.tw__ic_tweet_verified, 0)));
    }

    @SuppressWarnings("PrivateResource")
    public static void assertNonVerifiedUser(int tweetResId) {
        onView(allOf(withId(R.id.tw__tweet_author_full_name),
                isDescendantOfA(withId(tweetResId))))
                .check(matches(hasCompoundDrawable(0, 0, 0, 0)));
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

    public static Matcher<View> hasCompoundDrawable(final int start, final int top,
                                                         final int end, final int bottom) {
        return new BoundedMatcher<View, TextView>(TextView.class){
            @Override
            public void describeTo(Description description) {
                final String formatted =
                        String.format(Locale.getDefault(),
                                "has Compound Drawable: start=%d, top=%d, end=%d, bottom=%d",
                                start, top, end, bottom);
                description.appendText(formatted);
            }

            @Override
            public boolean matchesSafely(TextView view) {
                // We cannot verify the actual drawable, but we can verify one has been set.
                final Drawable [] drawables = view.getCompoundDrawables();
                if (drawables[0] != null && start == 0) {
                    return false;
                }
                if (drawables[1] != null && top == 0) {
                    return false;
                }
                if (drawables[2] != null && end == 0) {
                    return false;
                }
                if (drawables[3] != null && bottom == 0) {
                    return false;
                }

                return true;
            }
        };
    }
}
