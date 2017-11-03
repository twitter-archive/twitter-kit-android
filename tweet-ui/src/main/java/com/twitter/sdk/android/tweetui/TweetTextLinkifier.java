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

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.view.View;

import com.twitter.sdk.android.core.models.ModelUtils;
import com.twitter.sdk.android.tweetui.internal.ClickableLinkSpan;
import com.twitter.sdk.android.tweetui.internal.TweetMediaUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

final class TweetTextLinkifier {
    static final Pattern QUOTED_STATUS_URL =
            Pattern.compile("^https?://twitter\\.com(/#!)?/\\w+/status/\\d+$");
    static final Pattern VINE_URL =
            Pattern.compile("^https?://vine\\.co(/#!)?/v/\\w+$");

    private TweetTextLinkifier() {}

    /**
     * Returns a charSequence with the display urls substituted in place of the t.co links. It will
     * strip off the last photo entity, quote Tweet, and Vine card urls in the text. The return
     * value can be set directly onto a text view.
     *
     * @param tweetText             The formatted and adjusted tweet wrapper
     * @param linkListener          A listener to handle link clicks
     * @param linkColor             The link color
     * @param linkHighlightColor    The link background color when pressed
     * @param stripQuoteTweet       If true we should strip the quote Tweet URL
     * @param stripVineCard         If true we should strip the Vine card URL
     * @return                      The Tweet text with displayUrls substituted in
     */
    static CharSequence linkifyUrls(FormattedTweetText tweetText,
                                    final LinkClickListener linkListener,
                                    final int linkColor, final int linkHighlightColor,
                                    boolean stripQuoteTweet, boolean stripVineCard) {
        if (tweetText == null) return null;

        if (TextUtils.isEmpty(tweetText.text)) {
            return tweetText.text;
        }

        final SpannableStringBuilder spannable = new SpannableStringBuilder(tweetText.text);
        final List<FormattedUrlEntity> urls = ModelUtils.getSafeList(tweetText.urlEntities);
        final List<FormattedMediaEntity> media = ModelUtils.getSafeList(tweetText.mediaEntities);
        final List<FormattedUrlEntity> hashtags = ModelUtils.getSafeList(tweetText.hashtagEntities);
        final List<FormattedUrlEntity> mentions = ModelUtils.getSafeList(tweetText.mentionEntities);
        final List<FormattedUrlEntity> symbols = ModelUtils.getSafeList(tweetText.symbolEntities);
        /*
         * We combine and sort the entities here so that we can correctly calculate the offsets
         * into the text.
         */
        final List<FormattedUrlEntity> combined = mergeAndSortEntities(urls, media, hashtags,
                mentions, symbols);
        final FormattedUrlEntity strippedEntity = getEntityToStrip(tweetText.text, combined,
                stripQuoteTweet, stripVineCard);

        addUrlEntities(spannable, combined, strippedEntity, linkListener, linkColor,
                linkHighlightColor);

        return trimEnd(spannable);
    }

    /**
     * Trim trailing whitespaces. Similar to String#trim(), but only for trailing characters.
     */
    static CharSequence trimEnd(CharSequence charSequence) {
        int length = charSequence.length();

        while ((length > 0) && (charSequence.charAt(length - 1) <= ' ')) {
            length--;
        }

        // Avoid creating new object if length hasn't changed
        return length < charSequence.length() ? charSequence.subSequence(0, length) : charSequence;
    }

