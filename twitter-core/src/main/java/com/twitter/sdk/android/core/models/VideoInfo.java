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
 * Contains information about video.
 */
public class VideoInfo implements Serializable {
    /**
     * The aspect ratio of the video, as a simplified fraction of width and height in a 2-element
     * list. Typical values are [4, 3] or [16, 9].
     */
    @SerializedName("aspect_ratio")
    public final List<Integer> aspectRatio;

    /**
     * The length of the video, in milliseconds.
     */
    @SerializedName("duration_millis")
    public final long durationMillis;

    /**
     * Different encodings/streams of the video.
     */
    @SerializedName("variants")
    public final List<Variant> variants;

    private VideoInfo() {
        this(null, 0, null);
    }

    public VideoInfo(List<Integer> aspectRatio, long durationMillis, List<Variant> variants) {
        this.aspectRatio = ModelUtils.getSafeList(aspectRatio);
        this.durationMillis = durationMillis;
        this.variants = ModelUtils.getSafeList(variants);
    }

    public static class Variant implements Serializable {
        @SerializedName("bitrate")
        public final long bitrate;

        @SerializedName("content_type")
        public final String contentType;

        @SerializedName("url")
        public final String url;

        public Variant(long bitrate, String contentType, String url) {
            this.bitrate = bitrate;
            this.contentType = contentType;
            this.url = url;
        }
    }
}
