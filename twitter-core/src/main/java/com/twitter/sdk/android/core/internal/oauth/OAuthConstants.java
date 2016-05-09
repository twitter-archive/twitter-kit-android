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

package com.twitter.sdk.android.core.internal.oauth;

public class OAuthConstants {
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_GUEST_TOKEN = "x-guest-token";

    // OAuth1.0a parameter constants.
    public static final String PARAM_CALLBACK = "oauth_callback";
    public static final String PARAM_CONSUMER_KEY = "oauth_consumer_key";
    public static final String PARAM_NONCE = "oauth_nonce";
    public static final String PARAM_SIGNATURE_METHOD = "oauth_signature_method";
    public static final String PARAM_TIMESTAMP = "oauth_timestamp";
    public static final String PARAM_TOKEN = "oauth_token";
    public static final String PARAM_TOKEN_SECRET = "oauth_token_secret";
    public static final String PARAM_VERSION = "oauth_version";
    public static final String PARAM_SIGNATURE = "oauth_signature";
    public static final String PARAM_VERIFIER = "oauth_verifier";

    // OAuth2
    public static final String AUTHORIZATION_BASIC = "Basic";
    public static final String AUTHORIZATION_BEARER = "Bearer";

    public static final String PARAM_GRANT_TYPE = "grant_type";
    public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
}
