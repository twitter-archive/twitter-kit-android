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
    public static final String APP_CARD_TYPE = "APP_CARD";
    final String cardType;
    final String imageUri;
    final String packageName;
    final String appName;

    Card(String cardType, String imageUri, String appName, String packageName) {
        this.cardType = cardType;
        this.imageUri = imageUri;
        this.packageName = packageName;
        this.appName = appName;
    }

    /**
     * Creates a new App Card.
     * @param context Context from the package which the App card should use.
     * @param imageUri the Uri of an image to be shown in the Card. See
     *        <a href="https://dev.twitter.com/rest/public/uploading-media">Uploading Media</a>
     * @return an App Card. See <a href="https://dev.twitter.com/cards/types/app">Card Types</a>
     */
    public static Card createAppCard(Context context, Uri imageUri) {
        final String appName = getApplicationName(context);
        final String packageName = getPackageName(context);
        return new Card(APP_CARD_TYPE, imageUri.toString(), appName, packageName);
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

    private static String getApplicationName(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
    }

    private static String getPackageName(Context context) {
        return context.getPackageName();
    }
}
