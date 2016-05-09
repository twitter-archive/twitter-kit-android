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

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.FabricAndroidTestCase;
import io.fabric.sdk.android.FabricTestUtils;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.settings.AnalyticsSettingsData;
import io.fabric.sdk.android.services.settings.Settings;
import io.fabric.sdk.android.services.settings.SettingsData;
import io.fabric.sdk.android.services.settings.TestSettingsController;

import com.twitter.sdk.android.core.BuildConfig;
import com.twitter.sdk.android.core.GuestSessionProvider;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultScribeClientTest extends FabricAndroidTestCase {

    private static final int TEST_SEND_INTERVAL_SECONDS = 6000000 * 60; // 6 million minutes
    private static final int TEST_MAX_FILES_TO_KEEP = 100000;
    private static final String TEST_USER_AGENT = "user-agent";
    private static final String TEST_DEFAULT_SCRIBE_URL = "https://syndication.twitter.com";
    private static final String TEST_OVERRIDE_SCRIBE_URL = "http://api.twitter.com";
    private static final String TEST_SCRIBE_USER_AGENT_FORMAT
            = "Fabric/%s (Android %s) ExampleKit/%s";
    private static final String TEST_SCRIBE_KIT_NAME = "ExampleKit";
    private static final String TEST_KIT_VERSION = "1000";
    private static final String ANY_KIT_IDENTIFIER = ":)";
    private static final String REQUIRED_SCRIBE_URL_COMPONENT = "https://syndication.twitter.com";
    private static final long REQUIRED_LOGGED_OUT_USER_ID = 0L;
    private static final long TEST_ACTIVE_SESSION_ID = 1L;
    private static final String DEBUG_BUILD_TYPE = "debug";

    private ExampleKit testKit;
    private DefaultScribeClient scribeClient;
    private SessionManager<TwitterSession> mockTwitterSessionManager;
    private GuestSessionProvider mockGuestSessionProvider;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        FabricTestUtils.resetFabric();
        Settings.getInstance().setSettingsController(new TestSettingsController());
        Fabric.with(getContext(), new TwitterCore(new TwitterAuthConfig("", "")), new ExampleKit());
        testKit = Fabric.getKit(ExampleKit.class);

        mockTwitterSessionManager = mock(SessionManager.class);
        mockGuestSessionProvider = mock(GuestSessionProvider.class);

        scribeClient = new DefaultScribeClient(testKit, TEST_SCRIBE_KIT_NAME,
                mockTwitterSessionManager, mockGuestSessionProvider, mock(IdManager.class));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        FabricTestUtils.resetFabric();
    }

    private class ExampleKit extends Kit {
        @Override
        public String getIdentifier() {
            return ANY_KIT_IDENTIFIER;
        }

        @Override
        public String getVersion() {
            return TEST_KIT_VERSION;
        }

        @Override
        protected Object doInBackground() {
            return null;
        }
    }

    public void testGetScribeConfig_settingsDataNull() {
        final ScribeConfig scribeConfig
                = DefaultScribeClient.getScribeConfig(null, TEST_USER_AGENT);
        assertScribeConfig(TEST_USER_AGENT, ScribeConfig.DEFAULT_MAX_FILES_TO_KEEP,
                ScribeConfig.DEFAULT_SEND_INTERVAL_SECONDS, scribeConfig);
    }

    public void testGetScribeConfig_settingsDataAnalyticsSettingsDataNull() {
        final SettingsData settingsData
                = new SettingsData(0L, null, null, null, null, null, null, 0, 0);
        final ScribeConfig scribeConfig
                = DefaultScribeClient.getScribeConfig(settingsData, TEST_USER_AGENT);
        assertScribeConfig(TEST_USER_AGENT, ScribeConfig.DEFAULT_MAX_FILES_TO_KEEP,
                ScribeConfig.DEFAULT_SEND_INTERVAL_SECONDS, scribeConfig);
    }

    public void testGetScribeConfig_settingsDataValid() {
        final AnalyticsSettingsData analyticsSettingsData = new AnalyticsSettingsData(null,
                TEST_SEND_INTERVAL_SECONDS, 0, 0, TEST_MAX_FILES_TO_KEEP, true);
        final SettingsData settingsData = new SettingsData(0L, null, null, null, null,
                analyticsSettingsData, null, 0, 0);

        final ScribeConfig scribeConfig
                = DefaultScribeClient.getScribeConfig(settingsData, TEST_USER_AGENT);
        assertScribeConfig(TEST_USER_AGENT, TEST_MAX_FILES_TO_KEEP,
                TEST_SEND_INTERVAL_SECONDS, scribeConfig);
    }

    public void testGetScribeUrl_nullOverride() {
        final String scribeUrl
                = DefaultScribeClient.getScribeUrl(TEST_DEFAULT_SCRIBE_URL, null);
        assertEquals(TEST_DEFAULT_SCRIBE_URL, scribeUrl);
    }

    public void testGetScribeUrl_emptyOverride() {
        final String scribeUrl = DefaultScribeClient.getScribeUrl(TEST_DEFAULT_SCRIBE_URL, "");
        assertEquals(TEST_DEFAULT_SCRIBE_URL, scribeUrl);
    }

    public void testGetScribeUrl_override() {
        final String scribeUrl = DefaultScribeClient.getScribeUrl(TEST_DEFAULT_SCRIBE_URL,
                TEST_OVERRIDE_SCRIBE_URL);
        assertEquals(TEST_OVERRIDE_SCRIBE_URL, scribeUrl);
    }

    private void assertScribeConfig(String expectedUserAgent, int expectedMaxFilesToKeep,
                                    int expectedSendIntervalSeconds, ScribeConfig scribeConfig) {
        assertEquals(!BuildConfig.BUILD_TYPE.equals(DEBUG_BUILD_TYPE), scribeConfig.isEnabled);
        assertEquals(REQUIRED_SCRIBE_URL_COMPONENT, scribeConfig.baseUrl);
        assertEquals(BuildConfig.SCRIBE_SEQUENCE, scribeConfig.sequence);
        assertEquals(expectedUserAgent, scribeConfig.userAgent);
        assertEquals(expectedMaxFilesToKeep, scribeConfig.maxFilesToKeep);
        assertEquals(expectedSendIntervalSeconds, scribeConfig.sendIntervalSeconds);
    }

    public void testGetScribeUserAgent() {
        Fabric.with(getContext(), new ExampleKit());
        final Kit kit = Fabric.getKit(ExampleKit.class);
        final String userAgent = String.format(Locale.ENGLISH,
                TEST_SCRIBE_USER_AGENT_FORMAT,
                kit.getFabric().getVersion(), Build.VERSION.SDK_INT,
                kit.getVersion());

        assertEquals(userAgent, DefaultScribeClient.getUserAgent(TEST_SCRIBE_KIT_NAME, kit));
    }

    public void testGetActiveSession_activeSessionDoesNotExist() {
        assertNull(scribeClient.getActiveSession());
    }

    public void testGetActiveSession_activeSessionFirstManager() {
        final TwitterSession mockSession = mock(TwitterSession.class);

        when(mockTwitterSessionManager.getActiveSession()).thenReturn(mockSession);

        assertSame(mockSession, scribeClient.getActiveSession());
    }

    public void testGetScribeSessionId_nullSession() {
        assertEquals(REQUIRED_LOGGED_OUT_USER_ID, scribeClient.getScribeSessionId(null));
    }

    public void testGetScribeSessionId_activeSession() {
        final DefaultScribeClient scribeClient = new DefaultScribeClient(testKit,
                TEST_SCRIBE_KIT_NAME, mockTwitterSessionManager, mockGuestSessionProvider,
                mock(IdManager.class));
        final Session mockSession = mock(Session.class);
        when(mockSession.getId()).thenReturn(TEST_ACTIVE_SESSION_ID);

        assertEquals(TEST_ACTIVE_SESSION_ID, scribeClient.getScribeSessionId(mockSession));
    }
}
