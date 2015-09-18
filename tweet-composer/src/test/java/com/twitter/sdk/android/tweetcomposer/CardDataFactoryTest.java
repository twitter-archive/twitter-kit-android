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

import android.test.AndroidTestCase;

import com.twitter.sdk.android.tweetcomposer.internal.CardData;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CardDataFactoryTest extends AndroidTestCase {
    private static final String TEST_IMAGE_URI = "image_uri";
    private static final String TEST_APP_NAME = "Example App";
    private static final String TEST_PACKAGE_NAME = "com.example.app";
    private static final Long TEST_MEDIA_ID = 123L;

    @Test
    public void testCreateAppCardData() {
        final Card card = new Card(Card.APP_CARD_TYPE, TEST_IMAGE_URI, TEST_APP_NAME,
                TEST_PACKAGE_NAME);
        final CardData data = CardDataFactory.createAppCardData(card, TEST_MEDIA_ID);
        assertEquals(CardDataFactory.APP_CARD_TYPE, data.card);
        assertEquals("media://" + TEST_MEDIA_ID, data.image);
        assertEquals(TEST_PACKAGE_NAME, data.appGooglePlayId);
        assertEquals("{}", data.cardData);
        assertEquals(CardDataFactory.APP_CARD_CTA_KEY, data.ctaKey);
        assertEquals("123", data.deviceId);
    }
}
