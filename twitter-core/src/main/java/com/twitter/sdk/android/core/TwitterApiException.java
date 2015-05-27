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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.fabric.sdk.android.Fabric;
import com.twitter.sdk.android.core.internal.TwitterApiConstants;
import com.twitter.sdk.android.core.models.ApiError;

import java.io.UnsupportedEncodingException;

import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;

/**
 * Represents a Twitter API error.
 */
public class TwitterApiException extends TwitterException {
    public static final int DEFAULT_ERROR_CODE = 0;

    private final RetrofitError retrofitError;
    private final TwitterRateLimit twitterRateLimit;
    private final ApiError apiError;

    TwitterApiException(ApiError apiError, TwitterRateLimit twitterRateLimit,
                        RetrofitError retrofitError) {
        super(retrofitError.getMessage());
        this.retrofitError = retrofitError;
        this.apiError = apiError;
        this.twitterRateLimit = twitterRateLimit;
    }

    TwitterApiException(RetrofitError retrofitError) {

        super(createExceptionMessage(retrofitError));
        setStackTrace(retrofitError.getStackTrace());

        this.retrofitError = retrofitError;
        twitterRateLimit = createRateLimit(retrofitError);
        apiError = readApiError(retrofitError);
    }

    private static String createExceptionMessage(RetrofitError retrofitError) {
        if (retrofitError.getMessage() != null) {
            return retrofitError.getMessage();
        }
        if (retrofitError.getResponse() != null) {
            return "Status: " + retrofitError.getResponse().getStatus();
        }
        return "unknown error";
    }

    private static TwitterRateLimit createRateLimit(RetrofitError retrofitError) {
        if (retrofitError.getResponse() != null) {
            return new TwitterRateLimit(retrofitError.getResponse().getHeaders());
        }
        return null;
    }

    /**
     * Error code returned by API request.
     *
     * @return API error code
     */
    public int getErrorCode() {
        return apiError == null ? DEFAULT_ERROR_CODE : apiError.getCode();
    }

    /**
     * Error message returned by API request. Error message may change, the codes will stay the same.
     *
     * @return API error message
     */
    public String getErrorMessage() {
        return apiError == null ? null : apiError.getMessage();
    }

    public boolean canRetry() {
        final int status = retrofitError.getResponse().getStatus();
        return status < 400 || status > 499;
    }

    public RetrofitError getRetrofitError() {
        return retrofitError;
    }

    public TwitterRateLimit getTwitterRateLimit() {
        return twitterRateLimit;
    }

    public static final TwitterApiException convert(RetrofitError retrofitError) {
        return new TwitterApiException(retrofitError);
    }

    public static ApiError readApiError(RetrofitError retrofitError) {
        if (retrofitError == null || retrofitError.getResponse() == null ||
                retrofitError.getResponse().getBody() == null) {
            return null;
        }
        final byte[] responseBytes = ((TypedByteArray) retrofitError.getResponse().getBody())
                .getBytes();

        if (responseBytes == null) return null;
        final String response;
        try {
            response = new String(responseBytes, "UTF-8");
            return parseApiError(response);
        } catch (UnsupportedEncodingException e) {
            Fabric.getLogger().e(TwitterCore.TAG, "Failed to convert to string", e);
        }
        return null;
    }

    static ApiError parseApiError(String response) {
        final Gson gson = new Gson();
        try {
            // Get the "errors" object
            final JsonObject responseObj = new JsonParser().parse(response).getAsJsonObject();
            final ApiError[] apiErrors = gson.fromJson(
                    responseObj.get(TwitterApiConstants.Errors.ERRORS), ApiError[].class);
            if (apiErrors.length == 0) {
                return null;
            } else {
                // return the first api error.
                return apiErrors[0];
            }
        } catch (JsonSyntaxException e) {
            Fabric.getLogger().e(TwitterCore.TAG, "Invalid json: " + response, e);
        } catch (Exception e) {
            Fabric.getLogger().e(TwitterCore.TAG, "Unexpected response: " + response, e);
        }
        return null;
    }
}
