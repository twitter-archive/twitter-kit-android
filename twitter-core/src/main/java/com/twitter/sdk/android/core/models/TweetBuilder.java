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

package com.twitter.sdk.android.core.models;

import java.util.List;

public class TweetBuilder {
    private Coordinates coordinates;
    private String createdAt;
    private Object currentUserRetweet;
    private TweetEntities entities;
    private TweetEntities extendedEntities;
    private Integer favoriteCount;
    private boolean favorited;
    private String filterLevel;
    private long id = Tweet.INVALID_ID;
    private String idStr;
    private String inReplyToScreenName;
    private long inReplyToStatusId;
    private String inReplyToStatusIdStr;
    private long inReplyToUserId;
    private String inReplyToUserIdStr;
    private String lang;
    private Place place;
    private boolean possiblySensitive;
    private Object scopes;
    private long quotedStatusId;
    private String quotedStatusIdStr;
    private Tweet quotedStatus;
    private int retweetCount;
    private boolean retweeted;
    private Tweet retweetedStatus;
    private String source;
    private String text;
    private List<Integer> displayTextRange;
    private boolean truncated;
    private User user;
    private boolean withheldCopyright;
    private List<String> withheldInCountries;
    private String withheldScope;
    private Card card;

    public TweetBuilder setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public TweetBuilder setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public TweetBuilder setCurrentUserRetweet(Object currentUserRetweet) {
        this.currentUserRetweet = currentUserRetweet;
        return this;
    }

    public TweetBuilder setEntities(TweetEntities entities) {
        this.entities = entities;
        return this;
    }

    public TweetBuilder setExtendedEntities(TweetEntities extendedEntities) {
        this.extendedEntities = extendedEntities;
        return this;
    }

    public TweetBuilder setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
        return this;
    }

    public TweetBuilder setFavorited(boolean favorited) {
        this.favorited = favorited;
        return this;
    }

    public TweetBuilder setFilterLevel(String filterLevel) {
        this.filterLevel = filterLevel;
        return this;
    }

    public TweetBuilder setId(long id) {
        this.id = id;
        return this;
    }

    public TweetBuilder setIdStr(String idStr) {
        this.idStr = idStr;
        return this;
    }

    public TweetBuilder setInReplyToScreenName(String inReplyToScreenName) {
        this.inReplyToScreenName = inReplyToScreenName;
        return this;
    }

    public TweetBuilder setInReplyToStatusId(long inReplyToStatusId) {
        this.inReplyToStatusId = inReplyToStatusId;
        return this;
    }

    public TweetBuilder setInReplyToStatusIdStr(String inReplyToStatusIdStr) {
        this.inReplyToStatusIdStr = inReplyToStatusIdStr;
        return this;
    }

    public TweetBuilder setInReplyToUserId(long inReplyToUserId) {
        this.inReplyToUserId = inReplyToUserId;
        return this;
    }

    public TweetBuilder setInReplyToUserIdStr(String inReplyToUserIdStr) {
        this.inReplyToUserIdStr = inReplyToUserIdStr;
        return this;
    }

    public TweetBuilder setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public TweetBuilder setPlace(Place place) {
        this.place = place;
        return this;
    }

    public TweetBuilder setPossiblySensitive(boolean possiblySensitive) {
        this.possiblySensitive = possiblySensitive;
        return this;
    }

    public TweetBuilder setScopes(Object scopes) {
        this.scopes = scopes;
        return this;
    }

    public TweetBuilder setQuotedStatusId(long quotedStatusId) {
        this.quotedStatusId = quotedStatusId;
        return this;
    }

    public TweetBuilder setQuotedStatusIdStr(String quotedStatusIdStr) {
        this.quotedStatusIdStr = quotedStatusIdStr;
        return this;
    }

    public TweetBuilder setQuotedStatus(Tweet quotedStatus) {
        this.quotedStatus = quotedStatus;
        return this;
    }

    public TweetBuilder setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
        return this;
    }

    public TweetBuilder setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
        return this;
    }

    public TweetBuilder setRetweetedStatus(Tweet retweetedStatus) {
        this.retweetedStatus = retweetedStatus;
        return this;
    }

    public TweetBuilder setSource(String source) {
        this.source = source;
        return this;
    }

    public TweetBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public TweetBuilder setDisplayTextRange(List<Integer> displayTextRange) {
        this.displayTextRange = displayTextRange;
        return this;
    }

    public TweetBuilder setTruncated(boolean truncated) {
        this.truncated = truncated;
        return this;
    }

    public TweetBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public TweetBuilder setWithheldCopyright(boolean withheldCopyright) {
        this.withheldCopyright = withheldCopyright;
        return this;
    }

    public TweetBuilder setWithheldInCountries(List<String> withheldInCountries) {
        this.withheldInCountries = withheldInCountries;
        return this;
    }

    public TweetBuilder setWithheldScope(String withheldScope) {
        this.withheldScope = withheldScope;
        return this;
    }

    public TweetBuilder setCard(Card card) {
        this.card = card;
        return this;
    }

    public TweetBuilder copy(Tweet tweet) {
        this.coordinates = tweet.coordinates;
        this.createdAt = tweet.createdAt;
        this.currentUserRetweet = tweet.currentUserRetweet;
        this.entities = tweet.entities;
        this.extendedEntities = tweet.extendedEntities;
        this.favoriteCount = tweet.favoriteCount;
        this.favorited = tweet.favorited;
        this.filterLevel = tweet.filterLevel;
        this.id = tweet.id;
        this.idStr = tweet.idStr;
        this.inReplyToScreenName = tweet.inReplyToScreenName;
        this.inReplyToStatusId = tweet.inReplyToStatusId;
        this.inReplyToStatusIdStr = tweet.inReplyToStatusIdStr;
        this.inReplyToUserId = tweet.inReplyToUserId;
        this.inReplyToUserIdStr = tweet.inReplyToStatusIdStr;
        this.lang = tweet.lang;
        this.place = tweet.place;
        this.possiblySensitive = tweet.possiblySensitive;
        this.scopes = tweet.scopes;
        this.quotedStatusId = tweet.quotedStatusId;
        this.quotedStatusIdStr = tweet.quotedStatusIdStr;
        this.quotedStatus = tweet.quotedStatus;
        this.retweetCount = tweet.retweetCount;
        this.retweeted = tweet.retweeted;
        this.retweetedStatus = tweet.retweetedStatus;
        this.source = tweet.source;
        this.text = tweet.text;
        this.displayTextRange = tweet.displayTextRange;
        this.truncated = tweet.truncated;
        this.user = tweet.user;
        this.withheldCopyright = tweet.withheldCopyright;
        this.withheldInCountries = tweet.withheldInCountries;
        this.withheldScope = tweet.withheldScope;
        this.card = tweet.card;
        return this;
    }

    public Tweet build() {
        return new Tweet(coordinates, createdAt, currentUserRetweet, entities, extendedEntities,
                favoriteCount, favorited, filterLevel, id, idStr, inReplyToScreenName,
                inReplyToStatusId, inReplyToStatusIdStr, inReplyToUserId, inReplyToUserIdStr,
                lang, place, possiblySensitive, scopes, quotedStatusId, quotedStatusIdStr,
                quotedStatus, retweetCount, retweeted, retweetedStatus, source, text,
                displayTextRange, truncated, user, withheldCopyright, withheldInCountries,
                withheldScope, card);
    }
}
