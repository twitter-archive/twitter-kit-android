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

import java.util.Collections;
import java.util.List;

/**
 * Provides metadata and additional contextual information about content posted in a tweet.
 */
public class TweetEntities {

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

    /**
     * @deprecated use {@link TweetEntities#TweetEntities(List, List, List, List, List)} instead
     */
    @Deprecated
    public TweetEntities(List<UrlEntity> urls, List<MentionEntity> userMentions,
                         List<MediaEntity> media, List<HashtagEntity> hashtags) {
        this(urls, userMentions, media, hashtags, null);
    }

    public TweetEntities(List<UrlEntity> urls, List<MentionEntity> userMentions,
            List<MediaEntity> media, List<HashtagEntity> hashtags, List<SymbolEntity> symbols) {
        this.urls = getSafeList(urls);
        this.userMentions = getSafeList(userMentions);
        this.media = getSafeList(media);
        this.hashtags = getSafeList(hashtags);
        this.symbols = getSafeList(symbols);
    }

    private <T> List<T> getSafeList(List<T> entities) {
        // Entities may be null if Gson does not find object to parse. When that happens, make sure
        // to return an empty list.
        if (entities == null) {
            return Collections.EMPTY_LIST;
        } else {
            return Collections.unmodifiableList(entities);
        }
    }
}
