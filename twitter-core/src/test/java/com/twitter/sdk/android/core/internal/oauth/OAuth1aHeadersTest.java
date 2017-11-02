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

package com.twitter.sdk.android.core.internal.oauth;


import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
public class OAuth1aHeadersTest  {
    private static final String VERIFY_CREDENTIALS_URL = "api.twitter.com";
    private static final String ANY_AUTH_CREDENTIALS = "auth_credentials";
    private OAuth1aParameters oAuth1aParameters;
    private OAuth1aHeaders oAuthHeaders;

    @Before
    public void setUp() throws Exception {

        oAuth1aParameters = new MockOAuth1aParameters();
        oAuthHeaders = new MockOAuth1aHeaders();
    }

    @Test
    public void testGetOAuthEchoHeaders() throws Exception {
        final TwitterAuthConfig config = mock(TwitterAuthConfig.class);
        final TwitterAuthToken token = mock(TwitterAuthToken.class);

        final Map<String, String> headers = oAuthHeaders.getOAuthEchoHeaders(config, token, null,
                "GET", VERIFY_CREDENTIALS_URL, null);
        assertEquals(VERIFY_CREDENTIALS_URL, headers.get(OAuth1aHeaders
                .HEADER_AUTH_SERVICE_PROVIDER));
        assertEquals(ANY_AUTH_CREDENTIALS, headers.get(OAuth1aHeaders
                .HEADER_AUTH_CREDENTIALS));
    }

    @Test
    public void testGetAuthorizationHeader() throws Exception {
        final TwitterAuthConfig config = mock(TwitterAuthConfig.class);
        final TwitterAuthToken token = mock(TwitterAuthToken.class);

        assertEquals(ANY_AUTH_CREDENTIALS, oAuthHeaders.getAuthorizationHeader(config, token, null,
                "GET", VERIFY_CREDENTIALS_URL, null));
    }

    private class MockOAuth1aParameters extends OAuth1aParameters {
        MockOAuth1aParameters() {
            super(null, null, null, null, null, null);
        }

        @Override
        public String getAuthorizationHeader() {
            return ANY_AUTH_CREDENTIALS;
        }
    }

    private class MockOAuth1aHeaders extends OAuth1aHeaders {
        @Override
        OAuth1aParameters getOAuth1aParameters(TwitterAuthConfig authConfig, TwitterAuthToken
                authToken, String callback, String method, String url,
                Map<String, String> postParams) {
            return oAuth1aParameters;
        }
    }
}
