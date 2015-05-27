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

package com.twitter.sdk.android;

import android.test.AndroidTestCase;
import com.twitter.sdk.android.core.TwitterAuthConfig;

public class TwitterTest extends AndroidTestCase {
    private static final int KIT_COUNT = 4;

    private Twitter twitter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        twitter = new Twitter(new TwitterAuthConfig("", ""));
    }

    public void testGetVersion() {
        assertEquals(BuildConfig.VERSION_NAME + "." + BuildConfig.BUILD_NUMBER,
                twitter.getVersion());
    }

    public void testTwitter() {
        assertNotNull(twitter.core);
    }

    public void testTweetUi() {
        assertNotNull(twitter.tweetUi);
    }

    public void testTweetComposer() {
        assertNotNull(twitter.tweetComposer);
    }

    public void testDigits() {
        assertNotNull(twitter.digits);
    }

    public void testGetKits_notNull() {
        assertNotNull(twitter.getKits());
    }

    public void testGetKits_length() {
        assertEquals(KIT_COUNT, twitter.getKits().size());
    }

    public void testGetKits_containsTwitter() {
        assertTrue(twitter.getKits().contains(twitter.core));
    }

    public void testGetKits_containsTweetUi() {
        assertTrue(twitter.getKits().contains(twitter.tweetUi));
    }

    public void testGetKits_containsTweetComposer() {
        assertTrue(twitter.getKits().contains(twitter.tweetComposer));
    }

    public void testGetKits_containsDigits() {
        assertTrue(twitter.getKits().contains(twitter.digits));
    }
}
