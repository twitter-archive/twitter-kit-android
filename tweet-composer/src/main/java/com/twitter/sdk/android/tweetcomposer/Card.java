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

import android.content.Context;
import android.net.Uri;

import java.io.Serializable;

/**
 * Card is a Twitter Card which may be attached to a Tweet.
 */
public class Card implements Serializable {
    public static final String APP_CARD_TYPE = "promo_image_app";
    final String cardType;
    final String imageUri;
    final String appName;
    final String appIPadId;
    final String appIPhoneId;
    final String appGooglePlayId;

    Card(String cardType, String imageUri, String appName, String appIPhoneId, String appIPadId,
         String appGooglePlayId) {
        this.cardType = cardType;
        this.imageUri = imageUri;
        this.appName = appName;
        this.appIPadId = appIPadId;
        this.appIPhoneId = appIPhoneId;
        this.appGooglePlayId = appGooglePlayId;
    }

    /**
     * @return the type of the Card.
     */
    public String getCardType() {
        return cardType;
    }

    /**
     * @return true if the Card is an App Card.
     */
    static boolean isAppCard(Card card) {
        return card != null && card.getCardType() != null
                && card.getCardType().equals(APP_CARD_TYPE);
    }

    /**
     * App Card Builder.
     */
    public static class AppCardBuilder {
        private String appName;
        private Uri imageUri;
        private String appIPhoneId;
        private String appIPadId;
        private String appGooglePlayId;

        /**
         * Constructs an AppCardBuilder with the Context package's Google Play id.
         *
         * @param context in the package of the Google Play application included in App Cards.
         */
        public AppCardBuilder(Context context) {
            appName = getApplicationName(context);
            appGooglePlayId = getPackageName(context);
        }

        /**
         * Sets the App Card image Uri of an image to show in the Card.
         * @param imageUri a Uri to a local media document or file. For image requirements, see
         * <a href="https://dev.twitter.com/rest/public/uploading-media">Uploading Media</a>
         */
        public AppCardBuilder imageUri(Uri imageUri) {
            this.imageUri = imageUri;
            return this;
        }

        /**
         * Sets the Apple App Store id for the promoted iOS app shown on iOS displays.
         * @param appIPhoneId Apple App Store id (e.g. Twitter App is 333903271). The id must
         * correspond to a published iPhone app for Card Tweets to link correctly.
         */
        public AppCardBuilder iPhoneId(String appIPhoneId) {
            this.appIPhoneId = appIPhoneId;
            return this;
        }

        /**
         * Sets the Apple App Store id for the promoted iPad app shown on iOS displays.
         * @param appIPadId Apple App Store id (e.g. Twitter App is 333903271). The id must
         * correspond to a published iPad app for Card Tweets to link correctly.
         */
        public AppCardBuilder iPadId(String appIPadId) {
            this.appIPadId = appIPadId;
            return this;
        }

        /**
         * Sets the Google Play Store package name of the promoted Android app shown on Android
         * displays. Overrides the default package name which is determined from the Builder
         * context.
         *
         * @param appGooglePlayId Google Play Store package (e.g. "com.twitter.android"). The
         * package must correspond to a published app on Google Play for Card Tweets to link
         * correctly.
         */
        public AppCardBuilder googlePlayId(String appGooglePlayId) {
            this.appGooglePlayId = appGooglePlayId;
            return this;
        }

        /**
         * Builds a new App Card for the ComposerActivity.
         * @return an App Card. See <a href="https://dev.twitter.com/cards/types/app">Card Types</a>
         */
        public Card build() {
            if (imageUri == null) {
                throw new IllegalStateException("App Card requires a non-null imageUri");
            }
            return new Card(APP_CARD_TYPE, imageUri.toString(), appName, appIPhoneId, appIPadId,
                    appGooglePlayId);
        }
    }

    private static String getApplicationName(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
    }

    private static String getPackageName(Context context) {
        return context.getPackageName();
    }
}
