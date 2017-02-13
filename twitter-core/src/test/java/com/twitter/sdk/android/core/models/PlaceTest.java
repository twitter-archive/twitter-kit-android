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
import com.google.gson.stream.JsonReader;
import com.twitter.sdk.android.core.TestResources;
import com.twitter.sdk.android.core.internal.CommonUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class PlaceTest {

    private static final String EXPECTED_COUNTRY = "United States";
    private static final String EXPECTED_COUNTRY_CODE = "US";
    private static final String EXPECTED_FULL_NAME = "Twitter HQ, San Francisco";
    private static final String EXPECTED_ID = "247f43d441defc03";
    private static final String EXPECTED_NAME = "Twitter HQ";
    private static final String EXPECTED_PLACE_TYPE = "poi";
    private static final String EXPECTED_URL = "https://api.twitter.com/1.1/geo/id/247f43d441defc03.json";

    private static final String EXPECTED_ATTR_STREET_ADDRESS = "street_address";
    private static final String EXPECTED_ATTR_STREET_ADDRESS_VALUE = "795 Folsom St";
    private static final String EXPECTED_ATTR_623_ID = "623:id";
    private static final String EXPECTED_ATTR_623_ID_VALUE = "210176";
    private static final String EXPECTED_ATTR_TWITTER = "twitter";
    private static final String EXPECTED_ATTR_TWITTER_VALUE = "twitter";

    private static final Double EXPECTED_BOUNDING_BOX_LONGITUDE = -122.400612831116;
    private static final Double EXPECTED_BOUNDING_BOX_LATITUDE = 37.7821120598956;
    private static final String EXPECTED_BOUNDING_BOX_TYPE = "Polygon";

    @Rule
    public final TestResources testResources = new TestResources();

    private Gson gson;

    @Before
    public void setUp() throws Exception {
        gson = new Gson();
    }

    @Test
    public void testDeserialization() throws IOException {
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(testResources
                    .getAsStream("model_places.json")));
            final Place place = gson.fromJson(reader, Place.class);
            assertAttributes(place.attributes);
            assertBoundingBox(place.boundingBox);
            assertEquals(EXPECTED_COUNTRY, place.country);
            assertEquals(EXPECTED_COUNTRY_CODE, place.countryCode);
            assertEquals(EXPECTED_FULL_NAME, place.fullName);
            assertEquals(EXPECTED_ID, place.id);
            assertEquals(EXPECTED_NAME, place.name);
            assertEquals(EXPECTED_PLACE_TYPE, place.placeType);
            assertEquals(EXPECTED_URL, place.url);
        } finally {
            CommonUtils.closeQuietly(reader);
        }
    }

    private void assertAttributes(Map<String, String> attributes) {
        assertEquals(EXPECTED_ATTR_STREET_ADDRESS_VALUE,
                attributes.get(EXPECTED_ATTR_STREET_ADDRESS));
        assertEquals(EXPECTED_ATTR_623_ID_VALUE, attributes.get(EXPECTED_ATTR_623_ID));
        assertEquals(EXPECTED_ATTR_TWITTER_VALUE, attributes.get(EXPECTED_ATTR_TWITTER));
    }

    private void assertBoundingBox(Place.BoundingBox boundingBox) {
        assertEquals(EXPECTED_BOUNDING_BOX_TYPE, boundingBox.type);
        assertEquals(4, boundingBox.coordinates.get(0).size());
        for (List<Double> d: boundingBox.coordinates.get(0)) {
            assertEquals(EXPECTED_BOUNDING_BOX_LONGITUDE, d.get(0));
            assertEquals(EXPECTED_BOUNDING_BOX_LATITUDE, d.get(1));
        }
    }
}
