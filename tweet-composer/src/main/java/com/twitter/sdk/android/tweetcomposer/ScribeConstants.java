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

package com.twitter.sdk.android.tweetcomposer;

import com.twitter.sdk.android.core.internal.scribe.EventNamespace;

final class ScribeConstants {
    // namespaces with client "tfw" become SyndicationClientEvent scribes to /logs/tfw_client_event
    static final String SCRIBE_TFW_CLIENT = "tfw";

    static final String SCRIBE_PAGE = "android";
    static final String SCRIBE_SECTION = "composer";
    static final String SCRIBE_COMPONENT = "";

    static final String SCRIBE_IMPRESSION_ELEMENT = "";
    static final String SCRIBE_TWEET_ELEMENT = "tweet";
    static final String SCRIBE_CANCEL_ELEMENT = "cancel";

    static final String SCRIBE_IMPRESSION_ACTION = "impression";
    static final String SCRIBE_CLICK_ACTION = "click";

    static final EventNamespace.Builder ComposerEventBuilder = new EventNamespace.Builder()
            .setClient(SCRIBE_TFW_CLIENT)
            .setPage(SCRIBE_PAGE)
            .setSection(SCRIBE_SECTION);

    private ScribeConstants() {}
}
