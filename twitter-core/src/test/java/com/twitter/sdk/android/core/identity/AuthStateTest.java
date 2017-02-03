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

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class AuthStateTest  {

    private Activity mockActivity;
    private AuthHandler mockAuthHandler;

    @Before
    public void setUp() throws Exception {

        mockActivity = mock(Activity.class);
        mockAuthHandler = mock(AuthHandler.class);
        when(mockAuthHandler.authorize(mockActivity)).thenReturn(true);
    }

    @Test
    public void testBeginAuthorize() {
        final AuthState authState = new AuthState();
        final boolean result = authState.beginAuthorize(mockActivity, mockAuthHandler);
        assertTrue(result);
        assertTrue(authState.isAuthorizeInProgress());
        assertEquals(mockAuthHandler, authState.getAuthHandler());
    }

    @Test
    public void testBeginAuthorize_authorizeFails() {
        when(mockAuthHandler.authorize(mockActivity)).thenReturn(false);

        final AuthState authState = new AuthState();
        final boolean result = authState.beginAuthorize(mockActivity, mockAuthHandler);
        // Verify that attempting to begin authorize fails if the AuthHandler#authorize returns
        // false.
        assertFalse(result);
        assertFalse(authState.isAuthorizeInProgress());
        assertNull(authState.getAuthHandler());
    }

    @Test
    public void testBeginAuthorize_authorizeInProgress() {
        final AuthState authState = new AuthState();
        final boolean result = authState.beginAuthorize(mockActivity, mockAuthHandler);
        assertTrue(result);
        // Verify that attempting to begin another authorize fails since one is already in progress.
        assertFalse(authState.beginAuthorize(mockActivity, mockAuthHandler));
    }

    @Test
    public void testBeginAuthorize_authHandlerCompareAndSetFails() {
        final AuthState authState = new AuthState();
        final boolean result = authState.beginAuthorize(mockActivity,
                new AuthHandler(mock(TwitterAuthConfig.class), mock(Callback.class),
                        TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
                    @Override
                    public boolean authorize(Activity activity) {
                        // We use this opportunity to set authState's authHandlerRef so that we
                        // can verify behavior when compare and set fails. This is done because
                        // AtomicReference has methods that cannot be mocked.
                        authState.authHandlerRef.set(mock(AuthHandler.class));
                        return true;
                    }
                });
        assertFalse(result);
    }

    @Test
    public void testEndAuthorize() {
        final AuthState authState = new AuthState();
        final boolean result = authState.beginAuthorize(mockActivity, mockAuthHandler);
        assertTrue(result);
        assertTrue(authState.isAuthorizeInProgress());
        assertEquals(mockAuthHandler, authState.getAuthHandler());

        authState.endAuthorize();

        // Verify that end authorize resets everything.
        assertFalse(authState.isAuthorizeInProgress());
        assertNull(authState.getAuthHandler());
    }

    @Test
    public void testEndAuthorize_noAuthorizeInProgress() {
        final AuthState authState = new AuthState();
        // Verify that calling end authorize when there is no authorize in progress does not cause
        // any problems.
        authState.endAuthorize();
    }
}
