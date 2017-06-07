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

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Provides metadata and additional contextual information about content posted in a tweet.
 */
public class TweetEntities {

    static final TweetEntities EMPTY = new TweetEntities(null, null, null, null, null);

    /**
     * Represents URLs included in the text of a Tweet or within textual fields of a user object.
     */
    @SerializedName("urls")
    public final List<UrlEntity> urls;

    /**
     * Represents other Twitter users mentioned in the text of the Tweet.
     */
    @SerializedName("user_mentions")
    public final List<MentionEntity> userMentions;

    /**
     * Represents media elements uploaded with the Tweet.
     */
    @SerializedName("media")
    public final List<MediaEntity> media;

    /**
     * Represents hashtags which have been parsed out of the Tweet text.
     */
    @SerializedName("hashtags")
    public final List<HashtagEntity> hashtags;

    /**
     * Represents symbols which have been parsed out of the Tweet text.
     */
    @SerializedName("symbols")
    public final List<SymbolEntity> symbols;

    private TweetEntities() {
        this(null, null, null, null, null);
    }

    public TweetEntities(List<UrlEntity> urls, List<MentionEntity> userMentions,
            List<MediaEntity> media, List<HashtagEntity> hashtags, List<SymbolEntity> symbols) {
        this.urls = ModelUtils.getSafeList(urls);
        this.userMentions = ModelUtils.getSafeList(userMentions);
        this.media = ModelUtils.getSafeList(media);
        this.hashtags = ModelUtils.getSafeList(hashtags);
        this.symbols = ModelUtils.getSafeList(symbols);
    }

}
