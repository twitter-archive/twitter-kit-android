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
 * A Tweet is the basic atomic building block of all things Twitter. Tweets, also known more
 * generically as "status updates." Tweets can be embedded, replied to, favorited, unfavorited and
 * deleted.
 */
public class Tweet implements Identifiable {
    public static final long INVALID_ID = -1L;


    /**
     * Nullable. Represents the geographic location of this Tweet as reported by the user or client
     * application. The inner coordinates array is formatted as geoJSON (longitude first,
     * then latitude).
     */
    @SerializedName("coordinates")
    public final Coordinates coordinates;

    /**
     * UTC time when this Tweet was created.
     */
    @SerializedName("created_at")
    public final String createdAt;

    /**
     * Perspectival. Only surfaces on methods supporting the include_my_retweet parameter, when set
     * to true. Details the Tweet ID of the user's own retweet (if existent) of this Tweet.
     */
    @SerializedName("current_user_retweet")
    public final Object currentUserRetweet;

    /**
     * Entities which have been parsed out of the text of the Tweet.
     */
    @SerializedName("entities")
    public final TweetEntities entities;

    /**
     * Additional entities such as multi photos, animated gifs and video.
     */
    @SerializedName("extended_entities")
    public final TweetEntities extendedEntities;

    /**
     * Nullable. Indicates approximately how many times this Tweet has been "favorited" by Twitter
     * users.
     */
    @SerializedName("favorite_count")
    public final Integer favoriteCount;

    /**
     * Nullable. Perspectival. Indicates whether this Tweet has been favorited by the authenticating
     * user.
     */
    @SerializedName("favorited")
    public final boolean favorited;

    /**
     * Indicates the maximum value of the filter_level parameter which may be used and still stream
     * this Tweet. So a value of medium will be streamed on none, low, and medium streams.
     */
    @SerializedName("filter_level")
    public final String filterLevel;

    /**
     * The integer representation of the unique identifier for this Tweet. This number is greater
     * than 53 bits and some programming languages may have difficulty/silent defects in
     * interpreting it. Using a signed 64 bit integer for storing this identifier is safe. Use
     * id_str for fetching the identifier to stay on the safe side. See Twitter IDs, JSON and
     * Snowflake.
     */
    @SerializedName("id")
    public final long id;

    /**
     * The string representation of the unique identifier for this Tweet. Implementations should use
     * this rather than the large integer in id
     */
    @SerializedName("id_str")
    public final String idStr;

    /**
     * Nullable. If the represented Tweet is a reply, this field will contain the screen name of
     * the original Tweet's author.
     */
    @SerializedName("in_reply_to_screen_name")
    public final String inReplyToScreenName;

    /**
     * Nullable. If the represented Tweet is a reply, this field will contain the integer
     * representation of the original Tweet's ID.
     */
    @SerializedName("in_reply_to_status_id")
    public final long inReplyToStatusId;

    /**
     * Nullable. If the represented Tweet is a reply, this field will contain the string
     * representation of the original Tweet's ID.
     */
    @SerializedName("in_reply_to_status_id_str")
    public final String inReplyToStatusIdStr;

    /**
     * Nullable. If the represented Tweet is a reply, this field will contain the integer
     * representation of the original Tweet's author ID. This will not necessarily always be the
     * user directly mentioned in the Tweet.
     */
    @SerializedName("in_reply_to_user_id")
    public final long inReplyToUserId;

    /**
     * Nullable. If the represented Tweet is a reply, this field will contain the string
     * representation of the original Tweet's author ID. This will not necessarily always be the
     * user directly mentioned in the Tweet.
     */
    @SerializedName("in_reply_to_user_id_str")
    public final String inReplyToUserIdStr;

    /**
     * Nullable. When present, indicates a BCP 47 language identifier corresponding to the
     * machine-detected language of the Tweet text, or "und" if no language could be detected.
     */
    @SerializedName("lang")
    public final String lang;

    /**
     * Nullable. When present, indicates that the tweet is associated (but not necessarily
     * originating from) a Place.
     */
    @SerializedName("place")
    public final Place place;

    /**
     * Nullable. This field only surfaces when a tweet contains a link. The meaning of the field
     * doesn't pertain to the tweet content itself, but instead it is an indicator that the URL
     * contained in the tweet may contain content or media identified as sensitive content.
     */
    @SerializedName("possibly_sensitive")
    public final boolean possiblySensitive;

    /**
     * A set of key-value pairs indicating the intended contextual delivery of the containing Tweet.
     * Currently used by Twitter's Promoted Products.
     */
    @SerializedName("scopes")
    public final Object scopes;

    /**
     * This field only surfaces when the Tweet is a quote Tweet. This field contains the
     * integer value Tweet ID of the quoted Tweet.
     */
    @SerializedName("quoted_status_id")
    public final long quotedStatusId;

    /**
     * This field only surfaces when the Tweet is a quote Tweet. This is the string representation
     * Tweet ID of the quoted Tweet.
     */
    @SerializedName("quoted_status_id_str")
    public final String quotedStatusIdStr;

    /**
     * This field only surfaces when the Tweet is a quote Tweet. This attribute contains the
     * Tweet object of the original Tweet that was quoted.
     */
    @SerializedName("quoted_status")
    public final Tweet quotedStatus;

    /**
     * Number of times this Tweet has been retweeted. This field is no longer capped at 99 and will
     * not turn into a String for "100+"
     */
    @SerializedName("retweet_count")
    public final int retweetCount;

