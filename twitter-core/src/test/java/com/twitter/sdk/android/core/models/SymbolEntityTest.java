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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class SymbolEntityTest  {

    private static final String TEST_JSON = "{\"indices\":[32,36],\"text\":\"TWTR\"}";
    private static final int TEST_INDICES_START = 32;
    private static final int TEST_INDICES_END = 36;
    private static final String TEST_TEXT = "TWTR";

    private Gson gson;

    @Before
    public void setUp() throws Exception {
        gson = new Gson();
    }

    @Test
    public void testDeserialization() throws IOException {
        final SymbolEntity entity = gson.fromJson(TEST_JSON, SymbolEntity.class);
        assertEquals(TEST_INDICES_START, entity.getStart());
        assertEquals(TEST_INDICES_END, entity.getEnd());
        assertEquals(TEST_TEXT, entity.text);
    }
}
