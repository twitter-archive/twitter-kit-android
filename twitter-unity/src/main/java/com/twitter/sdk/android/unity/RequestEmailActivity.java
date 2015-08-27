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
import com.unity3d.player.UnityPlayer;

/**
 * Activity used to launch request email, receive the result, and publish result to Unity.
 */
public class RequestEmailActivity extends Activity {
    TwitterAuthClient authClient;
    TwitterSession twitterSession;
    String gameObjectName;
    String session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = getIntent().getStringExtra(TwitterKit.TWITTER_SESSION);
        gameObjectName = getIntent().getStringExtra(TwitterKit.GAME_OBJECT_NAME);
        twitterSession = TwitterSessionHelper.deserialize(session);
        new TwitterAuthClient().requestEmail(twitterSession, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                UnityPlayer.UnitySendMessage(gameObjectName, "RequestEmailComplete", result.data);
                finish();
            }

            @Override
            public void failure(TwitterException exception) {
                UnityPlayer.UnitySendMessage(gameObjectName, "RequestEmailFailure", "");
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
