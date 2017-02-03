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

import android.os.Parcel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(RobolectricTestRunner.class)
public class TwitterAuthTokenTest  {

    @Test
    public void testParcelable() {
        final TwitterAuthToken authToken = new TwitterAuthToken(TestFixtures.TOKEN,
                TestFixtures.SECRET);
        final Parcel parcel = Parcel.obtain();
        authToken.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        final TwitterAuthToken parceledAuthToken
                = TwitterAuthToken.CREATOR.createFromParcel(parcel);
        assertEquals(authToken, parceledAuthToken);
    }

    @Test
    public void testIsExpired() {
        final TwitterAuthToken token = new TwitterAuthToken(TestFixtures.TOKEN,
                TestFixtures.SECRET);
        assertFalse(token.isExpired());
    }
}
