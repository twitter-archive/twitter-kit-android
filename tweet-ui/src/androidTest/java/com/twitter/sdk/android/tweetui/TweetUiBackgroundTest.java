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

import android.test.InstrumentationTestCase;
import android.test.UiThreadTest;

import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCoreTestUtils;
import com.twitter.sdk.android.core.TwitterTestUtils;

import java.util.concurrent.ThreadPoolExecutor;

import static org.mockito.Mockito.mock;

public class TweetUiBackgroundTest extends InstrumentationTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Set a mock thread pool executor so we can run these tests knowing that doInBackground
        // has not been run.
        Twitter.initialize(new TwitterConfig.Builder(getInstrumentation().getTargetContext())
                .executorService(mock(ThreadPoolExecutor.class))
                .build());
    }

    @Override
    protected void tearDown() throws Exception {
        TwitterTestUtils.resetTwitter();
        TwitterCoreTestUtils.resetTwitterCore();
        TweetUiTestUtils.resetTweetUi();

        super.tearDown();
    }

    @UiThreadTest
    public void testRenderTweet_beforeInBackground() {
        try {
            new TweetView(getInstrumentation().getTargetContext(), TestFixtures.TEST_TWEET);
        } catch (IllegalArgumentException e) {
            fail(e.getMessage());
        }
    }

    public void testGetTweetRepository() {
        assertNotNull(TweetUi.getInstance().getTweetRepository());
    }
}
