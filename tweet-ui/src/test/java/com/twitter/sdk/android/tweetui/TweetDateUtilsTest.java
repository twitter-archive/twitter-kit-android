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

import android.content.res.Resources;
import android.text.format.DateUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class TweetDateUtilsTest {
    // this is an arbitrary date, but the relative date assertions are all based off of it
    private static final long NOW_IN_MILLIS = 1395345704198L;
    private static final long JACKS_FIRST_TWEET_IN_MILLIS = 1142974214000L;

    private Resources resources;
    private TimeZone realDefaultTimeZone;

    @Before
    public void setUp() throws Exception {
        resources = RuntimeEnvironment.application.getResources();

        // force timezone in utc so we get consistent values out of the formatter classes that rely
        // on using the default timezone. We restore in tearDown whatever the real default timezone
        // was in order to not interfere with other tests
        realDefaultTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        TweetDateUtils.DATE_TIME_RFC822.setTimeZone(TimeZone.getDefault());
    }

    @After
    public void tearDown() throws Exception {
        TimeZone.setDefault(realDefaultTimeZone);
    }

    @Test
    public void testApiTimeToLong_jacksFirstTweet() {
        assertEquals(JACKS_FIRST_TWEET_IN_MILLIS,
                TweetDateUtils.apiTimeToLong("Tue Mar 21 20:50:14 +0000 2006"));
    }

    @Test
    public void testApiTimeToLong_emptyString() {
        assertEquals(TweetDateUtils.INVALID_DATE,
                TweetDateUtils.apiTimeToLong(""));
    }

    @Test
    public void testApiTimeToLong_nullString() {
        assertEquals(TweetDateUtils.INVALID_DATE,
                TweetDateUtils.apiTimeToLong(null));
    }

    @Test
    public void testApiTimeToLong_invalidString() {
        assertEquals(TweetDateUtils.INVALID_DATE,
                TweetDateUtils.apiTimeToLong("11111"));
    }

    @Test
    public void testGetRelativeTimeString_now() {
        assertEquals("0s",
                TweetDateUtils.getRelativeTimeString(resources, NOW_IN_MILLIS, NOW_IN_MILLIS));
    }

    @Test
    public void testGetRelativeTimeString_secondsAgo() {
        final long tenSecondsAgo = NOW_IN_MILLIS - DateUtils.SECOND_IN_MILLIS * 10;
        assertEquals("10s",
                TweetDateUtils.getRelativeTimeString(resources, NOW_IN_MILLIS, tenSecondsAgo));
    }

    @Test
    public void testGetRelativeTimeString_minutesAgo() {
        final long twoMinutesAgo = NOW_IN_MILLIS - DateUtils.MINUTE_IN_MILLIS * 2;
        assertEquals("2m",
                TweetDateUtils.getRelativeTimeString(resources, NOW_IN_MILLIS, twoMinutesAgo));
    }

    @Test
    public void testGetRelativeTimeString_hoursAgo() {
        final long twoHoursAgo = NOW_IN_MILLIS - DateUtils.HOUR_IN_MILLIS * 2;
        assertEquals("2h",
                TweetDateUtils.getRelativeTimeString(resources, NOW_IN_MILLIS, twoHoursAgo));
    }

    @Test
    public void testGetRelativeTimeString_daysAgo() {
        final long twoDaysAgo = NOW_IN_MILLIS - DateUtils.DAY_IN_MILLIS * 2;
        assertEquals("Mar 18",
                TweetDateUtils.getRelativeTimeString(resources, NOW_IN_MILLIS, twoDaysAgo));
    }

    @Test
    public void testGetRelativeTimeString_lessThanAYearAgoWithinSameYear() {
        final long sixtyDaysAgo = NOW_IN_MILLIS - DateUtils.DAY_IN_MILLIS * 60;
        assertEquals("Jan 19",
                TweetDateUtils.getRelativeTimeString(resources, NOW_IN_MILLIS, sixtyDaysAgo));
    }

    @Test
    public void testGetRelativeTimeString_moreThanAYearAgo() {
        final long twoYearsAgo = NOW_IN_MILLIS - DateUtils.DAY_IN_MILLIS * 730;
        assertEquals("03/20/12",
                TweetDateUtils.getRelativeTimeString(resources, NOW_IN_MILLIS, twoYearsAgo));
    }

    @Test
    public void testGetRelativeTimeString_inTheFuture() {
        final long twoYearsIntoTheFuture = NOW_IN_MILLIS + DateUtils.DAY_IN_MILLIS * 730;
        assertEquals("03/19/16",
                TweetDateUtils.getRelativeTimeString(resources, NOW_IN_MILLIS,
                        twoYearsIntoTheFuture));
    }

    @Test
    public void testGetRelativeTimeString_negativeTime() {
        final long wayInthePast = -DateUtils.DAY_IN_MILLIS;
        assertEquals("12/31/69",
                TweetDateUtils.getRelativeTimeString(resources, NOW_IN_MILLIS, wayInthePast));
    }

    @Test
    public void testGetRelativeTimeString_zeroTime() {
        assertEquals("01/01/70",
                TweetDateUtils.getRelativeTimeString(resources, NOW_IN_MILLIS, 0));
    }
}
