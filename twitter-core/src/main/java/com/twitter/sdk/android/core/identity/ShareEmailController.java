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

import android.os.Bundle;
import android.os.ResultReceiver;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.User;

import io.fabric.sdk.android.Fabric;

class ShareEmailController {

    private static final String EMPTY_EMAIL = "";

    private final ShareEmailClient emailClient;
    private final ResultReceiver resultReceiver;

    public ShareEmailController(ShareEmailClient emailClient, ResultReceiver resultReceiver) {
        this.emailClient = emailClient;
        this.resultReceiver = resultReceiver;
    }

    public void executeRequest() {
        emailClient.getEmail(newCallback());
    }

    Callback<User> newCallback() {
        return new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                handleSuccess(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Fabric.getLogger().e(TwitterCore.TAG, "Failed to get email address.", exception);
                // Create new exception that can be safely serialized since Retrofit errors may
                // throw a NotSerializableException.
                sendResultCodeError(new TwitterException("Failed to get email address."));
            }
        };
    }

    void handleSuccess(User user) {
        if (user.email == null) {
            sendResultCodeError(new TwitterException("Your application may not have access to"
                    + " email addresses or the user may not have an email address. To request"
                    + " access, please visit https://support.twitter.com/forms/platform."));
        } else if (EMPTY_EMAIL.equals(user.email)) {
            sendResultCodeError(new TwitterException("This user does not have an email address."));
        } else {
            sendResultCodeOk(user.email);
        }
    }

    void sendResultCodeOk(String email) {
        final Bundle bundle = new Bundle();
        bundle.putString(ShareEmailClient.RESULT_DATA_EMAIL, email);
        resultReceiver.send(ShareEmailClient.RESULT_CODE_OK, bundle);
    }

    void sendResultCodeError(TwitterException exception) {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(ShareEmailClient.RESULT_DATA_ERROR, exception);
        resultReceiver.send(ShareEmailClient.RESULT_CODE_ERROR, bundle);
    }

    public void cancelRequest() {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(ShareEmailClient.RESULT_DATA_MSG,
                "The user chose not to share their email address at this time.");
        resultReceiver.send(ShareEmailClient.RESULT_CODE_CANCELED, bundle);
    }
}
