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

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.TextView;

import io.fabric.sdk.android.FabricActivityTestCase;
import io.fabric.sdk.android.FabricTestUtils;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.R;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShareEmailActivityTest extends FabricActivityTestCase<ShareEmailActivity> {

    private static final String TEST_APP_NAME = "app name";
    private static final String TEST_USER_NAME = "user name";
    private static final long TEST_SESSION_ID = 1L;
    private static final long TEST_SESSION_ID2 = 2L;

    private Context context;
    private TwitterSession mockSession;
    private ShareEmailController mockController;

    public ShareEmailActivityTest() {
        super(ShareEmailActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        context = getInstrumentation().getTargetContext();

        final TwitterCore twitterCore = new TwitterCore(new TwitterAuthConfig("", ""));
        FabricTestUtils.resetFabric();
        FabricTestUtils.with(context, twitterCore);

        mockSession = mock(TwitterSession.class);
        when(mockSession.getUserName()).thenReturn(TEST_USER_NAME);
        when(mockSession.getId()).thenReturn(TEST_SESSION_ID);
        mockController = mock(TestShareEmailController.class);

        final SessionManager<TwitterSession> sessionManager = TwitterCore.getInstance()
                .getSessionManager();
        sessionManager.setActiveSession(mockSession);
    }

    @Override
    protected void tearDown() throws Exception {
        FabricTestUtils.resetFabric();
        super.tearDown();
    }

    private void init() {
        final ShareEmailResultReceiver resultReceiver
                = new ShareEmailResultReceiver(mock(Callback.class));
        final Intent intent = new Intent(context, ShareEmailActivity.class)
                .putExtra(ShareEmailActivity.EXTRA_RESULT_RECEIVER, resultReceiver)
                .putExtra(ShareEmailActivity.EXTRA_SESSION_ID, TEST_SESSION_ID);
        init(intent);
    }

    private void init(Intent intent) {
        final ShareEmailActivity activity = startActivity(intent, null, null);
        activity.controller = mockController;
    }

    public void testOnCreate_extraResultReceiverMissing() {
        final Intent intent = new Intent(context, ShareEmailActivity.class);
        init(intent);
        assertTrue(isFinishCalled());
    }

    public void testOnCreate_extraSessionIdMissing() {
        final ShareEmailResultReceiver resultReceiver
                = new ShareEmailResultReceiver(mock(Callback.class));
        final Intent intent = new Intent(context, ShareEmailActivity.class)
                .putExtra(ShareEmailActivity.EXTRA_RESULT_RECEIVER, resultReceiver);
        init(intent);
        assertTrue(isFinishCalled());
    }

    public void testOnCreate_extraSessionIdUnknown() {
        final ShareEmailResultReceiver resultReceiver
                = new ShareEmailResultReceiver(mock(Callback.class));
        final Intent intent = new Intent(context, ShareEmailActivity.class)
                .putExtra(ShareEmailActivity.EXTRA_RESULT_RECEIVER, resultReceiver)
                .putExtra(ShareEmailActivity.EXTRA_SESSION_ID, TwitterSession.UNKNOWN_USER_ID);
        init(intent);
        assertTrue(isFinishCalled());
    }

    public void testOnCreate_extraSessionIdNotFound() {
        final ShareEmailResultReceiver resultReceiver
                = new ShareEmailResultReceiver(mock(Callback.class));
        final Intent intent = new Intent(context, ShareEmailActivity.class)
                .putExtra(ShareEmailActivity.EXTRA_RESULT_RECEIVER, resultReceiver)
                .putExtra(ShareEmailActivity.EXTRA_SESSION_ID, TEST_SESSION_ID2);
        init(intent);
        assertTrue(isFinishCalled());
    }

    public void testOnClickNotNow() {
        init();
        final Button button = (Button) getActivity().findViewById(R.id.tw__not_now_btn);
        button.performClick();
        verify(mockController).cancelRequest();
    }

    public void testOnClickAllow() {
        init();
        final Button button = (Button) getActivity().findViewById(R.id.tw__allow_btn);
        button.performClick();
        verify(mockController).executeRequest();
    }

    public void testSetUpShareEmailDesc() {
        init();
        final TextView textView = new TextView(context);
        final Context mockContext = mock(Context.class);
        final PackageManager mockPackageManager = mock(PackageManager.class);
        when(mockContext.getPackageManager()).thenReturn(mockPackageManager);
        when(mockPackageManager.getApplicationLabel(any(ApplicationInfo.class)))
                .thenReturn(TEST_APP_NAME);
        getActivity().setUpShareEmailDesc(mockContext, textView);
        assertEquals(context.getString(R.string.tw__share_email_desc, TEST_APP_NAME,
                        TEST_USER_NAME),
                textView.getText().toString());
    }

    public void testOnBackPressed() {
        init();
        getActivity().onBackPressed();
        verify(mockController).cancelRequest();
    }
}
