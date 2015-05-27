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

import io.fabric.sdk.android.Fabric;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.client.UrlConnectionClient;

/**
 * Provider of the Retrofit {@link retrofit.client.Client} that is used for API requests.
 * Uses OkHTTP if available, otherwise uses HttpUrlConnection.
 * Pins SSL Certs for requests
 */
public class DefaultClient implements Client {

    final Client wrappedClient;
    final SSLSocketFactory sslSocketFactory;

    public DefaultClient(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        if (hasOkHttpOnClasspath()) {
            wrappedClient = new OkClient() {
                @Override
                protected HttpURLConnection openConnection(Request request) throws IOException {
                    return openSslConnection(super.openConnection(request));
                }
            };
        } else {
            wrappedClient = new UrlConnectionClient() {
                @Override
                protected HttpURLConnection openConnection(Request request) throws IOException {
                    return openSslConnection(super.openConnection(request));
                }
            };
        }
    }

    @Override
    public Response execute(Request request) throws IOException {
        return wrappedClient.execute(request);
    }


    /** Determine whether or not OkHttp 1.6 or newer is present on the runtime classpath. */
    private boolean hasOkHttpOnClasspath() {
        boolean okUrlFactory = false;
        try {
            Class.forName("com.squareup.okhttp.OkUrlFactory");
            okUrlFactory = true;
        } catch (ClassNotFoundException e) {
        }

        boolean okHttpClient = false;
        try {
            Class.forName("com.squareup.okhttp.OkHttpClient");
            okHttpClient = true;
        } catch (ClassNotFoundException e) {
        }

        if (okHttpClient != okUrlFactory) {
            Fabric.getLogger().d(TwitterCore.TAG,
                    "Retrofit detected an unsupported OkHttp on the classpath.\n"
                    + "To use OkHttp with this version of Retrofit, you'll need:\n"
                    + "1. com.squareup.okhttp:okhttp:1.6.0 (or newer)\n"
                    + "2. com.squareup.okhttp:okhttp-urlconnection:1.6.0 (or newer)\n"
                    + "Note that OkHttp 2.0.0+ is supported!");
            return false;
        }

        return okHttpClient;
    }

    HttpURLConnection openSslConnection(final HttpURLConnection connection) {
        if (sslSocketFactory != null && connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
        }
        return connection;
    }
}
