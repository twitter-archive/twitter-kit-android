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

package com.twitter.sdk.android.tweetui.internal;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.internal.SessionProvider;

import java.util.List;

public class UserSessionProvider extends SessionProvider {

    public UserSessionProvider(List<SessionManager<? extends Session>> sessionManagers) {
        super(sessionManagers);
    }

    /*
     * Do not launch the User Auth flow by default, fail the request to allow developers to launch
     * the login flow.
     */
    public void requestAuth(Callback<Session> cb) {
        cb.failure(new TwitterAuthException("Twitter login required."));
    }
}
