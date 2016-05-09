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

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Signs requests with OAuth2 signature.
 */
public class GuestAuthInterceptor implements Interceptor {
    final GuestSessionProvider guestSessionProvider;

    public GuestAuthInterceptor(GuestSessionProvider guestSessionProvider) {
        this.guestSessionProvider = guestSessionProvider;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();

        final GuestSession session = guestSessionProvider.getCurrentSession();
        final GuestAuthToken token = session == null ? null : session.getAuthToken();
        if (token != null) {
            final Request.Builder builder = request.newBuilder();
            addAuthHeaders(builder, token);
            return chain.proceed(builder.build());
        }

        return chain.proceed(request);
    }

    static void addAuthHeaders(Request.Builder builder, GuestAuthToken token) {
        final String authHeader = token.getTokenType() + " " + token.getAccessToken();

        builder.header(OAuthConstants.HEADER_AUTHORIZATION, authHeader);
        builder.header(OAuthConstants.HEADER_GUEST_TOKEN, token.getGuestToken());
    }
}
