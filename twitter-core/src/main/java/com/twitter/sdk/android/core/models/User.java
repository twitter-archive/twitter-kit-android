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

import java.io.Serializable;
import java.util.List;

/**
 * Users can be anyone or anything. They tweet, follow, create lists, have a home_timeline, can be
 * mentioned, and can be looked up in bulk.
 */
public class User implements Serializable, Identifiable {
    private static final long serialVersionUID = 4663450696842173958L;
    public static final long INVALID_ID = -1L;

    /**
     * Indicates that the user has an account with "contributor mode" enabled, allowing for Tweets
     * issued by the user to be co-authored by another account. Rarely true.
     */
    @SerializedName("contributors_enabled")
    public final boolean contributorsEnabled;

    /**
     * The UTC datetime that the user account was created on Twitter.
     */
    @SerializedName("created_at")
    public final String createdAt;

    /**
     * When true, indicates that the user has not altered the theme or background of their user
     * profile.
     */
    @SerializedName("default_profile")
    public final boolean defaultProfile;

    /**
     * When true, indicates that the user has not uploaded their own avatar and a default egg avatar
     * is used instead.
     */
    @SerializedName("default_profile_image")
    public final boolean defaultProfileImage;

    /**
     * Nullable. The user-defined UTF-8 string describing their account.
     */
    @SerializedName("description")
    public final String description;

    /**
     * Nullable. The logged in user email address if available. Must have permission to access email
     * address.
     */
    @SerializedName("email")
    public final String email;

    /**
     * Entities which have been parsed out of the url or description fields defined by the user.
     * Read more about User Entities.
     */
    @SerializedName("entities")
    public final UserEntities entities;

    /**
     * The number of tweets this user has favorited in the account's lifetime. British spelling used
     * in the field name for historical reasons.
     */
    @SerializedName("favourites_count")
    public final int favouritesCount;

    /**
     * Nullable. Perspectival. When true, indicates that the authenticating user has issued a follow
     * request to this protected user account.
     */
    @SerializedName("follow_request_sent")
    public final boolean followRequestSent;

    /**
     * The number of followers this account currently has. Under certain conditions of duress, this
     * field will temporarily indicate "0."
     */
    @SerializedName("followers_count")
    public final int followersCount;

    /**
     * The number of users this account is following (AKA their "followings"). Under certain
     * conditions of duress, this field will temporarily indicate "0."
     */
    @SerializedName("friends_count")
    public final int friendsCount;

    /**
     * When true, indicates that the user has enabled the possibility of geotagging their Tweets.
     * This field must be true for the current user to attach geographic data when using
     * POST statuses / update.
     */
    @SerializedName("geo_enabled")
    public final boolean geoEnabled;

    /**
     * The integer representation of the unique identifier for this User. This number is greater
     * than 53 bits and some programming languages may have difficulty/silent defects in
     * interpreting it. Using a signed 64 bit integer for storing this identifier is safe. Use
     * id_str for fetching the identifier to stay on the safe side. See Twitter IDs, JSON and
     * Snowflake.
     */
    @SerializedName("id")
    public final long id;

    /**
     * The string representation of the unique identifier for this User. Implementations should use
     * this rather than the large, possibly un-consumable integer in id
     */
    @SerializedName("id_str")
    public final String idStr;

    /**
     * When true, indicates that the user is a participant in Twitter's translator community.
     */
    @SerializedName("is_translator")
    public final boolean isTranslator;

    /**
     * The BCP 47 code for the user's self-declared user interface language. May or may not have
     * anything to do with the content of their Tweets.
     */
    @SerializedName("lang")
    public final String lang;

    /**
     * The number of public lists that this user is a member of.
     */
    @SerializedName("listed_count")
    public final int listedCount;

    /**
     * Nullable. The user-defined location for this account's profile. Not necessarily a location
     * nor parseable. This field will occasionally be fuzzily interpreted by the Search service.
     */
    @SerializedName("location")
    public final String location;

    /**
     * The name of the user, as they've defined it. Not necessarily a person's name. Typically
     * capped at 20 characters, but subject to change.
     */
    @SerializedName("name")
    public final String name;

    /**
     * The hexadecimal color chosen by the user for their background.
     */
    @SerializedName("profile_background_color")
    public final String profileBackgroundColor;

    /**
     * A HTTP-based URL pointing to the background image the user has uploaded for their profile.
     */
    @SerializedName("profile_background_image_url")
    public final String profileBackgroundImageUrl;

    /**
     * A HTTPS-based URL pointing to the background image the user has uploaded for their profile.
     */
    @SerializedName("profile_background_image_url_https")
    public final String profileBackgroundImageUrlHttps;

    /**
     * When true, indicates that the user's profile_background_image_url should be tiled when
     * displayed.
     */
    @SerializedName("profile_background_tile")
    public final boolean profileBackgroundTile;

    /**
     * The HTTPS-based URL pointing to the standard web representation of the user's uploaded
     * profile banner. By adding a final path element of the URL, you can obtain different image
     * sizes optimized for specific displays. In the future, an API method will be provided to serve
     * these URLs so that you need not modify the original URL. For size variations, please see
     * User Profile Images and Banners.
     */
    @SerializedName("profile_banner_url")
    public final String profileBannerUrl;

    /**
     * A HTTP-based URL pointing to the user's avatar image. See User Profile Images and Banners.
     */
    @SerializedName("profile_image_url")
    public final String profileImageUrl;

    /**
     * A HTTPS-based URL pointing to the user's avatar image.
     */
    @SerializedName("profile_image_url_https")
    public final String profileImageUrlHttps;

    /**
     * The hexadecimal color the user has chosen to display links with in their Twitter UI.
     */
    @SerializedName("profile_link_color")
    public final String profileLinkColor;

