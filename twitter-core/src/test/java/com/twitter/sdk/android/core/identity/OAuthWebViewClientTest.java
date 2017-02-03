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

import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

import com.twitter.sdk.android.core.TestFixtures;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class OAuthWebViewClientTest  {

    private static final String COMPLETE_URL = "twittersdk://callback";
    private static final String RETURNED_URL = "twittersdk://callback?version=1.0.1-SNAPSHOT.dev"
            + "&app=test_app"
            + "&oauth_token=" + TestFixtures.TOKEN
            + "&oauth_verifier=" + TestFixtures.VERIFIER;
    private static final String TEST_URL = "https://test.com";
    private static final int TEST_ERROR_CODE = 1000;
    private static final String TEST_ERROR_DESC = "ERROR ERROR ERROR!";

    private static final String EXPECTED_VERSION_KEY = "version";
    private static final String EXPECTED_VERSION_VALUE = "1.0.1-SNAPSHOT.dev";
    private static final String EXPECTED_APP_KEY = "app";
    private static final String EXPECTED_APP_VALUE = "test_app";
    private static final String EXPECTED_OAUTH_TOKEN_KEY = "oauth_token";
    private static final String EXPECTED_OAUTH_TOKEN_VALUE = TestFixtures.TOKEN;
    private static final String EXPECTED_OAUTH_VERIFIER_KEY = "oauth_verifier";
    private static final String EXPECTED_OAUTH_VERIFIER_VALUE = TestFixtures.VERIFIER;

    private OAuthWebViewClient.Listener mockListener;
    private OAuthWebViewClient webViewClient;

    @Before
    public void setUp() throws Exception {


        mockListener = mock(OAuthWebViewClient.Listener.class);
        webViewClient = new OAuthWebViewClient(COMPLETE_URL, mockListener);
    }

    @Test
    public void testOnPageFinished() {
        final WebView mockWebView = mock(WebView.class);
        webViewClient.onPageFinished(mockWebView, COMPLETE_URL);
        verify(mockListener).onPageFinished(eq(mockWebView), eq(COMPLETE_URL));
    }

    @Test
    public void testShouldOverrideUrlLoading_urlStartsWithCompleteUrl() {
        webViewClient.shouldOverrideUrlLoading(mock(WebView.class), RETURNED_URL);

        final ArgumentCaptor<Bundle> bundleArgCaptor = ArgumentCaptor.forClass(Bundle.class);
        verify(mockListener).onSuccess(bundleArgCaptor.capture());

        final Bundle bundle = bundleArgCaptor.getValue();
        assertEquals(EXPECTED_VERSION_VALUE, bundle.getString(EXPECTED_VERSION_KEY));
        assertEquals(EXPECTED_APP_VALUE, bundle.getString(EXPECTED_APP_KEY));
        assertEquals(EXPECTED_OAUTH_TOKEN_VALUE, bundle.getString(EXPECTED_OAUTH_TOKEN_KEY));
        assertEquals(EXPECTED_OAUTH_VERIFIER_VALUE, bundle.getString(EXPECTED_OAUTH_VERIFIER_KEY));
    }

    @Test
    public void testShouldOverrideUrlLoading_urlDoesNotStartWithCompleteUrl() {
        webViewClient.shouldOverrideUrlLoading(mock(WebView.class), TEST_URL);
        verifyZeroInteractions(mockListener);
    }

    @Test
    public void testOnReceivedError() {
        webViewClient.onReceivedError(mock(WebView.class), TEST_ERROR_CODE, TEST_ERROR_DESC,
                TEST_URL);
        verifyOnError(TEST_ERROR_CODE, TEST_ERROR_DESC, TEST_URL);
    }

    @Test
    public void testOnReceivedSslError() {
        final SslError mockSslError = mock(SslError.class);
        when(mockSslError.getPrimaryError()).thenReturn(TEST_ERROR_CODE);

        webViewClient.onReceivedSslError(mock(WebView.class), mock(SslErrorHandler.class),
                mockSslError);
        verifyOnError(TEST_ERROR_CODE, null, null);
    }

    private void verifyOnError(int expectedErrorCode, String expectedErrorDesc,
            String expectedFailingUrl) {
        final ArgumentCaptor<WebViewException> exceptionArgCaptor
                = ArgumentCaptor.forClass(WebViewException.class);
        verify(mockListener).onError(exceptionArgCaptor.capture());

        final WebViewException exception = exceptionArgCaptor.getValue();
        assertEquals(expectedErrorCode, exception.getErrorCode());
        assertEquals(expectedErrorDesc, exception.getMessage());
        assertEquals(expectedFailingUrl, exception.getFailingUrl());
    }
}
