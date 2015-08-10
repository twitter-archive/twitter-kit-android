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

package com.twitter.sdk.android.tweetui.internal;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.internal.SessionProvider;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetui.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class GuestSessionProviderTest {
    private TwitterCore mockTwitterCore;
    private SessionProvider sessionProvider;

    @Before
    public void setUp() throws Exception {
        mockTwitterCore = mock(TwitterCore.class);
        sessionProvider = new GuestSessionProvider(mockTwitterCore, mock(List.class));
    }

    @Test
    public void testRequestAuth_callsLogInGuest() {
        sessionProvider.requestAuth(mock(Callback.class));
        verify(mockTwitterCore).logInGuest(any(GuestSessionProvider.AppSessionCallback.class));
    }
}
