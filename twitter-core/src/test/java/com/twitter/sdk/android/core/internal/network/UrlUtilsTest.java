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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.net.URI;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class UrlUtilsTest {

    private static final String[] ORIGINAL_STRINGS = new String[]{
            "Ladies + Gentlemen",
            "An encoded string!",
            "Dogs, Cats & Mice",
            "☃",
            "~`!@#$%^&*()+=,<.>?/",
            "-._~"
    };

    private static final String[] PERCENT_ENCODED_STRINGS = new String[]{
            "Ladies%20%2B%20Gentlemen",
            "An%20encoded%20string%21",
            "Dogs%2C%20Cats%20%26%20Mice",
            "%E2%98%83",
            "~%60%21%40%23%24%25%5E%26%2A%28%29%2B%3D%2C%3C.%3E%3F%2F",
            "-._~"
    };

    private static final String QUERY_PARAMS = "plainParam=1&&emptyParam&decodedParam=%2B2me&";
    private static final String PLAIN_PARAM_KEY = "plainParam";
    private static final String PLAIN_PARAM_VALUE = "1";
    private static final String EMPTY_PARAM_KEY = "emptyParam";
    private static final String EMPTY_PARAM_VALUE = "";
    private static final String DECODED_PARAM_KEY = "decodedParam";
    private static final String DECODED_PARAM_VALUE_PLAIN = "%2B2me";
    private static final String DECODED_PARAM_VALUE_DECODED = "+2me";
    private static final URI URI_WITH_PARAMS = URI.create("http://test.com?" + QUERY_PARAMS);

    @Test
    public void testPercentEncode() {
        int i = 0;
        for (String s : ORIGINAL_STRINGS) {
            assertEquals(PERCENT_ENCODED_STRINGS[i], UrlUtils.percentEncode(s));
            i += 1;
        }
    }

    @Test
    public void testGetQueryParams_nullDecode() {
        final TreeMap<String, String> params = UrlUtils.getQueryParams(EMPTY_PARAM_VALUE, true);
        assertNotNull(params);
        assertEquals(0, params.size());
    }

    @Test
    public void testGetQueryParams_nullNotDecode() {
        final TreeMap<String, String> params = UrlUtils.getQueryParams(EMPTY_PARAM_VALUE, false);
        assertNotNull(params);
        assertEquals(0, params.size());
    }

    @Test
    public void testGetQueryParams_allParamsDecode() {
        final TreeMap<String, String> params = UrlUtils.getQueryParams(QUERY_PARAMS, true);
        assertDecodedValue(params);
    }

    @Test
    public void testGetQueryParams_allParamsNotDecode() {
        final TreeMap<String, String> params = UrlUtils.getQueryParams(QUERY_PARAMS, false);
        assertNotDecodedValue(params);
    }

    @Test
    public void testURIParams_allParamsDecode() {
        final TreeMap<String, String> params = UrlUtils.getQueryParams(URI_WITH_PARAMS, true);
        assertDecodedValue(params);
    }

    @Test
    public void testURIParams_allParamsNotDecode() {
        final TreeMap<String, String> params = UrlUtils.getQueryParams(URI_WITH_PARAMS, false);
        assertNotDecodedValue(params);
    }

    private void assertNotDecodedValue(final TreeMap<String, String> params) {
        assertEquals(3, params.size());
        assertEquals(PLAIN_PARAM_VALUE, params.get(PLAIN_PARAM_KEY));
        assertEquals(EMPTY_PARAM_VALUE, params.get(EMPTY_PARAM_KEY));
        assertEquals(DECODED_PARAM_VALUE_PLAIN, params.get(DECODED_PARAM_KEY));
    }

    private void assertDecodedValue(final TreeMap<String, String> params) {
        assertEquals(3, params.size());
        assertEquals(PLAIN_PARAM_VALUE, params.get(PLAIN_PARAM_KEY));
        assertEquals(EMPTY_PARAM_VALUE, params.get(EMPTY_PARAM_KEY));
        assertEquals(DECODED_PARAM_VALUE_DECODED, params.get(DECODED_PARAM_KEY));
    }
}
