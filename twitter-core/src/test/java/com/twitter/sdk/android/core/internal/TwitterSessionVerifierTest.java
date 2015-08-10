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

import com.twitter.sdk.android.core.BuildConfig;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TestFixtures;
import com.twitter.sdk.android.core.internal.TwitterSessionVerifier.AccountServiceProvider;
import com.twitter.sdk.android.core.internal.scribe.DefaultScribeClient;
import com.twitter.sdk.android.core.internal.scribe.EventNamespace;
import com.twitter.sdk.android.core.services.AccountService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import retrofit.RetrofitError;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TwitterSessionVerifierTest {
    private static final String REQUIRED_IMPRESSION_CLIENT = "android";
    private static final String REQUIRED_IMPRESSION_PAGE = "credentials";
    private static final String REQUIRED_IMPRESSION_SECTION = "";
    private static final String REQUIRED_IMPRESSION_COMPONENT = "";
    private static final String REQUIRED_IMPRESSION_ELEMENT = "";
    private static final String REQUIRED_IMPRESSION_ACTION = "impression";
    private DefaultScribeClient mockScribeClient;
    private AccountServiceProvider mockAccountServiceProvider;
    private TwitterSessionVerifier verifier;
    private AccountService mockAccountService;
    private Session session;

    @Before
    public void setUp() throws Exception {
        mockAccountServiceProvider = mock(AccountServiceProvider.class);
        mockScribeClient = mock(DefaultScribeClient.class);
        mockAccountService = mock(AccountService.class);
        when(mockAccountServiceProvider.getAccountService(any(Session.class))).thenReturn
                (mockAccountService);
        session = new Session(null, TestFixtures.USER_ID);
        verifier = new TwitterSessionVerifier(mockAccountServiceProvider,
                mockScribeClient);
    }

    @Test
    public void testVerifySession() throws Exception {

        final ArgumentCaptor<EventNamespace> namespaceCaptor
                = ArgumentCaptor.forClass(EventNamespace.class);

        verifier.verifySession(session);

        verify(mockAccountService).verifyCredentials(true, false);
        verify(mockScribeClient).scribeSyndicatedSdkImpressionEvents(namespaceCaptor.capture());
        final EventNamespace ns = namespaceCaptor.getValue();
        assertEquals(REQUIRED_IMPRESSION_CLIENT, ns.client);
        assertEquals(REQUIRED_IMPRESSION_PAGE, ns.page);
        assertEquals(REQUIRED_IMPRESSION_SECTION, ns.section);
        assertEquals(REQUIRED_IMPRESSION_COMPONENT, ns.component);
        assertEquals(REQUIRED_IMPRESSION_ELEMENT, ns.element);
        assertEquals(REQUIRED_IMPRESSION_ACTION, ns.action);
    }

    @Test
    public void testVerifySession_scribeHandlesNullClient() {
        final TwitterSessionVerifier verifier = new TwitterSessionVerifier
                (mockAccountServiceProvider,
                null);
        try {
            verifier.verifySession(mock(Session.class));
        } catch (NullPointerException e) {
            fail("should handle a null scribe client");
        }
    }

    @Test
    public void testVerifySession_catchesRetrofitExceptionsAndFinishesVerification() {
        doThrow(mock(RetrofitError.class)).when(mockAccountService).verifyCredentials(true, false);

        verifier.verifySession(mock(Session.class));

        verify(mockAccountService).verifyCredentials(true, false);
        // success, we caught the exception
    }

}
