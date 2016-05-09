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

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.FabricAndroidTestCase;
import io.fabric.sdk.android.FabricTestUtils;
import io.fabric.sdk.android.services.concurrency.PriorityThreadPoolExecutor;

import static org.mockito.Mockito.*;

/**
 * Call Fabric.with instead of FabricTestUtils.with to detect background thread issues.
 */
public class TweetUiBackgroundTest extends FabricAndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Set a mock thread pool executor so we can run these tests knowing that doInBackground
        // has not been run.
        Fabric.with(new Fabric.Builder(getContext())
                .threadPoolExecutor(mock(PriorityThreadPoolExecutor.class))
                .kits(
                        new TwitterCore(new TwitterAuthConfig(TestFixtures.CONSUMER_KEY,
                                TestFixtures.CONSUMER_SECRET)),
                        new TweetUi())
                .build());
    }

    @Override
    protected void tearDown() throws Exception {
        FabricTestUtils.resetFabric();
        super.tearDown();
    }

    public void testRenderTweet_beforeInBackground() {
        try {
            final TweetView tv = new TweetView(getContext(), TestFixtures.TEST_TWEET);
        } catch (IllegalArgumentException e) {
            fail(e.getMessage());
        } finally {
            FabricTestUtils.resetFabric();
        }
    }

    public void testGetTweetRepository() {
        assertNotNull(TweetUi.getInstance().getTweetRepository());
    }
}
