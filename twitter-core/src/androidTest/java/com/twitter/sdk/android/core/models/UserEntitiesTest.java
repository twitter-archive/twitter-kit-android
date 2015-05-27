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

import io.fabric.sdk.android.FabricAndroidTestCase;
import io.fabric.sdk.android.services.common.CommonUtils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;

public class UserEntitiesTest extends FabricAndroidTestCase {

    private Gson gson;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        gson = new Gson();
    }

    public void testDeserialization() throws IOException {
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(
                    getContext().getAssets().open("model_userentities.json")));
            final UserEntities userEntities = gson.fromJson(reader, UserEntities.class);
            // We simply assert that we parsed it successfully and rely on our other unit tests to
            // verify parsing of the individual objects.
            assertNotNull(userEntities.url);
            assertFalse(userEntities.url.urls.isEmpty());

            assertNotNull(userEntities.description);
            assertEquals(Collections.EMPTY_LIST, userEntities.description.urls);
        } finally {
            CommonUtils.closeQuietly(reader);
        }
    }
}
