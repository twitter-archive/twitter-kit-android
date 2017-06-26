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
import android.net.Uri;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.TwitterSessionHelper;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.unity3d.player.UnityPlayer;

/**
 * A class to expose TwitterKit methods for Unity
 */
public class TwitterKit {
    public static final String GAME_OBJECT_NAME = "TwitterGameObject";

    /**
     * Initializes TwitterKit, must be called before {@link #login}
     * @param consumerKey Twitter App Consumer Key (API Key)
     * @param consumerSecret Twitter App Consumer Secret (API Secret)
     */
    public static void init(final String consumerKey, final String consumerSecret) {
        final TwitterConfig config = new TwitterConfig.Builder(
                UnityPlayer.currentActivity.getApplicationContext())
                .twitterAuthConfig(new TwitterAuthConfig(consumerKey, consumerSecret))
                .build();
        Twitter.initialize(config);
    }

    /**
     * Convenience method for launching Twitter login using JNI.
     */
    public static void login() {
        final Activity currentActivity = UnityPlayer.currentActivity;
        final Intent intent = new Intent(currentActivity, LoginActivity.class);
        currentActivity.startActivity(intent);
    }

    /**
     *  Convenience method for logging out active user using JNI.
     */
    public static void logout() {
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
    }

    /**
     *  Convenience method for retrieving active user using JNI.
     */
    public static String session() {
        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                .getActiveSession();
        return TwitterSessionHelper.serialize(session);
    }

    /**
     * Convenience method for starting Tweet composer with app card preview.
     *
     * @param session the user session
     * @param imageUri URI of the image
     * @param text the text to be included in the tweet
     * @param hashtags the hashtags to be included in the tweet
     */
    public static void compose(String session, String imageUri, String text, String[] hashtags) {
        final Activity currentActivity = UnityPlayer.currentActivity;

        final Intent intent = new ComposerActivity.Builder(currentActivity)
                .session(TwitterSessionHelper.deserialize(session))
                .image(imageUri != null ? Uri.parse(imageUri) : null)
                .text(text)
                .hashtags(hashtags)
                .createIntent();
        currentActivity.startActivity(intent);
    }

    /**
     *  Convenience method for requesting users email address using JNI.
     *
     * @param session the user session
     */
    public static void requestEmail(String session) {
        final TwitterSession twitterSession = TwitterSessionHelper.deserialize(session);
        new TwitterAuthClient().requestEmail(twitterSession, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                final UnityMessage message = new UnityMessage.Builder()
                        .setMethod("RequestEmailComplete")
                        .setData(result.data)
                        .build();
                message.send();
            }

            @Override
            public void failure(TwitterException exception) {
                final String error = new ApiError.Serializer()
                        .serialize(new ApiError(0, exception.getMessage()));
                final UnityMessage message = new UnityMessage.Builder()
                        .setMethod("RequestEmailFailed")
                        .setData(error)
                        .build();
                message.send();
            }
        });
    }
}
