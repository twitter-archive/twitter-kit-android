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

import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Tweet;

import static org.mockito.Mockito.*;

public class TweetRepositoryTest extends TweetUiTestCase {
    private TwitterCore twitterCoreKit;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        twitterCoreKit = TwitterCore.getInstance();
    }

    @Override
    protected void tearDown() throws Exception {
        twitterCoreKit.logOut();
        scrubClass(TweetRepositoryTest.class);  // gross
        super.tearDown();
    }

    public void testDefaultApiCallbackRunnableSuccess_updateCache() {
        final TestTweetRepository mockRepo = mock(TestTweetRepository.class);

        final TestTweetRepository.TweetApiCallback callback
                = mockRepo.new TweetApiCallback(null);
        callback.success(null, null);

        verify(mockRepo, times(1)).updateCache(any(Tweet.class));
    }
}
