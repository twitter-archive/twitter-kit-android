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

package com.twitter.sdk.android.core.identity;

import android.app.Activity;
import android.content.Intent;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * Abstract class for handling authorization requests.
 */
public abstract class AuthHandler {
    static final String EXTRA_TOKEN = "tk";
    static final String EXTRA_TOKEN_SECRET = "ts";
    static final String EXTRA_SCREEN_NAME = "screen_name";
    static final String EXTRA_USER_ID = "user_id";
    static final String EXTRA_AUTH_ERROR = "auth_error";

    static final int RESULT_CODE_ERROR = Activity.RESULT_FIRST_USER;

    protected final int requestCode;
    private final TwitterAuthConfig config;
    private final Callback<TwitterSession> callback;

    /**
     * @param authConfig  The {@link TwitterAuthConfig}.
     * @param callback    The listener to callback when authorization completes.
     * @param requestCode The request code.
     */
    AuthHandler(TwitterAuthConfig authConfig, Callback<TwitterSession> callback, int requestCode) {
        config = authConfig;
        this.callback = callback;
        this.requestCode = requestCode;
    }

    TwitterAuthConfig getAuthConfig() {
        return config;
    }

    Callback<TwitterSession> getCallback() {
        return callback;
    }

    /**
     * Called to request authorization.
     *
     * @return true if authorize request was successfully started.
     */
    public abstract boolean authorize(Activity activity);

    /**
     * Called when {@link android.app.Activity#onActivityResult(int, int, android.content.Intent)}
     * is called to complete the authorization flow.
     *
     * @param requestCode the request code used for SSO
     * @param resultCode  the result code returned by the SSO activity
     * @param data        the result data returned by the SSO activity
     */
    public boolean handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.requestCode != requestCode) {
            return false;
        }

        final Callback<TwitterSession> callback = getCallback();
        if (callback != null) {
            if (resultCode == Activity.RESULT_OK) {
                final String token = data.getStringExtra(EXTRA_TOKEN);
                final String tokenSecret = data.getStringExtra(EXTRA_TOKEN_SECRET);
                final String screenName = data.getStringExtra(EXTRA_SCREEN_NAME);
                final long userId = data.getLongExtra(EXTRA_USER_ID, 0L);
                callback.success(new Result<>(new TwitterSession(
                        new TwitterAuthToken(token, tokenSecret), userId, screenName), null));
            } else if (data != null && data.hasExtra(EXTRA_AUTH_ERROR)) {
                callback.failure(
                        (TwitterAuthException) data.getSerializableExtra(EXTRA_AUTH_ERROR));
            } else {
                callback.failure(new TwitterAuthException("Authorize failed."));
            }
        }
        return true;
    }
}
