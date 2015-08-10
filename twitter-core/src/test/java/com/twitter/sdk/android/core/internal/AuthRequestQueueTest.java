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
import com.twitter.sdk.android.core.BuildConfig;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.oauth.GuestAuthToken;
import com.twitter.sdk.android.core.internal.oauth.OAuth2Token;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class AuthRequestQueueTest {
    private SessionProvider mockSessionProvider;
    private GuestAuthToken mockGuestAuthToken;
    private OAuth2Token mockAppAuthToken;
    private Callback<Session> mockRequest;
    private Session mockSession;

    @Before
    public void setUp() throws Exception {
        mockSessionProvider = mock(SessionProvider.class);
        doNothing().when(mockSessionProvider).requestAuth(any(Callback.class));

        mockGuestAuthToken = mock(GuestAuthToken.class);
        mockAppAuthToken = mock(OAuth2Token.class);
        mockRequest = mock(Callback.class);
        mockSession = mock(Session.class);
    }

    // constructs an AuthRequestQueue with mocks, whose session provider returns the given
    // active session
    private AuthRequestQueue setupQueue(Session session) {
        when(mockSessionProvider.getActiveSession()).thenReturn(session);
        return new AuthRequestQueue(mockSessionProvider);
    }

    @Test
    public void testConstructor_awaitingSessionTrue() {
        final AuthRequestQueue authRequestQueue = setupQueue(null);
        assertTrue(authRequestQueue.awaitingSession.get());
    }

    /*
     * test addRequest queues request and does not requestAuth
     */
    @Test
    public void testAddRequest_addsToQueueAndNoCallToRequestAuth() {
        final AuthRequestQueue authRequestQueue = setupQueue(null);
        authRequestQueue.addRequest(mockRequest);
        assertEquals(1, authRequestQueue.queue.size());
        verify(mockSessionProvider, times(0)).requestAuth(any(Callback.class));
    }

    /*
     * test addRequest with no Session
     */
    @Test
    public void testAddRequest_notAwaitingSessionNoSession() {
        final AuthRequestQueue authRequestQueue = setupQueue(null);
        authRequestQueue.awaitingSession.set(false);
        authRequestQueue.addRequest(mockRequest);
        // asserts that:
        // - we added the request to the queue until a token is obtained
        // - we called requestAuth to get a session
        // - we set the awaitingSession flag to true
        assertEquals(1, authRequestQueue.queue.size());
        verify(mockSessionProvider, times(1)).requestAuth(any(Callback.class));
        assertTrue(authRequestQueue.awaitingSession.get());
    }

    /*
     * test addRequest with session that has no Auth Token
     */
    @Test
    public void testAddRequest_notAwaitingSessionHasSessionNoToken() {
        final AuthRequestQueue authRequestQueue = setupQueue(mock(AppSession.class));
        authRequestQueue.awaitingSession.set(false);
        authRequestQueue.addRequest(mockRequest);
        // asserts that:
        // - we added the request to the queue until a token is obtained
        // - we called requestAuth to get a session
        // - we set the awaitingSession flag to true
        assertEquals(1, authRequestQueue.queue.size());
        verify(mockSessionProvider, times(1)).requestAuth(any(Callback.class));
        assertTrue(authRequestQueue.awaitingSession.get());
    }


    @Test
    public void testAddRequest_awaitingSession() {
        final AuthRequestQueue authRequestQueue = setupQueue(null);
        authRequestQueue.addRequest(mockRequest);
        // asserts that
        // - we added the request to the queue until a token is obtained
        // - we did NOT call requestAuth, since an auth request is in-flight
        // - awaitingSession flag sanity check
        assertEquals(1, authRequestQueue.queue.size());
        verify(mockSessionProvider, times(0)).requestAuth(any(Callback.class));
        assertTrue(authRequestQueue.awaitingSession.get());
    }

    /*
     * test addRequest with OAuth2Service and guest auth token
     */
    @Test
    public void testAddRequest_withAuth() {
        final AppSession appSession = mock(AppSession.class);
        when(appSession.getAuthToken()).thenReturn(mockGuestAuthToken);
        final AuthRequestQueue authRequestQueue = setupQueue(appSession);
        authRequestQueue.flushQueueOnSuccess(mockSession);
        authRequestQueue.addRequest(mockRequest);
        // asserts that we skip the queue and add it straight to the net
        assertEquals(0, authRequestQueue.queue.size());
        verify(mockRequest, times(1)).success(any(Result.class));
    }

    /*
     * Test flushQueueOnSuccess
     */
    @Test
    public void testFlushQueueOnSuccess_drainsQueue() {
        final AuthRequestQueue authRequestQueue = setupQueue(null);
        authRequestQueue.addRequest(mockRequest);
        authRequestQueue.addRequest(mockRequest);
        assertEquals(2, authRequestQueue.queue.size());
        authRequestQueue.flushQueueOnSuccess(mockSession);
        assertEquals(0, authRequestQueue.queue.size());
    }

    @Test
    public void testFlushQueueOnSuccess_setsAwaitingSessionToFalse() {
        final AuthRequestQueue authRequestQueue = setupQueue(null);
        authRequestQueue.awaitingSession.set(true);
        authRequestQueue.flushQueueOnSuccess(mockSession);
        assertFalse(authRequestQueue.awaitingSession.get());
    }

    /*
     * Test flushQueueOnError
     */
    @Test
    public void testFlushQueueOnError_drainsQueue() {
        final TwitterAuthException exception = mock(TwitterAuthException.class);
        final AuthRequestQueue authRequestQueue = setupQueue(null);
        authRequestQueue.addRequest(mockRequest);
        authRequestQueue.addRequest(mockRequest);
        assertEquals(2, authRequestQueue.queue.size());
        authRequestQueue.flushQueueOnError(exception);
        assertEquals(0, authRequestQueue.queue.size());
        verify(mockRequest, times(2)).failure(any(TwitterException.class));
    }

    @Test
    public void testFlushQueueOnError_setsActiveFlagToFalse() {
        final AuthRequestQueue authRequestQueue = setupQueue(null);
        authRequestQueue.awaitingSession.set(true);
        authRequestQueue.flushQueueOnError(null);
        assertFalse(authRequestQueue.awaitingSession.get());
    }

    /*
     * Test hasValidSession
     */
    @Test
    public void testHasValidSession_noAppSession() {
        final AuthRequestQueue authRequestQueue = setupQueue(null);
        assertNull(authRequestQueue.getValidSession());
    }

    @Test
    public void testHasValidSession_hasAppSessionNoAuthToken() {
        final AuthRequestQueue authRequestQueue = setupQueue(mock(AppSession.class));
        assertNull(authRequestQueue.getValidSession());
    }

    @Test
    public void testHasValidSession_hasAppAuthToken() {
        final AppSession session = mock(AppSession.class);
        when(session.getAuthToken()).thenReturn(mockAppAuthToken);
        final AuthRequestQueue authRequestQueue = setupQueue(session);
        assertNotNull(authRequestQueue.getValidSession());
    }

    @Test
    public void testHasValidSession_hasGuestAuthToken() {
        final AppSession session = mock(AppSession.class);
        when(session.getAuthToken()).thenReturn(mockGuestAuthToken);
        final AuthRequestQueue authRequestQueue = setupQueue(session);
        assertNotNull(authRequestQueue.getValidSession());
    }

    @Test
    public void testHasValidSession_hasUserAuthToken() {
        final TwitterSession session = mock(TwitterSession.class);
        when(session.getAuthToken()).thenReturn(mock(TwitterAuthToken.class));
        final AuthRequestQueue authRequestQueue = setupQueue(session);
        assertNotNull(authRequestQueue.getValidSession());
    }

    @Test
    public void testSessionRestored_validSessionQueueNotEmpty() {
        final AuthRequestQueue authRequestQueue = setupQueue(null);
        final AppSession session = mock(AppSession.class);
        when(session.getAuthToken()).thenReturn(mockGuestAuthToken);
        final Callback mockCallback = mock(Callback.class);
        authRequestQueue.addRequest(mockCallback);

        authRequestQueue.sessionRestored(session);

        assertFalse(authRequestQueue.awaitingSession.get());
        verify(mockCallback).success(any(Result.class));
        verify(mockSessionProvider, times(0)).requestAuth(any(Callback.class));
    }

    @Test
    public void testSessionRestored_validSessionQueueEmpty() {
        final AuthRequestQueue authRequestQueue = setupQueue(null);
        final AppSession session = mock(AppSession.class);
        when(session.getAuthToken()).thenReturn(mockGuestAuthToken);

        authRequestQueue.sessionRestored(session);

        assertFalse(authRequestQueue.awaitingSession.get());
        verify(mockSessionProvider, times(0)).requestAuth(any(Callback.class));
    }

    @Test
    public void testSessionRestored_noSessionQueueEmpty() {
        final AuthRequestQueue authRequestQueue = setupQueue(null);

        authRequestQueue.sessionRestored(null);

        assertFalse(authRequestQueue.awaitingSession.get());
        verify(mockSessionProvider, times(0)).requestAuth(any(Callback.class));
    }

    @Test
    public void testSessionRestored_noSessionQueueNotEmpty() {
        final AuthRequestQueue authRequestQueue = setupQueue(null);
        final Callback mockCallback = mock(Callback.class);
        authRequestQueue.addRequest(mockCallback);
        authRequestQueue.sessionRestored(null);
        verify(mockSessionProvider, times(1)).requestAuth(any(Callback.class));
        verifyZeroInteractions(mockCallback);
        assertTrue(authRequestQueue.awaitingSession.get());
    }
}
