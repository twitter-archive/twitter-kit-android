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
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * OAuth 1.0a implementation of an {@link AuthHandler}
 */
class OAuthHandler extends AuthHandler {

    /**
     * @param authConfig The {@link com.twitter.sdk.android.core.TwitterAuthConfig}.
     * @param callback   The listener to callback when authorization completes.
     */
    OAuthHandler(TwitterAuthConfig authConfig, Callback<TwitterSession> callback,
            int requestCode) {
        super(authConfig, callback, requestCode);
    }

    @Override
    public boolean authorize(Activity activity) {
        activity.startActivityForResult(newIntent(activity), requestCode);
        return true;
    }

    Intent newIntent(Activity activity) {
        final Intent intent = new Intent(activity, OAuthActivity.class);
        intent.putExtra(OAuthActivity.EXTRA_AUTH_CONFIG, getAuthConfig());
        return intent;
    }
}
