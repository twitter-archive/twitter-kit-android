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
import android.text.style.ClickableSpan;

import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.UrlEntity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TweetTextLinkifierTest {
    static final String BASE_TEXT = "just setting up my twttr";
    static final String TEST_RLT_STRING = "ייִדיש משלי https://t.co/sfb4Id7esk\u200E";

    @Test
    public void testLinkifyUrls_nullFormattedTweetText() {
        try {
            TweetTextLinkifier.linkifyUrls(null, null, 0, 0, true, true);
        } catch (Exception e) {
            fail("threw unexpected exception");
        }
    }

    @Test
    public void testLinkifyUrls_newFormattedTweetText() {
        try {
            TweetTextLinkifier.linkifyUrls(new FormattedTweetText(), null, 0, 0, true, true);
        } catch (Exception e) {
            fail("threw unexpected exception");
        }
    }

    @Test
    public void testLinkifyUrls_oneUrlEntity() {
        final String url = "http://t.co/foo";
        final String displayUrl = "dev.twitter.com";
        final String fullText = BASE_TEXT + " " + "http://t.co/foo";
        final UrlEntity urlEntity
                = EntityFactory.newUrlEntity(fullText, url, displayUrl);

        final FormattedTweetText formattedText = new FormattedTweetText();
        formattedText.text = fullText;
        formattedText.urlEntities.add(new FormattedUrlEntity(urlEntity));

        final CharSequence linkifiedText
                = TweetTextLinkifier.linkifyUrls(formattedText, null, 0, 0, true, true);
        final String displayUrlFromEntity =
                linkifiedText.subSequence(urlEntity.getStart(), urlEntity.getEnd()).toString();
        assertEquals(urlEntity.displayUrl, displayUrlFromEntity);
    }

    @Test
    public void testLinkifyUrls_oneInvalidUrlEntity() {
        final String fullText = "";
        final UrlEntity urlEntity = new UrlEntity("x z", "y", "z", -1, 30);
        final FormattedTweetText formattedText = new FormattedTweetText();
        formattedText.text = fullText;
        formattedText.urlEntities.add(new FormattedUrlEntity(urlEntity));

        final CharSequence linkifiedText
                = TweetTextLinkifier.linkifyUrls(formattedText, null, 0, 0, true, true);
        assertEquals("", linkifiedText.toString());
    }

    @Test
    public void testLinkifyUrls_linkClickListener() {
        final String url = "http://t.co/foo";
        final String displayUrl = "dev.twitter.com";
        final String fullText = BASE_TEXT + " " + "http://t.co/foo";

        final LinkClickListener mockClickListener = mock(LinkClickListener.class);

        final UrlEntity urlEntity =
                EntityFactory.newUrlEntity(fullText, url, displayUrl);
        final FormattedTweetText formattedText = new FormattedTweetText();
        formattedText.text = fullText;
        formattedText.urlEntities.add(new FormattedUrlEntity(urlEntity));

        final SpannableStringBuilder linkifiedText = (SpannableStringBuilder)
                TweetTextLinkifier.linkifyUrls(formattedText, mockClickListener, 0, 0, true, true);
        final ClickableSpan[] clickables =
                linkifiedText.getSpans(urlEntity.getStart(), urlEntity.getEnd(),
                        ClickableSpan.class);
        assertEquals(1, clickables.length);
    }

    @Test
    public void testLinkifyUrls_verifyPhotoOnlyStrippedFromEnd() {
        final FormattedTweetText formattedText = setupPicTwitterEntities();
        final FormattedMediaEntity lastPhotoUrl = formattedText.mediaEntities.get(0);
        final CharSequence linkifiedText
                = TweetTextLinkifier.linkifyUrls(formattedText, null, 0, 0, true, true);

        // make sure we are stripping out a photo entity since it is the only media entity
        // that we can render inline
        assertEquals("photo", lastPhotoUrl.type);
        // assert that we do not strip it here and display it in the middle
        assertTrue(linkifiedText.toString().contains(lastPhotoUrl.displayUrl));
    }

    @Test
    public void testGetEntityToStrip_withLtrMarker() {
        final String result = TweetTextLinkifier.stripLtrMarker(TEST_RLT_STRING);

        assertNotEquals(TEST_RLT_STRING, result);
        assertFalse(result.endsWith(Character.toString('\u200E')));
    }

    @Test
    public void testGetEntityToStrip_withoutLtrMarker() {
        final String result = TweetTextLinkifier.stripLtrMarker(BASE_TEXT);

        assertEquals(BASE_TEXT, result);
        assertFalse(result.endsWith(Character.toString('\u200E')));
    }

    @Test
    public void testIsPhotoEntity_withPhotoUrl() {
        final MediaEntity mediaEntity = new MediaEntity("http://t.co/PFHCdlr4i0", null,
                "pic.twitter.com/abc", 27, 49, 0L, null, null, null, null, 0L, null, "photo", null,
                "");
        final FormattedUrlEntity formattedUrlEntity = new FormattedMediaEntity(mediaEntity);

        assertTrue(TweetTextLinkifier.isPhotoEntity(formattedUrlEntity));
    }

    @Test
    public void testIsQuotedStatus_withQuotedStatusUrl() {
        final UrlEntity urlEntity = new UrlEntity("https://t.co/kMXdOEnVMg",
                "https://twitter.com/nasajpl/status/634475698174865408",
                "twitter.com/nasajpl/status\u2026", 50, 72);
        final FormattedUrlEntity formattedUrlEntity = new FormattedUrlEntity(urlEntity);

        assertTrue(TweetTextLinkifier.isQuotedStatus(formattedUrlEntity));
    }

    @Test
    public void testIsVineCard_withVineUrl() {
        final UrlEntity urlEntity = new UrlEntity("https://t.co/NdpqweoNbi",
                "https://vine.co/v/eVmZVXbeDK1", "vine.co/v/eVmZVXbeDK1", 1, 23);
        final FormattedUrlEntity formattedUrlEntity = new FormattedUrlEntity(urlEntity);

        assertTrue(TweetTextLinkifier.isVineCard(formattedUrlEntity));
    }

    private FormattedTweetText setupPicTwitterEntities() {
        final String text = "first link is a pictwitter http://t.co/PFHCdlr4i0 " +
                "http://t.co/V3hLRdFdeN final text";

        final MediaEntity mediaEntity = new MediaEntity("http://t.co/PFHCdlr4i0", null,
                "pic.twitter.com/abc", 27, 49, 0L, null, null, null, null, 0L, null, "photo", null,
                "");

        final UrlEntity urlEntity = new UrlEntity("http://t.co/V3hLRdFdeN", null, "example.com", 50,
                72);

        final FormattedTweetText formattedText = new FormattedTweetText();
        formattedText.text = text;
        formattedText.urlEntities.add(new FormattedUrlEntity(urlEntity));
        formattedText.mediaEntities.add(new FormattedMediaEntity(mediaEntity));

        return formattedText;
    }

    @Test
    public void testTrimEnd_withoutTrailingSpace() {
        assertSame(BASE_TEXT, TweetTextLinkifier.trimEnd(BASE_TEXT));
    }

    @Test
    public void testTrimEnd_withTrailingSpace() {
        final CharSequence result = TweetTextLinkifier.trimEnd(BASE_TEXT + "\n\r\t ");
        assertEquals(BASE_TEXT, result);
        assertNotSame(BASE_TEXT, result);
    }

    /*
     * mergeAndSortEntities method
     */
    @Test
    public void testMergeAndSortEntities_nullMedia() {
        final List<FormattedUrlEntity> urls
                = new ArrayList<>();
        assertEquals(urls, TweetTextLinkifier.mergeAndSortEntities(urls, null));
    }

    @Test
    public void testMergeAndSortEntities_sortUrlsAndMedia() {
        final List<FormattedUrlEntity> urls = new ArrayList<>();
        final UrlEntity url = TestFixtures.newUrlEntity(2, 5);
        final FormattedUrlEntity adjustedUrl = new FormattedUrlEntity(url);
        urls.add(adjustedUrl);

        final List<FormattedMediaEntity> media = new ArrayList<>();
        final MediaEntity photo = TestFixtures.newMediaEntity(1, 5, "photo");
        final FormattedMediaEntity adjustedPhoto = new FormattedMediaEntity(photo);
        media.add(adjustedPhoto);

        final List<? extends FormattedUrlEntity> combined
                = TweetTextLinkifier.mergeAndSortEntities(urls, media);
        assertEquals(adjustedPhoto, combined.get(0));
        assertEquals(adjustedUrl, combined.get(1));
    }
}
