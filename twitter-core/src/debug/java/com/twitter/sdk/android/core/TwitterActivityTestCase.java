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

import android.app.Activity;

import io.fabric.sdk.android.FabricActivityTestCase;

public class TwitterActivityTestCase<T extends Activity> extends FabricActivityTestCase<T> {

    protected static final String CONSUMER_KEY = "testKey";
    protected static final String CONSUMER_SECRET = "testSecret";
    protected static final String TOKEN = "5ebe2294ecd0e0f08eab7690d2a6ee69";
    protected static final String SECRET = "94a08da1fecbb6e8b46990538c7b50b2";
    protected static final String USER = "rallat";
    protected static final long USER_ID = 15224484;
    protected static final String PHONE = "123456789";

    public TwitterActivityTestCase(Class<T> activityClass) {
        super(activityClass);
    }
}
