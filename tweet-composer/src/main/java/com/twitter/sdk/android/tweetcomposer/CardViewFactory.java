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
import android.view.View;

/**
 * CardViewFactory returns a View for a Card.
 */
class CardViewFactory {

    /**
     * @return a View for the Card.
     */
    View createCard(Context context, Card card) {
        if (card.cardType.equals(Card.APP_CARD_TYPE)) {
            final AppCardView cardView = new AppCardView(context);
            cardView.setCard(card);
            return cardView;
        }

        return null;
    }
}
