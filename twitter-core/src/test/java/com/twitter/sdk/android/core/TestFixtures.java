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

import com.twitter.sdk.android.core.internal.TwitterApiConstants;
import com.twitter.sdk.android.core.models.ApiError;

public final class TestFixtures {

    public static final String KEY = "key";
    public static final String TOKEN = "token";
    public static final String SECRET = "secret";
    public static final String VERIFIER = "verifier";
    public static final long USER_ID = 11L;
    public static final String SCREEN_NAME = "username";

    public static final ApiError TEST_APP_AUTH_ERROR = new ApiError("app auth error",
            TwitterApiConstants.Errors.APP_AUTH_ERROR_CODE);
    public static final ApiError TEST_GUEST_AUTH_ERROR = new ApiError("guest auth error",
            TwitterApiConstants.Errors.GUEST_AUTH_ERROR_CODE);
    public static final ApiError TEST_LEGACY_ERROR = new ApiError("legacy error",
            TwitterApiConstants.Errors.LEGACY_ERROR);
}
