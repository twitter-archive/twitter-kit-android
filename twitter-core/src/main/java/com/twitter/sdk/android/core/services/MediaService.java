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

package com.twitter.sdk.android.core.services;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.Media;

import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public interface MediaService {

    /**
     * Uploads media (images) to Twitter for use in a Tweet or Twitter-hosted Card. You may
     * upload the raw binary file or its base64 encoded contents. The media and media_data
     * parameters are mutually exclusive. Media uploads for images are limited to 5MB in file
     * size.
     * Supported MIME-types are PNG, JPEG, BMP, WEBP, GIF, and Animated Gif
     * @param media the raw binary file content to upload. Cannot be used with the mediaData
     *              parameter.
     * @param mediaData the base64-encoded file content to upload. Cannot be used with the media
     *                  parameter
     * @param cb The callback to invoke when the request completes.
     */
    @Multipart
    @POST("/1.1/media/upload.json")
    void upload(@Part("media") TypedFile media,
                @Part("media_data") TypedFile mediaData,
                @Part("additional_owners") TypedString additionalOwners,
                Callback<Media> cb);
}
