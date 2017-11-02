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

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Utilities for resolving various Uri's to file paths and MIME types.
 */
class FileUtils {
    private static final String MEDIA_SCHEME = "com.android.providers.media.documents";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && isMediaDocumentAuthority(uri)) {
            final String documentId = DocumentsContract.getDocumentId(uri); // e.g. "image:1234"
            final String[] parts = documentId.split(":");
            final String type = parts[0];

            final Uri contentUri;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else {
                // reject video or audio documents
                return null;
            }

            // query content resolver for MediaStore id column
            final String selection = "_id=?";
            final String[] args = new String[] {
                    parts[1]
            };
            return resolveFilePath(context, contentUri, selection, args);
        } else if (isContentScheme(uri)) {
            return resolveFilePath(context, uri, null, null);
        } else if (isFileScheme(uri)) {
            return uri.getPath();
        }
        return null;
    }

    static boolean isMediaDocumentAuthority(Uri uri) {
        return MEDIA_SCHEME.equalsIgnoreCase(uri.getAuthority());
    }

    static boolean isContentScheme(Uri uri) {
        return ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme());
    }

    static boolean isFileScheme(Uri uri) {
        return ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme());
    }

    static String resolveFilePath(Context context, Uri uri, String selection, String[] args) {
        Cursor cursor = null;
        final String[] projection = {MediaStore.Images.Media.DATA};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, args, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int i = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(i);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @return The MIME type for the given file.
     */
    static String getMimeType(File file) {
        final String ext = getExtension(file.getName());
        if (!TextUtils.isEmpty(ext)) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        }
        // default from https://dev.twitter.com/rest/public/uploading-media
        return "application/octet-stream";
    }

    /**
     * @return the extension of the given file name, excluding the dot. For example, "png", "jpg".
     */
    static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        final int i = filename.lastIndexOf(".");
        return i < 0 ? "" : filename.substring(i + 1);
    }
}
