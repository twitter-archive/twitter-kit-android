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

package com.twitter.sdk.android.core.internal;

import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.MockTwitterApiException;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TestFixtures;
import com.twitter.sdk.android.core.TwitterApiException;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import junit.framework.Assert;

import io.fabric.sdk.android.FabricAndroidTestCase;
import retrofit.RetrofitError;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class GuestCallbackTest extends FabricAndroidTestCase {

    SessionManager<AppSession> mockAppSessionManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mockAppSessionManager = mock(SessionManager.class);
    }

    public void testSuccess_callsCallback() {
        final Callback<Tweet> developerCallback = mock(Callback.class);
        final GuestCallback<Tweet> guestCallback = new GuestCallback<>(mockAppSessionManager,
                developerCallback);
        guestCallback.success(mock(Result.class));
        verify(developerCallback).success(any(Result.class));
    }

    public void testSuccess_handlesNullCallback() {
        final GuestCallback<Tweet> guestCallback = new GuestCallback<>(mockAppSessionManager, null);
        try {
            guestCallback.success(mock(Result.class));
        } catch (NullPointerException e) {
            Assert.fail("Should have handled null callback");
        }
    }

    public void testFailure_callsCallback() {
        final Callback<Tweet> developerCallback = mock(Callback.class);
        final GuestCallback<Tweet> guestCallback = new GuestCallback<>(mockAppSessionManager,
                developerCallback);
        guestCallback.failure(mock(MockTwitterApiException.class));
        verify(developerCallback).failure(any(MockTwitterApiException.class));
    }

    public void testFailure_handlesNullCallback() {
        final GuestCallback<Tweet> guestCallback = new GuestCallback<>(mockAppSessionManager, null);
        try {
            guestCallback.failure(mock(MockTwitterApiException.class));
        } catch (NullPointerException e) {
            Assert.fail("Should have handled null callback");
        }
    }

    public void testGuestAuthFailure_clearsAppSession() {
        final GuestCallback<Tweet> guestCallback = new GuestCallback<Tweet>(mockAppSessionManager,
                mock(Callback.class));
        final TwitterApiException guestAuthException
                = new MockTwitterApiException(TestFixtures.TEST_GUEST_AUTH_ERROR, null,
                mock(RetrofitError.class));
        guestCallback.failure(guestAuthException);
        verify(mockAppSessionManager).clearSession(TwitterSession.LOGGED_OUT_USER_ID);
    }

    public void testAppAuthFailure_clearsAppSession() {
        final GuestCallback<Tweet> guestCallback = new GuestCallback<Tweet>(mockAppSessionManager,
                mock(Callback.class));
        final TwitterApiException appAuthException
                = new MockTwitterApiException(TestFixtures.TEST_APP_AUTH_ERROR, null,
                mock(RetrofitError.class));
        guestCallback.failure(appAuthException);
        verify(mockAppSessionManager).clearSession(TwitterSession.LOGGED_OUT_USER_ID);
    }

    public void testOtherFailure_doesNotClearAppSession() {
        final GuestCallback<Tweet> guestCallback = new GuestCallback<Tweet>(mockAppSessionManager,
                mock(Callback.class));
        final TwitterApiException otherApiException
                = new MockTwitterApiException(TestFixtures.TEST_LEGACY_ERROR, null,
                mock(RetrofitError.class));
        guestCallback.failure(otherApiException);
        verifyZeroInteractions(mockAppSessionManager);
    }

    // should handle TwitterExceptions that are not TwitterApiExceptions
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

    public void testFailure_callsCallbackOnTwitterException() {
        final Callback<Tweet> developerCallback = mock(Callback.class);
        final GuestCallback<Tweet> guestCallback = new GuestCallback<>(mockAppSessionManager,
                developerCallback);
        final TwitterException twitterException = mock(TwitterException.class);
        guestCallback.failure(twitterException);
        verify(developerCallback).failure(twitterException);
    }
}
