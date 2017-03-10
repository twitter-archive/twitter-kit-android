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

import android.test.AndroidTestCase;

import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCoreTestUtils;
import com.twitter.sdk.android.core.TwitterTestUtils;
import com.twitter.sdk.android.core.internal.scribe.EventNamespace;

import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.mock;

public class TweetUiTest extends AndroidTestCase {

    private static final String ANY_CLIENT_NAME = "client";

    private TweetUi tweetUi;
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Twitter.initialize(new TwitterConfig.Builder(getContext())
                .executorService(mock(ExecutorService.class))
                .build());

        tweetUi = new TweetUi();
    }

    @Override
    protected void tearDown() throws Exception {
        TwitterTestUtils.resetTwitter();
        TwitterCoreTestUtils.resetTwitterCore();
        TweetUiTestUtils.resetTweetUi();

        super.tearDown();
    }

    public void testGetVersion() {
        assertEquals(BuildConfig.VERSION_NAME + "." + BuildConfig.BUILD_NUMBER,
                tweetUi.getVersion());
    }

    public void testGetIdentifier() {
        final String identifier = BuildConfig.GROUP + ":" + BuildConfig.ARTIFACT_ID;
        assertEquals(identifier, tweetUi.getIdentifier());
    }

    public void testGetInstance_tweeterStarted() {
        try {
            final TweetUi instance = TweetUi.getInstance();
            assertNotNull(instance);
        } catch (Exception ex) {
            fail("IllegalStateException was expected");
        }
    }

    public void testGetInstance_tweeterNotStarted() {
        TwitterTestUtils.resetTwitter();
        try {
            TweetUi.getInstance();
            fail("IllegalStateException was expected");
        } catch (Exception ex) {
            if (!(ex instanceof IllegalStateException)) {
                fail("IllegalStateException was expected");
            }
        }
    }

    public void testScribe_scribeClientNull() {
        final EventNamespace ns = new EventNamespace.Builder().setClient(ANY_CLIENT_NAME).builder();

        try {
            tweetUi.scribeClient = null;
            tweetUi.scribe(ns, ns);
        } catch (NullPointerException e) {
            fail("should have gracefully ignored events");
        }
    }
}
