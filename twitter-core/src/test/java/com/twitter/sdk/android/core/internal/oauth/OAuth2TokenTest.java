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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(RobolectricTestRunner.class)
public class OAuth2TokenTest  {

    private static final String TOKEN_TYPE = "tokenType";
    private static final String ACCESS_TOKEN = "accessToken";

    @Test
    public void testParcelable() {
        final OAuth2Token authToken = new OAuth2Token(TOKEN_TYPE, ACCESS_TOKEN);
        final Parcel parcel = Parcel.obtain();
        authToken.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        final OAuth2Token parceledAuthToken
                = OAuth2Token.CREATOR.createFromParcel(parcel);
        assertEquals(authToken, parceledAuthToken);
    }

    @Test
    public void testIsExpired() {
        final OAuth2Token token = new OAuth2Token(TOKEN_TYPE, ACCESS_TOKEN);
        assertFalse(token.isExpired());
    }
}
