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

/**
 * Error codes that may be returned from the API. For complete list of error codes, see
 * https://dev.twitter.com/overview/api/response-codes
 */
public class TwitterApiErrorConstants {
    public static final int RATE_LIMIT_EXCEEDED = 88;
    public static final int COULD_NOT_AUTHENTICATE = 32;
    public static final int CLIENT_NOT_PRIVILEGED = 87;
    public static final int PAGE_NOT_EXIST = 34;
    public static final int UNKNOWN_ERROR = -1;
}
