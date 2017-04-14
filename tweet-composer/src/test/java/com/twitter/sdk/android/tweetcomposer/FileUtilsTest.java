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

package com.twitter.sdk.android.tweetcomposer;

import android.net.Uri;
import android.webkit.MimeTypeMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowMimeTypeMap;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class FileUtilsTest {
    ShadowMimeTypeMap mimeTypeMap;

    @Before
    public void setUp() {
        final ShadowMimeTypeMap mimeTypeMap = (ShadowMimeTypeMap) ShadowExtractor
                .extract(MimeTypeMap.getSingleton());
        mimeTypeMap.addExtensionMimeTypMapping("jpg", "image/jpeg");
        mimeTypeMap.addExtensionMimeTypMapping("jpeg", "image/jpeg");
        mimeTypeMap.addExtensionMimeTypMapping("png", "image/png");
        this.mimeTypeMap = mimeTypeMap;
    }

    @Test
    public void testIsMediaDocumentAuthority() {
        final Uri uri = new Uri.Builder()
                .scheme("content")
                .authority("com.android.providers.media.documents")
                .path("image%3A59161")
                .build();
        assertTrue(FileUtils.isMediaDocumentAuthority(uri));
    }

    @Test
    public void testIsContentScheme() {
        final Uri uri = new Uri.Builder().scheme("content").build();
        assertTrue(FileUtils.isContentScheme(uri));
    }

    @Test
    public void testIsFileScheme() {
        final Uri uri = new Uri.Builder().scheme("file").build();
        assertTrue(FileUtils.isFileScheme(uri));
    }

    @Test
    public void testGetMimeType() {
        assertEquals("image/png", FileUtils.getMimeType(new File("file.png")));
        assertEquals("image/jpeg", FileUtils.getMimeType(new File("file.jpeg")));
        assertEquals("image/jpeg", FileUtils.getMimeType(new File("file.jpeg")));
        assertEquals("application/octet-stream", FileUtils.getMimeType(new File("")));
    }

    @Test
    public void testExtensionToMimeType() {
        assertEquals("image/png", mimeTypeMap.getMimeTypeFromExtension("png"));
        assertEquals("image/jpeg", mimeTypeMap.getMimeTypeFromExtension("jpg"));
        assertEquals("image/jpeg", mimeTypeMap.getMimeTypeFromExtension("jpeg"));
        assertEquals(null, mimeTypeMap.getMimeTypeFromExtension(""));
    }

    @Test
    public void testGetExtension() {
        assertEquals("", FileUtils.getExtension(""));
        assertEquals("", FileUtils.getExtension("file"));
        assertEquals("", FileUtils.getExtension("file."));
        assertEquals("png", FileUtils.getExtension("file.png"));
        assertEquals("jpg", FileUtils.getExtension("file.jpg"));
    }
}
