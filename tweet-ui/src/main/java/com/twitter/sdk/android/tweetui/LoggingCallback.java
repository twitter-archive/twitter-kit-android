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
import com.twitter.sdk.android.core.TwitterException;

/**
 * LoggingCallback logs error messages to the logger and passes TwitterExceptions through to the
 * given Callback. Concrete subclasses must implement success(Result<T> result) and optionally call
 * cb's success with the appropriate unpacked result data.
 * @param <T> expected response type
 */
abstract class LoggingCallback<T> extends Callback<T> {
    // Wrapped cb generic type is unknown, concrete subclass responsible for implementing
    // success(Result<T> result) and unpacking result to call cb with proper type checking
    private final Callback cb;
    private final Logger logger;

    /**
     * Constructs a LoggingCallback.
     * @param cb Wrapped Callback of any type
     * @param logger a Logger.
     */
    LoggingCallback(Callback cb, Logger logger) {
        this.cb = cb;
        this.logger = logger;
    }

    @Override
    public void failure(TwitterException exception) {
        logger.e(TweetUi.LOGTAG, exception.getMessage(), exception);
        if (cb != null) {
            cb.failure(exception);
        }
    }
}
