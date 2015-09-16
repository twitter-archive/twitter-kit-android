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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit.client.Header;
import retrofit.client.Request;
import retrofit.mime.FormUrlEncodedTypedOutput;
import retrofit.mime.TypedByteArray;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class AuthenticatedClientTest  {
    static final String POST_VERB = "POST";
    static final String POST_KEY = "test";
    static final String POST_KEY_2 = "test2%21";
    static final String POST_KEY_2_DECODED = "test2!";
    static final String POST_VALUE = "value";
    static final String POST_VALUE_2 = "value%202%21";
    static final String POST_VALUE_2_DECODED = "value 2!";
    static final String ANY_URL = "testurl";
    static final String BAD_CHAR_ENCODING = "UTF-811";
    static final String BAD_URL_ENCODING = "value %3f";
    static final String QUERY_NO_VALUE = POST_KEY + "&" + POST_KEY_2;
    static final String QUERY_WITH_VALUE = POST_KEY + "=" + POST_VALUE + "&" +
            POST_KEY_2 + "=" + POST_VALUE_2;
    static final String QUERY_BAD_PARAM = POST_KEY + "=" + POST_VALUE + "=" + POST_VALUE;
    static final Header TEST_HEADER = new Header("test", "test");

    private AuthenticatedClient client;

    @Before
    public void setUp() throws Exception {


        final TwitterAuthConfig config = mock(TwitterAuthConfig.class);
        final TwitterSession session = mock(TwitterSession.class);
        client = new AuthenticatedClient(config, session, null);
    }

    @Test
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
    @Test
    public void testGetPostParameters_formFieldPostSingleParams() throws IOException {

        final FormUrlEncodedTypedOutput output = new FormUrlEncodedTypedOutput();
        output.addField(POST_KEY, POST_VALUE);

        final Request request = new Request(POST_VERB, ANY_URL, null, output);
        final Map<String, String> params = client.getPostParams(request);

        assertEquals(POST_VALUE, params.get(POST_KEY));
    }

    /**
     * Sign body's made of test=value&test2=value type (FormUrlEncoded)
     */
    @Test
    public void testGetPostParameters_formFieldPostMultipleParams() throws IOException {

        final FormUrlEncodedTypedOutput output = new FormUrlEncodedTypedOutput();
        output.addField(POST_KEY, POST_VALUE);
        output.addField(POST_KEY_2, POST_VALUE);

        final Request request = new Request(POST_VERB, ANY_URL, null, output);
        final Map<String, String> params = client.getPostParams(request);

        assertEquals(POST_VALUE, params.get(POST_KEY));
        assertEquals(POST_VALUE, params.get(POST_KEY_2));
    }

    /**
     * Do not sign JSON Body Posts (Twitter API and sig doesn't support this anyway)
     * Shouldn't fail though
     */
    @Test
    public void testGetPostParameters_bodyPost() throws IOException {
        final Request request =
                new Request(POST_VERB, ANY_URL, null, new TypedByteArray(null, new byte[0]));

        final Map<String, String> params = client.getPostParams(request);
        assertTrue(params.isEmpty());
    }

    @Test
    public void testGetQueryParameters_emptyValue() throws IOException {
        final Map<String, String> params = client.getParameters(QUERY_NO_VALUE);

        assertTrue(params.containsKey(POST_KEY));
        assertEquals("", params.get(POST_KEY));
        assertTrue(params.containsKey(POST_KEY_2_DECODED));
        assertEquals("", params.get(POST_KEY_2_DECODED));
    }

    @Test
    public void testGetQueryParameters_withValue() throws IOException {
        final Map<String, String> params = client.getParameters(QUERY_WITH_VALUE);

        assertTrue(params.containsKey(POST_KEY));
        assertEquals(POST_VALUE, params.get(POST_KEY));
        assertTrue(params.containsKey(POST_KEY_2_DECODED));
        assertEquals(POST_VALUE_2_DECODED, params.get(POST_KEY_2_DECODED));
    }

    @Test
    public void testGetQueryParameters_withBadParam() throws IOException {
        try {
            client.getParameters(QUERY_BAD_PARAM);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("bad parameter", e.getMessage());
        }
    }

    @Test
    public void testDecode_withBadEncoding() throws IOException {
        try {
            client.decode(BAD_URL_ENCODING, BAD_CHAR_ENCODING);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("bad parameter encoding", e.getMessage());
        }
    }
}
