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

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Logger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterCoreTestUtils;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.TwitterTestUtils;

import org.mockito.ArgumentCaptor;

import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
            TwitterTestUtils.resetTwitter();
            TwitterCoreTestUtils.resetTwitterCore();
            final TwitterLoginButton button = new TwitterLoginButton(getContext()) {
                @Override
                protected Activity getActivity() {
                    return mock(Activity.class);
                }
            };

            assertFalse(button.isEnabled());

        } finally {
            TwitterTestUtils.resetTwitter();
        }
    }

    public void testConstructor_twitterStarted() throws Exception {
        try {
            Twitter.initialize(setUpLogTest());

            final TwitterLoginButton button = new TwitterLoginButton(getContext()) {
                @Override
                protected Activity getActivity() {
                    return mock(Activity.class);
                }
            };

            final Logger logger = Twitter.getLogger();
            verify(logger, never()).e(eq(TwitterLoginButton.TAG), anyString());
            assertTrue(button.isEnabled());

        } finally {
            TwitterTestUtils.resetTwitter();
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
        Twitter.initialize(new TwitterConfig.Builder(getContext())
                .executorService(mock(ExecutorService.class))
                .debug(true)
                .build());
        try {
            loginButton.performClick();
            fail("onClick should throw an exception when called and there is no callback");
        } catch (IllegalStateException e) {
            assertEquals("Callback must not be null, did you call setCallback?", e.getMessage());
        } finally {
            TwitterCoreTestUtils.resetTwitterCore();
            TwitterTestUtils.resetTwitter();
        }
    }

    public void testOnClick_callbackNullDebuggableFalse() throws Exception {
        Twitter.initialize(setUpLogTest());
        try {
            loginButton.performClick();
            assertLogMessage("Callback must not be null, did you call setCallback?");
        } finally {
            TwitterCoreTestUtils.resetTwitterCore();
            TwitterTestUtils.resetTwitter();
        }
    }

    public void testOnClick_activityNullDebuggableTrue() throws Exception {
        Twitter.initialize(new TwitterConfig.Builder(getContext())
                .executorService(mock(ExecutorService.class))
                .debug(true)
                .build());
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
            TwitterCoreTestUtils.resetTwitterCore();
            TwitterTestUtils.resetTwitter();
        }
    }

    public void testOnClick_activityNullDebuggableFalse() throws Exception {
        Twitter.initialize(setUpLogTest());
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
            TwitterCoreTestUtils.resetTwitterCore();
            TwitterTestUtils.resetTwitter();
        }
    }

    public void testOnClick_activityFinishingDebuggableFalse() throws Exception {
        Twitter.initialize(setUpLogTest());
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
            TwitterCoreTestUtils.resetTwitterCore();
            TwitterTestUtils.resetTwitter();
        }
    }

    private TwitterConfig setUpLogTest() {
        final Logger mockLogger = mock(Logger.class);
        when(mockLogger.isLoggable(TwitterCore.TAG, Log.WARN)).thenReturn(true);

        final TwitterConfig config = new TwitterConfig.Builder(getContext())
                .executorService(mock(ExecutorService.class))
                .logger(mockLogger)
                .build();

        return config;
    }

    private void assertLogMessage(String expectedMessage) {
        final ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(Twitter.getLogger()).w(eq(Twitter.TAG), argumentCaptor.capture());
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
            Twitter.initialize(new TwitterConfig.Builder(getContext())
                    .executorService(mock(ExecutorService.class))
                    .build());

            final TwitterLoginButton button = new TwitterLoginButton(getContext()) {
                @Override
                protected Activity getActivity() {
                    return mock(Activity.class);
                }
            };
            final TwitterAuthClient client = button.getTwitterAuthClient();
            assertNotNull(client);

        } finally {
            TwitterCoreTestUtils.resetTwitterCore();
            TwitterTestUtils.resetTwitter();
        }
    }

    public void testGetTwitterAuthClient_duplicateCalls() throws Exception {
        try {
            Twitter.initialize(new TwitterConfig.Builder(getContext())
                    .executorService(mock(ExecutorService.class))
                    .build());

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
            TwitterCoreTestUtils.resetTwitterCore();
            TwitterTestUtils.resetTwitter();
        }
    }
}
