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

/**
 * CardData for upload to the internal Twitter CardService.
 */
public class CardData {
    private static Serializer serializer;

    private CardData(String card, String image, String site, String description, String cardData,
                     String callToAction, String ctaKey, String deviceId, String appIPhoneId,
                     String appIPadId, String appGooglePlayId, String appCountry) {
        this.card = card;
        this.image = image;
        this.site = site;
        this.description = description;
        this.cardData = cardData;
        this.callToAction = callToAction;
        this.ctaKey = ctaKey;
        this.deviceId = deviceId;
        this.appIPhoneId = appIPhoneId;
        this.appIPadId = appIPadId;
        this.appGooglePlayId = appGooglePlayId;
        this.appCountry = appCountry;
    }

    @SerializedName("twitter:card")
    public final String card;

    @SerializedName("twitter:image")
    public final String image;

    @SerializedName("twitter:site")
    public final String site;

    @SerializedName("twitter:description")
    public final String description;

    @SerializedName("twitter:card_data")
    public final String cardData;

    @SerializedName("twitter:text:cta")
    public final String callToAction;

    @SerializedName("twitter:cta_key")
    public final String ctaKey;

    @SerializedName("twitter:text:did_value")
    public final String deviceId;

    @SerializedName("twitter:app:id:iphone")
    public final String appIPhoneId;

    @SerializedName("twitter:app:id:ipad")
    public final String appIPadId;

    @SerializedName("twitter:app:id:googleplay")
    public final String appGooglePlayId;

    @SerializedName("twitter:app:country")
    public final String appCountry;

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

    /**
     * CardData Builder.
     */
    public static class Builder {
        private String card;
        private String image;
        private String site;
        private String description;
        private String cardData;
        private String callToAction;
        private String ctaKey;
        private String deviceId;
        private String appIPhoneId;
        private String appIPadId;
        private String appGooglePlayId;
        private String appCountry;

        public Builder card(String card) {
            this.card = card;
            return this;
        }

        public Builder image(String image) {
            this.image = image;
            return this;
        }

        public Builder site(String site) {
            this.site = site;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder cardData(String data) {
            this.cardData = data;
            return this;
        }

        public Builder callToAction(String callToAction) {
            this.callToAction = callToAction;
            return this;
        }

        public Builder ctaKey(String ctaKey) {
            this.ctaKey = ctaKey;
            return this;
        }

        public Builder deviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder appIPhoneId(String appIPhoneId) {
            this.appIPhoneId = appIPhoneId;
            return this;
        }

        public Builder appIPadId(String appIPadId) {
            this.appIPadId = appIPadId;
            return this;
        }

        public Builder appGooglePlayId(String appGooglePlayId) {
            this.appGooglePlayId = appGooglePlayId;
            return this;
        }

        public Builder appCountry(String appCountry) {
            this.appCountry = appCountry;
            return this;
        }

        public CardData build() {
            return new CardData(card, image, site, description, cardData, callToAction,
                    ctaKey, deviceId, appIPhoneId, appIPadId, appGooglePlayId, appCountry);
        }
    }
}
