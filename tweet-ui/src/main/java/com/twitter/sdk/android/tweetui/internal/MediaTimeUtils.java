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

package com.twitter.sdk.android.tweetui.internal;

import java.util.Locale;

final class MediaTimeUtils {
    private static final String TIME_FORMAT_LONG = "%1$d:%2$02d:%3$02d";
    private static final String TIME_FORMAT_SHORT = "%1$d:%2$02d";

    private MediaTimeUtils() {}

    static String getPlaybackTime(long timeMillis) {
        final int timeSeconds = (int) (timeMillis / 1000);
        final int seconds = timeSeconds % 60;
        final int minutes = (timeSeconds / 60) % 60;
        final int hours = timeSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.getDefault(), TIME_FORMAT_LONG, hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), TIME_FORMAT_SHORT, minutes, seconds);
        }
    }
}
