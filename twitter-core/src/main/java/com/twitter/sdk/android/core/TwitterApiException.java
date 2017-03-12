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

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.twitter.sdk.android.core.models.ApiError;
import com.twitter.sdk.android.core.models.ApiErrors;
import com.twitter.sdk.android.core.models.SafeListAdapter;
import com.twitter.sdk.android.core.models.SafeMapAdapter;

import retrofit2.Response;

/**
 * Represents a Twitter API error.
 */
public class TwitterApiException extends TwitterException {
    public static final int DEFAULT_ERROR_CODE = 0;
    private final ApiError apiError;
    private final TwitterRateLimit twitterRateLimit;
    private final int code;
    private final Response response;

    public TwitterApiException(Response response) {
        this(response, readApiError(response), readApiRateLimit(response), response.code());
    }

    TwitterApiException(Response response, ApiError apiError, TwitterRateLimit twitterRateLimit,
            int code) {
        super(createExceptionMessage(code));
        this.apiError = apiError;
        this.twitterRateLimit = twitterRateLimit;
        this.code = code;
        this.response = response;
    }

    public int getStatusCode() {
        return code;
    }

    /**
     * Error code returned by API request.
     *
     * @return API error code
     */
    public int getErrorCode() {
        return apiError == null ? DEFAULT_ERROR_CODE : apiError.code;
    }

    /**
     * Error message returned by API request. Error message may change, the codes will stay the same.
     *
     * @return API error message
     */
    public String getErrorMessage() {
        return apiError == null ? null : apiError.message;
    }

    public TwitterRateLimit getTwitterRateLimit() {
        return twitterRateLimit;
    }

    public Response getResponse() {
        return response;
    }

    public static TwitterRateLimit readApiRateLimit(Response response) {
        return new TwitterRateLimit(response.headers());
    }

    public static ApiError readApiError(Response response) {
        try {
            // The response buffer can only be read once, so we clone the underlying buffer so the
            // response can be consumed down stream if necessary.
            final String body = response.errorBody().source().buffer().clone().readUtf8();
            if (!TextUtils.isEmpty(body)) {
                return parseApiError(body);
            }
        } catch (Exception e) {
            Twitter.getLogger().e(TwitterCore.TAG, "Unexpected response", e);
        }

        return null;
    }

    static ApiError parseApiError(String body) {
        final Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new SafeListAdapter())
                .registerTypeAdapterFactory(new SafeMapAdapter())
                .create();
        try {
            final ApiErrors apiErrors = gson.fromJson(body, ApiErrors.class);
            if (!apiErrors.errors.isEmpty()) {
                return apiErrors.errors.get(0);
            }
        } catch (JsonSyntaxException e) {
            Twitter.getLogger().e(TwitterCore.TAG, "Invalid json: " + body, e);
        }
        return null;
    }

    static String createExceptionMessage(int code) {
        return "HTTP request failed, Status: " + code;
    }
}
