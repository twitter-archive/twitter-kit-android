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

package com.twitter.sdk.android.core.identity;

import android.app.Activity;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

class ShareEmailClient extends TwitterApiClient {
    static final int RESULT_CODE_CANCELED = Activity.RESULT_CANCELED;
    static final int RESULT_CODE_OK = Activity.RESULT_OK;
    static final int RESULT_CODE_ERROR = Activity.RESULT_FIRST_USER;

    static final String RESULT_DATA_EMAIL = "email";
    static final String RESULT_DATA_MSG = "msg";
    static final String RESULT_DATA_ERROR = "error";

    ShareEmailClient(TwitterSession session) {
        super(session);
    }

    /**
     * Gets the user's email address from the Twitter API service.
     *
     * @param callback The callback to invoke when the request completes.
     */
    protected void getEmail(Callback<User> callback) {
        getService(EmailService.class).verifyCredentials(true, true).enqueue(callback);
    }

    interface EmailService {
        @GET("/1.1/account/verify_credentials.json?include_email=true")
        Call<User> verifyCredentials(
                @Query("include_entities") Boolean includeEntities,
                @Query("skip_status") Boolean skipStatus);
    }
}
