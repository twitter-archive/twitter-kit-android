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

package com.twitter.sdk.android.core;

import com.twitter.sdk.android.core.internal.VineCardUtils;
import com.twitter.sdk.android.core.models.BindingValues;
import com.twitter.sdk.android.core.models.Card;
import com.twitter.sdk.android.core.models.UserValue;

import java.util.HashMap;
import java.util.Map;

public final class TestFixtures {

    public static final String KEY = "key";
    public static final String TOKEN = "token";
    public static final String SECRET = "secret";
    public static final String VERIFIER = "verifier";
    public static final long USER_ID = 11L;
    public static final String SCREEN_NAME = "username";

    public static final String PLAYER_CARD_VINE = VineCardUtils.VINE_CARD;

    public static final String TEST_VINE_USER_ID = "586671909";

    public static Card sampleValidVineCard() {
        return new Card(createBindingValuesForCard(), PLAYER_CARD_VINE);
    }

    public static BindingValues createBindingValuesForCard() {
        final UserValue testUser = new UserValue(TEST_VINE_USER_ID);
        final Map<String, Object> testValues = new HashMap<>();
        testValues.put("site", testUser);

        final BindingValues bindingValues = new BindingValues(testValues);
        return bindingValues;
    }
}
