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
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.FabricAndroidTestCase;

import static org.mockito.Mockito.*;

public class SSOAuthHandlerTest extends FabricAndroidTestCase {

    private static final int REQUEST_CODE = TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE;
    private static final String INVALID_SIGNATURE = "AAAAAAAAAA";

    private SSOAuthHandler ssoAuthHandler;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ssoAuthHandler = new SSOAuthHandler(mock(TwitterAuthConfig.class),
                mock(Callback.class), REQUEST_CODE);
    }

    public void testIsAvailable_twitterInstalled() throws PackageManager.NameNotFoundException {
        final Context mockContext = mock(Context.class);
        TestUtils.setupTwitterInstalled(mockContext, SSOAuthHandler.APP_SIGNATURE);
        assertTrue(SSOAuthHandler.isAvailable(mockContext));
    }

    public void testIsAvailable_twitterInstalledInvalidSignature()
            throws PackageManager.NameNotFoundException {
        final Context mockContext = mock(Context.class);
        TestUtils.setupTwitterInstalled(mockContext, INVALID_SIGNATURE);
        assertFalse(SSOAuthHandler.isAvailable(mockContext));
    }

    public void testIsAvailable_twitterNotInstalled() throws PackageManager.NameNotFoundException {
        final Context mockContext = mock(Context.class);
        TestUtils.setUpTwitterNotInstalled(mockContext);
        assertFalse(SSOAuthHandler.isAvailable(mockContext));
    }

    public void testAuthorize_twitterInstalled() throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupTwitterInstalled(mockActivity, SSOAuthHandler.APP_SIGNATURE);
        assertTrue(ssoAuthHandler.authorize(mockActivity));
    }

    public void testAuthorize_twitterInstalledInvalidSignature()
            throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupTwitterInstalled(mockActivity, INVALID_SIGNATURE);
        assertFalse(ssoAuthHandler.authorize(mockActivity));
    }

    public void testAuthorize_twitterInstalledNoSsoActivity()
            throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupTwitterInstalled(mockActivity, SSOAuthHandler.APP_SIGNATURE);
        final PackageManager mockPm = mockActivity.getPackageManager();
        when(mockPm.getActivityInfo(SSOAuthHandler.SSO_ACTIVITY, 0))
                .thenThrow(mock(PackageManager.NameNotFoundException.class));
        assertFalse(ssoAuthHandler.authorize(mockActivity));
    }

    public void testAuthorize_twitterNotInstalled() throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setUpTwitterNotInstalled(mockActivity);
        assertFalse(ssoAuthHandler.authorize(mockActivity));
    }

    public void testAuthorize_startActivityForResultThrowsException()
            throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupTwitterInstalled(mockActivity, SSOAuthHandler.APP_SIGNATURE);
        doThrow(new RuntimeException()).when(mockActivity)
                .startActivityForResult(any(Intent.class), eq(REQUEST_CODE));
        assertFalse(ssoAuthHandler.authorize(mockActivity));
    }
}
