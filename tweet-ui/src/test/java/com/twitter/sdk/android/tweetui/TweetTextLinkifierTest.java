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

import com.twitter.sdk.android.core.models.HashtagEntity;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.MentionEntity;
import com.twitter.sdk.android.core.models.SymbolEntity;
import com.twitter.sdk.android.core.models.UrlEntity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
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
        formattedText.urlEntities.add(FormattedUrlEntity.createFormattedUrlEntity(urlEntity));

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
        formattedText.urlEntities.add(FormattedUrlEntity.createFormattedUrlEntity(urlEntity));

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
        formattedText.urlEntities.add(FormattedUrlEntity.createFormattedUrlEntity(urlEntity));

        final SpannableStringBuilder linkifiedText = (SpannableStringBuilder)
                TweetTextLinkifier.linkifyUrls(formattedText, mockClickListener, 0, 0, true,
                        true);
        final ClickableSpan[] clickables =
                linkifiedText.getSpans(urlEntity.getStart(), urlEntity.getEnd(),
                        ClickableSpan.class);
        assertEquals(1, clickables.length);
    }

    @Test
    public void testLinkifyHashtags_oneHashtagEntity() {
        final String hashtag = "TwitterForGood";
        final String fullHashtag = "#" + hashtag;
        final String fullText = BASE_TEXT + " " + fullHashtag;
        final HashtagEntity hashtagEntity = EntityFactory.newHashtagEntity(fullText, hashtag);

        final FormattedTweetText formattedText = new FormattedTweetText();
        formattedText.text = fullText;
        formattedText.hashtagEntities.add(FormattedUrlEntity.createFormattedUrlEntity(
                hashtagEntity));

        final CharSequence linkifiedText
                = TweetTextLinkifier.linkifyUrls(formattedText, null, 0, 0, true, true);
        final String displayUrlFromEntity = linkifiedText.subSequence(hashtagEntity.getStart(),
                        hashtagEntity.getEnd()).toString();
        assertEquals(fullHashtag, displayUrlFromEntity);
    }

    @Test
    public void testLinkifyHashtags_linkClickListener() {
        final String hashtag = "TwitterForGood";
        final String fullText = BASE_TEXT + " #" + hashtag;

        final LinkClickListener mockClickListener = mock(LinkClickListener.class);

        final HashtagEntity hashtagEntity = EntityFactory.newHashtagEntity(fullText, hashtag);
        final FormattedTweetText formattedText = new FormattedTweetText();
        formattedText.text = fullText;
        formattedText.hashtagEntities.add(FormattedUrlEntity.createFormattedUrlEntity(
                hashtagEntity));

        final SpannableStringBuilder linkifiedText = (SpannableStringBuilder)
                TweetTextLinkifier.linkifyUrls(formattedText, mockClickListener, 0, 0, true,
                        true);
        final ClickableSpan[] clickables =
                linkifiedText.getSpans(hashtagEntity.getStart(), hashtagEntity.getEnd(),
                        ClickableSpan.class);
        assertEquals(1, clickables.length);
    }

    @Test
    public void testLinkifyMentions_oneMentionEntity() {
        final String mention = "TwitterDev";
        final String fullMention = "@" + mention;
        final String fullText = BASE_TEXT + " " + fullMention;
        final MentionEntity mentionEntity = EntityFactory.newMentionEntity(fullText, mention);

        final FormattedTweetText formattedText = new FormattedTweetText();
        formattedText.text = fullText;
        formattedText.mentionEntities.add(FormattedUrlEntity.createFormattedUrlEntity(
                mentionEntity));

        final CharSequence linkifiedText
                = TweetTextLinkifier.linkifyUrls(formattedText, null, 0, 0, true, true);
        final String displayUrlFromEntity = linkifiedText.subSequence(mentionEntity.getStart(),
                mentionEntity.getEnd()).toString();
        assertEquals(fullMention, displayUrlFromEntity);
    }

    @Test
    public void testLinkifyMentions_linkClickListener() {
        final String mention = "TwitterDev";
        final String fullText = BASE_TEXT + " @" + mention;

        final LinkClickListener mockClickListener = mock(LinkClickListener.class);

        final MentionEntity mentionEntity = EntityFactory.newMentionEntity(fullText, mention);
        final FormattedTweetText formattedText = new FormattedTweetText();
        formattedText.text = fullText;
        formattedText.mentionEntities.add(FormattedUrlEntity.createFormattedUrlEntity(
                mentionEntity));

        final SpannableStringBuilder linkifiedText = (SpannableStringBuilder)
                TweetTextLinkifier.linkifyUrls(formattedText, mockClickListener, 0, 0, true,
                        true);
        final ClickableSpan[] clickables =
                linkifiedText.getSpans(mentionEntity.getStart(), mentionEntity.getEnd(),
                        ClickableSpan.class);
        assertEquals(1, clickables.length);
    }

    @Test
    public void testLinkifySymbols_oneSymbolEntity() {
        final String symbol = "TWTR";
        final String fullSymbol = "$" + symbol;
        final String fullText = BASE_TEXT + " " + fullSymbol;
        final SymbolEntity symbolEntity = EntityFactory.newSymbolEntity(fullText, symbol);

        final FormattedTweetText formattedText = new FormattedTweetText();
        formattedText.text = fullText;
        formattedText.symbolEntities.add(FormattedUrlEntity.createFormattedUrlEntity(
                symbolEntity));

        final CharSequence linkifiedText
                = TweetTextLinkifier.linkifyUrls(formattedText, null, 0, 0, true, true);
        final String displayUrlFromEntity = linkifiedText.subSequence(symbolEntity.getStart(),
                symbolEntity.getEnd()).toString();
        assertEquals(fullSymbol, displayUrlFromEntity);
    }

    @Test
    public void testLinkifySymbols_linkClickListener() {
        final String symbol = "TWTR";
        final String fullText = BASE_TEXT + " $" + symbol;

        final LinkClickListener mockClickListener = mock(LinkClickListener.class);

        final SymbolEntity symbolEntity = EntityFactory.newSymbolEntity(fullText, symbol);
        final FormattedTweetText formattedText = new FormattedTweetText();
        formattedText.text = fullText;
        formattedText.symbolEntities.add(FormattedUrlEntity.createFormattedUrlEntity(
                symbolEntity));

        final SpannableStringBuilder linkifiedText = (SpannableStringBuilder)
                TweetTextLinkifier.linkifyUrls(formattedText, mockClickListener, 0, 0, true,
                        true);
        final ClickableSpan[] clickables =
                linkifiedText.getSpans(symbolEntity.getStart(), symbolEntity.getEnd(),
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
        final FormattedUrlEntity formattedUrlEntity = new FormattedUrlEntity(urlEntity.getStart(),
                urlEntity.getEnd(), urlEntity.displayUrl, urlEntity.url, urlEntity.expandedUrl);
        assertTrue(TweetTextLinkifier.isQuotedStatus(formattedUrlEntity));
    }

    @Test
    public void testIsVineCard_withVineUrl() {
        final UrlEntity urlEntity = new UrlEntity("https://t.co/NdpqweoNbi",
                "https://vine.co/v/eVmZVXbeDK1", "vine.co/v/eVmZVXbeDK1", 1, 23);
        final FormattedUrlEntity formattedUrlEntity = new FormattedUrlEntity(urlEntity.getStart(),
                urlEntity.getEnd(), urlEntity.displayUrl, urlEntity.url, urlEntity.expandedUrl);

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
        formattedText.urlEntities.add(FormattedUrlEntity.createFormattedUrlEntity(urlEntity));
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
    public void testMergeAndSortEntities_emptyEntities() {
        final List<FormattedUrlEntity> urls = new ArrayList<>();
        final List<FormattedMediaEntity> media = new ArrayList<>();
        final List<FormattedUrlEntity> hashtags = new ArrayList<>();
        final List<FormattedUrlEntity> mentions = new ArrayList<>();
        final List<FormattedUrlEntity> symbols = new ArrayList<>();
        assertEquals(urls, TweetTextLinkifier.mergeAndSortEntities(urls, media, hashtags,
                mentions, symbols));
    }

    @Test
    public void testMergeAndSortEntities_sortUrlsAndMediaAndHashtags() {
        final List<FormattedUrlEntity> urls = new ArrayList<>();
        final UrlEntity urlEntity = TestFixtures.newUrlEntity(2, 5);
        final FormattedUrlEntity adjustedUrl = FormattedUrlEntity.createFormattedUrlEntity(
                urlEntity);
        urls.add(adjustedUrl);

        final List<FormattedMediaEntity> media = new ArrayList<>();
        final MediaEntity photo = TestFixtures.newMediaEntity(1, 5, "photo");
        final FormattedMediaEntity adjustedPhoto = new FormattedMediaEntity(photo);
        media.add(adjustedPhoto);

        final List<FormattedUrlEntity> hashtags = new ArrayList<>();
        final HashtagEntity hashtag = TestFixtures.newHashtagEntity("TwitterForGood", 0, 13);
        final FormattedUrlEntity adjustedHashtag =
                FormattedUrlEntity.createFormattedUrlEntity(hashtag);
        hashtags.add(adjustedHashtag);

        final List<FormattedUrlEntity> mentions = new ArrayList<>();
        final MentionEntity mention = TestFixtures.newMentionEntity("twitterdev", 0, 9);
        final FormattedUrlEntity adjustedMention =
                FormattedUrlEntity.createFormattedUrlEntity(mention);
        mentions.add(adjustedMention);

        final List<FormattedUrlEntity> symbols = new ArrayList<>();
        final SymbolEntity symbol = TestFixtures.newSymbolEntity("TWTR", 0, 3);
        final FormattedUrlEntity adjustedSymbol =
                FormattedUrlEntity.createFormattedUrlEntity(symbol);
        symbols.add(adjustedSymbol);

        final List<? extends FormattedUrlEntity> combined
                = TweetTextLinkifier.mergeAndSortEntities(urls, media, hashtags, mentions, symbols);
        assertEquals(adjustedPhoto, combined.get(3));
        assertEquals(adjustedUrl, combined.get(4));
        assertEquals(adjustedHashtag, combined.get(0));
        assertEquals(adjustedMention, combined.get(1));
        assertEquals(adjustedSymbol, combined.get(2));
    }
}
