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

import android.app.Activity;
import android.text.format.DateUtils;

import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;

/**
 * A session monitor for validating sessions.
 * @param <T>
 */
public class SessionMonitor<T extends Session> {
    protected final MonitorState monitorState;

    private final SystemCurrentTimeProvider time;
    private final SessionManager<T> sessionManager;
    private final ExecutorService executorService;
    private final SessionVerifier sessionVerifier;

    /**
     * @param sessionManager A user auth based session manager
     * @param executorService used to
     */
    public SessionMonitor(SessionManager<T> sessionManager, ExecutorService executorService,
            SessionVerifier<T> sessionVerifier) {
        this(sessionManager, new SystemCurrentTimeProvider(),
                executorService, new MonitorState(), sessionVerifier);
    }

    SessionMonitor(SessionManager<T> sessionManager, SystemCurrentTimeProvider time,
            ExecutorService executorService, MonitorState monitorState, SessionVerifier
            sessionVerifier) {
        this.time = time;
        this.sessionManager = sessionManager;
        this.executorService = executorService;
        this.monitorState = monitorState;
        this.sessionVerifier = sessionVerifier;
    }

    /**
     * This is how we hook into the activity lifecycle to detect if the user is using the app.
     * @param activityLifecycleManager
     */
    public void monitorActivityLifecycle(ActivityLifecycleManager activityLifecycleManager) {
        activityLifecycleManager.registerCallbacks(new ActivityLifecycleManager.Callbacks() {
            @Override
            public void onActivityStarted(Activity activity) {
                triggerVerificationIfNecessary();
            }
        });
    }

    /**
     * triggerVerificationIfNecessary checks if there are any sessions to verify and if enough time
     * has passed in order to run another verification. If it determines it can verify, it submits a
     * runnable that does the verification in a background thread.
     */
    public void triggerVerificationIfNecessary() {
        final Session session = sessionManager.getActiveSession();
        final long currentTime = time.getCurrentTimeMillis();
        final boolean startVerification = session != null &&
                monitorState.beginVerification(currentTime);
        if (startVerification) {
            executorService.submit(() -> verifyAll());
        }
    }

    protected void verifyAll() {
        for (T session : sessionManager.getSessionMap().values()) {
            sessionVerifier.verifySession(session);
        }
        monitorState.endVerification(time.getCurrentTimeMillis());
    }

    /**
     * Encapsulates time based state that rate limits our calls to the verification api.
     * Ensure we don't end up with racy parallel calls with beginVerification.
     */
    protected static class MonitorState {
        private static final long TIME_THRESHOLD_IN_MILLIS = 6 * DateUtils.HOUR_IN_MILLIS;

        public boolean verifying;
        public long lastVerification;

        private final Calendar utcCalendar;

        public MonitorState() {
            this.utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        }

        public synchronized boolean beginVerification(long currentTime) {
            final boolean isPastThreshold
                    = currentTime - lastVerification > TIME_THRESHOLD_IN_MILLIS;
            final boolean dayHasChanged = !isOnSameDate(currentTime, lastVerification);

            if (!verifying && (isPastThreshold || dayHasChanged)) {
                return verifying = true;
            }
            return false;
        }

        public synchronized void endVerification(long currentTime) {
            verifying = false;
            lastVerification = currentTime;
        }

        private boolean isOnSameDate(long timeA, long timeB) {
            utcCalendar.setTimeInMillis(timeA);
            final int dayA = utcCalendar.get(Calendar.DAY_OF_YEAR);
            final int yearA = utcCalendar.get(Calendar.YEAR);

            utcCalendar.setTimeInMillis(timeB);
            final int dayB = utcCalendar.get(Calendar.DAY_OF_YEAR);
            final int yearB = utcCalendar.get(Calendar.YEAR);

            return dayA == dayB && yearA == yearB;
        }
    }
}
