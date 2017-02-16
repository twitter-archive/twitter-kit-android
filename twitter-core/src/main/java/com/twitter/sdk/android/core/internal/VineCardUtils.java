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

package com.twitter.sdk.android.core.internal;

import com.twitter.sdk.android.core.models.Card;
import com.twitter.sdk.android.core.models.ImageValue;
import com.twitter.sdk.android.core.models.UserValue;

public class VineCardUtils {
    public static final String PLAYER_CARD = "player";
    public static final String VINE_CARD = "vine";
    public static final long VINE_USER_ID = 586671909;

    private VineCardUtils() {}

    public static boolean isVine(Card card) {
        return (PLAYER_CARD.equals(card.name) || VINE_CARD.equals(card.name)) && isVineUser(card);
    }

    private static boolean isVineUser(Card card) {
        final UserValue user = card.bindingValues.get("site");
        try {
            if (user != null && Long.parseLong(user.idStr) == VINE_USER_ID) {
                return true;
            }
        } catch (NumberFormatException ex) {
            return false;
        }

        return false;
    }

    public static String getPublisherId(Card card) {
        final UserValue user_value = card.bindingValues.get("site");
        return user_value.idStr;
    }

    public static String getStreamUrl(Card card) {
        return card.bindingValues.get("player_stream_url");
    }

    public static ImageValue getImageValue(Card card) {
        return card.bindingValues.get("player_image");
    }
}
