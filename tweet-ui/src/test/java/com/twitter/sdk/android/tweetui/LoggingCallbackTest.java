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

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Logger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class LoggingCallbackTest {
    static final String TEST_MESSAGE = "TEST_MESSAGE";

    @Test
    public void testFailure_callsCb() {
        final Callback<Tweet> developerCallback = mock(Callback.class);
        final LoggingCallback<Tweet> cb
                = new TestLoggingCallback<>(developerCallback, mock(Logger.class));
        cb.failure(mock(TwitterException.class));
        verify(developerCallback).failure(any(TwitterException.class));
    }

    @Test
    public void testFailure_handlesNullCb() {
        final Logger logger = mock(Logger.class);
        final LoggingCallback<Tweet> cb = new TestLoggingCallback<>(null, logger);
        try {
            cb.failure(new TwitterException(TEST_MESSAGE));
            verify(logger).e(any(String.class), any(String.class), any(Throwable.class));
        } catch (NullPointerException e) {
            fail("Should have handled null callback");
        }
    }

    @Test
    public void testFailure_logsFailure() {
        final Callback<Tweet> developerCallback = mock(Callback.class);
        final Logger logger = mock(Logger.class);
        final LoggingCallback<Tweet> cb = new TestLoggingCallback<>(developerCallback, logger);
        cb.failure(new TwitterException(TEST_MESSAGE));
        verify(logger).e(any(String.class), any(String.class), any(Throwable.class));
    }

    public class TestLoggingCallback<T> extends LoggingCallback<T> {

        public TestLoggingCallback(Callback<T> cb, Logger logger) {
            super(cb, logger);
        }

        @Override
        public void success(Result<T> result) {
            // intentionally blank, implements abstract success method
        }
    }
}
