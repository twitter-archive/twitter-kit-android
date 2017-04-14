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
import android.test.ActivityUnitTestCase;

import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCoreTestUtils;
import com.twitter.sdk.android.core.TwitterTestUtils;

import org.mockito.ArgumentCaptor;

import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class OAuthActivityTest extends ActivityUnitTestCase<OAuthActivity> {

    private Context context;
    private OAuthController mockController;

    public OAuthActivityTest() {
        super(OAuthActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        context = getInstrumentation().getTargetContext();
        Twitter.initialize(new TwitterConfig.Builder(context)
                .executorService(mock(ExecutorService.class))
                .build());
        mockController = mock(TestOAuthController.class);
    }

    @Override
    protected void tearDown() throws Exception {
        TwitterTestUtils.resetTwitter();
        TwitterCoreTestUtils.resetTwitterCore();
        super.tearDown();
    }

    private void init() {
        final Intent intent = new Intent(context, OAuthActivity.class)
                .putExtra(OAuthActivity.EXTRA_AUTH_CONFIG, new TwitterAuthConfig("", ""));
        final OAuthActivity activity = startActivity(intent, null, null);
        activity.oAuthController = mockController;
    }

    public void testOnBackPressed() {
        init();
        getActivity().onBackPressed();

        final ArgumentCaptor<TwitterAuthException> exceptionArgCaptor
                = ArgumentCaptor.forClass(TwitterAuthException.class);
        verify(mockController).handleAuthError(eq(Activity.RESULT_CANCELED),
                exceptionArgCaptor.capture());
        assertEquals("Authorization failed, request was canceled.",
                exceptionArgCaptor.getValue().getMessage());
    }

    public void testOnComplete() {
        init();
        getActivity().onComplete(Activity.RESULT_OK, new Intent());

        assertTrue(isFinishCalled());
    }
}
