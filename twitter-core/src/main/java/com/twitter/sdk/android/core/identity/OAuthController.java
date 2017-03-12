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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.oauth.OAuth1aService;
import com.twitter.sdk.android.core.internal.oauth.OAuthConstants;
import com.twitter.sdk.android.core.internal.oauth.OAuthResponse;

class OAuthController implements OAuthWebViewClient.Listener {

    interface Listener {
        void onComplete(int resultCode, Intent data);
    }

    final Listener listener;
    TwitterAuthToken requestToken;

    private final ProgressBar spinner;
    private final WebView webView;
    private final TwitterAuthConfig authConfig;
    private final OAuth1aService oAuth1aService;

    OAuthController(ProgressBar spinner, WebView webView, TwitterAuthConfig authConfig,
            OAuth1aService oAuth1aService, Listener listener) {
        this.spinner = spinner;
        this.webView = webView;
        this.authConfig = authConfig;
        this.oAuth1aService = oAuth1aService;
        this.listener = listener;
    }

    void startAuth() {
        // Step 1. Obtain a request token to start the sign in flow.
        Twitter.getLogger().d(TwitterCore.TAG, "Obtaining request token to start the sign in flow");
        oAuth1aService.requestTempToken(newRequestTempTokenCallback());
    }

    /**
     * Package private for testing.
     */
    Callback<OAuthResponse> newRequestTempTokenCallback() {
        return new Callback<OAuthResponse>() {
            @Override
            public void success(Result<OAuthResponse> result) {
                requestToken = result.data.authToken;
                final String authorizeUrl = oAuth1aService.getAuthorizeUrl(requestToken);
                // Step 2. Redirect user to web view to complete authorization flow.
                Twitter.getLogger().d(TwitterCore.TAG,
                        "Redirecting user to web view to complete authorization flow");
                setUpWebView(webView,
                        new OAuthWebViewClient(oAuth1aService.buildCallbackUrl(authConfig),
                                OAuthController.this), authorizeUrl, new OAuthWebChromeClient());
            }

            @Override
            public void failure(TwitterException error) {
                Twitter.getLogger().e(TwitterCore.TAG,
                        "Failed to get request token", error);
                // Create new exception that can be safely serialized since Retrofit errors may
                // throw a NotSerializableException.
                handleAuthError(AuthHandler.RESULT_CODE_ERROR,
                        new TwitterAuthException("Failed to get request token"));
            }
        };
    }

    protected void handleAuthError(int resultCode, TwitterAuthException error) {
        final Intent data = new Intent();
        data.putExtra(AuthHandler.EXTRA_AUTH_ERROR, error);
        listener.onComplete(resultCode, data);
    }

    /**
     * Package private for testing.
     */
    void setUpWebView(WebView webView, WebViewClient webViewClient, String url,
                      WebChromeClient webChromeClient) {
        final WebSettings webSettings = webView.getSettings();
        webSettings.setAllowFileAccess(false);
        webSettings.setJavaScriptEnabled(false);
        webSettings.setSaveFormData(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(webViewClient);
        webView.loadUrl(url);
        webView.setVisibility(View.INVISIBLE);
        webView.setWebChromeClient(webChromeClient);
    }

    private void handleWebViewSuccess(Bundle bundle) {
        Twitter.getLogger().d(TwitterCore.TAG, "OAuth web view completed successfully");
        if (bundle != null) {
            final String verifier = bundle.getString(OAuthConstants.PARAM_VERIFIER);
            if (verifier != null) {
                // Step 3. Convert the request token to an access token.
                Twitter.getLogger().d(TwitterCore.TAG,
                        "Converting the request token to an access token.");
                oAuth1aService.requestAccessToken(newRequestAccessTokenCallback(),
                        requestToken, verifier);
                return;
            }
        }

        // If we get here, we failed to complete authorization.
        Twitter.getLogger().e(TwitterCore.TAG,
                "Failed to get authorization, bundle incomplete " + bundle, null);
        handleAuthError(AuthHandler.RESULT_CODE_ERROR,
                new TwitterAuthException("Failed to get authorization, bundle incomplete"));
    }

    /**
     * Package private for testing.
     */
    Callback<OAuthResponse> newRequestAccessTokenCallback() {
        return new Callback<OAuthResponse>() {
            @Override
            public void success(Result<OAuthResponse> result) {
                final Intent data = new Intent();
                final OAuthResponse response = result.data;
                data.putExtra(AuthHandler.EXTRA_SCREEN_NAME, response.userName);
                data.putExtra(AuthHandler.EXTRA_USER_ID, response.userId);
                data.putExtra(AuthHandler.EXTRA_TOKEN, response.authToken.token);
                data.putExtra(AuthHandler.EXTRA_TOKEN_SECRET,
                        response.authToken.secret);
                listener.onComplete(Activity.RESULT_OK, data);
            }

            @Override
            public void failure(TwitterException error) {
                Twitter.getLogger().e(TwitterCore.TAG, "Failed to get access token", error);
                // Create new exception that can be safely serialized since Retrofit errors may
                // throw a NotSerializableException.
                handleAuthError(AuthHandler.RESULT_CODE_ERROR,
                        new TwitterAuthException("Failed to get access token"));
            }
        };
    }

    private void handleWebViewError(WebViewException error) {
        Twitter.getLogger().e(TwitterCore.TAG, "OAuth web view completed with an error", error);
        handleAuthError(AuthHandler.RESULT_CODE_ERROR,
                new TwitterAuthException("OAuth web view completed with an error"));
    }

    private void dismissWebView() {
        webView.stopLoading();
        dismissSpinner();
    }

    private void dismissSpinner() {
        spinner.setVisibility(View.GONE);
    }

    @Override
    public void onPageFinished(WebView webView, String url) {
        dismissSpinner();
        webView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess(Bundle bundle) {
        handleWebViewSuccess(bundle);
        dismissWebView();
    }

    @Override
    public void onError(WebViewException exception) {
        handleWebViewError(exception);
        dismissWebView();
    }
}
