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

package com.twitter.sdk.android.tweetui;

import com.twitter.sdk.android.core.models.HashtagEntity;
import com.twitter.sdk.android.core.models.MentionEntity;
import com.twitter.sdk.android.core.models.SymbolEntity;
import com.twitter.sdk.android.core.models.UrlEntity;

class EntityFactory {

    public static UrlEntity newUrlEntity(String text, String url, String displayUrl) {
        final int start = text.length() - url.length();
        final int end = text.length();

        return new UrlEntity(url, "http://" + displayUrl, displayUrl, start, end);
    }

    public static HashtagEntity newHashtagEntity(String text, String hashtag) {
        final int start = text.length() - hashtag.length() - 1;
        final int end = text.length();

        return new HashtagEntity(hashtag, start, end);
    }

    public static MentionEntity newMentionEntity(String text, String screenName) {
        final int start = text.length() - screenName.length() - 1;
        final int end = text.length();

        return new MentionEntity(100, "100", screenName, screenName, start, end);
    }

    public static SymbolEntity newSymbolEntity(String text, String symbol) {
        final int start = text.length() - symbol.length() - 1;
        final int end = text.length();

        return new SymbolEntity(symbol, start, end);
    }
}
