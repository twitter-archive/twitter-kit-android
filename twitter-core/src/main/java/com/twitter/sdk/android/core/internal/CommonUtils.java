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

import android.content.Context;
import android.content.res.Resources;

import com.twitter.sdk.android.core.Twitter;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class CommonUtils {
    static final String TRACE_ENABLED_RESOURCE_NAME = "com.twitter.sdk.android.TRACE_ENABLED";
    static final boolean TRACE_ENABLED_DEFAULT = false;
    private static Boolean clsTrace;

    public static String streamToString(InputStream is) throws IOException {
        // Previous code was running into this: http://code.google.com/p/android/issues/detail?id=14562
        // on Android 2.3.3. The below code, cribbed from: http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
        // does not exhibit that problem.
        final java.util.Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * Copies all available data from the {@link InputStream} into the {@link OutputStream}, using the
     * provided <code>buffer</code>. Neither stream is closed during this call.
     */
    public static void copyStream(InputStream is, OutputStream os, byte[] buffer)
            throws IOException {
        int count;
        while ((count = is.read(buffer)) != -1) {
            os.write(buffer, 0, count);
        }
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Closes a {@link Closeable}, ignoring any {@link IOException}s raised in the process.
     * Does nothing if the {@link Closeable} is <code>null</code>.
     *
     * @param c {@link Closeable} to close
     */
    public static void closeOrLog(Closeable c, String message) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                Twitter.getLogger().e(Twitter.TAG, message, e);
            }
        }
    }

    /**
     * Uses the given context's application icon to retrieve the package name for the resources for the context
     * This package name only differs from context.getPackageName() when using aapt parameter --rename-manifest-package
     * @param context Context to get resource package name from
     * @return String representing the package name of the resources for the given context
     */
    static String getResourcePackageName(Context context) {
        // There should always be an icon
        // http://developer.android.com/guide/topics/manifest/application-element.html#icon
        // safety check anyway to prevent exceptions
        final int iconId = context.getApplicationContext().getApplicationInfo().icon;
        if (iconId > 0) {
            return context.getResources().getResourcePackageName(iconId);
        } else {
            return context.getPackageName();
        }
    }

    static int getResourcesIdentifier(Context context, String key, String resourceType) {
        final Resources resources = context.getResources();
        return resources.getIdentifier(key, resourceType, getResourcePackageName(context));
    }

    /**
     * <p>
     * Gets a value for a boolean resource by its name. If a key is not present, the provided default value
     * will be returned.
     * </p>
     * <p>
     * Tries to look up a boolean value two ways:
     * <ol>
     * <li>As a <code>bool</code> resource. A discovered value is returned as-is.</li>
     * <li>As a <code>string</code> resource. A discovered value is turned into a boolean with
     * {@link Boolean#parseBoolean(String)} before being returned.</li>
     * </ol>
     * </p>
     *
     * @param context {@link Context} to use when accessing resources
     * @param key {@link String} name of the boolean value to look up
     * @param defaultValue value to be returned if the specified resource could be not be found.
     * @return {@link String} value of the specified property, or an empty string if it could not be found.
     */
    public static boolean getBooleanResourceValue(Context context, String key,
                                                  boolean defaultValue) {
        if (context != null) {
            final Resources resources = context.getResources();

            if (resources != null) {
                int id = getResourcesIdentifier(context, key, "bool");

                if (id > 0) {
                    return resources.getBoolean(id);
                }

                id = getResourcesIdentifier(context, key, "string");

                if (id > 0) {
                    return Boolean.parseBoolean(context.getString(id));
                }
            }
        }

        return defaultValue;
    }

    /**
     * <p>
     * Gets a value for a string resource by its name. If a key is not present, the provided default value
     * will be returned.
     * </p>
     *
     * @param context {@link Context} to use when accessing resources
     * @param key {@link String} name of the boolean value to look up
     * @param defaultValue value to be returned if the specified resource could be not be found.
     * @return {@link String} value of the specified property, or an empty string if it could not be found.
     */
    public static String getStringResourceValue(Context context, String key, String defaultValue) {
        if (context != null) {
            final Resources resources = context.getResources();

            if (resources != null) {
                final int id = getResourcesIdentifier(context, key, "string");

                if (id > 0) {
                    return resources.getString(id);
                }
            }
        }

        return defaultValue;
    }

    /**
     */
    static boolean isClsTrace(Context context) {
        // Since the cached value is a Boolean object, it can be null. If it's null, load the value
        // and cache it.
        if (clsTrace == null) {
            clsTrace = getBooleanResourceValue(context, TRACE_ENABLED_RESOURCE_NAME,
                    TRACE_ENABLED_DEFAULT);
        }

        return clsTrace;
    }

    /**
     * Used internally to log only when the com.twitter.sdk.android.TRACE_ENABLED resource value
     * is set to true.  When it is, this API passes processing to the log API.
     */
    public static void logControlled(Context context, String msg){
        if (isClsTrace(context)){
            Twitter.getLogger().d(Twitter.TAG, msg);
        }
    }

    /**
     * Used internally to log errors only when the com.twitter.sdk.android.TRACE_ENABLED resource
     * value is set to true.  When it is, this API passes processing to the logError API.
     */
    public static void logControlledError(Context context, String msg, Throwable tr){
        if (isClsTrace(context)){
            Twitter.getLogger().e(Twitter.TAG, msg);
        }
    }

    /**
     * Used internally to log only when the com.twitter.sdk.android.TRACE_ENABLED resource value
     * is set to true.  When it is, this API passes processing to the log API.
     */
    public static void logControlled(Context context, int level, String tag, String msg) {
        if (isClsTrace(context)) {
            Twitter.getLogger().log(level, Twitter.TAG, msg);
        }
    }

    /**
     *  If {@link Twitter#isDebug()}, throws an IllegalStateException,
     *  else logs a warning.
     *
     * @param logTag the log tag to use for logging
     * @param errorMsg the error message
     */
    public static void logOrThrowIllegalStateException(String logTag, String errorMsg) {
        if (Twitter.isDebug()) {
            throw new IllegalStateException(errorMsg);
        } else {
            Twitter.getLogger().w(logTag, errorMsg);
        }
    }
}
