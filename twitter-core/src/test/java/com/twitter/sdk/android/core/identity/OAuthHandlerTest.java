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
import android.os.Build;
import android.os.Bundle;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class OAuthHandlerTest  {

    private static final int REQUEST_CODE = TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE;

    private TwitterAuthConfig mockAuthConfig;
    private OAuthHandler authHandler;

    @Before
    public void setUp() throws Exception {

        mockAuthConfig = mock(TwitterAuthConfig.class);
        authHandler = new OAuthHandler(mockAuthConfig, mock(Callback.class), REQUEST_CODE);
    }

    @Test
    public void testAuthorize() {
        final Activity mockActivity = mock(Activity.class);
        doNothing().when(mockActivity).startActivityForResult(any(Intent.class), anyInt());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            doNothing().when(mockActivity).startActivityForResult(any(Intent.class), anyInt(),
                    any(Bundle.class));
        }
        authHandler.authorize(mockActivity);
        verify(mockActivity).startActivityForResult(any(Intent.class), eq(REQUEST_CODE));
    }

    @Test
    public void testNewIntent() {
        final Activity mockActivity = mock(Activity.class);
        final Intent intent = authHandler.newIntent(mockActivity);
        assertEquals(mockAuthConfig, intent.getParcelableExtra(OAuthActivity.EXTRA_AUTH_CONFIG));
    }
}
