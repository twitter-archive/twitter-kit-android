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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import javax.net.ssl.SSLSocketFactory;

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
                    params.putAll(getParameters(val));
                }
            }
        }
        return params;
    }

    /**
     * Returns a map of parameters from a {@code application/x-www-form-urlencoded} encoded string
     * @param input {@code application/x-www-form-urlencoded} encoded string
     * @return map of parameters
     */
    protected Map<String, String> getParameters(String input) {
        final Map<String, String> parameters = new HashMap<>();
        final Scanner scanner = new Scanner(input).useDelimiter("&");

        while (scanner.hasNext()) {
            final String[] param = scanner.next().split("=");
            if (param.length == 0 || param.length > 2) {
                throw new IllegalArgumentException("bad parameter");
            }

            final String name = decode(param[0], "UTF-8");
            String value = "";
            if (param.length == 2) {
                value = decode(param[1], "UTF-8");
            }

            parameters.put(name, value);
        }

        return Collections.unmodifiableMap(parameters);
    }

    protected String decode(String value, String encoding) {
        try {
            return URLDecoder.decode(value, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("bad parameter encoding");
        }
    }
}
