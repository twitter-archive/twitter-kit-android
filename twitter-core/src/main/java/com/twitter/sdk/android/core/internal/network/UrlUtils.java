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

package com.twitter.sdk.android.core.internal.network;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.TreeMap;

public final class UrlUtils {

    public static final String UTF8 = "UTF8";

    private UrlUtils() {
    }

    public static TreeMap<String, String> getQueryParams(URI uri, boolean decode) {
        return getQueryParams(uri.getRawQuery(), decode);
    }

    public static TreeMap<String, String> getQueryParams(String paramsString, boolean decode) {
        final TreeMap<String, String> params = new TreeMap<>();
        if (paramsString == null) {
            return params;
        }
        for (String nameValuePairString : paramsString.split("&")) {
            final String[] nameValuePair = nameValuePairString.split("=");
            if (nameValuePair.length == 2) {
                if (decode) {
                    params.put(urlDecode(nameValuePair[0]),
                            urlDecode(nameValuePair[1]));
                } else {
                    params.put(nameValuePair[0], nameValuePair[1]);
                }
            } else if (!TextUtils.isEmpty(nameValuePair[0])) {
                if (decode) {
                    params.put(urlDecode(nameValuePair[0]), "");
                } else {
                    params.put(nameValuePair[0], "");
                }
            }
        }
        return params;
    }

    public static String urlEncode(String s) {
        if (s == null) {
            return "";
        }
        try {
            return URLEncoder.encode(s, UrlUtils.UTF8);
        } catch (UnsupportedEncodingException unlikely) {
            throw new RuntimeException(unlikely.getMessage(), unlikely);
        }
    }

    public static String urlDecode(String s) {
        if (s == null) {
            return "";
        }
        try {
            return URLDecoder.decode(s, UrlUtils.UTF8);
        } catch (UnsupportedEncodingException unlikely) {
            throw new RuntimeException(unlikely.getMessage(), unlikely);
        }
    }

    /**
     * Percent encodes by doing the following:
     * 1) url encode string using UTF8
     * 2) apply additional encoding to string, replacing:
     *      "*" => "%2A"
     *      "+" => "%20"
     *      "%7E" => "~"
     *
     * @param s the string to encode
     * @return the encoded string
     */
    public static String percentEncode(String s) {
        if (s == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        final String encoded = UrlUtils.urlEncode(s);
        final int encodedLength = encoded.length();
        for (int i = 0; i < encodedLength; i++) {
            final char c = encoded.charAt(i);
            if (c == '*') {
                sb.append("%2A");
            } else if (c == '+') {
                sb.append("%20");
            } else if (c == '%' && (i + 2) < encodedLength &&
                    encoded.charAt(i + 1) == '7' &&
                    encoded.charAt(i + 2) == 'E') {
                sb.append('~');
                i += 2;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
