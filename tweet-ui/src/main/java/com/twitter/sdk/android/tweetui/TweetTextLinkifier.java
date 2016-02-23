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

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.view.View;

import com.twitter.sdk.android.tweetui.internal.ClickableLinkSpan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

final class TweetTextLinkifier {
    private static final String PHOTO_TYPE = "photo";

    private TweetTextLinkifier() {}

    /**
     * Returns a charSequence with the display urls substituted in place of the t.co links. It will
     * strip off the last photo entity in the text if stripLastPhotoEntity is true. The return
     * value can be set directly onto a text view.
     *
     * @param tweetText             The formatted and adjusted tweet wrapper
     * @param listener              A listener to handle link clicks
     * @param stripLastPhotoEntity  If true will strip the last photo entity from the linkified text
     * @return                      The Tweet text with displayUrls substituted in
     */
    static CharSequence linkifyUrls(FormattedTweetText tweetText,
            final LinkClickListener listener, boolean stripLastPhotoEntity, final int linkColor) {
        if (tweetText == null) return null;

        if (TextUtils.isEmpty(tweetText.text)) {
            return tweetText.text;
        }

        final SpannableStringBuilder spannable
                = new SpannableStringBuilder(tweetText.text);

        final List<FormattedUrlEntity> urls = tweetText.urlEntities;

        final List<FormattedMediaEntity> media
                = tweetText.mediaEntities;
        final FormattedMediaEntity lastPhoto;
        if (stripLastPhotoEntity) {
            lastPhoto = getLastPhotoEntity(tweetText);
        } else {
            lastPhoto = null;
        }

        /*
         * We combine and sort the entities here so that we can correctly calculate the offsets
         * into the text.
         */
        final List<FormattedUrlEntity> combined = mergeAndSortEntities(urls, media);

        addUrlEntities(spannable, combined, lastPhoto, listener, linkColor);
        return spannable;
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
            final List<FormattedMediaEntity> media) {
        if (media == null) return urls;

        final ArrayList<FormattedUrlEntity> combined
                = new ArrayList<>(urls);
        combined.addAll(media);
        Collections.sort(combined, new Comparator<FormattedUrlEntity>() {
            @Override
            public int compare(FormattedUrlEntity lhs, FormattedUrlEntity rhs) {
                if (lhs == null && rhs != null) return -1;
                if (lhs != null && rhs == null) return 1;
                if (lhs == null && rhs == null) return 0;
                if (lhs.start < rhs.start) return -1;
                if (lhs.start > rhs.start) return 1;

                return 0;
            }
        });
        return combined;
    }

    /**
     * Swaps display urls in for t.co urls and adjusts the remaining entity indices.
     *
     * @param spannable The final formatted text that we are building
     * @param entities  The combined list of media and url entities
     * @param lastPhoto If there is a final photo entity we should strip from the text
     * @param listener  The link click listener to attach to the span
     * @param linkColor The link color
     */
    private static void addUrlEntities(final SpannableStringBuilder spannable,
            final List<FormattedUrlEntity> entities,
            final FormattedMediaEntity lastPhoto,
            final LinkClickListener listener, final int linkColor) {
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
                if (lastPhoto != null && lastPhoto.start == url.start) {
                    spannable.replace(start, end, "");
                    len = end - start;
                    end -= len;
                    offset += len;
                } else if (!TextUtils.isEmpty(url.displayUrl)) {
                    spannable.replace(start, end, url.displayUrl);
                    len = end - (start + url.displayUrl.length());
                    end -= len;
                    offset += len;

                    final CharacterStyle span = new ClickableLinkSpan(Color.TRANSPARENT,
                            linkColor, false) {
                        @Override
                        public void onClick(View widget) {
                            if (listener == null) return;
                            listener.onUrlClicked(url.url);
                        }
                    };
                    spannable.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }

    private static FormattedMediaEntity getLastPhotoEntity(
            final FormattedTweetText formattedTweetText) {
        if (formattedTweetText == null) return null;

        final List<FormattedMediaEntity> mediaEntityList
                = formattedTweetText.mediaEntities;
        if (mediaEntityList.isEmpty()) return null;

        FormattedMediaEntity entity;
        for (int i = mediaEntityList.size() - 1; i >= 0; i--) {
            entity = mediaEntityList.get(i);
            if (PHOTO_TYPE.equals(entity.type)) {
                return entity;
            }
        }
        return null;
    }
}
