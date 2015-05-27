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

package com.twitter.sdk.android.core;

import android.app.Activity;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.FabricAndroidTestCase;
import io.fabric.sdk.android.FabricTestUtils;
import io.fabric.sdk.android.KitStub;

import java.util.List;
import java.util.concurrent.Callable;

import javax.net.ssl.SSLSocketFactory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TwitterCoreTest extends FabricAndroidTestCase {

    private static final String TWITTER_NOT_INIT_ERROR_MSG = "Must start Twitter Kit with Fabric.with() first";
    private static final String FABRIC_NOT_INIT_ERROR_MSG = "Must Initialize Fabric before using singleton()";
    private TwitterCore twitterCore;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        twitterCore = new TwitterCore(new TwitterAuthConfig("", ""));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        FabricTestUtils.resetFabric();
    }

    public void testLogOut_noSdkStart() {
        try {
            TwitterCore.getInstance().logOut();
            fail("Should fail if Fabric is not instantiated.");
        } catch (IllegalStateException ex) {
            assertEquals(FABRIC_NOT_INIT_ERROR_MSG, ex.getMessage());
        }
    }

    public void testLogOut_sdkStartNoTwitterKit() throws Exception {
        FabricTestUtils.with(getContext(), new KitStub<Result>());
        try {
            TwitterCore.getInstance().logOut();
            fail("Should fail if Twitter is not instantiated with Fabric.");
        } catch (IllegalStateException ie) {
            assertEquals(TWITTER_NOT_INIT_ERROR_MSG, ie.getMessage());
        }
    }

    public void testLogIn_noSdkStart() {
        final Callback<TwitterSession> mockCallback = mock(Callback.class);
        try {
            TwitterCore.getInstance().logIn(mock(Activity.class), mockCallback);
            fail("Should fail if Fabric is not instantiated.");
        } catch (IllegalStateException ie) {
            assertEquals(FABRIC_NOT_INIT_ERROR_MSG, ie.getMessage());
        }
    }

    public void testLogIn_sdkStartNoTwitterKit() throws Exception {
        FabricTestUtils.with(getContext(), new KitStub<Result>());
        final Callback<TwitterSession> mockCallback = mock(Callback.class);

        try {
            TwitterCore.getInstance().logIn(mock(Activity.class), mockCallback);
            fail("Should fail if Twitter is not instantiated with Fabric.");
        } catch (IllegalStateException ie) {
            assertEquals(TWITTER_NOT_INIT_ERROR_MSG, ie.getMessage());
        }
    }

    public void testLogInGuest_noSdkStart() {
        final Callback<AppSession> mockCallback = mock(Callback.class);
        try {
            TwitterCore.getInstance().logInGuest(mockCallback);
            fail("Should fail if Fabric is not instantiated.");
        } catch (IllegalStateException ie) {
            assertEquals(FABRIC_NOT_INIT_ERROR_MSG, ie.getMessage());
        }
    }

    public void testLogInGuest_sdkStartNoTwitterKit() throws Exception {
        FabricTestUtils.with(getContext(), new KitStub<Result>());
        final Callback<AppSession> mockCallback = mock(Callback.class);

        try {
            TwitterCore.getInstance().logInGuest(mockCallback);
            fail("Should fail if Twitter is not instantiated with Fabric.");
        } catch (IllegalStateException ie) {
            assertEquals(TWITTER_NOT_INIT_ERROR_MSG, ie.getMessage());
        }
    }

    public void testGetIdentifier() {
        final String identifier = BuildConfig.GROUP + ":" + BuildConfig.ARTIFACT_ID;
        assertEquals(identifier, twitterCore.getIdentifier());
    }

    public void testGetSSLSocketFactory_noSdkStart() {
        try {
            twitterCore.getSSLSocketFactory();
            fail("Should fail if Fabric is not instantiated.");
        } catch (IllegalStateException ex) {
            assertEquals(FABRIC_NOT_INIT_ERROR_MSG, ex.getMessage());
        }
    }

    public void testGetSSLSocketFactory_sdkStartNoTwitterKit() throws Exception {
        FabricTestUtils.with(getContext(), new KitStub<Result>());
        try {
            twitterCore.getSSLSocketFactory();
            fail("Should fail if Twitter is not instantiated with Fabric.");
        } catch (IllegalStateException ex) {
            assertEquals(TWITTER_NOT_INIT_ERROR_MSG, ex.getMessage());
        }
    }

    public void testGetSessionManager() throws Exception {
        FabricTestUtils.with(getContext(), twitterCore);
        assertNotNull(twitterCore.getSessionManager());
    }

    public void testGetSessionManager_twitterNotInitialized() throws Exception {
        FabricTestUtils.with(getContext(), new KitStub());
        try {
            twitterCore.getSessionManager();
            fail("Should fail if Twitter is not instantiated with Fabric.");
        } catch (IllegalStateException ex) {
            assertEquals(TWITTER_NOT_INIT_ERROR_MSG, ex.getMessage());
        }
    }

    public void testGetAppSessionManager() throws Exception {
        FabricTestUtils.with(getContext(), twitterCore);
        assertNotNull(twitterCore.getAppSessionManager());
    }

    public void testGetAppSessionManager_twitterNotInitialized() throws Exception {
        FabricTestUtils.with(getContext(), new KitStub());
        try {
            twitterCore.getAppSessionManager();
            fail("Should fail if Twitter is not instantiated with Fabric.");
        } catch (IllegalStateException ex) {
            assertEquals(TWITTER_NOT_INIT_ERROR_MSG, ex.getMessage());
        }
    }

    public void testGetApiClient_activeSessionExists() throws Exception {
        FabricTestUtils.with(getContext(), twitterCore);
        twitterCore.twitterSessionManager = setUpSessionManager(mock(TwitterSession.class));
        assertNotNull(twitterCore.getApiClient());
    }

    public void testGetApiClient_activeSessionDoesNotExist() throws Exception {
        FabricTestUtils.with(getContext(), twitterCore);
        try {
            twitterCore.getApiClient();
            fail("Should fail when there are no active sessions");
        } catch (IllegalStateException e) {
            assertEquals("Must have valid session. Did you authenticate with Twitter?",
                    e.getMessage());
        }
    }

    public void testGetApiClient_twitterNotInitialized() throws Exception {
        FabricTestUtils.with(getContext(), new KitStub<Result>());
        try {
            twitterCore.getApiClient();
            fail("Should fail if Twitter is not instantiated with Fabric.");
        } catch (IllegalStateException ex) {
            assertEquals(TWITTER_NOT_INIT_ERROR_MSG, ex.getMessage());
        }
    }

    public void testGetApiClient_withSession() throws Exception {
        FabricTestUtils.with(getContext(), twitterCore);
        assertNotNull(twitterCore.getApiClient(mock(Session.class)));
    }

    public void testGetApiClient_withSessionTwitterNotInitialized() throws Exception {
        FabricTestUtils.with(getContext(), new KitStub<Result>());
        try {
            twitterCore.getApiClient(mock(Session.class));
            fail("Should fail if Twitter is not instantiated with Fabric.");
        } catch (IllegalStateException ex) {
            assertEquals(TWITTER_NOT_INIT_ERROR_MSG, ex.getMessage());
        }
    }

    private <T extends Session> SessionManager<T> setUpSessionManager(T session) {
        final SessionManager<T> sessionManager = mock(SessionManager.class);
        when(sessionManager.getActiveSession()).thenReturn(session);
        return sessionManager;
    }

    public void testGetSSLSocketFactory_contention() throws Exception {
        // We don't want to use FabricTestUtils here because we want to test
        // this when onBackground is also running
        Fabric.with(getContext(), twitterCore);
        final ParallelCallableExecutor<SSLSocketFactory> executor =
                new ParallelCallableExecutor<>(
                        new SSLSocketFactoryCallable(twitterCore),
                        new SSLSocketFactoryCallable(twitterCore));
        final List<SSLSocketFactory> sslSocketFactories = executor.getAllValues();
        assertNotNull(sslSocketFactories.get(0));
        assertSame(sslSocketFactories.get(0), sslSocketFactories.get(1));
    }

    private static class SSLSocketFactoryCallable implements Callable<SSLSocketFactory> {
        private TwitterCore twitter;

        protected SSLSocketFactoryCallable(TwitterCore twitter) {
            this.twitter = twitter;
        }

        @Override
        public SSLSocketFactory call() {
            return twitter.getSSLSocketFactory();
        }
    }

}
