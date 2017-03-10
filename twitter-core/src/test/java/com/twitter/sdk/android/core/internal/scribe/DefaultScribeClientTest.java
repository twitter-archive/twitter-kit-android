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

package com.twitter.sdk.android.core.internal.scribe;

import android.os.Build;

import com.twitter.sdk.android.core.BuildConfig;
import com.twitter.sdk.android.core.GuestSessionProvider;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.IdManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class DefaultScribeClientTest {

    private static final String TEST_DEFAULT_SCRIBE_URL = "https://syndication.twitter.com";
    private static final String TEST_OVERRIDE_SCRIBE_URL = "http://api.twitter.com";
    private static final String TEST_SCRIBE_USER_AGENT_FORMAT
            = "TwitterKit/3.0 (Android %s) ExampleKit/%s";
    private static final String TEST_SCRIBE_KIT_NAME = "ExampleKit";
    private static final String TEST_KIT_VERSION = "1000";
    private static final String TEST_USER_AGENT = String.format(Locale.ENGLISH,
            TEST_SCRIBE_USER_AGENT_FORMAT, Build.VERSION.SDK_INT,
            TEST_KIT_VERSION);
    private static final String REQUIRED_SCRIBE_URL_COMPONENT = "https://syndication.twitter.com";
    private static final long REQUIRED_LOGGED_OUT_USER_ID = 0L;
    private static final long TEST_ACTIVE_SESSION_ID = 1L;
    private static final String DEBUG_BUILD_TYPE = "debug";

    private DefaultScribeClient scribeClient;
    private SessionManager<TwitterSession> mockTwitterSessionManager;
    private GuestSessionProvider mockGuestSessionProvider;

    @Before
    public void setUp() throws Exception {
        mockTwitterSessionManager = mock(SessionManager.class);
        mockGuestSessionProvider = mock(GuestSessionProvider.class);

        scribeClient = new DefaultScribeClient(RuntimeEnvironment.application,
                mock(TwitterAuthConfig.class), mockTwitterSessionManager, mockGuestSessionProvider,
                mock(IdManager.class), null);
    }

    @Test
    public void testGetScribeConfig_settingsDataNull() {
        final ScribeConfig scribeConfig
                = DefaultScribeClient.getScribeConfig(TEST_SCRIBE_KIT_NAME, TEST_KIT_VERSION);

        assertEquals(!BuildConfig.BUILD_TYPE.equals(DEBUG_BUILD_TYPE), scribeConfig.isEnabled);
        assertEquals(REQUIRED_SCRIBE_URL_COMPONENT, scribeConfig.baseUrl);
        assertEquals(BuildConfig.SCRIBE_SEQUENCE, scribeConfig.sequence);
        assertEquals(TEST_USER_AGENT, scribeConfig.userAgent);
        assertEquals(ScribeConfig.DEFAULT_MAX_FILES_TO_KEEP, scribeConfig.maxFilesToKeep);
        assertEquals(ScribeConfig.DEFAULT_SEND_INTERVAL_SECONDS, scribeConfig.sendIntervalSeconds);
    }

    @Test
    public void testGetScribeUrl_nullOverride() {
        final String scribeUrl
                = DefaultScribeClient.getScribeUrl(TEST_DEFAULT_SCRIBE_URL, null);
        assertEquals(TEST_DEFAULT_SCRIBE_URL, scribeUrl);
    }

    @Test
    public void testGetScribeUrl_emptyOverride() {
        final String scribeUrl = DefaultScribeClient.getScribeUrl(TEST_DEFAULT_SCRIBE_URL, "");
        assertEquals(TEST_DEFAULT_SCRIBE_URL, scribeUrl);
    }

    @Test
    public void testGetScribeUrl_override() {
        final String scribeUrl = DefaultScribeClient.getScribeUrl(TEST_DEFAULT_SCRIBE_URL,
                TEST_OVERRIDE_SCRIBE_URL);
        assertEquals(TEST_OVERRIDE_SCRIBE_URL, scribeUrl);
    }

    @Test
    public void testGetScribeUserAgent() {
        assertEquals(TEST_USER_AGENT,
                DefaultScribeClient.getUserAgent(TEST_SCRIBE_KIT_NAME, TEST_KIT_VERSION));
    }

    @Test
    public void testGetActiveSession_activeSessionDoesNotExist() {
        assertNull(scribeClient.getActiveSession());
    }

    @Test
    public void testGetActiveSession_activeSessionFirstManager() {
        final TwitterSession mockSession = mock(TwitterSession.class);

        when(mockTwitterSessionManager.getActiveSession()).thenReturn(mockSession);

        assertSame(mockSession, scribeClient.getActiveSession());
    }

    @Test
    public void testGetScribeSessionId_nullSession() {
        assertEquals(REQUIRED_LOGGED_OUT_USER_ID, scribeClient.getScribeSessionId(null));
    }

    @Test
    public void testGetScribeSessionId_activeSession() {
        final DefaultScribeClient scribeClient = new DefaultScribeClient(
                RuntimeEnvironment.application, mock(TwitterAuthConfig.class),
                mockTwitterSessionManager, mockGuestSessionProvider, mock(IdManager.class), null);
        final Session mockSession = mock(Session.class);
        when(mockSession.getId()).thenReturn(TEST_ACTIVE_SESSION_ID);

        assertEquals(TEST_ACTIVE_SESSION_ID, scribeClient.getScribeSessionId(mockSession));
    }
}
