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

package com.twitter.sdk.android.core.internal;

import android.text.TextUtils;

import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TwitterRequestHeaders {

    public static final String HEADER_USER_AGENT = "User-Agent";

    private final String method;
    private final String url;
    private final Map<String, String> postParams;
    private final TwitterAuthConfig authConfig;
    private final Session session;
    private final String userAgent;

    public TwitterRequestHeaders(String method, String url, TwitterAuthConfig authConfig,
            Session session, String userAgent, Map<String, String> postParams) {
        this.method = method;
        this.url = url;
        this.authConfig = authConfig;
        this.session = session;
        this.userAgent = userAgent;
        this.postParams = postParams;
    }

    public final Map<String, String> getHeaders() {
        final HashMap<String, String> headers = new HashMap<>();
        headers.putAll(getExtraHeaders());
        if (!TextUtils.isEmpty(userAgent)) {
            headers.put(HEADER_USER_AGENT, userAgent);
        }
        headers.putAll(getAuthHeaders());
        return headers;
    }

    /**
     * Returns a list of extra HTTP headers (besides Authorization and User-Agent) to go along with
     * this request
     */
    protected Map<String, String> getExtraHeaders() {
        return Collections.emptyMap();
    }

    /**
     * @return a map of auth headers to go along with this request. Override this method if you
     * need to provide a special Authorization header.
     */
    public Map<String, String> getAuthHeaders() {
        if (session != null && session.getAuthToken() != null) {
            return session.getAuthToken().getAuthHeaders(authConfig, getMethod(), url,
                    getPostParams());
        }
        return Collections.emptyMap();
    }

    protected String getMethod() {
        return method;
    }

    protected Map<String, String> getPostParams() {
        return postParams;
    }
}
