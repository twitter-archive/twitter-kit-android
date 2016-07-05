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

public class UserBuilder {
    private boolean contributorsEnabled;
    private String createdAt;
    private boolean defaultProfile;
    private boolean defaultProfileImage;
    private String description;
    private String email;
    private UserEntities entities;
    private int favouritesCount;
    private boolean followRequestSent;
    private int followersCount;
    private int friendsCount;
    private boolean geoEnabled;
    private long id = User.INVALID_ID;
    private String idStr;
    private boolean isTranslator;
    private String lang;
    private int listedCount;
    private String location;
    private String name;
    private String profileBackgroundColor;
    private String profileBackgroundImageUrl;
    private String profileBackgroundImageUrlHttps;
    private boolean profileBackgroundTile;
    private String profileBannerUrl;
    private String profileImageUrl;
    private String profileImageUrlHttps;
    private String profileLinkColor;
    private String profileSidebarBorderColor;
    private String profileSidebarFillColor;
    private String profileTextColor;
    private boolean profileUseBackgroundImage;
    private boolean protectedUser;
    private String screenName;
    private boolean showAllInlineMedia;
    private Tweet status;
    private int statusesCount;
    private String timeZone;
    private String url;
    private int utcOffset;
    private boolean verified;
    private List<String> withheldInCountries;
    private String withheldScope;

    public UserBuilder setContributorsEnabled(boolean contributorsEnabled) {
        this.contributorsEnabled = contributorsEnabled;
        return this;
    }

    public UserBuilder setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public UserBuilder setDefaultProfile(boolean defaultProfile) {
        this.defaultProfile = defaultProfile;
        return this;
    }

    public UserBuilder setDefaultProfileImage(boolean defaultProfileImage) {
        this.defaultProfileImage = defaultProfileImage;
        return this;
    }

    public UserBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public UserBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder setEntities(UserEntities entities) {
        this.entities = entities;
        return this;
    }

    public UserBuilder setFavouritesCount(int favouritesCount) {
        this.favouritesCount = favouritesCount;
        return this;
    }

    public UserBuilder setFollowRequestSent(boolean followRequestSent) {
        this.followRequestSent = followRequestSent;
        return this;
    }

    public UserBuilder setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
        return this;
    }

    public UserBuilder setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
        return this;
    }

    public UserBuilder setGeoEnabled(boolean geoEnabled) {
        this.geoEnabled = geoEnabled;
        return this;
    }

    public UserBuilder setId(long id) {
        this.id = id;
        return this;
    }

    public UserBuilder setIdStr(String idStr) {
        this.idStr = idStr;
        return this;
    }

    public UserBuilder setIsTranslator(boolean isTranslator) {
        this.isTranslator = isTranslator;
        return this;
    }

    public UserBuilder setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public UserBuilder setListedCount(int listedCount) {
        this.listedCount = listedCount;
        return this;
    }

    public UserBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public UserBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder setProfileBackgroundColor(String profileBackgroundColor) {
        this.profileBackgroundColor = profileBackgroundColor;
        return this;
    }

    public UserBuilder setProfileBackgroundImageUrl(String profileBackgroundImageUrl) {
        this.profileBackgroundImageUrl = profileBackgroundImageUrl;
        return this;
    }

    public UserBuilder setProfileBackgroundImageUrlHttps(String profileBackgroundImageUrlHttps) {
        this.profileBackgroundImageUrlHttps = profileBackgroundImageUrlHttps;
        return this;
    }

    public UserBuilder setProfileBackgroundTile(boolean profileBackgroundTile) {
        this.profileBackgroundTile = profileBackgroundTile;
        return this;
    }

    public UserBuilder setProfileBannerUrl(String profileBannerUrl) {
        this.profileBannerUrl = profileBannerUrl;
        return this;
    }

    public UserBuilder setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        return this;
    }

    public UserBuilder setProfileImageUrlHttps(String profileImageUrlHttps) {
        this.profileImageUrlHttps = profileImageUrlHttps;
        return this;
    }

    public UserBuilder setProfileLinkColor(String profileLinkColor) {
        this.profileLinkColor = profileLinkColor;
        return this;
    }

    public UserBuilder setProfileSidebarBorderColor(String profileSidebarBorderColor) {
        this.profileSidebarBorderColor = profileSidebarBorderColor;
        return this;
    }

    public UserBuilder setProfileSidebarFillColor(String profileSidebarFillColor) {
        this.profileSidebarFillColor = profileSidebarFillColor;
        return this;
    }

    public UserBuilder setProfileTextColor(String profileTextColor) {
        this.profileTextColor = profileTextColor;
        return this;
    }

    public UserBuilder setProfileUseBackgroundImage(boolean profileUseBackgroundImage) {
        this.profileUseBackgroundImage = profileUseBackgroundImage;
        return this;
    }

    public UserBuilder setProtectedUser(boolean protectedUser) {
        this.protectedUser = protectedUser;
        return this;
    }

    public UserBuilder setScreenName(String screenName) {
        this.screenName = screenName;
        return this;
    }

    public UserBuilder setShowAllInlineMedia(boolean showAllInlineMedia) {
        this.showAllInlineMedia = showAllInlineMedia;
        return this;
    }

    public UserBuilder setStatus(Tweet status) {
        this.status = status;
        return this;
    }

    public UserBuilder setStatusesCount(int statusesCount) {
        this.statusesCount = statusesCount;
        return this;
    }

    public UserBuilder setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public UserBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public UserBuilder setUtcOffset(int utcOffset) {
        this.utcOffset = utcOffset;
        return this;
    }

    public UserBuilder setVerified(boolean verified) {
        this.verified = verified;
        return this;
    }

    public UserBuilder setWithheldInCountries(List<String> withheldInCountries) {
        this.withheldInCountries = withheldInCountries;
        return this;
    }

    public UserBuilder setWithheldScope(String withheldScope) {
        this.withheldScope = withheldScope;
        return this;
    }

    public User build() {
        return new User(contributorsEnabled, createdAt, defaultProfile, defaultProfileImage,
                description, email, entities, favouritesCount, followRequestSent,
                followersCount, friendsCount, geoEnabled, id, idStr, isTranslator, lang,
                listedCount, location, name, profileBackgroundColor,
                profileBackgroundImageUrl, profileBackgroundImageUrlHttps, profileBackgroundTile,
                profileBannerUrl, profileImageUrl, profileImageUrlHttps,
                profileLinkColor, profileSidebarBorderColor, profileSidebarFillColor,
                profileTextColor, profileUseBackgroundImage, protectedUser, screenName,
                showAllInlineMedia, status, statusesCount, timeZone, url, utcOffset, verified,
                withheldInCountries, withheldScope);
    }
}
