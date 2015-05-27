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

import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;

import java.util.List;

/**
 * Mockable class that simplifies code that depends on sessions but is agnostic to session type.
 *
 * Note, the order of the SessionManager list is important if one type is more desirable to use
 * than another.
 */
public class ActiveSessionProvider {

    private final List<SessionManager<? extends  Session>> sessionManagers;

    public ActiveSessionProvider(List<SessionManager<? extends Session>> sessionManagers) {
        this.sessionManagers = sessionManagers;
    }

    public Session getActiveSession() {
        Session session = null;
        for (SessionManager<? extends Session> sessionManager : sessionManagers) {
            session = sessionManager.getActiveSession();
            if (session != null) {
                break;
            }
        }
        return session;
    }
}
