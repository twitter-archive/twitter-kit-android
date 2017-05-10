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

import com.twitter.sdk.android.core.internal.oauth.OAuth1aHeaders;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class OAuthSigningTests  {
    private static final String ANY_AUTH_HEADER = "Twitter Authority!";
    private static final String VERIFY_CREDENTIALS_URL = "https://twitter.com";

    private TwitterAuthConfig authConfig;
    private TwitterAuthToken authToken;
    private OAuthSigning authSigning;
    private OAuth1aHeaders oAuthHeaders;

    @Before
    public void setUp() throws Exception {

        oAuthHeaders = mock(OAuth1aHeaders.class);
        authConfig = new TwitterAuthConfig(TestFixtures.KEY, TestFixtures.SECRET);
        authToken = new TwitterAuthToken(TestFixtures.TOKEN, TestFixtures.SECRET);
        authSigning = new OAuthSigning(authConfig, authToken, oAuthHeaders);

        when(oAuthHeaders.getAuthorizationHeader(authConfig, authToken, null, "GET",
                OAuthSigning.VERIFY_CREDENTIALS_URL, null)).thenReturn(ANY_AUTH_HEADER);
    }

    @Test
    public void testConstructor_nullAuthConfig() {
        try {
            new OAuthSigning(null, authToken);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("authConfig must not be null", e.getMessage());
        }
    }

    @Test
    public void testConstructor_nullAuthToken() {
        try {
            new OAuthSigning(authConfig, null);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("authToken must not be null", e.getMessage());
        }
    }

    @Test
    public void testGetAuthorizationHeader() {
        authSigning.getAuthorizationHeader("POST", VERIFY_CREDENTIALS_URL, null);

        verify(oAuthHeaders).getAuthorizationHeader(authConfig, authToken, null,
                "POST", VERIFY_CREDENTIALS_URL, null);
    }

    @Test
    public void testGetOAuthEchoHeaders() {
        authSigning.getOAuthEchoHeaders("POST", VERIFY_CREDENTIALS_URL, null);

        verify(oAuthHeaders).getOAuthEchoHeaders(authConfig, authToken, null,
                "POST", VERIFY_CREDENTIALS_URL, null);
    }

    @Test
    public void testGetOAuthEchoHeadersForVerifyCredentials() {
        authSigning.getOAuthEchoHeadersForVerifyCredentials();

        verify(oAuthHeaders).getOAuthEchoHeaders(authConfig, authToken, null, "GET",
                OAuthSigning.VERIFY_CREDENTIALS_URL, null);
    }
}
