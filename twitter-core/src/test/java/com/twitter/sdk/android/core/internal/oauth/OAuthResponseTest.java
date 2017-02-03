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

package com.twitter.sdk.android.core.internal.oauth;

import android.os.Parcel;

import com.twitter.sdk.android.core.TestFixtures;
import com.twitter.sdk.android.core.TwitterAuthToken;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class OAuthResponseTest  {

    @Test
    public void testParcelable() {
        final TwitterAuthToken authToken = new TwitterAuthToken(TestFixtures.TOKEN,
                TestFixtures.SECRET);
        final OAuthResponse authResponse = new OAuthResponse(authToken, TestFixtures.SCREEN_NAME,
                TestFixtures.USER_ID);
        final Parcel parcel = Parcel.obtain();
        authResponse.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        final OAuthResponse parceledAuthResponse
                = OAuthResponse.CREATOR.createFromParcel(parcel);
        assertEquals(authResponse.authToken, parceledAuthResponse.authToken);
        assertEquals(TestFixtures.SCREEN_NAME, parceledAuthResponse.userName);
        assertEquals(TestFixtures.USER_ID, parceledAuthResponse.userId);
    }
}
