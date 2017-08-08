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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.twitter.Validator;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.services.AccountService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import retrofit2.Call;

import static com.twitter.sdk.android.tweetcomposer.TweetUploadService.TWEET_COMPOSE_CANCEL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ComposerControllerTest {
    private static final String TWEET_TEXT = "some text";
    private static final int REMAINING_CHAR_COUNT = 131;
    private static final int OVERFLOW_REMAINING_CHAR_COUNT = -3;
    private static final String ANY_TEXT = "text";
    private static final String ANY_HASHTAG = "#hashtag";
    private ComposerController controller;
    private ComposerView mockComposerView;
    private Context mockContext;
    private TwitterAuthToken mockAuthToken;
    private TwitterSession mockTwitterSession;
    private AccountService mockAccountService;
    private ComposerActivity.Finisher mockFinisher;
    private ComposerScribeClient mockComposerScribeClient;
    private ComposerController.DependencyProvider mockDependencyProvider;

    @Before
    public void setUp() throws Exception {
        mockComposerView = mock(ComposerView.class);
        mockContext = mock(Context.class);
        when(mockComposerView.getContext()).thenReturn(mockContext);

        mockFinisher = mock(ComposerActivity.Finisher.class);
        mockAuthToken = mock(TwitterAuthToken.class);
        mockTwitterSession = mock(TwitterSession.class);
        when(mockTwitterSession.getAuthToken()).thenReturn(mockAuthToken);

        final TwitterApiClient mockTwitterApiClient = mock(TwitterApiClient.class);
        mockAccountService = mock(AccountService.class);
        when(mockAccountService
                .verifyCredentials(any(Boolean.class), any(Boolean.class), any(Boolean.class)))
                .thenReturn(mock(Call.class));
        when(mockTwitterApiClient.getAccountService()).thenReturn(mockAccountService);

        mockComposerScribeClient = mock(ComposerScribeClient.class);

        mockDependencyProvider = mock(ComposerController.DependencyProvider.class);
        when(mockDependencyProvider.getApiClient(any(TwitterSession.class)))
                .thenReturn(mockTwitterApiClient);
        when(mockDependencyProvider.getTweetValidator()).thenReturn(new Validator());
        when(mockDependencyProvider.getScribeClient()).thenReturn(mockComposerScribeClient);
    }

    @Test
    public void testComposerController() {
        controller = new ComposerController(mockComposerView, mockTwitterSession, Uri.EMPTY,
                ANY_TEXT, ANY_HASHTAG, mockFinisher, mockDependencyProvider);
        assertEquals(mockTwitterSession, controller.session);
        // assert that
        // - sets callbacks on the view
        // - sets initial Tweet text and cursor position
        // - gets a TwitterApiClient AccountService to set the profile photo
        // - sets card view in composer
        // - scribes a Tweet Composer impression
        verify(mockComposerView).setCallbacks(any(ComposerController.ComposerCallbacks.class));
        verify(mockComposerView).setTweetText(ANY_TEXT + " " + ANY_HASHTAG);
        verify(mockComposerView).setImageView(Uri.EMPTY);
        verify(mockDependencyProvider).getApiClient(mockTwitterSession);
        verify(mockAccountService).verifyCredentials(eq(false), eq(true), eq(false));
        verify(mockComposerScribeClient).impression();
    }

    @Test
    public void testTweetTextLength() {
        controller = new ComposerController(mockComposerView, mockTwitterSession, Uri.EMPTY,
                ANY_TEXT, ANY_HASHTAG, mockFinisher, mockDependencyProvider);

        assertEquals(0, controller.tweetTextLength(null));
        assertEquals(0, controller.tweetTextLength(""));
        assertEquals(1, controller.tweetTextLength("☃"));
        assertEquals(5, controller.tweetTextLength("tweet"));
        assertEquals(39, controller.tweetTextLength("tweet with link https://example.com"));
        assertEquals(23, controller.tweetTextLength("https://example.com/foo/bar/foo"));
    }

    @Test
    public void testRemainingCharCount() {
        assertEquals(140, ComposerController.remainingCharCount(0));
        assertEquals(139, ComposerController.remainingCharCount(1));
        assertEquals(0, ComposerController.remainingCharCount(140));
        assertEquals(-1, ComposerController.remainingCharCount(141));
    }

    @Test
    public void testIsPostEnabled() {
        assertFalse(ComposerController.isPostEnabled(0));
        assertTrue(ComposerController.isPostEnabled(1));
        assertTrue(ComposerController.isPostEnabled(140));
        assertFalse(ComposerController.isPostEnabled(141));
    }

    @Test
    public void testIsTweetTextOverflow() {
        assertFalse(ComposerController.isTweetTextOverflow(0));
        assertFalse(ComposerController.isTweetTextOverflow(1));
        assertFalse(ComposerController.isTweetTextOverflow(140));
        assertTrue(ComposerController.isTweetTextOverflow(141));
    }

    @Test
    public void testComposerCallbacksImpl_onTextChangedOk() {
        mockTwitterSession = mock(TwitterSession.class);
        controller = new ComposerController(mockComposerView, mockTwitterSession, Uri.EMPTY,
                ANY_TEXT, ANY_HASHTAG, mockFinisher, mockDependencyProvider);
        final ComposerController.ComposerCallbacks callbacks
                = controller.new ComposerCallbacksImpl();
        callbacks.onTextChanged(TWEET_TEXT);

        verify(mockComposerView).setCharCount(REMAINING_CHAR_COUNT);
        verify(mockComposerView).setCharCountTextStyle(R.style.tw__ComposerCharCount);
        verify(mockComposerView).postTweetEnabled(true);
    }

    @Test
    public void testComposerCallbacksImpl_onTextChangedOverflow() {
        final String OVERFLOW_TEXT = "This tweet is longer than 140 characters. This tweet is " +
                "longer than 140 characters. This tweet is longer than 140 characters. Overflow." +
                "Overflow";
        controller = new ComposerController(mockComposerView, mockTwitterSession, Uri.EMPTY,
                ANY_TEXT, ANY_HASHTAG, mockFinisher, mockDependencyProvider);
        final ComposerController.ComposerCallbacks callbacks
                = controller.new ComposerCallbacksImpl();
        callbacks.onTextChanged(OVERFLOW_TEXT);

        verify(mockComposerView).setCharCount(OVERFLOW_REMAINING_CHAR_COUNT);
        verify(mockComposerView).setCharCountTextStyle(R.style.tw__ComposerCharCountOverflow);
        verify(mockComposerView).postTweetEnabled(false);
    }

    @Test
    public void testComposerCallbacksImpl_onTweetPost() {
        final Context mockContext = mock(Context.class);
        when(mockComposerView.getContext()).thenReturn(mockContext);

        controller = new ComposerController(mockComposerView, mockTwitterSession, Uri.EMPTY,
                ANY_TEXT, ANY_HASHTAG, mockFinisher, mockDependencyProvider);
        final ComposerController.ComposerCallbacks callbacks
                = controller.new ComposerCallbacksImpl();
        callbacks.onTweetPost(TWEET_TEXT);
        // assert that
        // - context is used to start the TweetUploadService
        // - intent extras contain the session token and tweet text and card
        // - scribes a Tweet Composer Tweet Click
        final ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mockContext).startService(intentCaptor.capture());
        final Intent intent = intentCaptor.getValue();
        assertEquals(TweetUploadService.class.getCanonicalName(),
                intent.getComponent().getClassName());
        assertEquals(mockAuthToken, intent.getParcelableExtra(TweetUploadService.EXTRA_USER_TOKEN));
        assertEquals(Uri.EMPTY, intent.getParcelableExtra(TweetUploadService.EXTRA_IMAGE_URI));
        verify(mockComposerScribeClient).click(eq(ScribeConstants.SCRIBE_TWEET_ELEMENT));
    }

    @Test
    public void testComposerCallbacksImpl_onClose() {
        controller = new ComposerController(mockComposerView, mockTwitterSession, Uri.EMPTY,
                ANY_TEXT, ANY_HASHTAG, mockFinisher, mockDependencyProvider);
        final ComposerController.ComposerCallbacks callbacks
                = controller.new ComposerCallbacksImpl();
        callbacks.onCloseClick();
        // assert that
        // - finishes the activity
        // - scribes a Tweet Composer Cancel click
        verify(mockFinisher).finish();
        verify(mockComposerScribeClient).click(eq(ScribeConstants.SCRIBE_CANCEL_ELEMENT));
    }

    @Test
    public void testSendCancelBroadcast() {
        controller = new ComposerController(mockComposerView, mockTwitterSession, Uri.EMPTY,
                ANY_TEXT, ANY_HASHTAG, mockFinisher, mockDependencyProvider);
        final ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        controller.sendCancelBroadcast();
        verify(mockContext).sendBroadcast(intentCaptor.capture());

        final Intent capturedIntent = intentCaptor.getValue();
        assertEquals(TWEET_COMPOSE_CANCEL, capturedIntent.getAction());
    }
}
