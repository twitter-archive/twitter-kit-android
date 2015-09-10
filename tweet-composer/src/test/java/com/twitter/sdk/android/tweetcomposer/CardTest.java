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

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CardTest {
    private static final String TEST_PACKAGE_NAME = "TEST.PACKAGE.NAME";
    private static final String TEST_APP_NAME = "TEST_APP_NAME";
    private static final Uri TEST_URI = Uri.EMPTY;
    private Context mockContext;

    @Before
    public void setUp() {
        final ApplicationInfo mockApplicationInfo = mock(ApplicationInfo.class);
        when(mockApplicationInfo.loadLabel(any(PackageManager.class))).thenReturn(TEST_APP_NAME);

        mockContext = mock(Context.class);
        when(mockContext.getApplicationInfo()).thenReturn(mockApplicationInfo);
        when(mockContext.getPackageName()).thenReturn(TEST_PACKAGE_NAME);
    }

    @Test
    public void createPreviewCard() {
        final Card card = Card.createAppCard(mockContext, TEST_URI);

        assertEquals(card.APP_CARD_TYPE, card.cardType);
        assertEquals(TEST_URI.toString(), card.imageUri);
        assertEquals(TEST_PACKAGE_NAME, card.packageName);
        assertEquals(TEST_APP_NAME, card.appName);
    }
}
