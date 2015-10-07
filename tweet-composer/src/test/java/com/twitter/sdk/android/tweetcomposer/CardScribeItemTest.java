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

import com.google.gson.Gson;
import com.twitter.sdk.android.core.internal.scribe.ScribeItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CardScribeItemTest {
    ScribeItem.CardEvent TEST_CARD_EVENT = new ScribeItem.CardEvent(8);
    String TEST_CARD_SCRIBE_ITEM = "{\"item_type\":0,\"card_event\":{\"promotion_card_type\":8}}";

    public void testBuilder() {
        final ScribeItem scribeItem = new ScribeItem.Builder()
                .setCardEvent(TEST_CARD_EVENT)
                .build();
        assertEquals(TEST_CARD_EVENT, scribeItem.cardEvent);
    }

    @Test
    public void testBuilder_empty() {
        final ScribeItem scribeItem = new ScribeItem.Builder().build();
        assertNull(scribeItem.cardEvent);
    }

    @Test
    public void testSerialization() throws Exception {
        final ScribeItem scribeItem = ScribeConstants.newCardScribeItem(mock(Card.class));
        final Gson gson = new Gson();
        assertEquals(TEST_CARD_SCRIBE_ITEM, gson.toJson(scribeItem));
    }
}
