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

package com.twitter.sdk.android.core.identity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import io.fabric.sdk.android.FabricAndroidTestCase;
import io.fabric.sdk.android.FabricTestUtils;
import io.fabric.sdk.android.KitStub;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TestFixtures;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.scribe.DefaultScribeClient;
import com.twitter.sdk.android.core.internal.scribe.EventNamespace;

import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class TwitterAuthClientTest extends FabricAndroidTestCase {

    private static final int TEST_REQUEST_CODE = 100;

    private Context mockContext;
    private TwitterAuthConfig mockAuthConfig;
    private SessionManager<TwitterSession> mockSessionManager;
    private AuthState mockAuthState;
    private Callback<TwitterSession> mockCallback;
    private DefaultScribeClient mockScribeClient;
    private TwitterAuthClient authClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mockContext = mock(Context.class);
        when(mockContext.getPackageName()).thenReturn(getClass().getPackage().toString());

        mockAuthConfig = mock(TwitterAuthConfig.class);
        when(mockAuthConfig.getRequestCode()).thenReturn(TEST_REQUEST_CODE);
        mockSessionManager = mock(SessionManager.class);
        mockAuthState = mock(TestAuthState.class);
        mockCallback = mock(Callback.class);
        mockScribeClient = mock(DefaultScribeClient.class);

        authClient = new TwitterAuthClient(mockContext, mockAuthConfig, mockSessionManager,
                mockAuthState);
    }

    public void testConstructor_noParameters() throws Exception {
        FabricTestUtils.with(getContext(), new KitStub());
        try {
            new TwitterAuthClient();
            fail("Expected IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("Must start Twitter Kit with Fabric.with() first", e.getMessage());
        } finally {
            FabricTestUtils.resetFabric();
        }
    }

    public void testGetRequestCode() {
        assertEquals(TEST_REQUEST_CODE, authClient.getRequestCode());
    }

    public void testAuthorize_activityNull() {
        try {
            authClient.authorize(null, mock(Callback.class));
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Activity must not be null.", e.getMessage());
        }
    }

    public void testAuthorize_activityIsFinishing() {
        final Activity mockActivity = mock(Activity.class);
        when(mockActivity.isFinishing()).thenReturn(true);

        // Verify that when activity is finishing, no further work is done.
        authClient.authorize(mockActivity, mockCallback);
        verifyZeroInteractions(mockAuthState);
    }

    public void testAuthorize_callbackNull() {
        try {
            authClient.authorize(mock(Activity.class), null);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Callback must not be null.", e.getMessage());
        }
    }

    public void testAuthorize_authorizeInProgress() throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupNoSSOAppInstalled(mockActivity);
        when(mockAuthState.isAuthorizeInProgress()).thenReturn(true);

        // Verify that when authorize is in progress, callback is notified of error.
        authClient.authorize(mockActivity, mockCallback);
        verify(mockCallback).failure(any(TwitterAuthException.class));
    }

    public void testAuthorize_ssoAvailable() throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupTwitterInstalled(mockActivity);
        when(mockAuthState.beginAuthorize(any(Activity.class), any(AuthHandler.class)))
                .thenReturn(true);

        // Verify that when SSO is available, SSOAuthHandler is used to complete the authorization
        // flow.
        authClient.authorize(mockActivity, mockCallback);
        verify(mockAuthState).beginAuthorize(eq(mockActivity), any(SSOAuthHandler.class));
    }

    public void testAuthorize_ssoAvailableViaTwitterDogfood()
            throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupTwitterInstalled(mockActivity);
        when(mockAuthState.beginAuthorize(any(Activity.class), any(AuthHandler.class)))
                .thenReturn(true);

        // Verify that when SSO is available, SSOAuthHandler is used to complete the authorization
        // flow.
        authClient.authorize(mockActivity, mockCallback);
        verify(mockAuthState).beginAuthorize(eq(mockActivity), any(SSOAuthHandler.class));
    }

    public void testAuthorize_ssoNotAvailable() throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupNoSSOAppInstalled(mockActivity);
        when(mockAuthState.beginAuthorize(any(Activity.class), any(AuthHandler.class)))
                .thenReturn(true);

        // Verify that when SSO is not available, OAuthHandler is used to complete the
        // authorization flow.
        authClient.authorize(mockActivity, mockCallback);
        verify(mockAuthState).beginAuthorize(eq(mockActivity), any(OAuthHandler.class));
    }

    public void testAuthorize_bothSsoAndOAuthFail() throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupTwitterInstalled(mockActivity);
        when(mockAuthState.beginAuthorize(any(Activity.class), any(AuthHandler.class)))
                .thenReturn(false);

        authClient.authorize(mockActivity, mockCallback);
        verify(mockAuthState, times(2)).beginAuthorize(eq(mockActivity), any(AuthHandler.class));
        final ArgumentCaptor<TwitterAuthException> argCaptor
                = ArgumentCaptor.forClass(TwitterAuthException.class);
        verify(mockCallback).failure(argCaptor.capture());
        assertEquals("Authorize failed.", argCaptor.getValue().getMessage());
    }

    public void testAuthorize_scribesImpression() throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupNoSSOAppInstalled(mockActivity);
        authClient = new TwitterAuthClient(mockContext, mockAuthConfig, mockSessionManager,
                mockAuthState) {
            @Override
            protected DefaultScribeClient getScribeClient() {
                return mockScribeClient;
            }
        };
        authClient.authorize(mockActivity, mockCallback);

        verify(mockScribeClient).scribeSyndicatedSdkImpressionEvents(any(EventNamespace.class));
    }

    public void testAuthorize_scribeHandlesNullClient()
            throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupNoSSOAppInstalled(mockActivity);

        authClient = new TwitterAuthClient(mockContext, mockAuthConfig, mockSessionManager,
                mockAuthState) {
            @Override
            protected DefaultScribeClient getScribeClient() {
                return null;
            }
        };

        try {
            authClient.authorize(mockActivity, mockCallback);
        } catch (NullPointerException e) {
            fail("should not crash with null scribe client");
        }
    }

    public void testOnActivityResult_noAuthorizeInProgress() {
        when(mockAuthState.isAuthorizeInProgress()).thenReturn(false);

        // Verify that if authorize is in progress, onActivityResult returns early.
        authClient.onActivityResult(TEST_REQUEST_CODE, Activity.RESULT_OK, mock(Intent.class));
        verify(mockAuthState).isAuthorizeInProgress();
        verifyNoMoreInteractions(mockAuthState);
    }

    public void testOnActivityResult_handleOnActivityResultTrue() {
        setUpAuthStateOnActivityResult(true);

        // Verify that when the activity result is handled, auth state is updated to end.
        authClient.onActivityResult(TEST_REQUEST_CODE, Activity.RESULT_OK, mock(Intent.class));
        verify(mockAuthState).isAuthorizeInProgress();
        verify(mockAuthState).getAuthHandler();
        verify(mockAuthState).endAuthorize();
    }

    public void testOnActivityResult_handleOnActivityResultFalse() {
        setUpAuthStateOnActivityResult(false);

        // Verify that when the activity result is not handled, auth state is not updated to end.
        authClient.onActivityResult(TEST_REQUEST_CODE, Activity.RESULT_OK, mock(Intent.class));
        verify(mockAuthState).isAuthorizeInProgress();
        verify(mockAuthState).getAuthHandler();
        verifyNoMoreInteractions(mockAuthState);
    }

    private void setUpAuthStateOnActivityResult(boolean handled) {
        final AuthHandler mockAuthHandler = mock(AuthHandler.class);
        when(mockAuthHandler.handleOnActivityResult(anyInt(), anyInt(), any(Intent.class)))
                .thenReturn(handled);
        when(mockAuthState.isAuthorizeInProgress()).thenReturn(true);
        when(mockAuthState.getAuthHandler()).thenReturn(mockAuthHandler);
    }

    public void testRequestEmail_nullSession() {
        try {
            authClient.requestEmail(null, mock(Callback.class));
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Session must not be null.", e.getMessage());
        }
    }

    public void testRequestEmail() {
        final TwitterSession mockSession = mock(TwitterSession.class);
        when(mockSession.getId()).thenReturn(TestFixtures.USER_ID);
        authClient.requestEmail(mockSession, mock(Callback.class));

        final ArgumentCaptor<Intent> argCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mockContext).startActivity(argCaptor.capture());
        assertShareEmailIntent(argCaptor.getValue());
    }

    public void testRequestEmail_nullCallback() {
        try {
            authClient.requestEmail(mock(TwitterSession.class), null);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Callback must not be null.", e.getMessage());
        }
    }

    public void testRequestEmail_scribesImpression() {
        final TwitterSession mockSession = mock(TwitterSession.class);
        when(mockSession.getId()).thenReturn(TestFixtures.USER_ID);
        authClient = new TwitterAuthClient(mockContext, mockAuthConfig, mockSessionManager,
                mockAuthState) {
            @Override
            protected DefaultScribeClient getScribeClient() {
                return mockScribeClient;
            }
        };

        authClient.requestEmail(mockSession, mock(Callback.class));

        verify(mockScribeClient).scribeSyndicatedSdkImpressionEvents(any(EventNamespace.class));
    }

    public void testReqestEmail_scribeHandlesNullClient() {
        final TwitterSession mockSession = mock(TwitterSession.class);
        when(mockSession.getId()).thenReturn(TestFixtures.USER_ID);
        authClient = new TwitterAuthClient(mockContext, mockAuthConfig, mockSessionManager,
                mockAuthState) {
            @Override
            protected DefaultScribeClient getScribeClient() {
                return null;
            }
        };

        try {
            authClient.requestEmail(mockSession, mock(Callback.class));
        } catch (NullPointerException e) {
            fail("should handle null scribe client");
        }
    }

    public void testNewShareEmailIntent() {
        final TwitterSession mockSession = mock(TwitterSession.class);
        when(mockSession.getId()).thenReturn(TestFixtures.USER_ID);
        final Intent intent = authClient.newShareEmailIntent(mockSession, mock(Callback.class));
        assertShareEmailIntent(intent);
    }

    private void assertShareEmailIntent(Intent intent) {
        final ComponentName component = new ComponentName(mockContext,
                ShareEmailActivity.class.getName());
        assertEquals(component, intent.getComponent());
        assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK, intent.getFlags());
        assertEquals(TestFixtures.USER_ID, intent.getLongExtra(ShareEmailActivity.EXTRA_SESSION_ID,
                TwitterSession.UNKNOWN_USER_ID));
        assertNotNull(intent.getParcelableExtra(ShareEmailActivity.EXTRA_RESULT_RECEIVER));
    }

    public void testCallbackWrapper_success() {
        final TwitterAuthClient.CallbackWrapper callbackWrapper
                = new TwitterAuthClient.CallbackWrapper(mockSessionManager, mockCallback);

        final TwitterSession mockSession = mock(TwitterSession.class);
        final Result<TwitterSession> mockResult = new Result<>(mockSession, null);
        callbackWrapper.success(mockResult);

        verify(mockSessionManager).setActiveSession(eq(mockSession));
        verify(mockCallback).success(eq(mockResult));
    }

    public void testCallbackWrapper_failure() {
        final TwitterAuthClient.CallbackWrapper callbackWrapper
                = new TwitterAuthClient.CallbackWrapper(mockSessionManager, mockCallback);

        final TwitterAuthException mockException = mock(TwitterAuthException.class);
        callbackWrapper.failure(mockException);

        verifyZeroInteractions(mockSessionManager);
        verify(mockCallback).failure(eq(mockException));
    }
}