    /**
     * Combines and sorts the two lists of entities, it only considers the start index as the
     * parameter to sort on because the api guarantees that we are to have non-overlapping entities.
     *
     * @param urls  Expected to be non-null
     * @param media Can be null
     * @return      Combined and sorted list of urls and media
     */
    static List<FormattedUrlEntity> mergeAndSortEntities(final List<FormattedUrlEntity> urls,
            final List<FormattedMediaEntity> media, final List<FormattedUrlEntity> hashtags,
            final List<FormattedUrlEntity> mentions, final List<FormattedUrlEntity> symbols) {
        final ArrayList<FormattedUrlEntity> combined = new ArrayList<>(urls);
        combined.addAll(media);
        combined.addAll(hashtags);
        combined.addAll(mentions);
        combined.addAll(symbols);
        Collections.sort(combined, (lhs, rhs) -> {
            if (lhs == null && rhs != null) return -1;
            if (lhs != null && rhs == null) return 1;
            if (lhs == null && rhs == null) return 0;
            if (lhs.start < rhs.start) return -1;
            if (lhs.start > rhs.start) return 1;

            return 0;
        });
        return combined;
    }

    /**
     * Swaps display urls in for t.co urls and adjusts the remaining entity indices.
     *
     * @param spannable          The final formatted text that we are building
     * @param entities           The combined list of media and url entities
     * @param strippedEntity     The trailing entity that we should strip from the text
     * @param linkListener       The link click listener to attach to the span
     * @param linkColor          The link color
     * @param linkHighlightColor The link background color when pressed
     */
    private static void addUrlEntities(final SpannableStringBuilder spannable,
            final List<FormattedUrlEntity> entities,
            final FormattedUrlEntity strippedEntity,
            final LinkClickListener linkListener,
            final int linkColor, final int linkHighlightColor) {
        if (entities == null || entities.isEmpty()) return;

        int offset = 0;
        int len;
        int start;
        int end;
        for (final FormattedUrlEntity url : entities) {
            start = url.start - offset;
            end = url.end - offset;
            if (start >= 0 && end <= spannable.length()) {
                // replace the last photo url with empty string, we can use the start indices as
                // as simple check, since none of this will work anyways if we have overlapping
                // entities
                if (strippedEntity != null && strippedEntity.start == url.start) {
                    spannable.replace(start, end, "");
                    len = end - start;
                    offset += len;
                } else if (!TextUtils.isEmpty(url.displayUrl)) {
                    spannable.replace(start, end, url.displayUrl);
                    len = end - (start + url.displayUrl.length());
                    end -= len;
                    offset += len;

                    final CharacterStyle span = new ClickableLinkSpan(linkHighlightColor,
                            linkColor, false) {
                        @Override
                        public void onClick(View widget) {
                            if (linkListener == null) return;
                            linkListener.onUrlClicked(url.url);
                        }
                    };
                    spannable.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }

    static FormattedUrlEntity getEntityToStrip(String tweetText, List<FormattedUrlEntity> combined,
                                               boolean stripQuoteTweet, boolean stripVineCard) {
        if (combined.isEmpty()) return null;

        final FormattedUrlEntity urlEntity = combined.get(combined.size() - 1);
        if (stripLtrMarker(tweetText).endsWith(urlEntity.url) && (isPhotoEntity(urlEntity) ||
                (stripQuoteTweet && isQuotedStatus(urlEntity)) ||
                (stripVineCard && isVineCard(urlEntity)))) {
            return urlEntity;
        }

        return null;
    }

    static String stripLtrMarker(String tweetText) {
        if (tweetText.endsWith(Character.toString('\u200E'))) {
            return tweetText.substring(0, tweetText.length() - 1);
        }

        return tweetText;
    }

    static boolean isPhotoEntity(final FormattedUrlEntity urlEntity) {
        return urlEntity instanceof FormattedMediaEntity &&
                TweetMediaUtils.PHOTO_TYPE.equals(((FormattedMediaEntity) urlEntity).type);
    }

    static boolean isQuotedStatus(final FormattedUrlEntity urlEntity) {
        return QUOTED_STATUS_URL.matcher(urlEntity.expandedUrl).find();
    }

    static boolean isVineCard(final FormattedUrlEntity urlEntity) {
        return VINE_URL.matcher(urlEntity.expandedUrl).find();
    }
}
