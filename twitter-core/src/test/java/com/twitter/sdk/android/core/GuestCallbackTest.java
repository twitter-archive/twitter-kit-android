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

package com.twitter.sdk.android.core;

import com.twitter.sdk.android.core.internal.TwitterApiConstants;
import com.twitter.sdk.android.core.models.Tweet;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class GuestCallbackTest  {

    SessionManager<AppSession> mockAppSessionManager;

    @Before
    public void setUp() throws Exception {
        mockAppSessionManager = mock(SessionManager.class);
    }

    @Test
    public void testSuccess_callsCallback() {
        final Callback<Tweet> developerCallback = mock(Callback.class);
        final GuestCallback<Tweet> guestCallback = new GuestCallback<>(mockAppSessionManager,
                developerCallback);
        guestCallback.success(mock(Result.class));
        verify(developerCallback).success(any(Result.class));
    }

    @Test
    public void testSuccess_handlesNullCallback() {
        final GuestCallback<Tweet> guestCallback = new GuestCallback<>(mockAppSessionManager, null);
        try {
            guestCallback.success(mock(Result.class));
        } catch (NullPointerException e) {
            Assert.fail("Should have handled null callback");
        }
    }

    @Test
    public void testFailure_callsCallback() {
        final Callback<Tweet> developerCallback = mock(Callback.class);
        final GuestCallback<Tweet> guestCallback = new GuestCallback<>(mockAppSessionManager,
                developerCallback);
        guestCallback.failure(mock(TwitterApiException.class));
        verify(developerCallback).failure(any(TwitterApiException.class));
    }

    @Test
    public void testFailure_handlesNullCallback() {
        final GuestCallback<Tweet> guestCallback = new GuestCallback<>(mockAppSessionManager, null);
        try {
            guestCallback.failure(mock(TwitterApiException.class));
        } catch (NullPointerException e) {
            Assert.fail("Should have handled null callback");
        }
    }

    @Test
    public void testGuestAuthFailure_clearsAppSession() {
        final GuestCallback<Tweet> guestCallback = new GuestCallback<Tweet>(mockAppSessionManager,
                mock(Callback.class));
        final TwitterApiException guestAuthException = mock(TwitterApiException.class);
        when(guestAuthException.getErrorCode()).thenReturn(
                TwitterApiConstants.Errors.GUEST_AUTH_ERROR_CODE);
        guestCallback.failure(guestAuthException);
        verify(mockAppSessionManager).clearSession(TwitterSession.LOGGED_OUT_USER_ID);
    }

    @Test
    public void testAppAuthFailure_clearsAppSession() {
        final GuestCallback<Tweet> guestCallback = new GuestCallback<Tweet>(mockAppSessionManager,
                mock(Callback.class));
        final TwitterApiException appAuthException = mock(TwitterApiException.class);
        when(appAuthException.getErrorCode()).thenReturn(
                TwitterApiConstants.Errors.APP_AUTH_ERROR_CODE);
        guestCallback.failure(appAuthException);
        verify(mockAppSessionManager).clearSession(TwitterSession.LOGGED_OUT_USER_ID);
    }

    @Test
    public void testOtherFailure_doesNotClearAppSession() {
        final GuestCallback<Tweet> guestCallback = new GuestCallback<Tweet>(mockAppSessionManager,
                mock(Callback.class));
        final TwitterApiException otherApiException = mock(TwitterApiException.class);
        when(otherApiException.getErrorCode()).thenReturn(
                TwitterApiConstants.Errors.LEGACY_ERROR);
        guestCallback.failure(otherApiException);
        verifyZeroInteractions(mockAppSessionManager);
    }

    // should handle TwitterExceptions that are not TwitterApiExceptions
    @Test
    public void testFailure_handleTwitterException() {
        final GuestCallback<Tweet> guestCallback = new GuestCallback<Tweet>(mockAppSessionManager,
                mock(Callback.class));
        final TwitterException twitterException = mock(TwitterException.class);
        try {
            guestCallback.failure(twitterException);
            verifyZeroInteractions(mockAppSessionManager);
        } catch (ClassCastException e) {
            Assert.fail("Should have handled TwitterException which is not a TwitterApiException");
        }
    }

    @Test
    public void testFailure_callsCallbackOnTwitterException() {
        final Callback<Tweet> developerCallback = mock(Callback.class);
        final GuestCallback<Tweet> guestCallback = new GuestCallback<>(mockAppSessionManager,
                developerCallback);
        final TwitterException twitterException = mock(TwitterException.class);
        guestCallback.failure(twitterException);
        verify(developerCallback).failure(twitterException);
    }
}
