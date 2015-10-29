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

package com.twitter.sdk.android.tweetui;

import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.TweetEntities;

import java.util.List;

final class TweetEntityUtils {
    private static final String PHOTO_TYPE = "photo";

    private TweetEntityUtils() {}
    /**
     * This method gets the last photo entity out of the tweet, this is the photo to display inline
     *
     * @param entities The Tweet entities
     * @return         The last photo entity of
     */
    static MediaEntity getLastPhotoEntity(final TweetEntities entities) {
        if (entities == null) return null;

        final List<MediaEntity> mediaEntityList = entities.media;
        if (mediaEntityList == null || mediaEntityList.isEmpty()) return null;

        MediaEntity entity;
        for (int i = mediaEntityList.size() - 1; i >= 0; i--) {
            entity = mediaEntityList.get(i);
            if (entity.type != null && entity.type.equals(PHOTO_TYPE)) {
                return entity;
            }
        }
        return null;
    }

    /**
     * Returns true if there is a media entity with the type of "photo"
     *
     * @param entities The Tweet entities
     * @return         true if there is a media entity with the type of "photo"
     */
    static boolean hasPhotoUrl(TweetEntities entities) {
        return getLastPhotoEntity(entities) != null;
    }
}
