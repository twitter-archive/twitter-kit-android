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

import com.twitter.sdk.android.core.TwitterCore;
import com.unity3d.player.UnityPlayer;

public class TwitterKit {
    public static final String GAME_OBJECT_NAME = "GAME_OBJECT_NAME";
    public static final String TWITTER_SESSION = "TWITTER_SESSION";

    /**
     * Convenience method for launching Twitter login using JNI.
     *
     * @param gameObjectName name of GameObject to receive LoginComplete or LoginFailure message.
     */
    public static void startLogin(String gameObjectName) {
        final Activity currentActivity = UnityPlayer.currentActivity;
        final Intent intent = new Intent(currentActivity, LoginActivity.class);
        intent.putExtra(GAME_OBJECT_NAME, gameObjectName);
        currentActivity.startActivity(intent);
    }

    /**
     *  Convenience method for requesting users email address using JNI.
     *
     * @param gameObjectName name of GameObject to receive RequestEmailComplete or RequestEmailFailure message.
     * @param session the user session
     */
    public static void startRequestEmail(String gameObjectName, String session) {
        final Activity currentActivity = UnityPlayer.currentActivity;
        final Intent intent = new Intent(currentActivity, RequestEmailActivity.class);
        intent.putExtra(GAME_OBJECT_NAME, gameObjectName);
        intent.putExtra(TWITTER_SESSION, session);
        currentActivity.startActivity(intent);
    }

    /**
     *  Convenience method for logging out active user using JNI.
     */
    public static void logOut() {
        TwitterCore.getInstance().logOut();
    }
}
