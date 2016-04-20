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

import com.twitter.sdk.android.core.internal.TwitterRequestHeaders;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.fabric.sdk.android.services.network.UrlUtils;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    final Session session;
    final TwitterAuthConfig authConfig;

    public AuthInterceptor(Session session, TwitterAuthConfig authConfig) {
        this.session = session;
        this.authConfig = authConfig;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();
        final Request hackRequest = request.newBuilder()
                .url(urlWorkaround(request.url()))
                .build();

        final Headers headers = getAuthHeaders(hackRequest);

        final Request newRequest = hackRequest
                .newBuilder()
                .headers(headers)
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

    Headers getAuthHeaders(Request request) throws IOException {
        final TwitterRequestHeaders authHeaders = new TwitterRequestHeaders(request.method(),
                request.url().toString(), authConfig, session, null, getPostParams(request));

        final Headers.Builder builder = request.headers().newBuilder();
        for (Map.Entry<String, String> header : authHeaders.getHeaders().entrySet()) {
            builder.add(header.getKey(), header.getValue());
        }
        return builder.build();
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
