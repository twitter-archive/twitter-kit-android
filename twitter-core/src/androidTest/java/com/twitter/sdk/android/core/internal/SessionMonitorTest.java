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

import android.text.format.DateUtils;

import io.fabric.sdk.android.ActivityLifecycleManager;
import io.fabric.sdk.android.FabricAndroidTestCase;
import io.fabric.sdk.android.services.common.SystemCurrentTimeProvider;

import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.services.AccountService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import retrofit.RetrofitError;

import static org.mockito.Mockito.*;

public class SessionMonitorTest extends FabricAndroidTestCase {

    private static final long TEST_TIME_1200_UTC = 1422014401245L;
    private static final long TEST_TIME_2359_UTC = 1422057541245L;
    private static final long TEST_TIME_0001_UTC = 1422057661245L;

    private SessionManager<Session> mockSessionManager;
    private SessionMonitor.AccountServiceProvider mockAccountServiceProvider;
    private SystemCurrentTimeProvider mockSystemCurrentTimeProvider;
    private AccountService mockAccountService;
    private ExecutorService mockExecutorService;
    private SessionMonitor.MonitorState mockMonitorState;

    private SessionMonitor<Session> sessionMonitor;
    private SessionMonitor.MonitorState monitorState;
    private Map<Long, Session> sessionMap;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mockSessionManager = mock(SessionManager.class);
        mockAccountServiceProvider = mock(SessionMonitor.AccountServiceProvider.class);
        mockSystemCurrentTimeProvider = mock(SystemCurrentTimeProvider.class);
        mockAccountService = mock(AccountService.class);
        mockExecutorService = mock(ExecutorService.class);
        mockMonitorState = mock(SessionMonitor.MonitorState.class);
        sessionMonitor = new SessionMonitor<>(mockSessionManager, mockSystemCurrentTimeProvider,
                mockAccountServiceProvider, mockExecutorService, mockMonitorState);
        monitorState = new SessionMonitor.MonitorState();
        final Session testSession = new Session(null, 1L);
        sessionMap = new HashMap<>();
        sessionMap.put(1L, testSession);

        when(mockAccountServiceProvider.getAccountService(any(Session.class)))
                .thenReturn(mockAccountService);
        when(mockSessionManager.getSessionMap()).thenReturn(sessionMap);
        when(mockSessionManager.getActiveSession()).thenReturn(testSession);
    }

    public void testMonitorActivityLifecycle_registersActivityLifecycleCallbacks() {
        final ActivityLifecycleManager mockActivityLifecycleManager =
                mock(ActivityLifecycleManager.class);
        sessionMonitor = new SessionMonitor<>(mockSessionManager, mockExecutorService);

        sessionMonitor.monitorActivityLifecycle(mockActivityLifecycleManager);

        verify(mockActivityLifecycleManager)
                .registerCallbacks(any(ActivityLifecycleManager.Callbacks.class));
    }

    public void testTriggerVerificationIfNecessary_respectsFalseStartVerification() {
        when(mockMonitorState.beginVerification(anyLong())).thenReturn(false);
        sessionMonitor.triggerVerificationIfNecessary();
        verifyZeroInteractions(mockExecutorService);
    }

    public void testTriggerVerificationIfNecessary_doesNotTriggerIfNoActiveSession() {
        when(mockMonitorState.beginVerification(anyLong())).thenReturn(true);
        when(mockSessionManager.getActiveSession()).thenReturn(null);

        sessionMonitor.triggerVerificationIfNecessary();

        verifyZeroInteractions(mockExecutorService);
    }

    public void testTriggerVerificationIfNecessary_submitsRunnable() {
        when(mockMonitorState.beginVerification(anyLong())).thenReturn(true);
        sessionMonitor.triggerVerificationIfNecessary();
        verify(mockExecutorService).submit(any(Runnable.class));
    }

    public void testVerifyAll_verifiesAllSessions() {
        sessionMap.put(2L, mock(Session.class));
        sessionMonitor.verifyAll();
        verify(mockAccountService, times(2)).verifyCredentials(true, false);
    }

    public void testVerifyAll_shouldNotImmediatelyReverify() {
        when(mockSystemCurrentTimeProvider.getCurrentTimeMillis()).thenReturn(TEST_TIME_1200_UTC);
        sessionMonitor.verifyAll();
        assertFalse(sessionMonitor.monitorState.beginVerification(TEST_TIME_1200_UTC + 1));
    }

    public void testVerifySession_catchesRetrofitExceptionsAndFinishesVerification() {
        doThrow(mock(RetrofitError.class)).when(mockAccountService).verifyCredentials(true, false);

        sessionMonitor.verifySession(mock(Session.class));

        verify(mockAccountService).verifyCredentials(true, false);
        // success, we caught the exception
    }

    public void testVerifySession_callsAccountService() {
        sessionMonitor.verifySession(mock(Session.class));
        verify(mockAccountService).verifyCredentials(true, false);
    }

    public void testMonitorStateStartVerification_duringVerification() {
        final long startTime = TEST_TIME_1200_UTC;
        final long now =  startTime + 9 * DateUtils.HOUR_IN_MILLIS;

        monitorState.lastVerification = startTime;
        monitorState.verifying = true;
        assertFalse(monitorState.beginVerification(now));
    }

    public void testMonitorStateStartVerification_beforeTimeThreshold() {
        final long startTime = TEST_TIME_1200_UTC;
        final long now =  startTime + 1 * DateUtils.HOUR_IN_MILLIS;

        monitorState.lastVerification = startTime;
        monitorState.verifying = false;
        assertFalse(monitorState.beginVerification(now));
    }

    public void testMonitorStateStartVerification_dayChangedButBeforeThreshold() {
        final long startTime = TEST_TIME_2359_UTC;
        final long now =  TEST_TIME_0001_UTC;

        monitorState.lastVerification = startTime;
        monitorState.verifying = false;
        assertTrue(monitorState.beginVerification(now));
    }

    public void testMonitorStateStartVerification_pastTimeThreshold() {
        final long startTime = TEST_TIME_1200_UTC;
        final long now =  startTime + 8 * DateUtils.HOUR_IN_MILLIS;

        monitorState.lastVerification = startTime;
        monitorState.verifying = false;
        assertTrue(monitorState.beginVerification(now));
    }

    public void testMonitorStateStartVerification_newState() {
        assertTrue(monitorState.beginVerification(System.currentTimeMillis()));
    }

    public void testMonitorStateStartVerification_marksVerificationInProgress() {
        assertFalse(monitorState.verifying);
        assertTrue(monitorState.beginVerification(System.currentTimeMillis()));
        assertTrue(monitorState.verifying);
    }

    public void testMonitorStateFinishVerification_marksVerificationDone() {
        monitorState.verifying = true;
        monitorState.lastVerification = TEST_TIME_1200_UTC;
        monitorState.endVerification(TEST_TIME_2359_UTC);
        assertFalse(monitorState.verifying);
        assertEquals(TEST_TIME_2359_UTC, monitorState.lastVerification);
    }
}
