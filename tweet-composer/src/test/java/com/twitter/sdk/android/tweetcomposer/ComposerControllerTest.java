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

import android.test.AndroidTestCase;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.services.AccountService;
import com.twitter.sdk.android.core.services.StatusesService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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
    private TwitterSession mockTwitterSession;
    private AccountService mockAccountService;
    private StatusesService mockStatusesService;
    private ComposerActivity.Finisher mockFinisher;
    private ComposerController.DependencyProvider mockDependencyProvider;

    @Before
    public void setUp() throws Exception {
        mockComposerView = mock(ComposerView.class);
        mockTwitterSession = mock(TwitterSession.class);
        mockFinisher = mock(ComposerActivity.Finisher.class);

        final TwitterApiClient mockTwitterApiClient = mock(TwitterApiClient.class);
        mockAccountService = mock(AccountService.class);
        mockStatusesService = mock(StatusesService.class);
        when(mockTwitterApiClient.getAccountService()).thenReturn(mockAccountService);
        when(mockTwitterApiClient.getStatusesService()).thenReturn(mockStatusesService);
        mockDependencyProvider = mock(ComposerController.DependencyProvider.class);
        when(mockDependencyProvider.getApiClient(any(TwitterSession.class)))
                .thenReturn(mockTwitterApiClient);
    }

    @Test
    public void testComposerController() {
        controller = new ComposerController(mockComposerView, mockTwitterSession, TWEET_TEXT,
                mockFinisher, mockDependencyProvider);
        assertEquals(mockTwitterSession, controller.session);
        // assert that
        // - sets callbacks on the view
        // - sets initial Tweet text and cursor position
        // - gets a TwitterApiClient AccountService to set the profile photo
        verify(mockComposerView).setCallbacks(any(ComposerController.ComposerCallbacks.class));
        verify(mockComposerView).setTweetText(TWEET_TEXT);
        verify(mockComposerView).setCursorAtEnd();
        verify(mockDependencyProvider).getApiClient(mockTwitterSession);
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
                mockFinisher, mockDependencyProvider);
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
                mockFinisher, mockDependencyProvider);
        final ComposerController.ComposerCallbacks callbacks
                = controller.new ComposerCallbacksImpl();
        callbacks.onTextChanged(OVERFLOW_TEXT);

        verify(mockComposerView).setCharCount(OVERFLOW_REMAINING_CHAR_COUNT);
        verify(mockComposerView).setCharCountTextStyle(R.style.tw__ComposerCharCountOverflow);
        verify(mockComposerView).postTweetEnabled(false);
    }

    @Test
    public void testComposerCallbacksImpl_onTweetPost() {
        controller = new ComposerController(mockComposerView, mockTwitterSession, ANY_TEXT,
                mockFinisher, mockDependencyProvider);
        final ComposerController.ComposerCallbacks callbacks
                = controller.new ComposerCallbacksImpl();
        callbacks.onTweetPost(TWEET_TEXT);

        verify(mockDependencyProvider, times(2)).getApiClient(mockTwitterSession);
        verify(mockStatusesService).update(eq(TWEET_TEXT), isNull(Long.class),
                isNull(Boolean.class), isNull(Double.class), isNull(Double.class),
                isNull(String.class), isNull(Boolean.class), eq(true),
                isNull(String.class), any(Callback.class));
    }

    @Test
    public void testComposerCallbacksImpl_onClose() {
        controller = new ComposerController(mockComposerView, mockTwitterSession, ANY_TEXT,
                mockFinisher, mockDependencyProvider);
        final ComposerController.ComposerCallbacks callbacks
                = controller.new ComposerCallbacksImpl();
        callbacks.onCloseClick();
        verify(mockFinisher).finish();
    }
}
