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
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class SafeMapAdapterTest  {
    private static final String TEST_JSON_MAP_EMPTY = "{\"map\":{}}";
    private static final String TEST_JSON_MAP_STRING_VALUES
            = "{\"map\": {\"k1\": \"v1\",\"k2\": \"v2\"}}";
    private static final String TEST_JSON_MAP_NUMBER_VALUES = "{\"map\": {\"k1\": 1,\"k2\": 2}}";

    private static final String TEST_ANY_STRING_KEY = "any key";
    private static final String TEST_ANY_STRING_VALUE = "any value";
    private static final int TEST_ANY_NUMBER = 100;

    private Gson gson;

    @Before
    public void setUp() throws Exception {

        gson = new GsonBuilder().registerTypeAdapterFactory(new SafeMapAdapter()).create();
    }

    @Test
    public void testDeserialization_emptyMapModel1() {
        final Model1 model = gson.fromJson(TEST_JSON_MAP_EMPTY, Model1.class);
        assertEquals(Collections.EMPTY_MAP, model.mapOfStrings);
    }

    @Test
    public void testDeserialization_validMapModel1() {
        final Model1 model = gson.fromJson(TEST_JSON_MAP_STRING_VALUES, Model1.class);
        try {
            model.mapOfStrings.put(TEST_ANY_STRING_KEY, TEST_ANY_STRING_VALUE);
        } catch (Exception e) {
            assertTrue(e instanceof UnsupportedOperationException);
        }
    }

    @Test
    public void testDeserialization_emptyList() {
        final Model2 model = gson.fromJson(TEST_JSON_MAP_EMPTY, Model2.class);
        assertEquals(Collections.EMPTY_MAP, model.stringLongMap);
    }

    @Test
    public void testDeserialization_validMapModel2() {
        final Model2 model = gson.fromJson(TEST_JSON_MAP_NUMBER_VALUES, Model2.class);
        try {
            model.stringLongMap.put(TEST_ANY_STRING_KEY, Long.valueOf(TEST_ANY_NUMBER));
        } catch (Exception e) {
            assertTrue(e instanceof UnsupportedOperationException);
        }
    }

    private static class Model1 {

        @SerializedName("map")
        public final Map<String, String> mapOfStrings;

        // Not used in testing, but needed because of final.
        Model1(Map<String, String> mapOfStrings) {
            this.mapOfStrings = mapOfStrings;
        }
    }

    private static class Model2 {

        @SerializedName("map")
        public final Map<String, Long> stringLongMap;

        // Not used in testing, but needed because of final.
        Model2(Map<String, Long> stringLongMap) {
            this.stringLongMap = stringLongMap;
        }
    }
}
