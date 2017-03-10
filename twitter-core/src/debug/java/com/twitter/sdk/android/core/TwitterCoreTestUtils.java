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

import java.util.concurrent.ConcurrentHashMap;

public final class TwitterCoreTestUtils {

    private TwitterCoreTestUtils() {}

    public static TwitterCore createTwitterCore(TwitterAuthConfig authConfig,
            ConcurrentHashMap<Session, TwitterApiClient> clients, TwitterApiClient guestClient) {

        TwitterCore.instance = new TwitterCore(authConfig, clients, guestClient);
        return TwitterCore.instance;
    }

    public static void resetTwitterCore() {
        TwitterCore.instance = null;
    }
}
