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
import android.content.Intent;
import android.test.AndroidTestCase;
import android.util.Log;
import android.view.View;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.FabricTestUtils;
import io.fabric.sdk.android.KitStub;
import io.fabric.sdk.android.Logger;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class TwitterLoginButtonTest extends AndroidTestCase {

    private static final int TEST_REQUEST_CODE = 100;

    private Activity mockActivity;
    private TwitterAuthClient mockAuthClient;
    private Callback<TwitterSession> mockCallback;
    private View.OnClickListener mockViewClickListener;
    private TwitterLoginButton loginButton;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mockActivity = mock(Activity.class);
        mockAuthClient = mock(TwitterAuthClient.class);
        when(mockAuthClient.getRequestCode()).thenReturn(TEST_REQUEST_CODE);
        doNothing().when(mockAuthClient).authorize(any(Activity.class), any(Callback.class));
        doNothing().when(mockAuthClient).onActivityResult(anyInt(), anyInt(), any(Intent.class));

        mockCallback = mock(Callback.class);
        mockViewClickListener = mock(View.OnClickListener.class);

        loginButton = new TwitterLoginButton(getContext(), null, 0, mockAuthClient) {
            // This is to allow us to test TwitterLoginButton without having to set up a real
            // activity.
            @Override
            protected Activity getActivity() {
                return mockActivity;
            }
        };
    }

    public void testConstructor_contextNotActivity() {
        try {
            loginButton = new TwitterLoginButton(getContext(), null, 0, mockAuthClient);
            fail("Constructor should throw an exception when provided context is not an activity");
        } catch (IllegalStateException e) {
            assertEquals(TwitterLoginButton.ERROR_MSG_NO_ACTIVITY, e.getMessage());
        }
    }

    public void testConstructor_contextNotActivityEditModeTrue() {
        loginButton = new TwitterLoginButton(getContext(), null, 0, mockAuthClient) {
            @Override
            public boolean isInEditMode() {
                return true;
            }
        };
        assertNull(loginButton.getActivity());
    }

    public void testConstructor_nullTwitterAuthClient() {
        final TwitterLoginButton button = new TwitterLoginButton(getContext()) {
            @Override
            protected Activity getActivity() {
                return mock(Activity.class);
            }
        };
        assertNull(button.authClient);
    }

    public void testConstructor_editMode() throws Exception {
            final TwitterLoginButton button = new TwitterLoginButton(getContext()) {
                @Override
                protected Activity getActivity() {
                    return mock(Activity.class);
                }

                @Override
                public boolean isInEditMode() {
                    return true;
                }
            };
            assertTrue(button.isEnabled());
    }

    public void testConstructor_twitterNotStarted() throws Exception {
        try {
            final Fabric fabric = new Fabric.Builder(getContext())
                    .debuggable(true)
                    .logger(mock(Logger.class))
                    .kits(new KitStub())
                    .build();
            FabricTestUtils.with(fabric);

            final TwitterLoginButton button = new TwitterLoginButton(getContext()) {
                @Override
                protected Activity getActivity() {
                    return mock(Activity.class);
                }
            };
            final Logger logger = Fabric.getLogger();
            verify(logger).e(eq(TwitterLoginButton.TAG),
                    eq("Must start Twitter Kit with Fabric.with() first"));
            assertFalse(button.isEnabled());

        } finally {
            FabricTestUtils.resetFabric();
        }
    }

    public void testConstructor_twitterStarted() throws Exception {
        try {
            final Fabric fabric = new Fabric.Builder(getContext())
                    .debuggable(true)
                    .kits(new TwitterCore(new TwitterAuthConfig("", "")))
                    .logger(mock(Logger.class))
                    .build();
            FabricTestUtils.with(fabric);

            final TwitterLoginButton button = new TwitterLoginButton(getContext()) {
                @Override
                protected Activity getActivity() {
                    return mock(Activity.class);
                }
            };

            final Logger logger = Fabric.getLogger();
            verify(logger, never()).e(eq(TwitterLoginButton.TAG),
                    eq("Must start Twitter Kit with Fabric.with() first"));
            assertTrue(button.isEnabled());

        } finally {
            FabricTestUtils.resetFabric();
        }
    }


    public void testSetCallback_callbackNull() {
        try {
            loginButton.setCallback(null);
            fail("setCallback should throw an exception when called with null callback");
        } catch (IllegalArgumentException e) {
            assertEquals("Callback cannot be null", e.getMessage());
        }
    }

    public void testGetCallback() {
        final Callback<TwitterSession> mockCallback = mock(Callback.class);
        loginButton.setCallback(mockCallback);
        assertSame(mockCallback, loginButton.getCallback());
    }

    public void testOnClick() {
        loginButton.setCallback(mockCallback);
        loginButton.performClick();
        verify(mockAuthClient).authorize(eq(mockActivity), eq(mockCallback));
    }

    public void testOnClick_withOnClickListener() {
        loginButton.setCallback(mockCallback);
        loginButton.setOnClickListener(mockViewClickListener);
        loginButton.performClick();
        verify(mockAuthClient).authorize(eq(mockActivity), eq(mockCallback));
        verify(mockViewClickListener).onClick(eq(loginButton));
    }

    public void testOnClick_callbackNullDebuggableTrue() throws Exception {
        final Fabric fabric = new Fabric.Builder(getContext())
                .kits(new KitStub())
                .debuggable(true)
                .build();
        FabricTestUtils.with(fabric);
        try {
            loginButton.performClick();
            fail("onClick should throw an exception when called and there is no callback");
        } catch (IllegalStateException e) {
            assertEquals("Callback must not be null, did you call setCallback?", e.getMessage());
        } finally {
            FabricTestUtils.resetFabric();
        }
    }

    public void testOnClick_callbackNullDebuggableFalse() throws Exception {
        final Fabric fabric = setUpLogTest();
        FabricTestUtils.with(fabric);
        try {
            loginButton.performClick();
            assertLogMessage("Callback must not be null, did you call setCallback?");
        } finally {
            FabricTestUtils.resetFabric();
        }
    }

    public void testOnClick_activityNullDebuggableTrue() throws Exception {
        final Fabric fabric = new Fabric.Builder(getContext())
                .kits(new KitStub())
                .debuggable(true)
                .build();
        FabricTestUtils.with(fabric);
        loginButton = new TwitterLoginButton(getContext(), null, 0, mockAuthClient) {
            // This is to allow us to test TwitterLoginButton without having to set up a real
            // activity.
            @Override
            protected Activity getActivity() {
                return null;
            }
        };
        loginButton.setCallback(mockCallback);

        try {
            loginButton.performClick();
            fail("onClick should throw an exception when called and there is no activity");
        } catch (IllegalStateException e) {
            assertEquals(TwitterLoginButton.ERROR_MSG_NO_ACTIVITY, e.getMessage());
        } finally {
            FabricTestUtils.resetFabric();
        }
    }

    public void testOnClick_activityNullDebuggableFalse() throws Exception {
        final Fabric fabric = setUpLogTest();
        FabricTestUtils.with(fabric);
        loginButton = new TwitterLoginButton(getContext(), null, 0, mockAuthClient) {
            // This is to allow us to test TwitterLoginButton without having to set up a real
            // activity.
            @Override
            protected Activity getActivity() {
                return null;
            }
        };
        loginButton.setCallback(mockCallback);

        try {
            loginButton.performClick();
            assertLogMessage(TwitterLoginButton.ERROR_MSG_NO_ACTIVITY);
        } finally {
            FabricTestUtils.resetFabric();
        }
    }

    public void testOnClick_activityFinishingDebuggableFalse() throws Exception {
        final Fabric fabric = setUpLogTest();
        FabricTestUtils.with(fabric);
        loginButton = new TwitterLoginButton(getContext(), null, 0, mockAuthClient) {
            // This is to allow us to test TwitterLoginButton without having to set up a real
            // activity.
            @Override
            protected Activity getActivity() {
                final Activity mockActivity = mock(Activity.class);
                when(mockActivity.isFinishing()).thenReturn(true);
                return mockActivity;
            }
        };
        loginButton.setCallback(mockCallback);

        try {
            loginButton.performClick();
            assertLogMessage(TwitterLoginButton.ERROR_MSG_NO_ACTIVITY);
        } finally {
            FabricTestUtils.resetFabric();
        }
    }

    private Fabric setUpLogTest() {
        final Logger mockLogger = mock(Logger.class);
        when(mockLogger.isLoggable(TwitterCore.TAG, Log.WARN)).thenReturn(true);

        final Fabric fabric = new Fabric.Builder(getContext())
                .kits(new KitStub())
                .debuggable(false)
                .logger(mockLogger)
                .build();
        return fabric;
    }

    private void assertLogMessage(String expectedMessage) {
        final ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(Fabric.getLogger()).w(eq(TwitterCore.TAG), argumentCaptor.capture());
        assertEquals(expectedMessage, argumentCaptor.getValue());
    }

    public void testOnActivityResult_requestCodeMatches() {
        final int requestCode = TEST_REQUEST_CODE;
        final int resultCode = Activity.RESULT_OK;
        final Intent mockData = mock(Intent.class);
        loginButton.onActivityResult(requestCode, resultCode, mockData);
        verify(mockAuthClient).getRequestCode();
        verify(mockAuthClient).onActivityResult(requestCode, resultCode, mockData);
    }

    public void testOnActivityResult_requestCodeDoesNotMatch() {
        final int requestCode = -1;
        final int resultCode = Activity.RESULT_OK;
        final Intent mockData = mock(Intent.class);
        loginButton.onActivityResult(requestCode, resultCode, mockData);
        verify(mockAuthClient).getRequestCode();
        verifyNoMoreInteractions(mockAuthClient);
    }

    public void testGetTwitterAuthClient() throws Exception {
        try {
            final Fabric fabric = new Fabric.Builder(getContext())
                    .kits(new TwitterCore(new TwitterAuthConfig("", "")))
                    .build();
            FabricTestUtils.with(fabric);

            final TwitterLoginButton button = new TwitterLoginButton(getContext()) {
                @Override
                protected Activity getActivity() {
                    return mock(Activity.class);
                }
            };
            final TwitterAuthClient client = button.getTwitterAuthClient();
            assertNotNull(client);

        } finally {
            FabricTestUtils.resetFabric();
        }
    }

    public void testGetTwitterAuthClient_duplicateCalls() throws Exception {
        try {
            final Fabric fabric = new Fabric.Builder(getContext())
                    .kits(new TwitterCore(new TwitterAuthConfig("", "")))
                    .build();
            FabricTestUtils.with(fabric);

            final TwitterLoginButton button = new TwitterLoginButton(getContext()) {
                @Override
                protected Activity getActivity() {
                    return mock(Activity.class);
                }
            };
            final TwitterAuthClient client = button.getTwitterAuthClient();
            final TwitterAuthClient client2 = button.getTwitterAuthClient();
            assertSame(client, client2);

        } finally {
            FabricTestUtils.resetFabric();
        }
    }
}
