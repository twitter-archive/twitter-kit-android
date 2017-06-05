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

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds values we need to correctly render tweet text. The values returned directly
 * from the REST API are html escaped for & < and > characters as well as not counting emoji
 * characters correctly in the entity indices.
 */
class FormattedTweetText {
    String text;
    final List<FormattedUrlEntity> urlEntities;
    final List<FormattedMediaEntity> mediaEntities;
    final List<FormattedUrlEntity> hashtagEntities;
    final List<FormattedUrlEntity> mentionEntities;
    final List<FormattedUrlEntity> symbolEntities;

    FormattedTweetText() {
        urlEntities = new ArrayList<>();
        mediaEntities = new ArrayList<>();
        hashtagEntities = new ArrayList<>();
        mentionEntities = new ArrayList<>();
        symbolEntities = new ArrayList<>();
    }
}
