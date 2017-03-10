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

package com.twitter.sdk.android.core;

import android.test.AndroidTestCase;

import com.twitter.sdk.android.core.internal.TwitterApi;
import com.twitter.sdk.android.core.services.FavoriteService;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.concurrent.ExecutorService;

import okhttp3.OkHttpClient;

import static org.mockito.Mockito.mock;

public class TwitterApiClientTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Twitter.initialize(new TwitterConfig.Builder(getContext())
                .executorService(mock(ExecutorService.class))
                .build());
    }

    @Override
    protected void tearDown() throws Exception {
        TwitterTestUtils.resetTwitter();
        TwitterCoreTestUtils.resetTwitterCore();
        super.tearDown();
    }

    public void testGetService_sdkNotStarted() {
        try {
            TwitterTestUtils.resetTwitter();
            new TwitterApiClient(mock(TwitterSession.class));
            fail();
        } catch (IllegalStateException ise) {
            assertEquals("Must initialize Twitter before using getInstance()", ise.getMessage());
        }
    }

    public void testConstructor_noSession() throws Exception {
        try {
            new TwitterApiClient((TwitterSession) null);
            fail();
        } catch (IllegalArgumentException ie) {
            assertEquals("Session must not be null.", ie.getMessage());
        }
    }

    public void testGetService_cachedService() throws Exception {
        final TwitterApiClient client = newTwitterApiClient();
        final StatusesService service = client.getService(StatusesService.class);
        assertSame(service, client.getService(StatusesService.class));
    }

    public void testGetService_differentServices() throws Exception {
        final TwitterApiClient client = newTwitterApiClient();
        final FavoriteService service = client.getService(FavoriteService.class);
        assertNotSame(service, client.getService(StatusesService.class));
    }

    public void testApiClient_cachedGuestAuthClient() throws Exception {
        final TwitterApiClient customApiClient = new TwitterApiClient(newOkHttpClient());
        TwitterCore.getInstance().addGuestApiClient(customApiClient);

        assertEquals(customApiClient, TwitterCore.getInstance().getGuestApiClient());
    }

    public void testApiClient_cachedUserAuthApiClient() throws Exception {
        final TwitterSession mockUserSession = mock(TwitterSession.class);
        final TwitterApiClient customApiClient =
                new TwitterApiClient(mockUserSession, newOkHttpClient());
        TwitterCore.getInstance().addApiClient(mockUserSession, customApiClient);

        assertEquals(customApiClient, TwitterCore.getInstance().getApiClient(mockUserSession));
    }

    private TwitterApiClient newTwitterApiClient() {
        return new TwitterApiClient(mock(OkHttpClient.class), new TwitterApi());
    }

    private OkHttpClient newOkHttpClient() {
        return new OkHttpClient.Builder().build();
    }
}
