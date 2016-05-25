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

import com.google.gson.Gson;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.TwitterSessionHelper;
import com.twitter.sdk.android.tweetcomposer.Card;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.unity3d.player.UnityPlayer;

public class TwitterKit {
    public static final String GAME_OBJECT_NAME = "TwitterGameObject";
    public static final String EXTRA_TWITTER_SESSION = "EXTRA_TWITTER_SESSION";

    /**
     * Convenience method for launching Twitter login using JNI.
     */
    public static void login() {
        final Activity currentActivity = UnityPlayer.currentActivity;
        final Intent intent = new Intent(currentActivity, LoginActivity.class);
        currentActivity.startActivity(intent);
    }

    /**
     *  Convenience method for requesting users email address using JNI.
     *
     * @param session the user session
     */
    public static void requestEmail(String session) {
        final Activity currentActivity = UnityPlayer.currentActivity;
        final Intent intent = new Intent(currentActivity, RequestEmailActivity.class);
        intent.putExtra(EXTRA_TWITTER_SESSION, session);
        currentActivity.startActivity(intent);
    }

    /**
     *  Convenience method for logging out active user using JNI.
     */
    public static void logout() {
        TwitterCore.getInstance().logOut();
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
     * @param config card settings
     */
    public static void compose(String session, String config, String[] hashtags) {
        final Activity currentActivity = UnityPlayer.currentActivity;
        final CardConfig cardConfig = new Gson().fromJson(config, CardConfig.class);
        final Card card = new Card.AppCardBuilder(currentActivity)
                .imageUri(Uri.parse(cardConfig.imageUri))
                .googlePlayId(cardConfig.appGooglePlayId)
                .iPadId(cardConfig.appIPadId)
                .iPhoneId(cardConfig.appIPhoneId)
                .build();

        final Intent intent = new ComposerActivity.Builder(currentActivity)
                .session(TwitterSessionHelper.deserialize(session))
                .card(card)
                .hashtags(hashtags)
                .createIntent();
        currentActivity.startActivity(intent);
    }

    static class CardConfig {
        final public String appIPhoneId;
        final public String appIPadId;
        final public String appGooglePlayId;
        final public String imageUri;

        CardConfig(String imageUri, String appGooglePlayId, String appIPadId,
                String appIPhoneId) {
            this.imageUri = imageUri;
            this.appGooglePlayId = appGooglePlayId;
            this.appIPadId = appIPadId;
            this.appIPhoneId = appIPhoneId;
        }
    }
}
