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

import android.os.Bundle;
import android.os.ResultReceiver;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.models.UserBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class ShareEmailControllerTest  {

    private static final String TEST_EMAIL_ADDRESS = "test@test.com";
    private static final String TEST_EXCEPTION_MESSAGE = "test exception message";

    private ShareEmailClient mockEmailClient;
    private ResultReceiver mockResultReceiver;
    private ArgumentCaptor<Bundle> bundleArgCaptor;

    private ShareEmailController controller;

    @Before
    public void setUp() throws Exception {


        mockEmailClient = mock(ShareEmailClient.class);
        mockResultReceiver = mock(ResultReceiver.class);
        bundleArgCaptor = ArgumentCaptor.forClass(Bundle.class);

        controller = new ShareEmailController(mockEmailClient, mockResultReceiver);
    }

    @Test
    public void testExecuteRequest() {
        controller.executeRequest();
        verify(mockEmailClient).getEmail(any(Callback.class));
    }

    @Test
    public void testNewCallback_success() {
        final Callback<User> callback = controller.newCallback();
        callback.success(new Result<>(
                new UserBuilder().setEmail(TEST_EMAIL_ADDRESS).build(),
                null));

        // Verify the callback's success method results in a RESULT_CODE_OK to the ResultReceiver.
        verify(mockResultReceiver).send(eq(ShareEmailClient.RESULT_CODE_OK), any(Bundle.class));
    }

    @Test
    public void testNewCallback_failure() {
        final Callback<User> callback = controller.newCallback();
        final TwitterException exception = new TwitterException(TEST_EXCEPTION_MESSAGE);
        callback.failure(exception);

        // Verify the callback's failure method results in a RESULT_CODE_ERROR to the ResultReceiver
        // and that a new TwitterException is created to pass to the ResultReceiver.
        verify(mockResultReceiver).send(eq(ShareEmailClient.RESULT_CODE_ERROR),
                bundleArgCaptor.capture());
        final TwitterException dataError = (TwitterException) bundleArgCaptor.getValue()
                .getSerializable(ShareEmailClient.RESULT_DATA_ERROR);
        assertNotNull(dataError);
        assertNotSame(exception, dataError);
        assertNull(dataError.getCause());
    }

    @Test
    public void testHandleSuccess_emailNull() {
        controller.handleSuccess(new UserBuilder().build());

        verify(mockResultReceiver).send(eq(ShareEmailClient.RESULT_CODE_ERROR),
                bundleArgCaptor.capture());

        assertEquals("Your application may not have access to email addresses or the user may not"
                        + " have an email address. To request access, please visit"
                        + " https://support.twitter.com/forms/platform.",
                ((TwitterException) bundleArgCaptor.getValue()
                        .get(ShareEmailClient.RESULT_DATA_ERROR)).getMessage());
    }

    @Test
    public void testHandleSuccess_emailEmpty() {
        controller.handleSuccess(new UserBuilder().setEmail("").build());

        verify(mockResultReceiver).send(eq(ShareEmailClient.RESULT_CODE_ERROR),
                bundleArgCaptor.capture());
        assertEquals("This user does not have an email address.",
                ((TwitterException) bundleArgCaptor.getValue()
                        .get(ShareEmailClient.RESULT_DATA_ERROR)).getMessage());
    }

    @Test
    public void testHandleSuccess_emailValid() {
        final User user = new UserBuilder().setEmail(TEST_EMAIL_ADDRESS).build();
        controller.handleSuccess(user);

        verify(mockResultReceiver).send(eq(ShareEmailClient.RESULT_CODE_OK),
                bundleArgCaptor.capture());
        assertEquals(TEST_EMAIL_ADDRESS,
                bundleArgCaptor.getValue().getString(ShareEmailClient.RESULT_DATA_EMAIL));
    }

    @Test
    public void testSendResultCodeOk() {
        controller.sendResultCodeOk(TEST_EMAIL_ADDRESS);

        verify(mockResultReceiver).send(eq(ShareEmailClient.RESULT_CODE_OK),
                bundleArgCaptor.capture());
        assertEquals(TEST_EMAIL_ADDRESS,
                bundleArgCaptor.getValue().getString(ShareEmailClient.RESULT_DATA_EMAIL));
    }

    @Test
    public void testSendResultCodeError() {
        final TwitterException exception = new TwitterException(TEST_EXCEPTION_MESSAGE);
        controller.sendResultCodeError(exception);

        verify(mockResultReceiver).send(eq(ShareEmailClient.RESULT_CODE_ERROR),
                bundleArgCaptor.capture());
        assertEquals(exception, bundleArgCaptor.getValue().get(ShareEmailClient.RESULT_DATA_ERROR));
    }

    @Test
    public void testCancelRequest() {
        controller.cancelRequest();

        verify(mockResultReceiver).send(eq(ShareEmailClient.RESULT_CODE_CANCELED),
                bundleArgCaptor.capture());
        assertEquals("The user chose not to share their email address at this time.",
                bundleArgCaptor.getValue().getString(ShareEmailClient.RESULT_DATA_MSG));
    }
}
