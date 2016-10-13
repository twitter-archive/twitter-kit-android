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

package com.twitter.sdk.android.core.internal;

import com.twitter.sdk.android.core.GuestSessionProvider;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.network.OkHttpClientHelper;

import java.util.List;

import io.fabric.sdk.android.FabricAndroidTestCase;
import io.fabric.sdk.android.FabricTestUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import static org.mockito.Mockito.mock;

public class OkHttpClientHelperTest extends FabricAndroidTestCase {

    private TwitterCore twitterCore;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        twitterCore = new TwitterCore(new TwitterAuthConfig("", ""));
        FabricTestUtils.with(getContext(), twitterCore);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        FabricTestUtils.resetFabric();
    }

    public void testGetCustomOkHttpClient_guestAuth() throws Exception {
        final Interceptor mockInterceptor = mock(Interceptor.class);
        final OkHttpClient customHttpClient = new OkHttpClient.Builder()
                .addInterceptor(mockInterceptor).build();

        final GuestSessionProvider sessionProvider = mock(GuestSessionProvider.class);
        final OkHttpClient guestAuthHttpClient = OkHttpClientHelper.getCustomOkHttpClient(
                customHttpClient,
                sessionProvider,
                twitterCore.getSSLSocketFactory());

        final List<Interceptor> interceptors = guestAuthHttpClient.interceptors();
        assertTrue(interceptors.contains(mockInterceptor));
    }

    public void testGetCustomOkHttpClient_userAuth() throws Exception {
        final Interceptor mockInterceptor = mock(Interceptor.class);
        final OkHttpClient customHttpClient = new OkHttpClient.Builder()
                .addInterceptor(mockInterceptor).build();

        final TwitterSession mockSession = mock(TwitterSession.class);
        final OkHttpClient guestAuthHttpClient = OkHttpClientHelper.getCustomOkHttpClient(
                customHttpClient,
                mockSession,
                new TwitterAuthConfig("", ""),
                twitterCore.getSSLSocketFactory());

        final List<Interceptor> interceptors = guestAuthHttpClient.interceptors();
        assertTrue(interceptors.contains(mockInterceptor));
    }
}
