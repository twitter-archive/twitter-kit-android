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

import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.internal.oauth.OAuth1aHeaders;
import com.twitter.sdk.android.core.internal.oauth.OAuthConstants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Signs requests with OAuth1a signature
 */
public class OAuth1aInterceptor implements Interceptor {
    final Session<? extends TwitterAuthToken> session;
    final TwitterAuthConfig authConfig;

    public OAuth1aInterceptor(Session<? extends TwitterAuthToken> session,
            TwitterAuthConfig authConfig) {
        this.session = session;
        this.authConfig = authConfig;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();
        final Request hackRequest = request.newBuilder()
                .url(urlWorkaround(request.url()))
                .build();

        final Request newRequest = hackRequest
                .newBuilder()
                .header(OAuthConstants.HEADER_AUTHORIZATION, getAuthorizationHeader(hackRequest))
                .build();

        return chain.proceed(newRequest);
    }

    HttpUrl urlWorkaround(HttpUrl url) {
        final HttpUrl.Builder builder = url.newBuilder().query(null);

        final int size = url.querySize();
        for (int i = 0; i < size; i++) {
            builder.addEncodedQueryParameter(UrlUtils.percentEncode(url.queryParameterName(i)),
                    UrlUtils.percentEncode(url.queryParameterValue(i)));
        }

        return builder.build();
    }

    String getAuthorizationHeader(Request request) throws IOException {
        return new OAuth1aHeaders().getAuthorizationHeader(authConfig,
                session.getAuthToken(), null, request.method(), request.url().toString(),
                getPostParams(request));
    }

    Map<String, String> getPostParams(Request request) throws IOException {
        final Map<String, String> params = new HashMap<>();
        if ("POST".equals(request.method().toUpperCase(Locale.US))) {
            final RequestBody output = request.body();
            if (output instanceof FormBody) {
                final FormBody body = (FormBody) output;
                for (int i = 0; i < body.size(); i++) {
                    params.put(body.encodedName(i), body.value(i));
                }
            }
        }
        return params;
    }
}
