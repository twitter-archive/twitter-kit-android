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

package com.twitter.sdk.android.core.internal.oauth;

import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.internal.network.UrlUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import okio.ByteString;

class OAuth1aParameters {
    private static final String VERSION = "1.0";
    private static final String SIGNATURE_METHOD = "HMAC-SHA1";

    /**
     * Secure random number generator to sign requests.
     */
    private static final SecureRandom RAND = new SecureRandom();

    private final TwitterAuthConfig authConfig;
    private final TwitterAuthToken authToken;
    private final String callback;
    private final String method;
    private final String url;
    private final Map<String, String> postParams;

    OAuth1aParameters(TwitterAuthConfig authConfig, TwitterAuthToken authToken,
            String callback, String method, String url, Map<String, String> postParams) {
        this.authConfig = authConfig;
        this.authToken = authToken;
        this.callback = callback;
        this.method = method;
        this.url = url;
        this.postParams = postParams;
    }

    /**
     * @return the authorization header for inclusion in HTTP request headers for a request token.
     */
    public String getAuthorizationHeader() {
        final String nonce = getNonce();
        final String timestamp = getTimestamp();
        final String signatureBase = constructSignatureBase(nonce, timestamp);
        final String signature = calculateSignature(signatureBase);
        return constructAuthorizationHeader(nonce, timestamp, signature);
    }

    private String getNonce() {
        return String.valueOf(System.nanoTime()) + String.valueOf(Math.abs(RAND.nextLong()));
    }

    private String getTimestamp() {
        final long secondsFromEpoch = System.currentTimeMillis() / 1000;
        return Long.toString(secondsFromEpoch);
    }

    String constructSignatureBase(String nonce, String timestamp) {
        // Get query parameters from request.
        final URI uri = URI.create(url);
        final TreeMap<String, String> params = UrlUtils.getQueryParams(uri, true);
        if (postParams != null) {
            params.putAll(postParams);
        }

        // Add OAuth parameters.
        if (callback != null) {
            params.put(OAuthConstants.PARAM_CALLBACK, callback);
        }
        params.put(OAuthConstants.PARAM_CONSUMER_KEY, authConfig.getConsumerKey());
        params.put(OAuthConstants.PARAM_NONCE, nonce);
        params.put(OAuthConstants.PARAM_SIGNATURE_METHOD, SIGNATURE_METHOD);
        params.put(OAuthConstants.PARAM_TIMESTAMP, timestamp);
        if (authToken != null && authToken.token != null) {
            params.put(OAuthConstants.PARAM_TOKEN, authToken.token);
        }
        params.put(OAuthConstants.PARAM_VERSION, VERSION);

        // Construct the signature base.
        final String baseUrl = uri.getScheme() + "://" + uri.getHost() + uri.getPath();
        final StringBuilder sb = new StringBuilder()
                .append(method.toUpperCase(Locale.ENGLISH))
                .append('&')
                .append(UrlUtils.percentEncode(baseUrl))
                .append('&')
                .append(getEncodedQueryParams(params));
        return sb.toString();
    }

    private String getEncodedQueryParams(TreeMap<String, String> params) {
        final StringBuilder paramsBuf = new StringBuilder();
        final int numParams = params.size();
        int current = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            paramsBuf.append(UrlUtils.percentEncode(UrlUtils.percentEncode(entry.getKey())))
                    .append("%3D")
                    .append(UrlUtils.percentEncode(UrlUtils.percentEncode(entry.getValue())));
            current += 1;
            if (current < numParams) {
                paramsBuf.append("%26");
            }
        }
        return paramsBuf.toString();
    }

    String calculateSignature(String signatureBase) {
        try {
            final String key = getSigningKey();
            // Calculate the signature by passing both the signature base and signing key to the
            // HMAC-SHA1 hashing algorithm
            final byte[] signatureBaseBytes = signatureBase.getBytes(UrlUtils.UTF8);
            final byte[] keyBytes = key.getBytes(UrlUtils.UTF8);
            final SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");
            final Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKey);
            final byte[] signatureBytes = mac.doFinal(signatureBaseBytes);
            return ByteString.of(signatureBytes, 0, signatureBytes.length).base64();
        } catch (InvalidKeyException e) {
            Twitter.getLogger().e(TwitterCore.TAG, "Failed to calculate signature", e);
            return "";
        } catch (NoSuchAlgorithmException e) {
            Twitter.getLogger().e(TwitterCore.TAG, "Failed to calculate signature", e);
            return "";
        } catch (UnsupportedEncodingException e) {
            Twitter.getLogger().e(TwitterCore.TAG, "Failed to calculate signature", e);
            return "";
        }
    }

    private String getSigningKey() {
        final String tokenSecret = authToken != null ? authToken.secret : null;
        return new StringBuilder()
                .append(UrlUtils.urlEncode(authConfig.getConsumerSecret()))
                .append('&')
                .append(UrlUtils.urlEncode(tokenSecret))
                .toString();
    }

    String constructAuthorizationHeader(String nonce, String timestamp, String signature) {
        final StringBuilder sb = new StringBuilder("OAuth");
        appendParameter(sb, OAuthConstants.PARAM_CALLBACK, callback);
        appendParameter(sb, OAuthConstants.PARAM_CONSUMER_KEY, authConfig.getConsumerKey());
        appendParameter(sb, OAuthConstants.PARAM_NONCE, nonce);
        appendParameter(sb, OAuthConstants.PARAM_SIGNATURE, signature);
        appendParameter(sb, OAuthConstants.PARAM_SIGNATURE_METHOD, SIGNATURE_METHOD);
        appendParameter(sb, OAuthConstants.PARAM_TIMESTAMP, timestamp);
        final String token = authToken != null ? authToken.token : null;
        appendParameter(sb, OAuthConstants.PARAM_TOKEN, token);
        appendParameter(sb, OAuthConstants.PARAM_VERSION, VERSION);
        // Remove the extra ',' at the end.
        return sb.substring(0, sb.length() - 1);
    }

    private void appendParameter(StringBuilder sb, String name, String value) {
        if (value != null) {
            sb.append(' ')
                    .append(UrlUtils.percentEncode(name)).append("=\"")
                    .append(UrlUtils.percentEncode(value)).append("\",");
        }
    }
}
