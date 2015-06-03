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

package com.twitter.sdk.android.core.internal.scribe;

import android.content.Context;

import io.fabric.sdk.android.services.common.IdManager;

import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLSocketFactory;

/**
 * Test class to allow mocking of ScribeFilesSender.
 */
public class TestScribeFilesSender extends ScribeFilesSender {

    public TestScribeFilesSender(Context context, ScribeConfig scribeConfig, long ownerId,
            TwitterAuthConfig authConfig, List<SessionManager<? extends Session>> sessionManagers,
            SSLSocketFactory sslSocketFactory, ExecutorService executorService,
            IdManager idManager) {
        super(context, scribeConfig, ownerId, authConfig, sessionManagers, sslSocketFactory,
                executorService, idManager);
    }
}
