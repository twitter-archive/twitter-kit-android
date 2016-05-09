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

/**
 * Client for requesting guest auth.
 */
class GuestAuthClient {
    private final OAuth2Service service;

    /**
     * Constructor.
     * @param service a OAuth2Service instance for obtaining guest or app auth
     * @throws java.lang.IllegalArgumentException if service is null
     */
    GuestAuthClient(OAuth2Service service) {
        if (service == null) {
            throw new IllegalArgumentException("OAuth2Service must not be null");
        }
        this.service = service;
    }

    /**
     * Request guest authentication token be set on the app session manager via the OAuth2Service.
     * @param callback callback receiving an GuestSession on success
     * @throws java.lang.IllegalArgumentException if guestSessionManager is null
     */
    void authorize(SessionManager<GuestSession> appSessionManager,
                   Callback<GuestSession> callback) {
        if (appSessionManager == null) {
            throw new IllegalArgumentException("SessionManager must not be null");
        }
        service.requestGuestAuthToken(new CallbackWrapper(appSessionManager, callback));
    }

    /**
     * Callback to OAuth2Service wrapping a developer's logInGuest callback
     */
    class CallbackWrapper extends Callback<GuestAuthToken> {
        private final SessionManager<GuestSession> appSessionManager;
        private final Callback<GuestSession> callback;

        CallbackWrapper(SessionManager<GuestSession> appSessionManager,
                Callback<GuestSession> callback) {
            this.appSessionManager = appSessionManager;
            this.callback = callback;
        }

        @Override
        public void success(Result<GuestAuthToken> result) {
            final GuestSession session = new GuestSession(result.data);
            // set session in manager, manager makes session active if there is no active session
            appSessionManager.setSession(session.getId(), session);
            if (callback != null) {
                callback.success(new Result<>(session, result.response));
            }
        }

        @Override
        public void failure(TwitterException exception) {
            if (callback != null) {
                callback.failure(exception);
            }
        }
    }
}
