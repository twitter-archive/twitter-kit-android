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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class SSOAuthHandlerTest  {

    private static final int REQUEST_CODE = TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE;
    private static final String INVALID_SIGNATURE = "AAAAAAAAAA";

    private SSOAuthHandler ssoAuthHandler;

    @Before
    public void setUp() throws Exception {
        ssoAuthHandler = new SSOAuthHandler(mock(TwitterAuthConfig.class),
                mock(Callback.class), REQUEST_CODE);
    }

    @Test
    public void testIsAvailable_twitterInstalled() throws PackageManager.NameNotFoundException {
        final Context mockContext = mock(Context.class);
        TestUtils.setupTwitterInstalled(mockContext, SSOAuthHandler.TWITTER_SIGNATURE);
        assertTrue(SSOAuthHandler.isAvailable(mockContext));
    }

    @Test
    public void testIsAvailable_twitterInstalledInvalidSignature()
            throws PackageManager.NameNotFoundException {
        final Context mockContext = mock(Context.class);
        TestUtils.setupTwitterInstalled(mockContext, INVALID_SIGNATURE);
        assertFalse(SSOAuthHandler.isAvailable(mockContext));
    }

    @Test
    public void testIsAvailable_twitterInstalledWithValidAndInvalidSignature()
            throws PackageManager.NameNotFoundException {
        final Context mockContext = mock(Context.class);
        TestUtils.setupTwitterInstalled(mockContext, SSOAuthHandler.TWITTER_SIGNATURE,
                INVALID_SIGNATURE);
        assertFalse(SSOAuthHandler.isAvailable(mockContext));
    }

    @Test
    public void testIsAvailable_twitterDogfoodInstalled()
            throws PackageManager.NameNotFoundException {
        final Context mockContext = mock(Context.class);
        TestUtils.setupTwitterDogfoodInstalled(mockContext, SSOAuthHandler.DOGFOOD_SIGNATURE);
        assertTrue(SSOAuthHandler.isAvailable(mockContext));
    }

    @Test
    public void testIsAvailable_twitterDogfoodInstalledInvalidSignature()
            throws PackageManager.NameNotFoundException {
        final Context mockContext = mock(Context.class);
        TestUtils.setupTwitterDogfoodInstalled(mockContext, INVALID_SIGNATURE);
        assertFalse(SSOAuthHandler.isAvailable(mockContext));
    }

    @Test
    public void testIsAvailable_noSSOAppsInstalled() throws PackageManager.NameNotFoundException {
        final Context mockContext = mock(Context.class);
        TestUtils.setupNoSSOAppInstalled(mockContext);
        assertFalse(SSOAuthHandler.isAvailable(mockContext));
    }

    @Test
    public void testAuthorize_twitterInstalled() throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupTwitterInstalled(mockActivity, SSOAuthHandler.TWITTER_SIGNATURE);
        assertTrue(ssoAuthHandler.authorize(mockActivity));
    }

    @Test
    public void testAuthorize_twitterInstalledInvalidSignature()
            throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupTwitterInstalled(mockActivity, INVALID_SIGNATURE);
        assertFalse(ssoAuthHandler.authorize(mockActivity));
    }

    @Test
    public void testAuthorize_twitterDogfoodInstalled()
            throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupTwitterDogfoodInstalled(mockActivity, SSOAuthHandler.DOGFOOD_SIGNATURE);
        assertTrue(ssoAuthHandler.authorize(mockActivity));
    }

    @Test
    public void testAuthorize_twitterDogfoodInstalledInvalidSignature()
            throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupTwitterDogfoodInstalled(mockActivity, INVALID_SIGNATURE);
        assertFalse(ssoAuthHandler.authorize(mockActivity));
    }

    @Test
    public void testAuthorize_twitterInstalledNoSsoActivity()
            throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupTwitterInstalled(mockActivity, SSOAuthHandler.TWITTER_SIGNATURE);
        when(mockActivity.getPackageManager().queryIntentActivities(any(Intent.class),
                anyInt())).thenReturn(Collections.emptyList());
        assertFalse(ssoAuthHandler.authorize(mockActivity));
    }

    @Test
    public void testAuthorize_noSSOAppsInstalled() throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupNoSSOAppInstalled(mockActivity);
        assertFalse(ssoAuthHandler.authorize(mockActivity));
    }

    @Test
    public void testAuthorize_startActivityForResultThrowsException()
            throws PackageManager.NameNotFoundException {
        final Activity mockActivity = mock(Activity.class);
        TestUtils.setupTwitterInstalled(mockActivity, SSOAuthHandler.TWITTER_SIGNATURE);
        doThrow(new RuntimeException()).when(mockActivity)
                .startActivityForResult(any(Intent.class), eq(REQUEST_CODE));
        assertFalse(ssoAuthHandler.authorize(mockActivity));
    }
}
