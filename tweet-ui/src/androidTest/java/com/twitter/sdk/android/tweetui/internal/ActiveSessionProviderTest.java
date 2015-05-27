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

import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.FabricAndroidTestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class ActiveSessionProviderTest extends FabricAndroidTestCase {

    private List<SessionManager<? extends Session>> sessionManagers;
    private SessionManager<TwitterSession> mockTwitterSessionManager;
    private SessionManager<AppSession> mockAppSessionManager;
    private ActiveSessionProvider activeSessionProvider;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sessionManagers = new ArrayList<>();
        mockTwitterSessionManager = mock(SessionManager.class);
        mockAppSessionManager = mock(SessionManager.class);

        sessionManagers.add(mockTwitterSessionManager);
        sessionManagers.add(mockAppSessionManager);

        activeSessionProvider = new ActiveSessionProvider(sessionManagers);
    }

    public void testGetActiveSession_activeSessionDoesNotExist() {
        assertNull(activeSessionProvider.getActiveSession());
    }

    public void testGetActiveSession_activeSessionFirstManager() {
        final TwitterSession mockSession = mock(TwitterSession.class);
        when(mockTwitterSessionManager.getActiveSession()).thenReturn(mockSession);
        assertSame(mockSession, activeSessionProvider.getActiveSession());

        // Verify that we exited the loop early.
        verifyZeroInteractions(mockAppSessionManager);
    }

    public void testGetActiveSession_activeSessionSecondManager() {
        final AppSession mockSession = mock(AppSession.class);
        when(mockAppSessionManager.getActiveSession()).thenReturn(mockSession);
        assertSame(mockSession, activeSessionProvider.getActiveSession());
    }
}
