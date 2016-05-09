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

import com.twitter.sdk.android.core.internal.TwitterApi;
import com.twitter.sdk.android.core.internal.oauth.GuestAuthToken;
import com.twitter.sdk.android.core.internal.oauth.OAuth2Service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import javax.net.ssl.SSLSocketFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class GuestAuthClientTest  {
    private TwitterCore mockTwitterCore;
    private TwitterApi mockTwitterApi;
    private SSLSocketFactory mockSslSocketFactory;
    private OAuth2Service fakeOAuth2Service;
    private SessionManager<GuestSession> guestSessionManager;
    private GuestAuthClient guestAuthClient;
    private Callback<GuestSession> mockCallback;

    @Before
    public void setUp() throws Exception {

        mockTwitterCore = mock(TwitterCore.class);
        mockTwitterApi = new TwitterApi();
        guestSessionManager = new SimpleSessionManager<>();
        mockCallback = mock(Callback.class);
        mockSslSocketFactory = mock(SSLSocketFactory.class);
    }

    @Test
    public void testConstructor_nullService() {
        try {
            guestAuthClient = new GuestAuthClient(null);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("OAuth2Service must not be null", e.getMessage());
        }
    }

    @Test
    public void testAuthorize_nullAppSessionManager() {
        fakeOAuth2Service =
                new FakeSuccessOAuth2Service(mockTwitterCore, mockSslSocketFactory, mockTwitterApi);
        guestAuthClient = new GuestAuthClient(fakeOAuth2Service);
        try {
            guestAuthClient.authorize(null, mockCallback);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("SessionManager must not be null", e.getMessage());
        }
    }

    @Test
    public void testAuthorize_serviceSuccess() {
        fakeOAuth2Service =
                new FakeSuccessOAuth2Service(mockTwitterCore, mockSslSocketFactory, mockTwitterApi);
        guestAuthClient = new GuestAuthClient(fakeOAuth2Service);
        assertNull(guestSessionManager.getActiveSession());
        guestAuthClient.authorize(guestSessionManager, mockCallback);
        // assert an GuestSession was set in the AppSessionManager and made primary
        assertNotNull(guestSessionManager.getActiveSession());
        // assert that GuestAuthClient invokes the success callback with a Result
        verify(mockCallback, times(1)).success(any(Result.class));
    }

    @Test
    public void testAuthorize_serviceSuccessNullCallback() {
        fakeOAuth2Service =
                new FakeSuccessOAuth2Service(mockTwitterCore, mockSslSocketFactory, mockTwitterApi);
        guestAuthClient = new GuestAuthClient(fakeOAuth2Service);
        assertNull(guestSessionManager.getActiveSession());
        guestAuthClient.authorize(guestSessionManager, null);
        // assert an GuestSession was set in the AppSessionManager and made primary
        assertNotNull(guestSessionManager.getActiveSession());
        // assert that GuestAuthClient does NOT call the success method on a null callback
        verifyZeroInteractions(mockCallback);
    }

    @Test
    public void testAuthorize_serviceFailure() {
        fakeOAuth2Service =
                new FakeFailureOAuth2Service(mockTwitterCore, mockSslSocketFactory, mockTwitterApi);
        guestAuthClient = new GuestAuthClient(fakeOAuth2Service);
        guestAuthClient.authorize(guestSessionManager, mockCallback);
        // assert that GuestAuthClient invokes the failure callback when service fails to get auth
        verify(mockCallback, times(1)).failure(any(TwitterException.class));
    }

    @Test
    public void testAuthorize_serviceFailureNullCallback() {
        fakeOAuth2Service =
                new FakeFailureOAuth2Service(mockTwitterCore, mockSslSocketFactory, mockTwitterApi);
        guestAuthClient = new GuestAuthClient(fakeOAuth2Service);
        guestAuthClient.authorize(guestSessionManager, null);
        // assert that GuestAuthClient does NOT call the failure method on a null callback
        verifyZeroInteractions(mockCallback);
    }

    /**
     * Fakes an OAuth2Service where network requests for guest auth tokens succeed.
     */
    class FakeSuccessOAuth2Service extends OAuth2Service {

        FakeSuccessOAuth2Service(TwitterCore twitterCore, SSLSocketFactory sslSocketFactory,
                          TwitterApi api) {
            super(twitterCore, sslSocketFactory, api);
        }

        @Override
        public void requestGuestAuthToken(Callback<GuestAuthToken> callback) {
            final GuestAuthToken guestAuthToken = mock(GuestAuthToken.class);
            callback.success(new Result<>(guestAuthToken, null));
        }
    }

    /**
     * Fakes an OAuth2Service where network requests for guest auth tokens fail.
     */
    class FakeFailureOAuth2Service extends OAuth2Service {

        FakeFailureOAuth2Service(TwitterCore twitterCore, SSLSocketFactory sslSocketFactory,
            TwitterApi api) {
            super(twitterCore, sslSocketFactory, api);
        }

        @Override
        public void requestGuestAuthToken(Callback<GuestAuthToken> callback) {
            callback.failure(new TwitterException("fake exception"));
        }
    }
}
