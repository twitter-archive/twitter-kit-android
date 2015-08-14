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

package com.twitter.sdk.android.tweetui.internal;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.SessionProvider;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.internal.oauth.AppAuthToken;
import com.twitter.sdk.android.core.internal.oauth.GuestAuthToken;
import com.twitter.sdk.android.tweetui.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class GuestSessionProviderTest {
    private TwitterCore mockTwitterCore;
    private SessionProvider sessionProvider;
    private List<SessionManager<? extends Session>> sessionManagers;
    private SessionManager<TwitterSession> mockSessionManager;
    private SessionManager<AppSession> mockAppSessionManager;

    @Before
    public void setUp() throws Exception {
        mockTwitterCore = mock(TwitterCore.class);
        sessionManagers = new ArrayList<>();
        mockSessionManager = mock(SessionManager.class);
        mockAppSessionManager = mock(SessionManager.class);
    }

    @Test
    public void testRequestAuth_callsLogInGuest() {
        sessionProvider = new GuestSessionProvider(mockTwitterCore, mock(List.class));
        sessionProvider.requestAuth(mock(Callback.class));
        verify(mockTwitterCore).logInGuest(any(GuestSessionProvider.AppSessionCallback.class));
    }

    @Test
    public void testGetActionSession_userAuthTokenSessionAllowed() {
        final TwitterAuthToken userAuthToken = mock(TwitterAuthToken.class);
        final TwitterSession twitterSession = mock(TwitterSession.class);
        when(twitterSession.getAuthToken()).thenReturn(userAuthToken);
        when(mockSessionManager.getActiveSession()).thenReturn(twitterSession);
        sessionManagers.add(mockSessionManager);
        sessionProvider = new GuestSessionProvider(mockTwitterCore, sessionManagers);
        assertEquals(twitterSession, sessionProvider.getActiveSession());
    }

    @Test
    public void testGetActionSession_guestAuthTokenSessionAllowed() {
        final GuestAuthToken guestAuthToken = mock(GuestAuthToken.class);
        final AppSession guestSession = mock(AppSession.class);
        when(guestSession.getAuthToken()).thenReturn(guestAuthToken);
        when(mockAppSessionManager.getActiveSession()).thenReturn(guestSession);
        sessionManagers.add(mockAppSessionManager);
        sessionProvider = new GuestSessionProvider(mockTwitterCore, sessionManagers);
        assertEquals(guestSession, sessionProvider.getActiveSession());
    }

    @Test
    public void testGetActionSession_appAuthTokenSessionReturnsNull() {
        final AppAuthToken appAuthToken = mock(AppAuthToken.class);
        final AppSession appSession = mock(AppSession.class);
        when(appSession.getAuthToken()).thenReturn(appAuthToken);
        when(mockAppSessionManager.getActiveSession()).thenReturn(appSession);
        sessionManagers.add(mockAppSessionManager);
        sessionProvider = new GuestSessionProvider(mockTwitterCore, sessionManagers);
        assertNull(sessionProvider.getActiveSession());
    }
}
