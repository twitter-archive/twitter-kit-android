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
import com.twitter.sdk.android.core.internal.VineCardUtils;
import com.twitter.sdk.android.core.models.Card;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

import java.io.Serializable;

public class ScribeItem implements Serializable {
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
     * Optional field.
     */
    @SerializedName("card_event")
    public final CardEvent cardEvent;

    /**
     * Media details.
     * Optional field.
     */
    @SerializedName("media_details")
    public final MediaDetails mediaDetails;

    private ScribeItem(Integer itemType, Long id, String description, CardEvent cardEvent,
            MediaDetails mediaDetails) {
        this.itemType = itemType;
        this.id = id;
        this.description = description;
        this.cardEvent = cardEvent;
        this.mediaDetails = mediaDetails;
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

    public static ScribeItem fromTweetCard(long tweetId, Card card) {
        return new ScribeItem.Builder()
                .setItemType(ScribeItem.TYPE_TWEET)
                .setId(tweetId)
                .setMediaDetails(createCardDetails(tweetId, card))
                .build();
    }

    public static ScribeItem fromMediaEntity(long tweetId, MediaEntity mediaEntity) {
        return new ScribeItem.Builder()
                .setItemType(ScribeItem.TYPE_TWEET)
                .setId(tweetId)
                .setMediaDetails(createMediaDetails(tweetId, mediaEntity))
                .build();
    }

    static ScribeItem.MediaDetails createMediaDetails(long tweetId,
                                                             MediaEntity mediaEntity) {
        return new ScribeItem.MediaDetails(tweetId, getMediaType(mediaEntity), mediaEntity.id);
    }

    static ScribeItem.MediaDetails createCardDetails(long tweetId, Card card) {
        return new ScribeItem.MediaDetails(tweetId, MediaDetails.TYPE_VINE,
                Long.valueOf(VineCardUtils.getPublisherId(card)));
    }

    static int getMediaType(MediaEntity mediaEntity) {
        if (MediaDetails.GIF_TYPE.equals(mediaEntity.type)) {
            return ScribeItem.MediaDetails.TYPE_ANIMATED_GIF;
        } else {
            return ScribeItem.MediaDetails.TYPE_CONSUMER;
        }
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
        if (cardEvent != null ? !cardEvent.equals(that.cardEvent) : that.cardEvent != null)
            return false;
        return !(mediaDetails != null ? !mediaDetails.equals(that.mediaDetails) : that
                .mediaDetails != null);
    }

    @Override
    public int hashCode() {
        int result = itemType != null ? itemType.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (cardEvent != null ? cardEvent.hashCode() : 0);
        result = 31 * result + (mediaDetails != null ? mediaDetails.hashCode() : 0);
        return result;
    }

    /**
     * Card event.
     */
    public static class CardEvent implements Serializable {

        @SerializedName("promotion_card_type")
        final int promotionCardType;

        public CardEvent(int cardType) {
            promotionCardType = cardType;
        }

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

    /**
     * Media details.
     */
    public static class MediaDetails implements Serializable {
        public static final int TYPE_CONSUMER = 1;
        public static final int TYPE_AMPLIFY = 2;
        public static final int TYPE_ANIMATED_GIF = 3;
        public static final int TYPE_VINE = 4;

        public static final String GIF_TYPE = "animated_gif";

        @SerializedName("content_id")
        public final long contentId;

        @SerializedName("media_type")
        public final int mediaType;

        @SerializedName("publisher_id")
        public final long publisherId;

        public MediaDetails(long contentId, int mediaType, long publisherId) {
            this.contentId = contentId;
            this.mediaType = mediaType;
            this.publisherId = publisherId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final MediaDetails that = (MediaDetails) o;

            if (contentId != that.contentId) return false;
            if (mediaType != that.mediaType) return false;
            return publisherId == that.publisherId;
        }

        @Override
        public int hashCode() {
            int result = (int) (contentId ^ (contentId >>> 32));
            result = 31 * result + mediaType;
            result = 31 * result + (int) (publisherId ^ (publisherId >>> 32));
            return result;
        }
    }

    public static class Builder {
        private Integer itemType;
        private Long id;
        private String description;
        private CardEvent cardEvent;
        private MediaDetails mediaDetails;

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

        public Builder setMediaDetails(MediaDetails mediaDetails) {
            this.mediaDetails = mediaDetails;
            return this;
        }

        public ScribeItem build() {
            return new ScribeItem(itemType, id, description, cardEvent, mediaDetails);
        }
    }
}
