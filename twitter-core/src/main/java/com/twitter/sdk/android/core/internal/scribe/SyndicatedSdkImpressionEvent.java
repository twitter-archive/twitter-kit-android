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

import java.util.Collections;
import java.util.List;

/**
 * SyndicatedSdkImpressionEvent is nearly identical to SyndicationClientEvent but the events end up
 * in a different folder in hdfs with a different retention policy. This event type primarily exists
 * for counting unique users across 30 and 60 day periods.
 *
 * This class has been moved into TwitterCore until a final scribing category can be determined for
 * all first party twitter kit data.
 */
public class SyndicatedSdkImpressionEvent extends ScribeEvent {
    public static final String CLIENT_NAME = "android";
    private static final String SCRIBE_CATEGORY = "syndicated_sdk_impression";

    /**
     * External Ids can contain other external ids (e.g. Facebook) but in our case we will only
     * scribe the advertising id.
     * Required field.
     */
    @SerializedName("external_ids")
    public final ExternalIds externalIds;

    /**
     * When the app was initially installed. Currently we are not tracking the data that is
     * serialized in this field, so for now to reduce engineering effort we are putting 0 in as the
     * value. The semantic value of this field is to track when the install id was initially
     * created for the host application.
     * Required field.
     */
    @SerializedName("device_id_created_at")
    public final long deviceIdCreatedAt;

    /**
     * The current language that the application is running in.
     * Optional field.
     */
    @SerializedName("language")
    public final String language;

    public SyndicatedSdkImpressionEvent(EventNamespace eventNamespace, long timestamp,
                String language, String adId) {
        this(eventNamespace, timestamp, language, adId, Collections.emptyList());
    }

    public SyndicatedSdkImpressionEvent(EventNamespace eventNamespace, long timestamp,
            String language, String adId, List<ScribeItem> items) {
        super(SCRIBE_CATEGORY, eventNamespace, timestamp, items);
        this.language = language;
        this.externalIds = new ExternalIds(adId);
        this.deviceIdCreatedAt = 0; // see field comment
    }

    public class ExternalIds {
        /**
         * The advertising id.
         *
         * This is parsed differently than the external ids in SyndicationClientEvent.
         * The codification of ad_id as the key is in
         * science/src/scala/com/twitter/scribelib/SyndicationMobileImpressionsLogEventMarshaller.scala
         *
         * Optional field.
         */
        @SerializedName("AD_ID")
        public final String adId;

        public ExternalIds(String adId) {
            this.adId = adId;
        }
    }
}
