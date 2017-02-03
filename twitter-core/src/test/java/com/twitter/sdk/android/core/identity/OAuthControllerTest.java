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
import com.twitter.sdk.android.core.TestFixtures;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.oauth.OAuth1aService;
import com.twitter.sdk.android.core.internal.oauth.OAuthConstants;
import com.twitter.sdk.android.core.internal.oauth.OAuthResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class OAuthControllerTest  {

    private static final String TEST_URL = "https://test.com";
    private static final int TEST_ERROR_CODE = 1000;
    private static final String TEST_ERROR_DESC = "ERROR ERROR ERROR!";
    private static final String TEST_VERIFIER_VALUE = "e1BmelAyStYjkAEtqGPBQ8xNlh5GOVtU";

    private ProgressBar mockSpinner;
    private WebView mockWebView;
    private WebSettings mockWebSettings;
    private OAuth1aService mockOAuth1aService;
    private OAuthController.Listener mockListener;
    private OAuthController controller;

    @Before
    public void setUp() throws Exception {


        mockSpinner = mock(ProgressBar.class);
        mockWebView = mock(WebView.class);
        mockWebSettings = mock(WebSettings.class);
        when(mockWebView.getSettings()).thenReturn(mockWebSettings);

        mockOAuth1aService = mock(OAuth1aService.class);
        mockListener = mock(OAuthController.Listener.class);
        controller = new OAuthController(mockSpinner, mockWebView, mock(TwitterAuthConfig.class),
                mockOAuth1aService, mockListener);
    }

    @Test
    public void testStartAuth() {
        controller.startAuth();
        verify(mockOAuth1aService).requestTempToken(any(Callback.class));
    }

    @Test
    public void testNewRequestTempTokenCallback_success() {
        final Callback<OAuthResponse> callback = controller.newRequestTempTokenCallback();
        final TwitterAuthToken mockRequestToken = mock(TwitterAuthToken.class);
        final OAuthResponse oAuthResponse = new OAuthResponse(mockRequestToken, null, 0L);
        callback.success(new Result<>(oAuthResponse, null));

        assertEquals(mockRequestToken, controller.requestToken);
        verify(mockOAuth1aService).getAuthorizeUrl(eq(mockRequestToken));
    }

    @Test
    public void testNewRequestTempTokenCallback_failure() {
        final Callback<OAuthResponse> callback = controller.newRequestTempTokenCallback();
        final TwitterException mockException = mock(TwitterException.class);
        callback.failure(mockException);
        verifyOnCompleteWithError("Failed to get request token");
    }

    private void verifyOnCompleteWithError(String expectedMsg) {
        final ArgumentCaptor<Intent> intentArgCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mockListener).onComplete(eq(AuthHandler.RESULT_CODE_ERROR),
                intentArgCaptor.capture());
        final Intent data = intentArgCaptor.getValue();
        final TwitterAuthException authException
                = (TwitterAuthException) data.getSerializableExtra(AuthHandler.EXTRA_AUTH_ERROR);
        assertNull(authException.getCause());
        assertEquals(expectedMsg, authException.getMessage());
    }

    @Test
    public void testHandleAuthError() {
        final TwitterAuthException mockException = mock(TwitterAuthException.class);
        controller.handleAuthError(AuthHandler.RESULT_CODE_ERROR, mockException);

        final ArgumentCaptor<Intent> intentArgCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mockListener).onComplete(eq(AuthHandler.RESULT_CODE_ERROR),
                intentArgCaptor.capture());
        final Intent data = intentArgCaptor.getValue();
        assertEquals(mockException, data.getSerializableExtra(AuthHandler.EXTRA_AUTH_ERROR));
    }

    @Test
    public void testSetUpWebView() {
        final WebViewClient mockWebClient = mock(WebViewClient.class);
        final WebChromeClient mockWebChromeClient = mock(WebChromeClient.class);
        controller.setUpWebView(mockWebView, mockWebClient, TEST_URL, mockWebChromeClient);
        verify(mockWebSettings).setJavaScriptEnabled(false);
        verify(mockWebSettings).setAllowFileAccess(false);
        verify(mockWebSettings).setSaveFormData(false);
        verify(mockWebView).setVerticalScrollBarEnabled(false);
        verify(mockWebView).setHorizontalScrollBarEnabled(false);
        verify(mockWebView).setWebViewClient(mockWebClient);
        verify(mockWebView).loadUrl(TEST_URL);
        verify(mockWebView).setVisibility(View.INVISIBLE);
        verify(mockWebView).setWebChromeClient(mockWebChromeClient);
    }

    @Test
    public void testNewAccessTokenCallback_success() {
        final Callback<OAuthResponse> callback = controller.newRequestAccessTokenCallback();
        final OAuthResponse oAuthResponse = new OAuthResponse(
                new TwitterAuthToken(TestFixtures.TOKEN, TestFixtures.SECRET),
                TestFixtures.SCREEN_NAME, TestFixtures.USER_ID);
        callback.success(new Result<>(oAuthResponse, null));

        final ArgumentCaptor<Intent> intentArgCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mockListener).onComplete(eq(Activity.RESULT_OK), intentArgCaptor.capture());
        final Intent data = intentArgCaptor.getValue();
        assertEquals(TestFixtures.SCREEN_NAME, data.getStringExtra(AuthHandler.EXTRA_SCREEN_NAME));
        assertEquals(TestFixtures.USER_ID, data.getLongExtra(AuthHandler.EXTRA_USER_ID, 0L));
        assertEquals(TestFixtures.TOKEN, data.getStringExtra(AuthHandler.EXTRA_TOKEN));
        assertEquals(TestFixtures.SECRET, data.getStringExtra(AuthHandler.EXTRA_TOKEN_SECRET));
    }

    @Test
    public void testNewAccessTokenCallback_failure() {
        final Callback<OAuthResponse> callback = controller.newRequestAccessTokenCallback();
        final TwitterException mockException = mock(TwitterException.class);
        callback.failure(mockException);
        verifyOnCompleteWithError("Failed to get access token");
    }

    @Test
    public void testOnPageFinished() {
        controller.onPageFinished(mockWebView, TEST_URL);
        verify(mockSpinner).setVisibility(View.GONE);
        verify(mockWebView).setVisibility(View.VISIBLE);
    }

    @Test
    public void testOnSuccess() {
        final Bundle bundle = new Bundle();
        bundle.putString(OAuthConstants.PARAM_VERIFIER, TEST_VERIFIER_VALUE);
        controller.onSuccess(bundle);
        verify(mockOAuth1aService).requestAccessToken(any(Callback.class),
                eq(controller.requestToken), eq(TEST_VERIFIER_VALUE));
        verify(mockWebView).stopLoading();
    }

    @Test
    public void testOnSuccess_bundleNull() {
        controller.onSuccess(null);
        verifyOnCompleteWithError("Failed to get authorization, bundle incomplete");
        verify(mockWebView).stopLoading();
    }

    @Test
    public void testOnSuccess_bundleIncomplete() {
        controller.onSuccess(Bundle.EMPTY);
        verifyOnCompleteWithError("Failed to get authorization, bundle incomplete");
        verify(mockWebView).stopLoading();
    }

    @Test
    public void testOnError() {
        final WebViewException webViewException = new WebViewException(TEST_ERROR_CODE,
                TEST_ERROR_DESC, TEST_URL);
        controller.onError(webViewException);
        verifyOnCompleteWithError("OAuth web view completed with an error");
    }
}
