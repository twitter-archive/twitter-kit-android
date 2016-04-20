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
import com.google.gson.JsonSyntaxException;
import com.twitter.sdk.android.core.models.ApiError;
import com.twitter.sdk.android.core.models.ApiErrors;

import io.fabric.sdk.android.Fabric;
import retrofit2.Response;

/**
 * Represents a Twitter API error.
 */
public class TwitterApiException extends TwitterException {
    public static final int DEFAULT_ERROR_CODE = 0;
    private final ApiError apiError;
    private final TwitterRateLimit twitterRateLimit;

    TwitterApiException(Response response) {
        super(createExceptionMessage(response));
        apiError = readApiError(response);
        twitterRateLimit = new TwitterRateLimit(response.headers());
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

    public static ApiError readApiError(Response response) {
        try {
            final String body = response.errorBody().string();
            if (!TextUtils.isEmpty(body)) {
                return parseApiError(body);
            }
        } catch (Exception e) {
            Fabric.getLogger().e(TwitterCore.TAG, "Unexpected response", e);
        }

        return null;
    }

    static ApiError parseApiError(String body) {
        final Gson gson = new Gson();
        try {
            final ApiErrors apiErrors = gson.fromJson(body, ApiErrors.class);
            if (!apiErrors.errors.isEmpty()) {
                return apiErrors.errors.get(0);
            }
        } catch (JsonSyntaxException e) {
            Fabric.getLogger().e(TwitterCore.TAG, "Invalid json: " + body, e);
        }
        return null;
    }

    static String createExceptionMessage(Response response) {
        return "HTTP request failed, Status: " + response.code();
    }
}
