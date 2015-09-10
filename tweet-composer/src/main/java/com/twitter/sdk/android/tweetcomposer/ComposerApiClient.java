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

package com.twitter.sdk.android.tweetcomposer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twitter.sdk.android.core.AuthenticatedClient;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.SafeListAdapter;
import com.twitter.sdk.android.core.models.SafeMapAdapter;
import com.twitter.sdk.android.tweetcomposer.internal.CardService;

import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLSocketFactory;

import retrofit.RestAdapter;
import retrofit.android.MainThreadExecutor;
import retrofit.converter.GsonConverter;

class ComposerApiClient extends TwitterApiClient {
    private static final String CARDS_ENDPOINT = "https://caps.twitter.com";
    final RestAdapter cardsAdapter;

    ComposerApiClient(TwitterAuthConfig authConfig, Session session,
                     SSLSocketFactory sslSocketFactory, ExecutorService executorService) {
        super(session);

        final Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new SafeListAdapter())
                .registerTypeAdapterFactory(new SafeMapAdapter())
                .create();

        cardsAdapter = new RestAdapter.Builder()
                .setClient(new AuthenticatedClient(authConfig, session, sslSocketFactory))
                .setEndpoint(CARDS_ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .setExecutors(executorService, new MainThreadExecutor())
                .build();
    }

    ComposerApiClient(TwitterSession session) {
        this(TwitterCore.getInstance().getAuthConfig(), session,
                TwitterCore.getInstance().getSSLSocketFactory(),
                TwitterCore.getInstance().getFabric().getExecutorService());
    }

    /**
     * @return {@link com.twitter.sdk.android.tweetcomposer.StatusesService}
     */
    StatusesService getComposerStatusesService() {
        return getService(StatusesService.class);
    }

    /**
     * @return {@link com.twitter.sdk.android.tweetcomposer.internal.CardService}
     */
    CardService getCardService() {
        return getAdapterService(cardsAdapter, CardService.class);
    }
}
