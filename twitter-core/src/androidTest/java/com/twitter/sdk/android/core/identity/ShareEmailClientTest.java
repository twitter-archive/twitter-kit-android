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

import io.fabric.sdk.android.FabricAndroidTestCase;
import io.fabric.sdk.android.FabricTestUtils;
import retrofit2.Call;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TestFixtures;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShareEmailClientTest extends FabricAndroidTestCase {

    private ShareEmailClient.EmailService mockEmailService;
    private ShareEmailClient shareEmailClient;

    public void setUp() throws Exception {
        super.setUp();

        FabricTestUtils.resetFabric();
        FabricTestUtils.with(getContext(),
                new TwitterCore(new TwitterAuthConfig(TestFixtures.KEY, TestFixtures.SECRET)));

        mockEmailService = mock(ShareEmailClient.EmailService.class);
        when(mockEmailService.verifyCredentials(anyBoolean(), anyBoolean()))
                .thenReturn(mock(Call.class));
        shareEmailClient = new ShareEmailClient(mock(TwitterSession.class)) {

            @Override
            protected <T> T getService(Class<T> cls) {
                if (cls.equals(EmailService.class)) {
                    return (T) mockEmailService;
                } else {
                    return super.getService(cls);
                }
            }
        };
    }

    @Override
    protected void tearDown() throws Exception {
        FabricTestUtils.resetFabric();
        super.tearDown();
    }

    public void testGetEmail() throws Exception {
        final Callback<User> mockCallback = mock(Callback.class);
        shareEmailClient.getEmail(mockCallback);

        verify(mockEmailService).verifyCredentials(eq(true), eq(true));
    }
}
