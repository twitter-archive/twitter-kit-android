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

/**
 * Represents other Twitter users mentioned in the text of the Tweet.
 */
public class MentionEntity extends Entity {

    /**
     * ID of the mentioned user, as an integer.
     */
    @SerializedName("id")
    public final long id;

    /**
     * ID of the mentioned user, as a string.
     */
    @SerializedName("id_str")
    public final String idStr;

    /**
     * Display name of the referenced user.
     */
    @SerializedName("name")
    public final String name;

    /**
     * Screen name of the referenced user.
     */
    @SerializedName("screen_name")
    public final String screenName;

    public MentionEntity(long id, String idStr, String name, String screenName, int start,
            int end) {
        super(start, end);
        this.id = id;
        this.idStr = idStr;
        this.name = name;
        this.screenName = screenName;
    }
}
