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

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.scribe.DefaultScribeClient;
import com.twitter.sdk.android.core.internal.scribe.EventNamespace;
import com.twitter.sdk.android.core.internal.scribe.TwitterCoreScribeClientHolder;
import com.twitter.sdk.android.core.services.AccountService;

import java.io.IOException;

public class TwitterSessionVerifier implements SessionVerifier<TwitterSession> {
    static final String SCRIBE_CLIENT = "android";
    static final String SCRIBE_PAGE = "credentials";
    static final String SCRIBE_SECTION = ""; // intentionally blank
    static final String SCRIBE_COMPONENT = ""; // intentionally blank
    static final String SCRIBE_ELEMENT = ""; // intentionally blank
    static final String SCRIBE_ACTION = "impression";
    private final AccountServiceProvider accountServiceProvider;
    private final DefaultScribeClient scribeClient;

    public TwitterSessionVerifier() {
        this.accountServiceProvider = new AccountServiceProvider();
        this.scribeClient = TwitterCoreScribeClientHolder.getScribeClient();
    }

    TwitterSessionVerifier(AccountServiceProvider accountServiceProvider, DefaultScribeClient
            scribeClient) {
        this.accountServiceProvider = accountServiceProvider;
        this.scribeClient = scribeClient;
    }

    /**
     * Verify session uses the synchronous api to simplify marking when verification is done.
     *
     * @param session
     */
    public void verifySession(final TwitterSession session) {
        final AccountService accountService = accountServiceProvider.getAccountService(session);
        try {
            scribeVerifySession();
            accountService.verifyCredentials(true, false, false).execute();
        } catch (IOException | RuntimeException e) {
            // We ignore failures since we will attempt the verification again the next time
            // the verification period comes up. This has the potential to lose events, but we
            // are not aiming towards 100% capture rate.
        }
    }

    private void scribeVerifySession() {
        if (scribeClient == null) return;

        final EventNamespace ns = new EventNamespace.Builder()
                .setClient(SCRIBE_CLIENT)
                .setPage(SCRIBE_PAGE)
                .setSection(SCRIBE_SECTION)
                .setComponent(SCRIBE_COMPONENT)
                .setElement(SCRIBE_ELEMENT)
                .setAction(SCRIBE_ACTION)
                .builder();

        scribeClient.scribe(ns);
    }

    /**
     * Produces new service instances, this code is a separate class so that we can more easily test
     * SessionMonitor
     */
    protected static class AccountServiceProvider {
        public AccountService getAccountService(TwitterSession session) {
            return new TwitterApiClient(session).getAccountService();
        }
    }
}
