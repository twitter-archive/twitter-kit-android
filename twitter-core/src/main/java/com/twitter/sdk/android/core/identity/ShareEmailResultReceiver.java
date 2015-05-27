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
import com.twitter.sdk.android.core.TwitterException;

class ShareEmailResultReceiver extends ResultReceiver {

    private final Callback<String> callback;

    public ShareEmailResultReceiver(Callback<String> callback) {
        super(null);

        if (callback == null) {
            throw new IllegalArgumentException("Callback must not be null");
        }
        this.callback = callback;
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case ShareEmailClient.RESULT_CODE_OK: {
                callback.success(new Result<>(
                        resultData.getString(ShareEmailClient.RESULT_DATA_EMAIL), null));
                break;
            }
            case ShareEmailClient.RESULT_CODE_CANCELED: {
                callback.failure(new TwitterException(resultData.getString(
                        ShareEmailClient.RESULT_DATA_MSG)));
                break;
            }
            case ShareEmailClient.RESULT_CODE_ERROR: {
                callback.failure((TwitterException) resultData.getSerializable(
                        ShareEmailClient.RESULT_DATA_ERROR));
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid result code " + resultCode);
            }
        }
    }
}
