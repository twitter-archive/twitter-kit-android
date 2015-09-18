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
import android.test.AndroidTestCase;
import android.view.View;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.services.AccountService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ComposerControllerTest extends AndroidTestCase {
    private static final String TWEET_TEXT = "some text";
    private static final String ANY_TEXT = "any text";
    private static final int REMAINING_CHAR_COUNT = 131;
    private static final int OVERFLOW_REMAINING_CHAR_COUNT = -3;
    private ComposerController controller;
    private ComposerView mockComposerView;
    private Context mockContext;
    private TwitterAuthToken mockAuthToken;
    private TwitterSession mockTwitterSession;
    private AccountService mockAccountService;
    private CardViewFactory mockCardViewFactory;
    private Card mockCard;
    private ComposerActivity.Finisher mockFinisher;
    private ComposerController.DependencyProvider mockDependencyProvider;

    @Before
    public void setUp() throws Exception {
        mockComposerView = mock(ComposerView.class);
        mockContext = mock(Context.class);
        when(mockComposerView.getContext()).thenReturn(mockContext);

        mockCard = mock(Card.class);
        mockFinisher = mock(ComposerActivity.Finisher.class);
        mockAuthToken = mock(TwitterAuthToken.class);
        mockTwitterSession = mock(TwitterSession.class);
        when(mockTwitterSession.getAuthToken()).thenReturn(mockAuthToken);

        final TwitterApiClient mockTwitterApiClient = mock(TwitterApiClient.class);
        mockAccountService = mock(AccountService.class);
        when(mockTwitterApiClient.getAccountService()).thenReturn(mockAccountService);

        mockCardViewFactory = mock(CardViewFactory.class);
        when(mockCardViewFactory.createCard(any(Context.class), any(Card.class)))
                .thenReturn(mock(View.class));

        mockDependencyProvider = mock(ComposerController.DependencyProvider.class);
        when(mockDependencyProvider.getApiClient(any(TwitterSession.class)))
                .thenReturn(mockTwitterApiClient);
        when(mockDependencyProvider.getCardViewFactory()).thenReturn(mockCardViewFactory);
    }

    @Test
    public void testComposerController() {
        controller = new ComposerController(mockComposerView, mockTwitterSession, TWEET_TEXT,
                mockCard, mockFinisher, mockDependencyProvider);
        assertEquals(mockTwitterSession, controller.session);
        // assert that
        // - sets callbacks on the view
        // - sets initial Tweet text and cursor position
        // - gets a TwitterApiClient AccountService to set the profile photo
        // - sets card view in composer
        verify(mockComposerView).setCallbacks(any(ComposerController.ComposerCallbacks.class));
        verify(mockComposerView).setTweetText(TWEET_TEXT);
        verify(mockComposerView).setCursorAtEnd();
        verify(mockComposerView).setCardView(any(View.class));
        verify(mockDependencyProvider).getApiClient(mockTwitterSession);
        verify(mockDependencyProvider).getCardViewFactory();
        verify(mockAccountService).verifyCredentials(eq(false), eq(true), any(Callback.class));
    }

    @Test
    public void testTweetTextLength() {
        assertEquals(0, ComposerController.tweetTextLength(null));
        assertEquals(0, ComposerController.tweetTextLength(""));
        assertEquals(1, ComposerController.tweetTextLength("☃"));
        assertEquals(5, ComposerController.tweetTextLength("tweet"));
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
        controller = new ComposerController(mockComposerView, mockTwitterSession, ANY_TEXT,
                mockCard, mockFinisher, mockDependencyProvider);
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
        controller = new ComposerController(mockComposerView, mockTwitterSession, ANY_TEXT,
                mockCard, mockFinisher, mockDependencyProvider);
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

        controller = new ComposerController(mockComposerView, mockTwitterSession, TWEET_TEXT,
                mockCard, mockFinisher, mockDependencyProvider);
        final ComposerController.ComposerCallbacks callbacks
                = controller.new ComposerCallbacksImpl();
        callbacks.onTweetPost(TWEET_TEXT);
        // assert that
        // - context is used to start the TweetUploadService
        // - intent extras contain the session token and tweet text and card
        final ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mockContext).startService(intentCaptor.capture());
        final Intent intent = intentCaptor.getValue();
        assertEquals(TweetUploadService.class.getCanonicalName(),
                intent.getComponent().getClassName());
        assertEquals(TWEET_TEXT, intent.getStringExtra(TweetUploadService.EXTRA_TWEET_TEXT));
        assertEquals(mockAuthToken, intent.getParcelableExtra(TweetUploadService.EXTRA_USER_TOKEN));
        assertEquals(mockCard, intent.getSerializableExtra(TweetUploadService.EXTRA_TWEET_CARD));
        assertEquals(ComposerController.DEFAULT_CALL_TO_ACTION,
                intent.getStringExtra(TweetUploadService.EXTRA_TWEET_CALL_TO_ACTION));
    }

    @Test
    public void testComposerCallbacksImpl_onClose() {
        controller = new ComposerController(mockComposerView, mockTwitterSession, ANY_TEXT,
                mockCard, mockFinisher, mockDependencyProvider);
        final ComposerController.ComposerCallbacks callbacks
                = controller.new ComposerCallbacksImpl();
        callbacks.onCloseClick();
        verify(mockFinisher).finish();
    }
}
