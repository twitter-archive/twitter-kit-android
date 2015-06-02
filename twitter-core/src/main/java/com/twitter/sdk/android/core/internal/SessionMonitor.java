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
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.internal.scribe.DefaultScribeClient;
import com.twitter.sdk.android.core.internal.scribe.EventNamespace;
import com.twitter.sdk.android.core.internal.scribe.TwitterCoreScribeClientHolder;
import com.twitter.sdk.android.core.services.AccountService;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;

import io.fabric.sdk.android.ActivityLifecycleManager;
import io.fabric.sdk.android.services.common.SystemCurrentTimeProvider;
import retrofit.RetrofitError;

/**
 * A session monitor for validating sessions.
 * @param <T>
 */
public class SessionMonitor<T extends Session> {
    static final String SCRIBE_CLIENT = "android";
    static final String SCRIBE_PAGE = "credentials";
    static final String SCRIBE_SECTION = ""; // intentionally blank
    static final String SCRIBE_COMPONENT = ""; // intentionally blank
    static final String SCRIBE_ELEMENT = ""; // intentionally blank
    static final String SCRIBE_ACTION = "impression";

    protected final MonitorState monitorState;

    private final SystemCurrentTimeProvider time;
    private final AccountServiceProvider accountServiceProvider;
    private final SessionManager<T> sessionManager;
    private final ExecutorService executorService;

    /**
     * @param sessionManager A user auth based session manager
     * @param executorService used to
     */
    public SessionMonitor(SessionManager<T> sessionManager, ExecutorService executorService) {
        this(sessionManager, new SystemCurrentTimeProvider(), new AccountServiceProvider(),
                executorService, new MonitorState());
    }

    SessionMonitor(SessionManager<T> sessionManager, SystemCurrentTimeProvider time,
            AccountServiceProvider accountServiceProvider, ExecutorService executorService,
            MonitorState monitorState) {
        this.time = time;
        this.sessionManager = sessionManager;
        this.accountServiceProvider = accountServiceProvider;
        this.executorService = executorService;
        this.monitorState = monitorState;
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
     *
     * Note on monitoring digits sessions. There is an initial case where the active session from
     * the Digits SessionManager is a session using an app session. The app session will be
     * attempted to be verified until the user logs in using Digits. This will at worst case cause
     * 1 wasted request every 6 hours.
     */
    public void triggerVerificationIfNecessary() {
        final Session session = sessionManager.getActiveSession();
        final long currentTime = time.getCurrentTimeMillis();
        final boolean startVerification = session != null &&
                monitorState.beginVerification(currentTime);
        if (startVerification) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    verifyAll();
                }
            });
        }
    }

    protected void verifyAll() {
        for (T session : sessionManager.getSessionMap().values()) {
            verifySession(session);
        }
        monitorState.endVerification(time.getCurrentTimeMillis());
    }

    /**
     * Verify session uses the synchronous api to simplify marking when verification is done.
     * @param session
     */
    protected void verifySession(final Session session) {
        final AccountService accountService = accountServiceProvider.getAccountService(session);
        try {
            scribeVerifySession();
            accountService.verifyCredentials(true, false);
        } catch (RetrofitError e) {
            // We ignore failures since we will attempt the verification again the next time
            // the verification period comes up. This has the potential to lose events, but we
            // are not aiming towards 100% capture rate.
        }
    }

    protected DefaultScribeClient getScribeClient() {
        return TwitterCoreScribeClientHolder.getScribeClient();
    }

    protected void scribeVerifySession() {
        final DefaultScribeClient scribeClient = getScribeClient();
        if (scribeClient == null) return;

        final EventNamespace ns = new EventNamespace.Builder()
                .setClient(SCRIBE_CLIENT)
                .setPage(SCRIBE_PAGE)
                .setSection(SCRIBE_SECTION)
                .setComponent(SCRIBE_COMPONENT)
                .setElement(SCRIBE_ELEMENT)
                .setAction(SCRIBE_ACTION)
                .builder();

        scribeClient.scribeSyndicatedSdkImpressionEvents(ns);
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

    /**
     * Produces new service instances, this code is a separate class so that we can more easily test
     * SessionMonitor
     */
    protected static class AccountServiceProvider {
        public AccountService getAccountService(Session session) {
            return new TwitterApiClient(session).getAccountService();
        }
    }
}
