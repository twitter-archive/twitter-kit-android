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

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * The Twitter API uses HTTP status code of 403 to indicate guest session needs to be refreshed.
 * However, the OkHttp Authenticator that refreshes guest sessions only responds to 401. So we map
 * all 403 to 401 responses.
 */
public class GuestAuthNetworkInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.code() == 403) {
            response = response.newBuilder().code(401).message("Unauthorized").build();
        }
        return response;
    }
}
