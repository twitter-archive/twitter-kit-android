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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class GuestAuthNetworkInterceptorTest {
    static final String TEST_URL = "https://api.twitter.com";

    @Mock
    Interceptor.Chain mockChain;
    Request request;
    GuestAuthNetworkInterceptor interceptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        request = new Request.Builder().url(TEST_URL).build();
        interceptor = new GuestAuthNetworkInterceptor();
    }

    @Test
    public void testIntercept_with403() throws Exception {
        final Response response = new Response.Builder()
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .code(403)
                .message("Forbidden")
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(request)).thenReturn(response);

        final Response modifiedResponse = interceptor.intercept(mockChain);

        assertFalse(response == modifiedResponse);
        assertEquals(401, modifiedResponse.code());
    }

    @Test
    public void testIntercept_with400() throws Exception {
        final Response response = new Response.Builder()
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .message("Bad Request")
                .code(400)
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(request)).thenReturn(response);

        final Response unmodifiedResponse = interceptor.intercept(mockChain);

        assertTrue(response == unmodifiedResponse);
        assertEquals(400, unmodifiedResponse.code());
    }
}
