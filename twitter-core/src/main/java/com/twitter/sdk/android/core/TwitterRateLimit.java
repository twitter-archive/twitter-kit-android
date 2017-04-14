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

import okhttp3.Headers;

/**
 * Represents the rate limit data returned on the headers of a request
 *
 * @see <a href="https://dev.twitter.com/rest/public/rate-limiting">Rate Limiting</a>
 */
public class TwitterRateLimit  {

    private static final String LIMIT_KEY = "x-rate-limit-limit";
    private static final String REMAINING_KEY = "x-rate-limit-remaining";
    private static final String RESET_KEY = "x-rate-limit-reset";

    private int requestLimit;
    private int remainingRequest;
    private long resetSeconds;

    TwitterRateLimit(final Headers headers) {
        if (headers == null) {
            throw new IllegalArgumentException("headers must not be null");
        }
        for (int i = 0; i < headers.size(); i++) {
            if (LIMIT_KEY.equals(headers.name(i))) {
                requestLimit = Integer.valueOf(headers.value(i));
            } else if (REMAINING_KEY.equals(headers.name(i))) {
                remainingRequest = Integer.valueOf(headers.value(i));
            } else if (RESET_KEY.equals(headers.name(i))) {
                resetSeconds = Long.valueOf(headers.value(i));
            }
        }
    }

    /**
     * Returns the rate limit ceiling for that given request
     */
    public int getLimit() {
        return requestLimit;
    }

    /**
     * Returns the number of requests left for the 15 minute window
     */
    public int getRemaining() {
        return remainingRequest;
    }

    /**
     * Returns epoch time that rate limit reset will happen.
     */
    public long getReset() {
        return resetSeconds;
    }
}
