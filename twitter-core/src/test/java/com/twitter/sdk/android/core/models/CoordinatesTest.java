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
public class CoordinatesTest  {

    private static final String TEST_JSON = "{\n"
            + "    \"coordinates\":\n"
            + "    [\n"
            + "        -75.14310264,\n"
            + "        40.05701649\n"
            + "    ],\n"
            + "    \"type\":\"Point\"\n"
            + "}\n";
    private static final Double TEST_COORDINATES_LONGITUDE = Double.valueOf(-75.14310264);
    private static final Double TEST_COORDINATES_LATITUDE = Double.valueOf(40.05701649);
    private static final String TEST_TYPE = "Point";

    private Gson gson;

    @Before
    public void setUp() throws Exception {

        gson = new Gson();
    }

    @Test
    public void testDeserialization() throws IOException {
        final Coordinates coordinates = gson.fromJson(TEST_JSON, Coordinates.class);
        assertEquals(TEST_COORDINATES_LONGITUDE, coordinates.getLongitude());
        assertEquals(TEST_COORDINATES_LATITUDE, coordinates.getLatitude());
        assertEquals(TEST_TYPE, coordinates.type);
    }
}
