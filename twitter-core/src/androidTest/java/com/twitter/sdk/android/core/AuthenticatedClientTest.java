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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.FabricAndroidTestCase;
import retrofit.client.Header;
import retrofit.client.Request;
import retrofit.mime.FormUrlEncodedTypedOutput;
import retrofit.mime.TypedByteArray;

import static org.mockito.Mockito.mock;

public class AuthenticatedClientTest extends FabricAndroidTestCase {
    static final String POST_VERB = "POST";
    static final String POST_KEY = "test";
    static final String POST_KEY_2 = "test2";
    static final String POST_VALUE = "value";
    static final String ANY_URL = "testurl";
    static final Header TEST_HEADER = new Header("test", "test");

    private AuthenticatedClient client;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final TwitterAuthConfig config = mock(TwitterAuthConfig.class);
        final TwitterSession session = mock(TwitterSession.class);
        client = new AuthenticatedClient(config, session, null);
    }

    public void testGetAuthHeaders_preservesOriginalHeaders() throws IOException {
        final List<Header> headers = new ArrayList<>();
        headers.add(TEST_HEADER);
        final Request request = new Request(POST_VERB, ANY_URL, headers, null);
        final List<Header> modifiedHeaders = client.getAuthHeaders(request);
        assertNotNull(modifiedHeaders);
        assertFalse(modifiedHeaders.isEmpty());
        assertTrue(modifiedHeaders.contains(TEST_HEADER));
    }

    /**
     * Sign body's made of test=value type (FormUrlEncoded)
     */
    public void testGetPostParameters_formFieldPostSingleParams() throws IOException {

        final FormUrlEncodedTypedOutput output = new FormUrlEncodedTypedOutput();
        output.addField(POST_KEY, POST_VALUE);

        final Request request = new Request(POST_VERB, ANY_URL, null, output);
        final Map<String, String> params = client.getPostParams(request);

        assertEquals(params.get(POST_KEY), POST_VALUE);
    }

    /**
     * Sign body's made of test=value&test2=value type (FormUrlEncoded)
     */
    public void testGetPostParameters_formFieldPostMultipleParams() throws IOException {

        final FormUrlEncodedTypedOutput output = new FormUrlEncodedTypedOutput();
        output.addField(POST_KEY, POST_VALUE);
        output.addField(POST_KEY_2, POST_VALUE);

        final Request request = new Request(POST_VERB, ANY_URL, null, output);
        final Map<String, String> params = client.getPostParams(request);

        assertEquals(params.get(POST_KEY), POST_VALUE);
        assertEquals(params.get(POST_KEY_2), POST_VALUE);
    }

    /**
     * Do not sign JSON Body Posts (Twitter API and sig doesn't support this anyway)
     * Shouldn't fail though
     */
    public void testGetPostParameters_bodyPost() throws IOException {
        final Request request =
                new Request(POST_VERB, ANY_URL, null, new TypedByteArray(null, new byte[0]));

        final Map<String, String> params = client.getPostParams(request);
        assertTrue(params.isEmpty());
    }
}
