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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import static org.mockito.Mockito.*;

public final class TestUtils {

    private TestUtils() {
        // Private constructor
    }

    public static void setupTwitterInstalled(Context mockContext)
            throws PackageManager.NameNotFoundException {
        setupTwitterInstalled(mockContext, SSOAuthHandler.TWITTER_SIGNATURE);
    }

    public static void setupTwitterInstalled(Context mockContext, String signature)
            throws PackageManager.NameNotFoundException {
        final PackageManager mockPm = mock(PackageManager.class);
        final PackageInfo mockPackageInfo = mock(PackageInfo.class);
        mockPackageInfo.signatures = new Signature[] {
                new Signature(signature)
        };
        when(mockContext.getPackageManager()).thenReturn(mockPm);
        when(mockPm.getPackageInfo(SSOAuthHandler.TWITTER_PACKAGE_NAME,
                PackageManager.GET_SIGNATURES)).thenReturn(mockPackageInfo);
        when(mockPm.getPackageInfo(SSOAuthHandler.DOGFOOD_PACKAGE_NAME,
                PackageManager.GET_SIGNATURES))
                .thenThrow(new PackageManager.NameNotFoundException());
    }

    public static void setupNoSSOAppInstalled(Context mockContext)
            throws PackageManager.NameNotFoundException {
        final PackageManager mockPm = mock(PackageManager.class);
        when(mockContext.getPackageManager()).thenReturn(mockPm);
        when(mockPm.getPackageInfo(SSOAuthHandler.TWITTER_PACKAGE_NAME,
                PackageManager.GET_SIGNATURES))
                .thenThrow(new PackageManager.NameNotFoundException());
        when(mockPm.getPackageInfo(SSOAuthHandler.DOGFOOD_PACKAGE_NAME,
                PackageManager.GET_SIGNATURES))
                .thenThrow(new PackageManager.NameNotFoundException());
    }
}
