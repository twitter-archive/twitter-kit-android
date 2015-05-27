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

package com.twitter.sdk.android.core.models;

import com.google.gson.Gson;

import java.io.IOException;

import io.fabric.sdk.android.FabricAndroidTestCase;

public class UrlEntityTest extends FabricAndroidTestCase {

    private static final String TEST_JSON
            = "{\"indices\":[32,52], \"url\":\"http:\\/\\/t.co\\/IOwBrTZR\","
            + "\"display_url\":\"youtube.com\\/watch?v=oHg5SJ\\u2026\","
            + "\"expanded_url\":\"http:\\/\\/www.youtube.com\\/watch?v=oHg5SJYRHA0\"}";
    private static final int TEST_INDICES_START = 32;
    private static final int TEST_INDICES_END = 52;
    private static final String TEST_URL = "http://t.co/IOwBrTZR";
    private static final String TEST_DISPLAY_URL = "youtube.com/watch?v=oHg5SJ\u2026";
    private static final String TEST_EXPANDED_URL = "http://www.youtube.com/watch?v=oHg5SJYRHA0";

    private Gson gson;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        gson = new Gson();
    }

    public void testDeserialization() throws IOException {
        final UrlEntity entity = gson.fromJson(TEST_JSON, UrlEntity.class);
        assertEquals(TEST_INDICES_START, entity.getStart());
        assertEquals(TEST_INDICES_END, entity.getEnd());
        assertEquals(TEST_URL, entity.url);
        assertEquals(TEST_DISPLAY_URL, entity.displayUrl);
        assertEquals(TEST_EXPANDED_URL, entity.expandedUrl);
    }
}
