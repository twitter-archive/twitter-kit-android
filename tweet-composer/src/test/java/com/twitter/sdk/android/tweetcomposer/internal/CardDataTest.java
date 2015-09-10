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

package com.twitter.sdk.android.tweetcomposer.internal;

import android.test.AndroidTestCase;

import com.twitter.sdk.android.tweetcomposer.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CardDataTest extends AndroidTestCase {
    private static final String TEST_CARD = "awesome_image_app";
    private static final String TEST_IMAGE = "media://12345";
    private static final String TEST_APP_ID = "com.example.app";
    private static final String TEST_CARD_DATA = "{}";
    private static final String TEST_CALL_TO_ACTION = "Click Now";
    private static final String TEST_DEVICE_ID = "0123456789";
    private static final String EXPECTED_JSON = "{\"twitter:card\":\"awesome_image_app\"," +
        "\"twitter:image\":\"media://12345\",\"twitter:app:id:googleplay\":\"com.example.app\"," +
        "\"twitter:card_data\":\"{}\",\"twitter:text:cta\":\"Click Now\"," +
        "\"twitter:text:did_value\":\"0123456789\"}";

    @Test
    public void testBuilder() {
        final CardData data = new CardData.Builder()
                .card(TEST_CARD)
                .image(TEST_IMAGE)
                .appIdGooglePlay(TEST_APP_ID)
                .cardData(TEST_CARD_DATA)
                .cta(TEST_CALL_TO_ACTION)
                .deviceId(TEST_DEVICE_ID)
                .build();
        assertEquals(TEST_CARD, data.card);
        assertEquals(TEST_IMAGE, data.image);
        assertEquals(TEST_APP_ID, data.appIdGooglePlay);
        assertEquals(TEST_CARD_DATA, data.cardData);
        assertEquals(TEST_CALL_TO_ACTION, data.cta);
        assertEquals(TEST_DEVICE_ID, data.deviceId);
    }

    @Test
    public void testToString() {
        final CardData data = new CardData.Builder()
                .card(TEST_CARD)
                .image(TEST_IMAGE)
                .appIdGooglePlay(TEST_APP_ID)
                .cardData(TEST_CARD_DATA)
                .cta(TEST_CALL_TO_ACTION)
                .deviceId(TEST_DEVICE_ID)
                .build();
        assertEquals(EXPECTED_JSON, data.toString());
    }
}
