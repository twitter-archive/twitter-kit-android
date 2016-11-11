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

import io.fabric.sdk.android.FabricAndroidTestCase;
import io.fabric.sdk.android.FabricTestUtils;
import io.fabric.sdk.android.KitStub;

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

    public void testGuestSessionManager_noSdkStart() {
        try {
            TwitterCore.getInstance().getGuestSessionProvider();
            fail("Should fail if Fabric is not instantiated.");
        } catch (IllegalStateException ie) {
            assertEquals(FABRIC_NOT_INIT_ERROR_MSG, ie.getMessage());
        }
    }

    public void testGuestSessionManager_sdkStartNoTwitterKit() throws Exception {
        FabricTestUtils.with(getContext(), new KitStub<Result>());
        try {
            TwitterCore.getInstance().getGuestSessionProvider();
            fail("Should fail if Twitter is not instantiated with Fabric.");
        } catch (IllegalStateException ie) {
            assertEquals(TWITTER_NOT_INIT_ERROR_MSG, ie.getMessage());
        }
    }

    public void testGetIdentifier() {
        final String identifier = BuildConfig.GROUP + ":" + BuildConfig.ARTIFACT_ID;
        assertEquals(identifier, twitterCore.getIdentifier());
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
        assertNotNull(twitterCore.getGuestSessionProvider());
    }

    public void testGetAppSessionManager_twitterNotInitialized() throws Exception {
        FabricTestUtils.with(getContext(), new KitStub());
        try {
            twitterCore.getGuestSessionProvider();
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
        assertNotNull(twitterCore.getApiClient(mock(TwitterSession.class)));
    }

    public void testGetApiClient_withSessionTwitterNotInitialized() throws Exception {
        FabricTestUtils.with(getContext(), new KitStub<Result>());
        try {
            twitterCore.getApiClient(mock(TwitterSession.class));
            fail("Should fail if Twitter is not instantiated with Fabric.");
        } catch (IllegalStateException ex) {
            assertEquals(TWITTER_NOT_INIT_ERROR_MSG, ex.getMessage());
        }
    }

    public void testGetGuestApiClient_twitterNotInitialized() throws Exception {
        FabricTestUtils.with(getContext(), new KitStub<Result>());
        try {
            twitterCore.getGuestApiClient();
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
}
