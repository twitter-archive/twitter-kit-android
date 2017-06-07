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
import java.util.Map;

/**
 * A place is a specific, named location with corresponding geo coordinates.
 */
public class Place {

    /**
     * Place Attributes are metadata about places. An attribute is a key-value pair of arbitrary
     * strings, but with some conventions.
     */
    @SerializedName("attributes")
    public final Map<String, String> attributes;

    /**
     * A bounding box of coordinates which encloses this place.
     */
    @SerializedName("bounding_box")
    public final BoundingBox boundingBox;

    /**
     * Name of the country containing this place.
     */
    @SerializedName("country")
    public final String country;

    /**
     * Shortened country code representing the country containing this place.
     */
    @SerializedName("country_code")
    public final String countryCode;

    /**
     * Full human-readable representation of the place's name.
     */
    @SerializedName("full_name")
    public final String fullName;

    /**
     * ID representing this place. Note that this is represented as a string, not an integer.
     */
    @SerializedName("id")
    public final String id;

    /**
     * Short human-readable representation of the place's name.
     */
    @SerializedName("name")
    public final String name;

    /**
     * The type of location represented by this place.
     */
    @SerializedName("place_type")
    public final String placeType;

    /**
     * URL representing the location of additional place metadata for this place.
     */
    @SerializedName("url")
    public final String url;

    public Place(Map<String, String> attributes, BoundingBox boundingBox, String country,
            String countryCode, String fullName, String id, String name, String placeType,
            String url) {
        this.attributes = ModelUtils.getSafeMap(attributes);
        this.boundingBox = boundingBox;
        this.country = country;
        this.countryCode = countryCode;
        this.fullName = fullName;
        this.id = id;
        this.name = name;
        this.placeType = placeType;
        this.url = url;
    }

    public static class BoundingBox {
        /**
         * A series of longitude and latitude points, defining a box which will contain the Place
         * entity this bounding box is related to. Each point is an array in the form of
         * [longitude, latitude]. Points are grouped into an array per bounding box. Bounding box
         * arrays are wrapped in one additional array to be compatible with the polygon notation.
         */
        @SerializedName("coordinates")
        public final List<List<List<Double>>> coordinates;

        /**
         * The type of data encoded in the coordinates property. This will be "Polygon" for bounding
         * boxes.
         */
        @SerializedName("type")
        public final String type;

        private BoundingBox() {
            this(null, null);
        }

        public BoundingBox(List<List<List<Double>>> coordinates, String type) {
            this.coordinates = ModelUtils.getSafeList(coordinates);
            this.type = type;
        }
    }
}
