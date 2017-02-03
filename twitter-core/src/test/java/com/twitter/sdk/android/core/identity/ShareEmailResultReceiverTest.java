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

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class ShareEmailResultReceiverTest  {

    private static final String TEST_EMAIL_ADDRESS = "test@test.com";
    private static final String TEST_MESSAGE = "test message";
    private static final String TEST_EXCEPTION_MESSAGE = "test exception message";
    private static final int TEST_RESULT_CODE_UNKNOWN = -1;

    private Callback<String> mockCallback;
    private ArgumentCaptor<Result> resultArgCaptor;
    private ArgumentCaptor<TwitterException> exceptionArgCaptor;
    private ShareEmailResultReceiver resultReceiver;

    @Before
    public void setUp() throws Exception {


        mockCallback = mock(Callback.class);
        resultArgCaptor = ArgumentCaptor.forClass(Result.class);
        exceptionArgCaptor = ArgumentCaptor.forClass(TwitterException.class);

        resultReceiver = new ShareEmailResultReceiver(mockCallback);
    }

    @Test
    public void testConstructor_nullCallback() {
        try {
            new ShareEmailResultReceiver(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Callback must not be null", e.getLocalizedMessage());
        }
    }

    @Test
    public void testOnReceiveResult_resultCodeOk() {
        final Bundle resultData = new Bundle();
        resultData.putString(ShareEmailClient.RESULT_DATA_EMAIL, TEST_EMAIL_ADDRESS);
        resultReceiver.onReceiveResult(ShareEmailClient.RESULT_CODE_OK, resultData);

        verify(mockCallback).success(resultArgCaptor.capture());
        assertEquals(TEST_EMAIL_ADDRESS, (String) resultArgCaptor.getValue().data);
    }

    @Test
    public void testOnReceiveResult_resultCodeCanceled() {
        final Bundle resultData = new Bundle();
        resultData.putString(ShareEmailClient.RESULT_DATA_MSG, TEST_MESSAGE);
        resultReceiver.onReceiveResult(ShareEmailClient.RESULT_CODE_CANCELED, resultData);

        verify(mockCallback).failure(exceptionArgCaptor.capture());
        assertEquals(TEST_MESSAGE, exceptionArgCaptor.getValue().getLocalizedMessage());
    }

    @Test
    public void testOnReceiveResult_resultCodeError() {
        final TwitterException exception = new TwitterException(TEST_EXCEPTION_MESSAGE);
        final Bundle resultData = new Bundle();
        resultData.putSerializable(ShareEmailClient.RESULT_DATA_ERROR, exception);
        resultReceiver.onReceiveResult(ShareEmailClient.RESULT_CODE_ERROR, resultData);

        verify(mockCallback).failure(exceptionArgCaptor.capture());
        assertEquals(TEST_EXCEPTION_MESSAGE, exceptionArgCaptor.getValue().getLocalizedMessage());
    }

    @Test
    public void testOnReceiveResult_resultCodeUnknown() {
        try {
            resultReceiver.onReceiveResult(TEST_RESULT_CODE_UNKNOWN, new Bundle());
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid result code " + TEST_RESULT_CODE_UNKNOWN,
                    e.getLocalizedMessage());
        }
    }
}
