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

import com.twitter.sdk.android.core.internal.TwitterApi;
import com.twitter.sdk.android.core.internal.oauth.OAuth1aHeaders;

import java.util.Map;

/**
 * Provides convenience methods for generating OAuth headers for Twitter
 **/
public class OAuthSigning {
    static final String VERIFY_CREDENTIALS_URL = TwitterApi.BASE_HOST_URL +
            "/1.1/account/verify_credentials.json";

    final TwitterAuthConfig authConfig;
    final TwitterAuthToken authToken;
    final OAuth1aHeaders oAuth1aHeaders;

    /**
     * Constructs OAuthSigning with TwitterAuthConfig and TwitterAuthToken
     *
     * @param authConfig The auth config.
     * @param authToken  The auth token to use to sign the request.
     */
    public OAuthSigning(TwitterAuthConfig authConfig, TwitterAuthToken authToken) {
        this(authConfig, authToken, new OAuth1aHeaders());
    }

    OAuthSigning(TwitterAuthConfig authConfig, TwitterAuthToken authToken,
            OAuth1aHeaders oAuth1aHeaders) {
        if (authConfig == null) {
            throw new IllegalArgumentException("authConfig must not be null");
        }
        if (authToken == null) {
            throw new IllegalArgumentException("authToken must not be null");
        }

        this.authConfig = authConfig;
        this.authToken = authToken;
        this.oAuth1aHeaders = oAuth1aHeaders;
    }

    /**
     * Gets authorization header for inclusion in HTTP request headers.
     *
     * @param method The HTTP method.
     * @param url The url.
     * @param postParams The post parameters.
     */
    public String getAuthorizationHeader(String method, String url,
            Map<String, String> postParams) {
        return oAuth1aHeaders.getAuthorizationHeader(authConfig, authToken, null, method, url,
                postParams);
    }

    /**
     * Returns OAuth Echo header using given parameters.
     *
     * OAuth Echo allows you to securely delegate an API request to a third party. For example,
     * you may wish to verify a users credentials from your backend (i.e. the third party). This
     * method provides the OAuth parameters required to make an authenticated request from your
     * backend.
     *
     * @param method     The HTTP method (GET, POST, PUT, DELETE, etc).
     * @param url        The url delegation should be sent to (e.g. https://api.twitter.com/1.1/account/verify_credentials.json).
     * @param postParams The post parameters.
     * @return A map of OAuth Echo headers
     * @see <a href="https://dev.twitter.com/oauth/echo">OAuth Echo</a>
     */
    public Map<String, String> getOAuthEchoHeaders(String method, String url,
            Map<String, String> postParams) {
        return oAuth1aHeaders.getOAuthEchoHeaders(authConfig, authToken, null, method, url,
                postParams);
    }

    /**
     * Returns OAuth Echo header for <a href="https://dev.twitter.com/rest/reference/get/account/verify_credentials">verify_credentials</a> endpoint.
     *
     * @return A map of OAuth Echo headers
     * @see #getOAuthEchoHeaders(String, String, java.util.Map)
     */
    public Map<String, String> getOAuthEchoHeadersForVerifyCredentials() {
        return oAuth1aHeaders.getOAuthEchoHeaders(authConfig, authToken, null,
                "GET", VERIFY_CREDENTIALS_URL, null);
    }

}
