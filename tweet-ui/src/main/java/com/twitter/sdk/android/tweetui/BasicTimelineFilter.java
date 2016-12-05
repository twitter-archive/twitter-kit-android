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

import android.text.TextUtils;

import com.twitter.sdk.android.core.models.HashtagEntity;
import com.twitter.sdk.android.core.models.MentionEntity;
import com.twitter.sdk.android.core.models.SymbolEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.UrlEntity;

import java.net.IDN;
import java.text.BreakIterator;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import okhttp3.HttpUrl;

public class BasicTimelineFilter implements TimelineFilter {
    private final BreakIterator wordIterator;
    private final Set<String> keywordConstraints;
    private final Set<String> hashTagConstraints;
    private final Set<String> urlConstraints;
    private final Set<String> handleConstraints;

    public BasicTimelineFilter(FilterValues filterValues) {
        this(filterValues, Locale.getDefault());
    }

    public BasicTimelineFilter(FilterValues filterValues, Locale locale) {
        final Comparator<String> comparator = new IgnoreCaseComparator(locale);

        wordIterator = BreakIterator.getWordInstance(locale);
        keywordConstraints = new TreeSet<>(comparator);
        keywordConstraints.addAll(filterValues.keywords);

        hashTagConstraints = new TreeSet<>(comparator);
        for (String hashtag : filterValues.hashtags) {
            final String sanitizedHashtag = normalizeHashtag(hashtag);
            hashTagConstraints.add(sanitizedHashtag);
        }

        handleConstraints = new HashSet<>(filterValues.handles.size());
        for (String handle : filterValues.handles) {
            final String sanitizedHandle = normalizeHandle(handle);
            handleConstraints.add(sanitizedHandle);
        }

        urlConstraints = new HashSet<>(filterValues.urls.size());
        for (String url : filterValues.urls) {
            final String sanitizedUrl = normalizeUrl(url);
            urlConstraints.add(sanitizedUrl);
        }
    }

    @Override
    public List<Tweet> filter(List<Tweet> tweets) {
        final List<Tweet> filteredTweets = new ArrayList<>();
        for (int idx = 0; idx < tweets.size(); idx++) {
            final Tweet tweet = tweets.get(idx);
            if (!shouldFilterTweet(tweet)) {
                filteredTweets.add(tweet);
            }
        }

        return Collections.unmodifiableList(filteredTweets);
    }

    @Override
    public int totalFilters() {
        return keywordConstraints.size() + hashTagConstraints.size()
                + urlConstraints.size() + handleConstraints.size();
    }

    boolean shouldFilterTweet(Tweet tweet) {
        if (tweet.user != null &&
                containsMatchingScreenName(tweet.user.screenName)) {
            return true;
        }

        if (tweet.entities != null &&
                (containsMatchingHashtag(tweet.entities.hashtags) ||
                        containsMatchingSymbol(tweet.entities.symbols) ||
                        containsMatchingUrl(tweet.entities.urls) ||
                        containsMatchingMention(tweet.entities.userMentions))) {
            return true;
        }

        return containsMatchingText(tweet);
    }

    boolean containsMatchingText(Tweet tweet) {
        wordIterator.setText(tweet.text);
        int start = wordIterator.first();
        for (int end = wordIterator.next();
             end != BreakIterator.DONE;
             start = end, end = wordIterator.next()) {
            final String word = tweet.text.substring(start, end);

            if (keywordConstraints.contains(word)) {
                return true;
            }
        }

        return false;
    }

    boolean containsMatchingHashtag(List<HashtagEntity> hashtags) {
        for (HashtagEntity entity : hashtags) {
            if (hashTagConstraints.contains(entity.text)) {
                return true;
            }
        }

        return false;
    }

    boolean containsMatchingSymbol(List<SymbolEntity> symbols) {
        for (SymbolEntity entity : symbols) {
            if (hashTagConstraints.contains(entity.text)) {
                return true;
            }
        }

        return false;
    }

    boolean containsMatchingUrl(List<UrlEntity> urls) {
        for (UrlEntity entity : urls) {
            final String url = normalizeUrl(entity.expandedUrl);
            if (urlConstraints.contains(url)) {
                return true;
            }
        }

        return false;
    }

    boolean containsMatchingMention(List<MentionEntity> mentions) {
        for (MentionEntity entity : mentions) {
            final String name = normalizeHandle(entity.screenName);
            if (handleConstraints.contains(name)) {
                return true;
            }
        }

        return false;
    }

    boolean containsMatchingScreenName(String screenName) {
        final String name = normalizeHandle(screenName);
        return handleConstraints.contains(name);
    }

    static String normalizeUrl(String url) {
        try {
            final HttpUrl parsedUrl = HttpUrl.parse(url);
            if (parsedUrl == null || parsedUrl.host() == null) {
                return IDN.toASCII(url).toLowerCase(Locale.US);
            }

            return parsedUrl.host().toLowerCase(Locale.US);
        } catch (IllegalArgumentException e) {
            return url;
        }
    }

    static String normalizeHashtag(String hashtag) {
        if (TextUtils.isEmpty(hashtag)) {
            return hashtag;
        }

        final char firstChar = hashtag.charAt(0);
        if (firstChar == '#' || firstChar == '\uFF03' || firstChar == '$') {
            hashtag = hashtag.substring(1, hashtag.length());
        }

        return hashtag;
    }

    static String normalizeHandle(String handle) {
        if (TextUtils.isEmpty(handle)) {
            return handle;
        }

        final char firstChar = handle.charAt(0);
        if (firstChar == '@' || firstChar == '\uFF20') {
            handle = handle.substring(1, handle.length());
        }

        return handle.toLowerCase(Locale.US);
    }

    static class IgnoreCaseComparator implements Comparator<String> {
        private final Collator collator;

        IgnoreCaseComparator(Locale locale) {
            collator = Collator.getInstance(locale);
            collator.setStrength(Collator.PRIMARY);
        }

        public int compare(String string1, String string2) {
            return collator.compare(string1, string2);
        }
    }
}
