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

import com.twitter.sdk.android.core.models.Media;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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
     */
    @Multipart
    @POST("https://upload.twitter.com/1.1/media/upload.json")
    Call<Media> upload(@Part("media") RequestBody media,
                @Part("media_data") RequestBody mediaData,
                @Part("additional_owners") RequestBody additionalOwners);
}
