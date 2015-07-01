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
import com.twitter.sdk.android.core.internal.SessionProvider;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetui.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class SessionProviderTest {
    private List<SessionManager<? extends Session>> sessionManagers;
    private SessionManager<TwitterSession> mockTwitterSessionManager;
    private SessionManager<AppSession> mockAppSessionManager;
    private SessionProvider sessionProvider;

    @Before
    public void setUp() throws Exception {
        sessionManagers = new ArrayList<>();
        mockTwitterSessionManager = mock(SessionManager.class);
        mockAppSessionManager = mock(SessionManager.class);
        sessionManagers.add(mockTwitterSessionManager);
        sessionManagers.add(mockAppSessionManager);
        sessionProvider = new TestSessionProvider(sessionManagers);
    }

    @Test
    public void testGetActiveSession_activeSessionDoesNotExist() {
        assertNull(sessionProvider.getActiveSession());
    }

    @Test
    public void testGetActiveSession_activeSessionFirstManager() {
        final TwitterSession mockSession = mock(TwitterSession.class);
        when(mockTwitterSessionManager.getActiveSession()).thenReturn(mockSession);
        assertSame(mockSession, sessionProvider.getActiveSession());
        // Verify that we exited the loop early.
        verifyZeroInteractions(mockAppSessionManager);
    }

    @Test
    public void testGetActiveSession_activeSessionSecondManager() {
        final AppSession mockSession = mock(AppSession.class);
        when(mockAppSessionManager.getActiveSession()).thenReturn(mockSession);
        assertSame(mockSession, sessionProvider.getActiveSession());
    }

    // testing purposes
    class TestSessionProvider extends SessionProvider {
        public TestSessionProvider(List<SessionManager<? extends Session>> sessionManagers) {
            super(sessionManagers);
        }

        @Override
        public void requestAuth(Callback<Session> cb) {
            // tested in concrete SessionProvider's
        }
    }
}
