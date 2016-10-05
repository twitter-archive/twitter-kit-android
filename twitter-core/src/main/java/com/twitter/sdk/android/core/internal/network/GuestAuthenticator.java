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

package com.twitter.sdk.android.core.internal.network;

import com.twitter.sdk.android.core.GuestSession;
import com.twitter.sdk.android.core.GuestSessionProvider;
import com.twitter.sdk.android.core.internal.oauth.GuestAuthToken;
import com.twitter.sdk.android.core.internal.oauth.OAuthConstants;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Refreshes guest auth session when server indicates session is expired.
 */
public class GuestAuthenticator implements Authenticator {
    static final int MAX_RETRIES = 2;
    final GuestSessionProvider guestSessionProvider;

    public GuestAuthenticator(GuestSessionProvider guestSessionProvider) {
        this.guestSessionProvider = guestSessionProvider;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        return reauth(response);
    }

    Request reauth(Response response) {
        if (canRetry(response)) {
            final GuestSession session = guestSessionProvider
                    .refreshCurrentSession(getExpiredSession(response));
            final GuestAuthToken token = session == null ? null : session.getAuthToken();
            if (token != null) {
                return resign(response.request(), token);
            }
        }

        return null;
    }

    GuestSession getExpiredSession(Response response) {
        final Headers headers = response.request().headers();
        final String auth = headers.get(OAuthConstants.HEADER_AUTHORIZATION);
        final String guest = headers.get(OAuthConstants.HEADER_GUEST_TOKEN);

        if (auth != null && guest != null) {
            final GuestAuthToken token =
                    new GuestAuthToken("bearer", auth.replace("bearer ", ""), guest);
            return new GuestSession(token);
        }

        return null;
    }

    Request resign(Request request, GuestAuthToken token) {
        final Request.Builder builder = request.newBuilder();
        GuestAuthInterceptor.addAuthHeaders(builder, token);
        return builder.build();
    }

    boolean canRetry(Response response) {
        int responseCount = 1;
        while ((response = response.priorResponse()) != null) {
            responseCount++;
        }

        return responseCount < MAX_RETRIES;
    }
}
