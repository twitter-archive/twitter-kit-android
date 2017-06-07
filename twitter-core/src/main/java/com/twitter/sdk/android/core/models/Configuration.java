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
 * Current configuration used by Twitter
 */
public class Configuration {
    /**
     * Maximum number of characters per direct message
     */
    @SerializedName("dm_text_character_limit")
    public final int dmTextCharacterLimit;

    /**
     * Slugs which are not user names
     */
    @SerializedName("non_username_paths")
    public final List<String> nonUsernamePaths;

    /**
     * Maximum size in bytes for the media file.
     */
    @SerializedName("photo_size_limit")
    public final long photoSizeLimit;

    /**
     * Maximum resolution for the media files.
     */
    @SerializedName("photo_sizes")
    public final MediaEntity.Sizes photoSizes;

    /**
     * Current t.co URL length
     */
    @SerializedName("short_url_length_https")
    public final int shortUrlLengthHttps;

    private Configuration() {
        this(0, null, 0, null, 0);
    }

    public Configuration(int dmTextCharacterLimit, List<String> nonUsernamePaths,
            long photoSizeLimit, MediaEntity.Sizes photoSizes, int shortUrlLengthHttps) {
        this.dmTextCharacterLimit = dmTextCharacterLimit;
        this.nonUsernamePaths = ModelUtils.getSafeList(nonUsernamePaths);
        this.photoSizeLimit = photoSizeLimit;
        this.photoSizes = photoSizes;
        this.shortUrlLengthHttps = shortUrlLengthHttps;
    }
}
