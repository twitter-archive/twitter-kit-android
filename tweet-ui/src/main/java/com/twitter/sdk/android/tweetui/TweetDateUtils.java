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

import android.content.res.Resources;
import android.support.v4.util.SparseArrayCompat;
import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// Cribbed from twitter-android-internal, renamed and formatted to our standards
// methods here should only be accessed on the main thread
final class TweetDateUtils {
    // Sat Mar 14 02:34:20 +0000 2009
    static final SimpleDateFormat DATE_TIME_RFC822
            = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
    static final DateFormatter RELATIVE_DATE_FORMAT = new DateFormatter();
    static final long INVALID_DATE = -1;

    private TweetDateUtils() {}

    static long apiTimeToLong(String apiTime) {
        if (apiTime == null) return INVALID_DATE;

        try {
            return DATE_TIME_RFC822.parse(apiTime).getTime();
        } catch (ParseException e) {
            return INVALID_DATE;
        }
    }

    static boolean isValidTimestamp(String timestamp) {
        return TweetDateUtils.apiTimeToLong(timestamp) != TweetDateUtils.INVALID_DATE;
    }

    /**
     * @return the given timestamp with a prepended "•"
     */
    static String dotPrefix(String timestamp) {
        return "• " + timestamp;
    }

    /**
     * This method is not thread safe. It has been modified from the original to not rely on global
     * time state. If a timestamp is in the future we return it as an absolute date string. Within
     * the same second we return 0s
     *
     * @param res resource
     * @param currentTimeMillis timestamp for offset
     * @param timestamp timestamp
     * @return the relative time string
     */
    static String getRelativeTimeString(Resources res, long currentTimeMillis, long timestamp) {
        final long diff = currentTimeMillis - timestamp;
        if (diff >= 0) {
            if (diff < DateUtils.MINUTE_IN_MILLIS) { // Less than a minute ago
                final int secs = (int) (diff / 1000);
                return res.getQuantityString(R.plurals.tw__time_secs, secs, secs);
            } else if (diff < DateUtils.HOUR_IN_MILLIS) { // Less than an hour ago
                final int mins = (int) (diff / DateUtils.MINUTE_IN_MILLIS);
                return res.getQuantityString(R.plurals.tw__time_mins, mins, mins);
            } else if (diff < DateUtils.DAY_IN_MILLIS) { // Less than a day ago
                final int hours = (int) (diff / DateUtils.HOUR_IN_MILLIS);
                return res.getQuantityString(R.plurals.tw__time_hours, hours, hours);
            } else {
                final Calendar now = Calendar.getInstance();
                now.setTimeInMillis(currentTimeMillis);
                final Calendar c = Calendar.getInstance();
                c.setTimeInMillis(timestamp);
                final Date d = new Date(timestamp);

                if (now.get(Calendar.YEAR) == c.get(Calendar.YEAR)) {
                    // Same year
                    return RELATIVE_DATE_FORMAT.formatShortDateString(res, d);
                } else {
                    // Outside of our year
                    return RELATIVE_DATE_FORMAT.formatLongDateString(res, d);
                }
            }
        }
        return RELATIVE_DATE_FORMAT.formatLongDateString(res, new Date(timestamp));
    }

    static class DateFormatter {
        private final SparseArrayCompat<SimpleDateFormat> dateFormatArray =
                new SparseArrayCompat<>();
        private Locale currentLocale;

        synchronized String formatLongDateString(Resources res, Date date) {
            return getDateFormat(res, R.string.tw__relative_date_format_long).format(date);
        }

        synchronized String formatShortDateString(Resources res, Date date) {
            return getDateFormat(res, R.string.tw__relative_date_format_short).format(date);
        }

        private synchronized DateFormat getDateFormat(Resources res, int patternId) {

            // Check if the locale changed, reference check for performance
            if (currentLocale == null || currentLocale != res.getConfiguration().locale) {
                currentLocale = res.getConfiguration().locale;
                dateFormatArray.clear();
            }

            SimpleDateFormat format = dateFormatArray.get(patternId);
            if (format == null) {
                // Create format if not cached
                final String pattern = res.getString(patternId);
                format = new SimpleDateFormat(pattern, Locale.getDefault());
                dateFormatArray.put(patternId, format);
            }
            return format;
        }
    }
}
