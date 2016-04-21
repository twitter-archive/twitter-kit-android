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

import com.twitter.sdk.android.tweetcomposer.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CardDataTest {
    private static final String TEST_CARD = "card_type";
    private static final String TEST_DESCRIPTION = "description";
    private static final String TEST_SITE = "site";
    private static final String TEST_COUNTRY = "usa";
    private static final String TEST_APP_CARD = "awesome_image_app";
    private static final String TEST_IMAGE = "media://12345";
    private static final String TEST_GOOGLE_PLAY_ID = "com.example.app";
    private static final String TEST_CARD_DATA = "{}";
    private static final String TEST_CALL_TO_ACTION = "Click Now";
    private static final String TEST_DEVICE_ID = "0123456789";
    private static final String CARD_JSON = "{\"twitter:card\":\"card_type\"," +
        "\"twitter:image\":\"media://12345\",\"twitter:site\":\"site\"," +
        "\"twitter:description\":\"description\",\"twitter:app:country\":\"usa\"}";
    private static final String APP_CARD_JSON = "{\"twitter:card\":\"awesome_image_app\"," +
        "\"twitter:image\":\"media://12345\",\"twitter:card_data\":\"{}\"," +
        "\"twitter:text:cta\":\"Click Now\",\"twitter:text:did_value\":\"0123456789\"," +
        "\"twitter:app:id:googleplay\":\"com.example.app\"}";

    @Test
    public void testBuilder() {
        final CardData data = new CardData.Builder()
                .card(TEST_CARD)
                .image(TEST_IMAGE)
                .appGooglePlayId(TEST_GOOGLE_PLAY_ID)
                .cardData(TEST_CARD_DATA)
                .callToAction(TEST_CALL_TO_ACTION)
                .deviceId(TEST_DEVICE_ID)
                .build();
        assertEquals(TEST_CARD, data.card);
        assertEquals(TEST_IMAGE, data.image);
        assertEquals(TEST_GOOGLE_PLAY_ID, data.appGooglePlayId);
        assertEquals(TEST_CARD_DATA, data.cardData);
        assertEquals(TEST_CALL_TO_ACTION, data.callToAction);
        assertEquals(TEST_DEVICE_ID, data.deviceId);
    }

    @Test
    public void testCardDataToString() {
        final CardData data = new CardData.Builder()
                .card(TEST_CARD)
                .description(TEST_DESCRIPTION)
                .site(TEST_SITE)
                .appCountry(TEST_COUNTRY)
                .image(TEST_IMAGE)
                .build();
        assertEquals(CARD_JSON, data.toString());
    }

    @Test
    public void testAppCardDataToString() {
        final CardData data = new CardData.Builder()
                .card(TEST_APP_CARD)
                .image(TEST_IMAGE)
                .appGooglePlayId(TEST_GOOGLE_PLAY_ID)
                .cardData(TEST_CARD_DATA)
                .callToAction(TEST_CALL_TO_ACTION)
                .deviceId(TEST_DEVICE_ID)
                .build();
        assertEquals(APP_CARD_JSON, data.toString());
    }
}
