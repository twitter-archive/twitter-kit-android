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

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class CardData {
    private static Serializer serializer;

    private CardData(String card, String image, String appId, String cardData, String cta,
                     String deviceId) {
        this.card = card;
        this.image = image;
        this.appIdGooglePlay = appId;
        this.cardData = cardData;
        this.cta = cta;
        this.deviceId = deviceId;
    }

    @SerializedName("twitter:card")
    public final String card;

    @SerializedName("twitter:image")
    public final String image;

    @SerializedName("twitter:app:id:googleplay")
    public final String appIdGooglePlay;

    @SerializedName("twitter:card_data")
    public final String cardData;

    @SerializedName("twitter:text:cta")
    public final String cta;

    @SerializedName("twitter:text:did_value")
    public final String deviceId;

    Serializer getSerializer() {
        if (serializer == null) {
            serializer = new Serializer();
        }
        return serializer;
    }

    @Override
    public String toString() {
        // Required bc the Cards API accepts form-urlencoded requests with nested CardData JSON
        // Retrofit converts @Fields to strings, without using registered Converters.
        // https://github.com/square/retrofit/blob/master/retrofit/src/main/java/retrofit/http/Field.java#L28
        return getSerializer().serialize(this);
    }

    static class Serializer {

        private final Gson gson;

        Serializer() {
            this.gson = new Gson();
        }

        String serialize(CardData data) {
            return this.gson.toJson(data);
        }
    }

    public static class Builder {
        private String card;
        private String image;
        private String appIdGooglePlay;
        private String cardData;
        private String cta;
        private String deviceId;

        public Builder card(String card) {
            this.card = card;
            return this;
        }

        public Builder image(String image) {
            this.image = image;
            return this;
        }

        public Builder appIdGooglePlay(String appId) {
            this.appIdGooglePlay = appId;
            return this;
        }

        public Builder cardData(String data) {
            this.cardData = data;
            return this;
        }

        public Builder cta(String cta) {
            this.cta = cta;
            return this;
        }

        public Builder deviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public CardData build() {
            return new CardData(card, image, appIdGooglePlay, cardData, cta, deviceId);
        }
    }
}
