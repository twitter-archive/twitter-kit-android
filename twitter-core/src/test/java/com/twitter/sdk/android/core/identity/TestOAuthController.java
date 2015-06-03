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

import android.webkit.WebView;
import android.widget.ProgressBar;

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.internal.oauth.OAuth1aService;

/**
 * Test class to allow mocking of OAuthController.
 */
public class TestOAuthController extends OAuthController {

    TestOAuthController(ProgressBar spinner, WebView webView, TwitterAuthConfig authConfig,
            OAuth1aService oAuth1aService, Listener listener) {
        super(spinner, webView, authConfig, oAuth1aService, listener);
    }
}
