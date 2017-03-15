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

import android.test.AndroidTestCase;

import com.google.gson.GsonBuilder;
import com.twitter.sdk.android.core.internal.CommonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class ScribeEventTransformTest extends AndroidTestCase {
    static final String TEST_MESSAGE = "TEST MESSAGE";
    static final String TEST_ITEM_TYPE = "\"item_type\":6";
    static final String TEST_DESCRIPTION = "\"description\":\"TEST MESSAGE\"";
    private ScribeEvent.Transform transform;
    private EventNamespace eventNamespace;
    private String scribeEventJsonString;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        transform = new ScribeEvent.Transform(new GsonBuilder().create());

        eventNamespace = new EventNamespace.Builder()
                .setClient("testclient")
                .setPage("testpage")
                .setSection("testsection")
                .setComponent("testcomponent")
                .setElement("testelement")
                .setAction("testaction")
                .builder();

        InputStream is = null;
        try {
            is = getContext().getAssets().open("scribe_event.json");
            scribeEventJsonString = CommonUtils.streamToString(is).trim();
        } finally {
            CommonUtils.closeQuietly(is);
        }
    }

    public void testToBytes() throws IOException {
        final ScribeEvent scribeEvent =
                new ScribeEvent("testcategory", eventNamespace, 1404426136717L);
        final byte[] bytes = transform.toBytes(scribeEvent);
        assertEquals(scribeEventJsonString, new String(bytes, "UTF-8"));
    }

    public void testToBytes_withItems() throws IOException {
        final ScribeItem scribeItem = ScribeItem.fromMessage(TEST_MESSAGE);
        final List<ScribeItem> itemList = Arrays.asList(scribeItem);
        final ScribeEvent scribeEvent =
                new ScribeEvent("testcategory", eventNamespace, 1404426136717L, itemList);
        final byte[] bytes = transform.toBytes(scribeEvent);

        assertTrue(new String(bytes, "UTF-8").contains(TEST_ITEM_TYPE));
        assertTrue(new String(bytes, "UTF-8").contains(TEST_DESCRIPTION));
    }
}
