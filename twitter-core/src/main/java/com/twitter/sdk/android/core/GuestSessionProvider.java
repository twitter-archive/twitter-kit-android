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

package com.twitter.sdk.android.core;

import com.twitter.sdk.android.core.internal.oauth.GuestAuthToken;
import com.twitter.sdk.android.core.internal.oauth.OAuth2Service;

import java.util.concurrent.CountDownLatch;

public class GuestSessionProvider {
    private final OAuth2Service oAuth2Service;
    private final SessionManager<GuestSession> sessionManager;

    public GuestSessionProvider(OAuth2Service oAuth2Service,
            SessionManager<GuestSession> sessionManager) {
        this.oAuth2Service = oAuth2Service;
        this.sessionManager = sessionManager;
    }

    public synchronized GuestSession getCurrentSession() {
        final GuestSession session = sessionManager.getActiveSession();
        if (isSessionValid(session)) {
            return session;
        }

        refreshToken();

        return sessionManager.getActiveSession();
    }

    public synchronized GuestSession refreshCurrentSession(GuestSession expiredSession) {
        final GuestSession session = sessionManager.getActiveSession();
        if (expiredSession != null && expiredSession.equals(session)) {
            refreshToken();
        }

        return sessionManager.getActiveSession();
    }

    void refreshToken() {
        Twitter.getLogger().d("GuestSessionProvider", "Refreshing expired guest session.");
        final CountDownLatch latch = new CountDownLatch(1);
        oAuth2Service.requestGuestAuthToken(new Callback<GuestAuthToken>() {
            @Override
            public void success(Result<GuestAuthToken> result) {
                sessionManager.setActiveSession(new GuestSession(result.data));
                latch.countDown();
            }

            @Override
            public void failure(TwitterException exception) {
                sessionManager.clearSession(GuestSession.LOGGED_OUT_USER_ID);
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            sessionManager.clearSession(GuestSession.LOGGED_OUT_USER_ID);
        }
    }

    boolean isSessionValid(GuestSession session) {
        return session != null
                && session.getAuthToken() != null
                && !session.getAuthToken().isExpired();
    }
}
