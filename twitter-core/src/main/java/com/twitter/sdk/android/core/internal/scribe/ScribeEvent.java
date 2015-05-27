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

package com.twitter.sdk.android.core.internal.scribe;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import io.fabric.sdk.android.services.events.EventTransform;

import java.io.IOException;

public class ScribeEvent {
    /**
     * The current format version. This value is used to avoid ambiguity if scribe fields or their
     * definitions, are changed. This version is set by the client analytics team.
     * See go/mobileanalyticsguide.
     */
    private static final String CURRENT_FORMAT_VERSION = "2";

    /**
     * The event namespace describing what and where event occurred.
     * Required field.
     */
    @SerializedName("event_namespace")
    private final EventNamespace eventNamespace;
    /**
     * The time in ms since Jan 1, 1970 UTC that the event occurred.
     * Required field.
     */
    @SerializedName("ts")
    private final String timestamp;
    /**
     * The format version used to avoid ambiguity if scribe fields or their definitions, are
     * changed.
     * Required field.
     */
    @SerializedName("format_version")
    private final String formatVersion;

    @SerializedName("_category_")
    private final String category;

    public ScribeEvent(String category, EventNamespace eventNamespace, long timestamp) {
        this.category = category;
        this.eventNamespace = eventNamespace;
        this.timestamp = String.valueOf(timestamp);
        this.formatVersion =  CURRENT_FORMAT_VERSION;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("event_namespace=").append(eventNamespace)
                .append(", ts=").append(timestamp)
                .append(", format_version=").append(formatVersion)
                .append(", _category_=").append(category)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ScribeEvent that = (ScribeEvent) o;

        if (category != null ?
                !category.equals(that.category) : that.category != null) {
            return false;
        }
        if (eventNamespace != null ?
                !eventNamespace.equals(that.eventNamespace) : that.eventNamespace != null) {
            return false;
        }
        if (formatVersion != null ?
                !formatVersion.equals(that.formatVersion) : that.formatVersion != null) {
            return false;
        }
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = eventNamespace != null ? eventNamespace.hashCode() : 0;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (formatVersion != null ? formatVersion.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }

    public static class Transform implements EventTransform<ScribeEvent> {
        private final Gson gson;

        public Transform(Gson gson) {
            this.gson = gson;
        }

        @Override
        public byte[] toBytes(ScribeEvent event) throws IOException {
            return gson.toJson(event).getBytes("UTF-8");
        }
    }
}