    /**
     * The hexadecimal color the user has chosen to display sidebar borders with in their Twitter
     * UI.
     */
    @SerializedName("profile_sidebar_border_color")
    public final String profileSidebarBorderColor;

    /**
     * The hexadecimal color the user has chosen to display sidebar backgrounds with in their
     * Twitter UI.
     */
    @SerializedName("profile_sidebar_fill_color")
    public final String profileSidebarFillColor;

    /**
     * The hexadecimal color the user has chosen to display text with in their Twitter UI.
     */
    @SerializedName("profile_text_color")
    public final String profileTextColor;

    /**
     * When true, indicates the user wants their uploaded background image to be used.
     */
    @SerializedName("profile_use_background_image")
    public final boolean profileUseBackgroundImage;

    /**
     * When true, indicates that this user has chosen to protect their Tweets. See About Public and
     * Protected Tweets.
     */
    @SerializedName("protected")
    public final boolean protectedUser;

    /**
     * The screen name, handle, or alias that this user identifies themselves with. screen_names are
     * unique but subject to change. Use id_str as a user identifier whenever possible. Typically a
     * maximum of 15 characters long, but some historical accounts may exist with longer names.
     */
    @SerializedName("screen_name")
    public final String screenName;

    /**
     * Indicates that the user would like to see media inline. Somewhat disused.
     */
    @SerializedName("show_all_inline_media")
    public final boolean showAllInlineMedia;

    /**
     * Nullable. If possible, the user's most recent tweet or retweet. In some circumstances, this
     * data cannot be provided and this field will be omitted, null, or empty. Perspectival
     * attributes within tweets embedded within users cannot always be relied upon. See Why are
     * embedded objects stale or inaccurate?.
     */
    @SerializedName("status")
    public final Tweet status;

    /**
     * The number of tweets (including retweets) issued by the user.
     */
    @SerializedName("statuses_count")
    public final int statusesCount;

    /**
     * Nullable. A string describing the Time Zone this user declares themselves within.
     */
    @SerializedName("time_zone")
    public final String timeZone;

    /**
     * Nullable. A URL provided by the user in association with their profile.
     */
    @SerializedName("url")
    public final String url;

    /**
     * Nullable. The offset from GMT/UTC in seconds.
     */
    @SerializedName("utc_offset")
    public final int utcOffset;

    /**
     * When true, indicates that the user has a verified account. See Verified Accounts.
     */
    @SerializedName("verified")
    public final boolean verified;

    /**
     * When present, indicates a textual representation of the two-letter country codes this user is
     * withheld from.
     */
    @SerializedName("withheld_in_countries")
    public final List<String> withheldInCountries;

    /**
     * When present, indicates whether the content being withheld is the "status" or a "user."
     */
    @SerializedName("withheld_scope")
    public final String withheldScope;

    public User(boolean contributorsEnabled, String createdAt, boolean defaultProfile,
            boolean defaultProfileImage, String description, String emailAddress,
            UserEntities entities, int favouritesCount, boolean followRequestSent,
            int followersCount, int friendsCount, boolean geoEnabled, long id, String idStr,
            boolean isTranslator, String lang, int listedCount, String location, String name,
            String profileBackgroundColor, String profileBackgroundImageUrl,
            String profileBackgroundImageUrlHttps, boolean profileBackgroundTile,
            String profileBannerUrl, String profileImageUrl, String profileImageUrlHttps,
            String profileLinkColor, String profileSidebarBorderColor,
            String profileSidebarFillColor, String profileTextColor,
            boolean profileUseBackgroundImage, boolean protectedUser, String screenName,
            boolean showAllInlineMedia, Tweet status, int statusesCount, String timeZone,
            String url, int utcOffset, boolean verified, List<String> withheldInCountries,
            String withheldScope) {
        this.contributorsEnabled = contributorsEnabled;
        this.createdAt = createdAt;
        this.defaultProfile = defaultProfile;
        this.defaultProfileImage = defaultProfileImage;
        this.description = description;
        this.email = emailAddress;
        this.entities = entities;
        this.favouritesCount = favouritesCount;
        this.followRequestSent = followRequestSent;
        this.followersCount = followersCount;
        this.friendsCount = friendsCount;
        this.geoEnabled = geoEnabled;
        this.id = id;
        this.idStr = idStr;
        this.isTranslator = isTranslator;
        this.lang = lang;
        this.listedCount = listedCount;
        this.location = location;
        this.name = name;
        this.profileBackgroundColor = profileBackgroundColor;
        this.profileBackgroundImageUrl = profileBackgroundImageUrl;
        this.profileBackgroundImageUrlHttps = profileBackgroundImageUrlHttps;
        this.profileBackgroundTile = profileBackgroundTile;
        this.profileBannerUrl = profileBannerUrl;
        this.profileImageUrl = profileImageUrl;
        this.profileImageUrlHttps = profileImageUrlHttps;
        this.profileLinkColor = profileLinkColor;
        this.profileSidebarBorderColor = profileSidebarBorderColor;
        this.profileSidebarFillColor = profileSidebarFillColor;
        this.profileTextColor = profileTextColor;
        this.profileUseBackgroundImage = profileUseBackgroundImage;
        this.protectedUser = protectedUser;
        this.screenName = screenName;
        this.showAllInlineMedia = showAllInlineMedia;
        this.status = status;
        this.statusesCount = statusesCount;
        this.timeZone = timeZone;
        this.url = url;
        this.utcOffset = utcOffset;
        this.verified = verified;
        this.withheldInCountries = withheldInCountries;
        this.withheldScope = withheldScope;
    }

    @Override
    public long getId() {
        return this.id;
    }
}
