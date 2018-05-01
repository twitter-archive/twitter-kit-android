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

import com.twitter.sdk.android.core.TestFixtures;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.TwitterSessionVerifier.AccountServiceProvider;
import com.twitter.sdk.android.core.services.AccountService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import retrofit2.mock.Calls;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class TwitterSessionVerifierTest {
    private AccountServiceProvider mockAccountServiceProvider;
    private TwitterSessionVerifier verifier;
    private AccountService mockAccountService;
    private TwitterSession session;

    @Before
    public void setUp() throws Exception {
        mockAccountServiceProvider = mock(AccountServiceProvider.class);
        mockAccountService = mock(AccountService.class);
        when(mockAccountServiceProvider.getAccountService(any(TwitterSession.class))).thenReturn
                (mockAccountService);
        session = mock(TwitterSession.class);
        when(session.getId()).thenReturn(TestFixtures.USER_ID);
        verifier = new TwitterSessionVerifier(mockAccountServiceProvider);
    }

    @Test
    public void testVerifySession() throws Exception {
        verifier.verifySession(session);

        verify(mockAccountService).verifyCredentials(true, false, false);
    }

    @Test
    public void testVerifySession_catchesRetrofitExceptionsAndFinishesVerification() {
        doReturn(Calls.failure(new IOException()))
                .when(mockAccountService).verifyCredentials(true, false, false);

        verifier.verifySession(session);

        verify(mockAccountService).verifyCredentials(true, false, false);
        // success, we caught the exception
    }
}
