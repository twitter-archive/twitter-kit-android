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

import retrofit2.Call;
import retrofit2.Response;

/**
 * Communicates responses from a server or offline requests. One and only one method will be
 * invoked in response to a given request.
 * <p>
 * Callback methods are executed using the {@link retrofit2.Retrofit} callback executor. When none is
 * specified, the following defaults are used:
 * <ul>
 * <li>Callbacks are executed on the application's main (UI) thread.</li>
 * </ul>
 *
 * @param <T> expected response type
 */
public abstract class Callback<T> implements retrofit2.Callback<T> {

    @Override
    public final void onResponse(Call<T> call, Response<T> response){
        if (response.isSuccessful()) {
            success(new Result<>(response.body(), response));
        } else {
            failure(new TwitterApiException(response));
        }
    }

    @Override
    public final void onFailure(Call<T> call, Throwable t) {
        failure(new TwitterException("Request Failure", t));
    }

    /**
     * Called when call completes successfully.
     *
     * @param result the parsed result.
     */
    public abstract void success(Result<T> result);

    /**
     * Unsuccessful call due to network failure, non-2XX status code, or unexpected
     * exception.
     */
    public abstract void failure(TwitterException exception);
}
