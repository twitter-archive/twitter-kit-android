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

package com.twitter.sdk.android.core.internal;

import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.AuthToken;
import com.twitter.sdk.android.core.BuildConfig;
import com.twitter.sdk.android.core.TestFixtures;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.oauth.GuestAuthToken;
import com.twitter.sdk.android.core.internal.oauth.OAuth2Service;
import com.twitter.sdk.android.core.internal.oauth.OAuth2Token;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class TwitterRequestHeadersTest  {

    private static final String GET = "GET";
    private static final String TEST_URL = "http://testurl";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String GUEST_TOKEN = "guestToken";

    private final TwitterAuthConfig authConfig = new TwitterAuthConfig("consumerKey",
            "consumerSecret");
    private final TwitterAuthToken authToken = new TwitterAuthToken("token", "secret");
    private final TwitterSession session = new TwitterSession(authToken, TestFixtures.USER_ID,
            TestFixtures.SCREEN_NAME);
    private final String userAgent = "TwitterRequestHeadersTest";

    @Test
    public void testGetHeaders() {
        final TwitterRequestHeaders requestHeaders = new TwitterRequestHeaders(GET,
                TEST_URL, authConfig, session, userAgent, null);
        assertMinimumHeaders(requestHeaders.getHeaders());
    }

    private void assertMinimumHeaders(Map<String, String> headers) {
        assertNotNull(headers);
        assertNotNull(headers.get(AuthToken.HEADER_AUTHORIZATION));
        assertEquals(userAgent, headers.get(TwitterRequestHeaders.HEADER_USER_AGENT));
    }

    @Test
    public void testGetHeaders_appAuthToken() {
        final AppSession sessionAppAuthToken = mock(AppSession.class);
         when(sessionAppAuthToken.getAuthToken()).thenReturn(new OAuth2Token(null, ACCESS_TOKEN));

        final TwitterRequestHeaders requestHeaders = new TwitterRequestHeaders(GET,
                TEST_URL, authConfig, sessionAppAuthToken, userAgent, null);
        final Map<String, String> headers = requestHeaders.getHeaders();
        assertMinimumHeaders(headers);
        assertEquals(OAuth2Service.getAuthorizationHeader(sessionAppAuthToken.getAuthToken()),
                headers.get(OAuth2Token.HEADER_AUTHORIZATION));
    }

    @Test
    public void testGetHeaders_guestAuthToken() {
        final AppSession sessionGuestAuthToken = mock(AppSession.class);
        when(sessionGuestAuthToken.getAuthToken())
                .thenReturn(new GuestAuthToken(null, ACCESS_TOKEN, GUEST_TOKEN));
        final TwitterRequestHeaders requestHeaders = new TwitterRequestHeaders(GET,
                TEST_URL, authConfig, sessionGuestAuthToken, userAgent, null);
        final Map<String, String> headers = requestHeaders.getHeaders();
        assertMinimumHeaders(headers);
        assertEquals(OAuth2Service.getAuthorizationHeader(sessionGuestAuthToken.getAuthToken()),
                headers.get(GuestAuthToken.HEADER_AUTHORIZATION)
        );
        assertEquals(GUEST_TOKEN, headers.get(GuestAuthToken.HEADER_GUEST_TOKEN));
    }

    @Test
    public void testGetHeaders_withNullSession()  {
        final TwitterRequestHeaders requestHeaders = new TwitterRequestHeaders(GET,
                TEST_URL, authConfig, null, userAgent, null);
        final Map<String, String> headerMap = requestHeaders.getHeaders();
        assertHeadersWhenNoAuth(headerMap);
    }

    private void assertHeadersWhenNoAuth(Map<String, String> headers) {
        assertEquals(1, headers.size());
        assertEquals(userAgent, headers.get(TwitterRequestHeaders.HEADER_USER_AGENT));
    }

    @Test
    public void testGetHeaders_extraHeaders() {
        final String extraHeader = "Extra header";
        final String extraHeaderValue = "Extra header value";
        final TwitterRequestHeaders requestHeaders = new TwitterRequestHeaders(GET,
                TEST_URL, authConfig, session, userAgent, null) {
            @Override
            protected Map<String, String> getExtraHeaders() {
                final Map<String, String> extras = new HashMap<>(1);
                extras.put(extraHeader, extraHeaderValue);
                return extras;
            }
        };
        final Map<String, String> headerMap = requestHeaders.getHeaders();
        assertMinimumHeaders(headerMap);
        assertTrue(headerMap.containsKey(extraHeader));
        assertEquals(extraHeaderValue, headerMap.get(extraHeader));
    }
}
