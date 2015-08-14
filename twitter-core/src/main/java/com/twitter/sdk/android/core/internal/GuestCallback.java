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

package com.twitter.sdk.android.core.internal;

import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterApiException;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import io.fabric.sdk.android.Fabric;

/**
 * GuestCallback is a wrapper callback which clears the AppSession on app or guest auth token
 * errors. GuestCallback should be used when making requests using guest auth.
 * @param <T> success result type (e.g. Tweet, List<Tweet>, etc.)
 */
public class GuestCallback<T> extends Callback<T> {
    protected SessionManager<AppSession> appSessionManager;
    protected Callback<T> cb;

    /**
     * Constructs a wrapper callback which clears the AppSession on failures due to token
     * exceptions.
     * @param cb Callback to be wrapped.
     */
    public GuestCallback(Callback<T> cb) {
        this(TwitterCore.getInstance(), cb);
    }

    GuestCallback(TwitterCore twitterCore, Callback<T> cb) {
        this(twitterCore.getAppSessionManager(), cb);
    }

    GuestCallback(SessionManager<AppSession> sessionManager, Callback<T> cb) {
        this.appSessionManager = sessionManager;
        this.cb = cb;
    }

    /**
     * Calls through to the wrapped callback.
     * @param result the parsed result.
     */
    @Override
    public void success(Result<T> result) {
        if (cb != null) {
            cb.success(result);
        }
    }

    /**
     * Checks the exception and handles token expiration errors by clearing the AppSession from the
     * TwitterCore AppSessionManager.
     * Derived classes should call through to the base implementation.
     * @param exception A Twitter Error.
     */
    @Override
    public void failure(TwitterException exception) {
        if (exception instanceof TwitterApiException) {
            final TwitterApiException apiException = (TwitterApiException) exception;
            final int errorCode = apiException.getErrorCode();
            Fabric.getLogger().e(TwitterCore.TAG, "API call failure.", apiException);

            // clear session if guest auth token or app auth token invalid
            if (errorCode == TwitterApiConstants.Errors.APP_AUTH_ERROR_CODE ||
                    errorCode == TwitterApiConstants.Errors.GUEST_AUTH_ERROR_CODE) {
                if (appSessionManager != null) {
                    appSessionManager.clearSession(TwitterSession.LOGGED_OUT_USER_ID);
                }
            }
        }
        if (cb != null) {
            cb.failure(exception);
        }
    }
}
