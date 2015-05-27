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

package com.twitter.sdk.android.core.internal.scribe;

import io.fabric.sdk.android.FabricAndroidTestCase;
import io.fabric.sdk.android.services.common.CommonUtils;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;

public class ScribeEventTransformTest extends FabricAndroidTestCase {

    private ScribeEvent.Transform transform;
    private ScribeEvent scribeEvent;
    private String scribeEventJsonString;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        transform = new ScribeEvent.Transform(new GsonBuilder().create());

        final EventNamespace eventNamespace = new EventNamespace.Builder()
                .setClient("testclient")
                .setPage("testpage")
                .setSection("testsection")
                .setComponent("testcomponent")
                .setElement("testelement")
                .setAction("testaction")
                .builder();
        scribeEvent = new ScribeEvent("testcategory", eventNamespace, 1404426136717L);

        InputStream is = null;
        try {
            is = getContext().getAssets().open("scribe_event.json");
            scribeEventJsonString = CommonUtils.streamToString(is).trim();
        } finally {
            CommonUtils.closeQuietly(is);
        }
    }

    public void testToBytes() throws IOException {
        final byte[] bytes = transform.toBytes(scribeEvent);
        assertEquals(scribeEventJsonString, new String(bytes, "UTF-8"));
    }
}
