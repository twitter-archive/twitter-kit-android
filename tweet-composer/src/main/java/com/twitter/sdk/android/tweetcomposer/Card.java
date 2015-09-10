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

public class Card implements Serializable {
    public static final String APP_CARD_TYPE = "promo_image_app";
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

    public static Card createAppCard(Context context, Uri imageUri) {
        final String appName = getApplicationName(context);
        final String packageName = getPackageName(context);
        return new Card(APP_CARD_TYPE, imageUri.toString(), appName, packageName);
    }

    private static String getApplicationName(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
    }

    private static String getPackageName(Context context) {
        return context.getPackageName();
    }
}
