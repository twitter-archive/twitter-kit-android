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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class UserTest {

    private static final long EXPECTED_ID = 795649L;
    private static final String EXPECTED_NAME = "Ryan Sarver";
    private static final String EXPECTED_SCREEN_NAME = "rsarver";
    private static final String EXPECTED_PROFILE_IMAGE_URL_HTTPS
            = "https://si0.twimg.com/profile_images/1777569006/image1327396628_normal.png";
    private static final boolean EXPECTED_VERIFIED = false;
    private static final String EXPECTED_WITHHELD_IN_COUNTRIES = "XY";

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
                    .getAsStream("model_user.json")));
            final User user = gson.fromJson(reader, User.class);
            // We simply assert that we parsed it successfully and rely on our other unit tests to
            // verify parsing of the individual objects.
            assertEquals(EXPECTED_ID, user.id);
            assertEquals(EXPECTED_ID, user.getId());
            assertEquals(EXPECTED_NAME, user.name);
            assertTrue(user.entities.url.urls.size() > 0);
            assertTrue(user.entities.description.urls.isEmpty());
            assertEquals(EXPECTED_SCREEN_NAME, user.screenName);
            assertEquals(EXPECTED_PROFILE_IMAGE_URL_HTTPS, user.profileImageUrlHttps);
            assertEquals(EXPECTED_VERIFIED, user.verified);
            assertNotNull(user.status);
            assertNotNull(user.withheldInCountries);
            assertEquals(1, user.withheldInCountries.size());
            assertEquals(EXPECTED_WITHHELD_IN_COUNTRIES, user.withheldInCountries.get(0));
        } finally {
            CommonUtils.closeQuietly(reader);
        }
    }
}
