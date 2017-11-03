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

import java.util.Collections;
import java.util.List;

public class ScribeEventFactory {

    public static ScribeEvent newScribeEvent(EventNamespace ns, long timestamp, String language,
            String advertisingId) {
        return newScribeEvent(ns, "", timestamp, language, advertisingId,
                Collections.emptyList());
    }

    public static ScribeEvent newScribeEvent(EventNamespace ns, String eventInfo, long timestamp,
                                             String language, String advertisingId,
                                             List<ScribeItem> items) {
        switch (ns.client) {
            case SyndicationClientEvent.CLIENT_NAME:
                return new SyndicationClientEvent(ns, eventInfo, timestamp, language, advertisingId,
                        items);
            default:
                return new SyndicatedSdkImpressionEvent(ns, timestamp, language, advertisingId,
                        items);
        }
    }
}
