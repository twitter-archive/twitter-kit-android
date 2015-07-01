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

package com.twitter.sdk.android.core.services.params;

/**
 *
 * The parameter value is specified by "latitude,longitude,radius", where radius units must be
 * specified as either "mi" (miles) or "km" (kilometers). Note that you cannot use the near
 * operator via the API to geocode arbitrary locations; however you can use this geocode parameter
 * to search near geocodes directly.
 *
 * Example Values: 37.781157,-122.398720,1mi
 *
 * <a href="https://dev.twitter.com/rest/reference/get/search/tweets">GET search/tweets</a>
 */
public class Geocode {

    public enum Distance {
        MILES("mi"),
        KILOMETERS("km");

        public final String identifier;

        Distance(String identifier) {
            this.identifier = identifier;
        }
    }

    public final double latitude;
    public final double longitude;
    public final int radius;
    public final Distance distance;

    public Geocode(double latitude, double longitude, int radius, Distance distance) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return latitude + "," + longitude + "," + radius + distance.identifier;
    }
}
