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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.SSLSocketFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import retrofit.client.Header;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.mime.FormUrlEncodedTypedOutput;
import retrofit.mime.TypedOutput;

/**
 * Provides all functionality of {@link DefaultClient} and
 * additionally adds header signing via {@link com.twitter.sdk.android.core.Session}
 */
public class AuthenticatedClient extends DefaultClient {

    private static final String FAKE_URL = "https://twitter.com"; //Unused, just for parsing body
    private final Session session;
    private final TwitterAuthConfig authConfig;

    public AuthenticatedClient(TwitterAuthConfig config, Session session,
            SSLSocketFactory sslSocketFactory) {
        super(sslSocketFactory);
        authConfig = config;
        this.session = session;
    }

    @Override
    public Response execute(Request request) throws IOException {
        request = new Request(request.getMethod(), request.getUrl(),
                getAuthHeaders(request), request.getBody());


        final Response response = wrappedClient.execute(request);
        return response;
    }

    protected List<Header> getAuthHeaders(Request request) throws IOException {
        final TwitterRequestHeaders authHeaders = new TwitterRequestHeaders(request.getMethod(),
                request.getUrl(), authConfig, session, null, getPostParams(request));

        // Copies the headers from the original list
        final List<Header> headers = new ArrayList<>(request.getHeaders());
        for (Map.Entry<String, String> header : authHeaders.getHeaders().entrySet()) {
            headers.add(new Header(header.getKey(), header.getValue()));
        }
        return headers;
    }

    /**
     * Parse the {@link retrofit.mime.FormUrlEncodedTypedOutput} Body from a request into a
     * temporary Uri and use the available URI methods to extract the body parameters into a Map.
     */
    protected Map<String, String> getPostParams(Request request) throws IOException {
        final Map<String, String> params = new TreeMap<>();
        if ("POST".equals(request.getMethod().toUpperCase(Locale.US))) {
            final TypedOutput output = request.getBody();
            if (output instanceof FormUrlEncodedTypedOutput) {
                final ByteArrayOutputStream os = new ByteArrayOutputStream();
                output.writeTo(os);
                final String val = os.toString("UTF-8");
                if (val.length() > 0) {
                    final URI bodyUri = URI.create(FAKE_URL + "/?" + val);
                    for (NameValuePair pair : URLEncodedUtils.parse(bodyUri, "UTF-8")) {
                        params.put(pair.getName(), pair.getValue());
                    }
                }
            }
        }
        return params;
    }
}
