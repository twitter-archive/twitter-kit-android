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

package com.twitter.sdk.android.tweetcomposer;

import com.twitter.sdk.android.core.internal.scribe.DefaultScribeClient;
import com.twitter.sdk.android.core.internal.scribe.EventNamespace;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ScribeClientImplTest {
    private DefaultScribeClient mockDefaultScribeClient;
    private ScribeClient scribeClient;

    @Before
    public void setUp() throws Exception {
        mockDefaultScribeClient = mock(DefaultScribeClient.class);
    }

    @Test
    public void testScribe() throws Exception {
        scribeClient = new ScribeClientImpl(mockDefaultScribeClient);
        final EventNamespace mockEventNamespace = mock(EventNamespace.class);
        scribeClient.scribe(mockEventNamespace);
        verify(mockDefaultScribeClient).scribe(mockEventNamespace);
    }
}