    /**
     * Perspectival. Indicates whether this Tweet has been retweeted by the authenticating user.
     */
    @SerializedName("retweeted")
    public final boolean retweeted;

    /**
     * Users can amplify the broadcast of tweets authored by other users by retweeting. Retweets can
     * be distinguished from typical Tweets by the existence of a retweeted_status attribute. This
     * attribute contains a representation of the original Tweet that was retweeted. Note that
     * retweets of retweets do not show representations of the intermediary retweet, but only the
     * original tweet. (Users can also unretweet a retweet they created by deleting their retweet.)
     */
    @SerializedName("retweeted_status")
    public final Tweet retweetedStatus;

    /**
     * Utility used to post the Tweet, as an HTML-formatted string. Tweets from the Twitter website
     * have a source value of web.
     */
    @SerializedName("source")
    public final String source;

    /**
     * The actual UTF-8 text of the status update. See twitter-text for details on what is currently
     * considered valid characters.
     */
    @SerializedName(value = "text", alternate = {"full_text"})
    public final String text;


    /**
     * An array of two unicode code point indices, identifying the inclusive start and exclusive end
     * of the displayable content of the Tweet.
     */
    @SerializedName("display_text_range")
    public final List<Integer> displayTextRange;

    /**
     * Indicates whether the value of the text parameter was truncated, for example, as a result of
     * a retweet exceeding the 140 character Tweet length. Truncated text will end in ellipsis, like
     * this ... Since Twitter now rejects long Tweets vs truncating them, the large majority of
     * Tweets will have this set to false.
     * Note that while native retweets may have their toplevel text property shortened, the original
     * text will be available under the retweeted_status object and the truncated parameter will be
     * set to the value of the original status (in most cases, false).
     */
    @SerializedName("truncated")
    public final boolean truncated;

    /**
     * The user who posted this Tweet. Perspectival attributes embedded within this object are
     * unreliable. See Why are embedded objects stale or inaccurate?.
     */
    @SerializedName("user")
    public final User user;

    /**
     * When present and set to "true", it indicates that this piece of content has been withheld due
     * to a DMCA complaint.
     */
    @SerializedName("withheld_copyright")
    public final boolean withheldCopyright;

    /**
     * When present, indicates a list of uppercase two-letter country codes this content is withheld
     * from. Twitter supports the following non-country values for this field:
     * "XX" - Content is withheld in all countries
     * "XY" - Content is withheld due to a DMCA request.
     */
    @SerializedName("withheld_in_countries")
    public final List<String> withheldInCountries;

    /**
     * When present, indicates whether the content being withheld is the "status" or a "user."
     */
    @SerializedName("withheld_scope")
    public final String withheldScope;

    /**
     * Nullable. Card data used to attach rich photos, videos and media experience to Tweets.
     */
    @SerializedName("card")
    public final Card card;

    private Tweet() {
        this(null, null, null, TweetEntities.EMPTY, TweetEntities.EMPTY, 0, false, null, 0, "0",
                null, 0, "0", 0, "0", null, null, false, null, 0, "0", null, 0, false, null, null,
                null, null, false, null, false, null, null, null);
    }

    public Tweet(Coordinates coordinates, String createdAt, Object currentUserRetweet,
            TweetEntities entities, TweetEntities extendedEntities, Integer favoriteCount,
            boolean favorited, String filterLevel, long id, String idStr,
            String inReplyToScreenName, long inReplyToStatusId, String inReplyToStatusIdStr,
            long inReplyToUserId, String inReplyToUserIdStr, String lang, Place place,
            boolean possiblySensitive, Object scopes, long quotedStatusId, String quotedStatusIdStr,
            Tweet quotedStatus, int retweetCount, boolean retweeted, Tweet retweetedStatus,
            String source, String text, List<Integer> displayTextRange, boolean truncated,
            User user, boolean withheldCopyright, List<String> withheldInCountries,
            String withheldScope, Card card) {
        this.coordinates = coordinates;
        this.createdAt = createdAt;
        this.currentUserRetweet = currentUserRetweet;
        this.entities = entities == null ? TweetEntities.EMPTY : entities;
        this.extendedEntities = extendedEntities == null ? TweetEntities.EMPTY : extendedEntities;
        this.favoriteCount = favoriteCount;
        this.favorited = favorited;
        this.filterLevel = filterLevel;
        this.id = id;
        this.idStr = idStr;
        this.inReplyToScreenName = inReplyToScreenName;
        this.inReplyToStatusId = inReplyToStatusId;
        this.inReplyToStatusIdStr = inReplyToStatusIdStr;
        this.inReplyToUserId = inReplyToUserId;
        this.inReplyToUserIdStr = inReplyToUserIdStr;
        this.lang = lang;
        this.place = place;
        this.possiblySensitive = possiblySensitive;
        this.scopes = scopes;
        this.quotedStatusId = quotedStatusId;
        this.quotedStatusIdStr = quotedStatusIdStr;
        this.quotedStatus = quotedStatus;
        this.retweetCount = retweetCount;
        this.retweeted = retweeted;
        this.retweetedStatus = retweetedStatus;
        this.source = source;
        this.text = text;
        this.displayTextRange = ModelUtils.getSafeList(displayTextRange);
        this.truncated = truncated;
        this.user = user;
        this.withheldCopyright = withheldCopyright;
        this.withheldInCountries = ModelUtils.getSafeList(withheldInCountries);
        this.withheldScope = withheldScope;
        this.card = card;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Tweet)) return false;
        final Tweet other = (Tweet) o;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return (int) this.id;
    }

}
