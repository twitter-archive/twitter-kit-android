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

package com.twitter.sdk.android.core.internal.network;

import com.twitter.sdk.android.core.GuestSession;
import com.twitter.sdk.android.core.GuestSessionProvider;
import com.twitter.sdk.android.core.internal.oauth.GuestAuthToken;
import com.twitter.sdk.android.core.internal.oauth.OAuth2Token;
import com.twitter.sdk.android.core.internal.oauth.OAuthConstants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import okhttp3.Interceptor;
import okhttp3.Request;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class GuestAuthInterceptorTest {
    static final String TEST_GUEST_TOKEN = "139854932048";
    static final String TEST_ACCESS_TOKEN = "AjhdlsjreurWfjdiskdjieidfkdjshrow";
    static final String TEST_HEADER_AUTHORIZATION = OAuth2Token.TOKEN_TYPE_BEARER
            + " " + TEST_ACCESS_TOKEN;
    static final String TEST_URL = "https://api.twitter.com";
    static final String TEST_HEADER = "TEST_HEADER";

    @Mock
    GuestSessionProvider mockGuestSessionProvider;
    @Mock
    GuestSession mockGuestSession;
    @Mock
    GuestAuthToken mockAuthToken;
    @Mock
    Interceptor.Chain mockChain;
    @Captor
    ArgumentCaptor<Request> requestCaptor;
    GuestAuthInterceptor interceptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(mockAuthToken.getGuestToken()).thenReturn(TEST_GUEST_TOKEN);
        when(mockAuthToken.getAccessToken()).thenReturn(TEST_ACCESS_TOKEN);
        when(mockAuthToken.getTokenType()).thenReturn(OAuth2Token.TOKEN_TYPE_BEARER);

        when(mockGuestSession.getAuthToken()).thenReturn(mockAuthToken);

        when(mockGuestSessionProvider.getCurrentSession()).thenReturn(mockGuestSession);

        interceptor = new GuestAuthInterceptor(mockGuestSessionProvider);
    }

    @Test
    public void testIntercept() throws Exception {
        final Request request = new Request.Builder().url(TEST_URL).build();
        when(mockChain.request()).thenReturn(request);

        interceptor.intercept(mockChain);

        verify(mockChain).proceed(requestCaptor.capture());

        final Request signedRequest = requestCaptor.getValue();
        assertEquals(TEST_HEADER_AUTHORIZATION,
                signedRequest.header(OAuthConstants.HEADER_AUTHORIZATION));
        assertEquals(TEST_GUEST_TOKEN, signedRequest.header(OAuthConstants.HEADER_GUEST_TOKEN));
    }

    @Test
    public void testIntercept_nullSessionFromProvider() throws Exception {
        final Request request = new Request.Builder().url(TEST_URL).build();
        when(mockChain.request()).thenReturn(request);
        when(mockGuestSessionProvider.getCurrentSession()).thenReturn(null);

        interceptor.intercept(mockChain);

        verify(mockChain).proceed(requestCaptor.capture());

        final Request unsignedRequest = requestCaptor.getValue();
        assertEquals(request, unsignedRequest);
    }

    @Test
    public void testAddAuthHeaders() {
        final Request.Builder builder = new Request.Builder().url(TEST_URL);
        GuestAuthInterceptor.addAuthHeaders(builder, mockAuthToken);
        final Request request = builder.build();

        assertEquals(TEST_HEADER_AUTHORIZATION,
                request.header(OAuthConstants.HEADER_AUTHORIZATION));
        assertEquals(TEST_GUEST_TOKEN, request.header(OAuthConstants.HEADER_GUEST_TOKEN));
    }

    @Test
    public void testAddAuthHeaders_removesOldHeaders() {
        final Request.Builder builder = new Request.Builder().url(TEST_URL);
        builder.header(OAuthConstants.HEADER_AUTHORIZATION, "23233");
        builder.header(OAuthConstants.HEADER_GUEST_TOKEN, "djfhjASEfjvncdjfhdkjASjshdj");
        builder.header(TEST_HEADER, TEST_HEADER);
        GuestAuthInterceptor.addAuthHeaders(builder, mockAuthToken);
        final Request request = builder.build();

        assertEquals(TEST_HEADER_AUTHORIZATION,
                request.header(OAuthConstants.HEADER_AUTHORIZATION));
        assertEquals(TEST_GUEST_TOKEN, request.header(OAuthConstants.HEADER_GUEST_TOKEN));
        assertEquals(TEST_HEADER, request.header(TEST_HEADER));
    }
}
