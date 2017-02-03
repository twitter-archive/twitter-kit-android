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

package com.twitter.sdk.android.core.internal;

import android.net.Uri;
import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class TwitterApiTest  {

    @Test
    public void testBuildUponBaseHost_singlePath() {
        final String baseHost = "testbasehost";
        final TwitterApi api = new TwitterApi(baseHost);
        final Uri.Builder builder = api.buildUponBaseHostUrl("path1");
        assertEquals(baseHost + "/path1", builder.build().toString());
    }

    @Test
    public void testBuildUponBaseHost_multiplePaths() {
        final String baseHost = "testbasehost";
        final TwitterApi api = new TwitterApi(baseHost);
        final Uri.Builder builder = api.buildUponBaseHostUrl("path1", "path2");
        assertEquals(baseHost + "/path1/path2", builder.build().toString());
    }

    @Test
    public void testBuildUserAgent() {
        final String clientName = "TwitterAndroidSDK";
        final String version = "1.0.0.1";
        final String userAgent = TwitterApi.buildUserAgent(clientName, version);
        assertEquals(
                // client_name/client_version model/os_version (manufacturer;device;brand;product)
                String.format("%s/%s %s/%s (%s;%s;%s;%s)",
                        clientName, version, Build.MODEL, Build.VERSION.RELEASE, Build.MANUFACTURER,
                        Build.MODEL, Build.BRAND, Build.PRODUCT),
                userAgent);
    }

    @Test
    public void testNormalizeString() {
        assertEquals("Twitter", TwitterApi.normalizeString("Tw" + '\u00ED' + "tter\r\n\t"));
    }
}
