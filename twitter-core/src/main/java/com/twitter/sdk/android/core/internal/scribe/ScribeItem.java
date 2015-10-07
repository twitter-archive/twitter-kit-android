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

package com.twitter.sdk.android.core.internal.scribe;

import com.google.gson.annotations.SerializedName;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

public class ScribeItem {
    /**
     * Scribe item types. See ItemType in
     * See: source/tree/science/src/thrift/com/twitter/clientapp/gen/client_app.thrift
     */
    public static final int TYPE_TWEET = 0;
    public static final int TYPE_USER = 3;
    public static final int TYPE_MESSAGE = 6;

    /**
     * The type of item (tweet, message, etc).
     * Optional field.
     */
    @SerializedName("item_type")
    public final Integer itemType;

    /**
     * A numerical id associated with the item.
     * Optional field.
     */
    @SerializedName("id")
    public final Long id;

    /**
     *  A description of the item.
     *  Optional field.
     */
    @SerializedName("description")
    public final String description;

    /**
     * Card event.
     * source/tree/science/src/thrift/com/twitter/clientapp/gen/client_app.thrift
     */
    @SerializedName("card_event")
    public final CardEvent cardEvent;

    private ScribeItem(Integer itemType, Long id, String description, CardEvent cardEvent) {
        this.itemType = itemType;
        this.id = id;
        this.description = description;
        this.cardEvent = cardEvent;
    }

    public static ScribeItem fromTweet(Tweet tweet) {
        return new ScribeItem.Builder()
                .setItemType(TYPE_TWEET)
                .setId(tweet.id)
                .build();
    }

    public static ScribeItem fromUser(User user) {
        return new ScribeItem.Builder()
                .setItemType(TYPE_USER)
                .setId(user.id)
                .build();
    }

    public static ScribeItem fromMessage(String message) {
        return new ScribeItem.Builder()
                .setItemType(TYPE_MESSAGE)
                .setDescription(message)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ScribeItem that = (ScribeItem) o;
        if (itemType != null ? !itemType.equals(that.itemType) : that.itemType != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        return !(cardEvent != null ? !cardEvent.equals(that.cardEvent) : that.cardEvent != null);
    }

    @Override
    public int hashCode() {
        int result = itemType != null ? itemType.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (cardEvent != null ? cardEvent.hashCode() : 0);
        return result;
    }

    /**
     * Card event.
     */
    public static class CardEvent {
        public CardEvent(int cardType) {
            promotionCardType = cardType;
        }

        @SerializedName("promotion_card_type")
        final int promotionCardType;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final CardEvent cardEvent = (CardEvent) o;
            return promotionCardType == cardEvent.promotionCardType;
        }

        @Override
        public int hashCode() {
            return promotionCardType;
        }
    }

    public static class Builder {
        private Integer itemType;
        private Long id;
        private String description;
        private CardEvent cardEvent;

        public Builder setItemType(int itemType) {
            this.itemType = itemType;
            return this;
        }

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setCardEvent(CardEvent cardEvent) {
            this.cardEvent = cardEvent;
            return this;
        }

        public ScribeItem build() {
            return new ScribeItem(itemType, id, description, cardEvent);
        }
    }
}
