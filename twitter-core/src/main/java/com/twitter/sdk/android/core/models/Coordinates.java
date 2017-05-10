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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents coordinates of a Tweet's location.
 */
public class Coordinates {

    public static final int INDEX_LONGITUDE = 0;
    public static final int INDEX_LATITUDE = 1;

    /**
     * The longitude and latitude of the Tweet's location, as an collection in the form of
     * [longitude, latitude].
     */
    @SerializedName("coordinates")
    public final List<Double> coordinates;

    /**
     * The type of data encoded in the coordinates property. This will be "Point" for Tweet
     * coordinates fields.
     */
    @SerializedName("type")
    public final String type;

    public Coordinates(Double longitude, Double latitude, String type) {
        final List<Double> coords = new ArrayList<>(2);
        coords.add(INDEX_LONGITUDE, longitude);
        coords.add(INDEX_LATITUDE, latitude);

        this.coordinates = ModelUtils.getSafeList(coords);
        this.type = type;
    }

    public Double getLongitude() {
        return coordinates.get(INDEX_LONGITUDE);
    }

    public Double getLatitude() {
        return coordinates.get(INDEX_LATITUDE);
    }
}
