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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TestFixtures;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.scribe.DefaultScribeClient;
import com.twitter.sdk.android.core.internal.scribe.EventNamespace;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.models.UserBuilder;
import com.twitter.sdk.android.core.services.AccountService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.mock.Calls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class TwitterAuthClientTest {

    private static final int TEST_REQUEST_CODE = 100;
    private static final String TEST_EMAIL = "foo@twitter.com";

    private Context mockContext;
    private TwitterCore mockTwitterCore;
    private TwitterAuthConfig mockAuthConfig;
    private SessionManager<TwitterSession> mockSessionManager;
    private AuthState mockAuthState;
    private Callback<TwitterSession> mockCallback;
    private DefaultScribeClient mockScribeClient;
    private TwitterAuthClient authClient;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        when(mockContext.getPackageName()).thenReturn(getClass().getPackage().toString());

        mockTwitterCore = mock(TwitterCore.class);
        mockAuthConfig = mock(TwitterAuthConfig.class);
        when(mockAuthConfig.getRequestCode()).thenReturn(TEST_REQUEST_CODE);
        mockSessionManager = mock(SessionManager.class);
        mockAuthState = mock(AuthState.class);
        mockCallback = mock(Callback.class);
        mockScribeClient = mock(DefaultScribeClient.class);

        authClient = new TwitterAuthClient(mockTwitterCore, mockAuthConfig, mockSessionManager,
                mockAuthState);
    }

    @Test
    public void testGetRequestCode() {
        assertEquals(TEST_REQUEST_CODE, authClient.getRequestCode());
    }

    @Test
    public void testAuthorize_activityNull() {
        try {
            authClient.authorize(null, mock(Callback.class));
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Activity must not be null.", e.getMessage());
        }
    }

    @Test
    public void testAuthorize_activityIsFinishing() {
        final Activity mockActivity = mock(Activity.class);
        when(mockActivity.isFinishing()).thenReturn(true);

        // Verify that when activity is finishing, no further work is done.
        authClient.authorize(mockActivity, mockCallback);
        verifyZeroInteractions(mockAuthState);
    }

    @Test
    public void testAuthorize_callbackNull() {
        try {
            authClient.authorize(mock(Activity.class), null);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Callback must not be null.", e.getMessage());
        }
    }

    @Test
    public void testAuthorize_cancelAuthorize() {
        authClient.cancelAuthorize();
        verify(mockAuthState).endAuthorize();
    }

    @Test
    public void testAuthorize_authorizeInProgress() throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupNoSSOAppInstalled(mockActivity);
        when(mockAuthState.isAuthorizeInProgress()).thenReturn(true);

        // Verify that when authorize is in progress, callback is notified of error.
        authClient.authorize(mockActivity, mockCallback);
        verify(mockCallback).failure(any(TwitterAuthException.class));
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
    public void testAuthorize_scribesImpression() throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupNoSSOAppInstalled(mockActivity);
        authClient = new TwitterAuthClient(mockTwitterCore, mockAuthConfig, mockSessionManager,
                mockAuthState) {
            @Override
            protected DefaultScribeClient getScribeClient() {
                return mockScribeClient;
            }
        };
        authClient.authorize(mockActivity, mockCallback);

        verify(mockScribeClient).scribe(any(EventNamespace.class));
    }

    @Test
    public void testAuthorize_scribeHandlesNullClient()
            throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupNoSSOAppInstalled(mockActivity);

        authClient = new TwitterAuthClient(mockTwitterCore, mockAuthConfig, mockSessionManager,
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

    @Test
    public void testOnActivityResult_noAuthorizeInProgress() {
        when(mockAuthState.isAuthorizeInProgress()).thenReturn(false);

        // Verify that if authorize is in progress, onActivityResult returns early.
        authClient.onActivityResult(TEST_REQUEST_CODE, Activity.RESULT_OK, mock(Intent.class));
        verify(mockAuthState).isAuthorizeInProgress();
        verifyNoMoreInteractions(mockAuthState);
    }

    @Test
    public void testOnActivityResult_handleOnActivityResultTrue() {
        setUpAuthStateOnActivityResult(true);

        // Verify that when the activity result is handled, auth state is updated to end.
        authClient.onActivityResult(TEST_REQUEST_CODE, Activity.RESULT_OK, mock(Intent.class));
        verify(mockAuthState).isAuthorizeInProgress();
        verify(mockAuthState).getAuthHandler();
        verify(mockAuthState).endAuthorize();
    }

    @Test
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

    @Test
    public void testCallbackWrapper_success() {
        final TwitterAuthClient.CallbackWrapper callbackWrapper
                = new TwitterAuthClient.CallbackWrapper(mockSessionManager, mockCallback);

        final TwitterSession mockSession = mock(TwitterSession.class);
        final Result<TwitterSession> mockResult = new Result<>(mockSession, null);
        callbackWrapper.success(mockResult);

        verify(mockSessionManager).setActiveSession(eq(mockSession));
        verify(mockCallback).success(eq(mockResult));
    }

    @Test
    public void testCallbackWrapper_failure() {
        final TwitterAuthClient.CallbackWrapper callbackWrapper
                = new TwitterAuthClient.CallbackWrapper(mockSessionManager, mockCallback);

        final TwitterAuthException mockException = mock(TwitterAuthException.class);
        callbackWrapper.failure(mockException);

        verifyZeroInteractions(mockSessionManager);
        verify(mockCallback).failure(eq(mockException));
    }

    @Test
    public void testRequestEmail_withSuccess() {
        final User user = new UserBuilder().setEmail(TEST_EMAIL).build();
        final Call<User> call = Calls.response(user);
        setupMockAccountService(call);

        authClient.requestEmail(mock(TwitterSession.class), new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                assertEquals(TEST_EMAIL, result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                fail("Expected Callback#success to be called");
            }
        });
    }

    @Test
    public void testRequestEmail_withFailure() {
        final IOException networkException = new IOException("Network failure");
        final Call<User> call = Calls.failure(networkException);
        setupMockAccountService(call);

        authClient.requestEmail(mock(TwitterSession.class), new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                fail("Expected Callback#failure to be called");
            }

            @Override
            public void failure(TwitterException exception) {
                assertEquals(exception.getCause(), networkException);
            }
        });
    }

    @Test
    public void testRequestEmail_scribesImpression() {
        final IOException networkException = new IOException("Network failure");
        final Call<User> call = Calls.failure(networkException);
        setupMockAccountService(call);

        final TwitterSession mockSession = mock(TwitterSession.class);
        when(mockSession.getId()).thenReturn(TestFixtures.USER_ID);
        authClient = new TwitterAuthClient(mockTwitterCore, mockAuthConfig, mockSessionManager,
                mockAuthState) {
            @Override
            protected DefaultScribeClient getScribeClient() {
                return mockScribeClient;
            }
        };

        authClient.requestEmail(mockSession, mock(Callback.class));

        verify(mockScribeClient).scribe(any(EventNamespace.class));
    }

    private void setupMockAccountService(Call<User> call) {
        final AccountService mockAccountService = mock(AccountService.class);
        when(mockAccountService.verifyCredentials(anyBoolean(), anyBoolean(), eq(true)))
                .thenReturn(call);
        final TwitterApiClient mockApiClient = mock(TwitterApiClient.class);
        when(mockApiClient.getAccountService()).thenReturn(mockAccountService);
        when(mockTwitterCore.getApiClient(any(TwitterSession.class))).thenReturn(mockApiClient);
    }
}
