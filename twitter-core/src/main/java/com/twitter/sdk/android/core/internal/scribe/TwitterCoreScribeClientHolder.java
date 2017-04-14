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

package com.twitter.sdk.android.core.internal.scribe;

import android.annotation.SuppressLint;
import android.content.Context;

import com.twitter.sdk.android.core.GuestSessionProvider;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.internal.IdManager;

public class TwitterCoreScribeClientHolder {

    @SuppressLint("StaticFieldLeak")
    private static DefaultScribeClient instance;

    /**
     * @return instance can be null
     */
    public static DefaultScribeClient getScribeClient() {
        return instance;
    }

    /**
     * Must be called on background thread
     */
    public static void initialize(Context context,
            SessionManager<? extends Session<TwitterAuthToken>> sessionManagers,
            GuestSessionProvider guestSessionProvider, IdManager idManager, String kitName,
            String kitVersion) {

        final ScribeConfig config = DefaultScribeClient.getScribeConfig(kitName, kitVersion);
        instance = new DefaultScribeClient(context, sessionManagers, guestSessionProvider,
                idManager, config);
    }
}
