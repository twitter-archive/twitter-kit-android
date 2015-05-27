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

import com.twitter.sdk.android.core.DefaultClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.internal.TwitterApi;

import javax.net.ssl.SSLSocketFactory;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Base class for OAuth service.
 */
abstract class OAuthService {

    private static final String CLIENT_NAME = "TwitterAndroidSDK";

    private final TwitterCore twitterCore;
    private final SSLSocketFactory sslSocketFactory;
    private final TwitterApi api;
    private final String userAgent;
    private final RestAdapter apiAdapter;

    public OAuthService(TwitterCore twitterCore, SSLSocketFactory sslSocketFactory,
            TwitterApi api) {
        this.twitterCore = twitterCore;
        this.sslSocketFactory = sslSocketFactory;
        this.api = api;
        userAgent = TwitterApi.buildUserAgent(CLIENT_NAME, twitterCore.getVersion());


        apiAdapter = new RestAdapter.Builder()
                .setEndpoint(getApi().getBaseHostUrl())
                .setClient(new DefaultClient(this.sslSocketFactory))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("User-Agent", getUserAgent());
                    }
                })
                .build();
    }

    protected TwitterCore getTwitterCore() {
        return twitterCore;
    }

    protected SSLSocketFactory getSSLSocketFactory() {
        return sslSocketFactory;
    }

    protected TwitterApi getApi() {
        return api;
    }

    protected String getUserAgent() {
        return userAgent;
    }

    protected RestAdapter getApiAdapter() {
        return apiAdapter;
    }
}
