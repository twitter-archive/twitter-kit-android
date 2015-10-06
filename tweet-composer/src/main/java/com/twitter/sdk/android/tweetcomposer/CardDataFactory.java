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

import com.twitter.sdk.android.tweetcomposer.internal.CardData;

/**
 * CardDataFactory methods return CardData models for upload to the CardService.
 */
class CardDataFactory {
    static final String APP_CARD_TYPE = "promo_image_app";
    static final String APP_CARD_CTA_KEY = "open";
    private static final String MEDIA_SCHEME = "media://";

    /**
     * @return App Card CardData instance.
     */
    static CardData createAppCardData(Card card, Long mediaId, String advertisingId) {
        return new CardData.Builder()
                .card(APP_CARD_TYPE)
                .image(getCardMedia(mediaId))
                .appIPhoneId(card.appIPhoneId)
                .appIPadId(card.appIPadId)
                .appGooglePlayId(card.appGooglePlayId)
                .cardData("{}")
                .ctaKey(APP_CARD_CTA_KEY)
                .deviceId(advertisingId)
                .build();
    }

    /**
     * @return Media image path for a Card. For example "media://1234".
     */
    static String getCardMedia(Long mediaId) {
        return MEDIA_SCHEME + Long.toString(mediaId);
    }
}
