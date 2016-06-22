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

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * SyndicationClientEvent is nearly identical to SyndicatedSdkImpression. Events of this type will
 * end up in the /logs/tfw_client_event folder in hdfs.
 */
public class SyndicationClientEvent extends ScribeEvent {
    public static final String CLIENT_NAME = "tfw";
    private static final String SCRIBE_CATEGORY = "tfw_client_event";

    /**
     * The current language that the application is running in.
     * Optional field.
     */
    @SerializedName("language")
    public final String language;

    /**
     * Can be set to a free form String. Typically used by clients to record api errors.
     */
    @SerializedName("event_info")
    public final String eventInfo;

    /**
     * External Ids can contain other external ids (e.g. Facebook) but in our case we will only
     * scribe the advertising id.
     * Optional field.
     */
    @SerializedName("external_ids")
    public final ExternalIds externalIds;

    public SyndicationClientEvent(EventNamespace eventNamespace,  String eventInfo, long timestamp,
                                  String language, String adId, List<ScribeItem> items) {
        super(SCRIBE_CATEGORY, eventNamespace, timestamp, items);
        this.language = language;
        this.eventInfo = eventInfo;
        externalIds = new ExternalIds(adId);
    }

    public class ExternalIds {
        /**
         * The advertising id.
         *
         * Alert! This serialized name may seem wrong, however...
         * see: source/science/src/thrift/com/twitter/clientapp/gen/client_app.thrift for number
         * this is parsed by marshalExternalIds in
         * source/science/src/scala/com/twitter/scribelib/ClientEventMarshaller.java
         *
         * Optional field.
         */
        @SerializedName("6")
        public final String adId;

        public ExternalIds(String adId) {
            this.adId = adId;
        }
    }
}
