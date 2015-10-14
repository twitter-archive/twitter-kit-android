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

package com.twitter.sdk.android.unity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.TwitterSessionHelper;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

/**
 * Activity used to launch Twitter login, receive the result, and publish result to Unity.
 */
public class LoginActivity extends Activity {
    TwitterAuthClient authClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authClient = new TwitterAuthClient();
        authClient.authorize(this, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                final String session = TwitterSessionHelper.serialize(result.data);
                final UnityMessage message = new UnityMessage.Builder()
                        .setMethod("LoginComplete")
                        .setData(session)
                        .build();
                message.send();
                finish();
            }

            @Override
            public void failure(TwitterException ex) {
                final String error = new ApiError.Serializer()
                        .serialize(new ApiError(0, ex.getMessage()));
                final UnityMessage message = new UnityMessage.Builder()
                        .setMethod("LoginFailed")
                        .setData(error)
                        .build();
                message.send();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        authClient.onActivityResult(requestCode, resultCode, data);
    }
}
