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

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@SuppressWarnings("checkstyle:linelength")
public class OAuth1aParametersTest  {

    private static class TestParameters {
        public final String method;
        public final String url;
        public final Map<String, String> postParams;
        public final String consumerKey;
        public final String consumerSecret;
        public final String callback;
        public final String nonce;
        public final String timestamp;
        public final String token;
        public final String tokenSecret;
        public final String expectedSignatureBase;
        public final String expectedSignature;
        public final String expectedAuthHeader;

        TestParameters(String method, String url, Map<String, String> postParams,
                String consumerKey, String consumerSecret, String callback, String nonce,
                String timestamp, String token, String tokenSecret,
                String expectedSignatureBase, String expectedSignature,
                String expectedAuthHeader) {
            this.method = method;
            this.url = url;
            this.postParams = postParams;
            this.consumerKey = consumerKey;
            this.consumerSecret = consumerSecret;
            this.callback = callback;
            this.nonce = nonce;
            this.timestamp = timestamp;
            this.token = token;
            this.tokenSecret = tokenSecret;
            this.expectedSignatureBase = expectedSignatureBase;
            this.expectedSignature = expectedSignature;
            this.expectedAuthHeader = expectedAuthHeader;
        }
    }

    private static final TestParameters[] TEST_PARAMETERS = new TestParameters[] {
            // tokens/secrets (disabled) from https://dev.twitter.com/web/sign-in/implementing
            new TestParameters(
                    "POST",
                    "https://api.twitter.com/oauth/request_token",
                    null,
                    "cChZNFj6T5R0TigYB9yd1w",
                    "L8qq9PZyRg6ieKGEKhZolGC0vJWLw8iEJ88DRdyOg",
                    "http://localhost/sign-in-with-twitter/",
                    "ea9ec8429b68d6b77cd5600adbbb0456",
                    "1318467427",
                    null,
                    null,
                    "POST&https%3A%2F%2Fapi.twitter.com%2Foauth%2Frequest_token&oauth_callback%3Dhttp%253A%252F%252Flocalhost%252Fsign-in-with-twitter%252F%26oauth_consumer_key%3DcChZNFj6T5R0TigYB9yd1w%26oauth_nonce%3Dea9ec8429b68d6b77cd5600adbbb0456%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1318467427%26oauth_version%3D1.0",
                    "F1Li3tvehgcraF8DMJ7OyxO4w9Y=",
                    "OAuth oauth_callback=\"http%3A%2F%2Flocalhost%2Fsign-in-with-twitter%2F\", oauth_consumer_key=\"cChZNFj6T5R0TigYB9yd1w\", oauth_nonce=\"ea9ec8429b68d6b77cd5600adbbb0456\", oauth_signature=\"F1Li3tvehgcraF8DMJ7OyxO4w9Y%3D\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"1318467427\", oauth_version=\"1.0\""),
            new TestParameters(
                    "POST",
                    "https://api.twitter.com/1/statuses/update.json?include_entities=true",
                    new HashMap<String, String>() {
                        {
                            put("status", "Hello Ladies + Gentlemen, a signed OAuth request!");
                        }
                    },
                    "xvz1evFS4wEEPTGEFPHBog",
                    "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw",
                    null,
                    "kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg",
                    "1318622958",
                    "370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb",
                    "LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE",
                    "POST&https%3A%2F%2Fapi.twitter.com%2F1%2Fstatuses%2Fupdate.json&include_entities%3Dtrue%26oauth_consumer_key%3Dxvz1evFS4wEEPTGEFPHBog%26oauth_nonce%3DkYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1318622958%26oauth_token%3D370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb%26oauth_version%3D1.0%26status%3DHello%2520Ladies%2520%252B%2520Gentlemen%252C%2520a%2520signed%2520OAuth%2520request%2521",
                    "tnnArxj06cWHq44gCs1OSKk/jLY=",
                    "OAuth oauth_consumer_key=\"xvz1evFS4wEEPTGEFPHBog\", oauth_nonce=\"kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg\", oauth_signature=\"tnnArxj06cWHq44gCs1OSKk%2FjLY%3D\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"1318622958\", oauth_token=\"370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb\", oauth_version=\"1.0\"")
    };

    @Test
    public void testConstructSignatureBase() {
        for (TestParameters testParameters : TEST_PARAMETERS) {
            final OAuth1aParameters oAuth1aParameters = toOAuth1aParameters(testParameters);
            final String signatureBase = oAuth1aParameters.constructSignatureBase(
                    testParameters.nonce, testParameters.timestamp);
            assertEquals(testParameters.expectedSignatureBase, signatureBase);
        }
    }

    private OAuth1aParameters toOAuth1aParameters(TestParameters testParameters) {
        final TwitterAuthToken authToken;
        if (testParameters.token != null && testParameters.tokenSecret != null) {
            authToken = new TwitterAuthToken(testParameters.token, testParameters.tokenSecret);
        } else {
            authToken = null;
        }
        return new OAuth1aParameters(
                new TwitterAuthConfig(testParameters.consumerKey, testParameters.consumerSecret),
                authToken,
                testParameters.callback, testParameters.method, testParameters.url,
                testParameters.postParams);
    }

    @Test
    public void testCalculateSignature() {
        for (TestParameters testParameters : TEST_PARAMETERS) {
            final OAuth1aParameters oAuth1aParameters = toOAuth1aParameters(testParameters);
            final String signature
                    = oAuth1aParameters.calculateSignature(testParameters.expectedSignatureBase);
            assertEquals(testParameters.expectedSignature, signature);
        }
    }

    @Test
    public void testConstructAuthorizationHeader() {
        for (TestParameters testParameters : TEST_PARAMETERS) {
            final OAuth1aParameters oAuth1aParameters = toOAuth1aParameters(testParameters);
            final String authHeader = oAuth1aParameters.constructAuthorizationHeader(
                    testParameters.nonce, testParameters.timestamp,
                    testParameters.expectedSignature);
            assertEquals(testParameters.expectedAuthHeader, authHeader);
        }
    }
}
