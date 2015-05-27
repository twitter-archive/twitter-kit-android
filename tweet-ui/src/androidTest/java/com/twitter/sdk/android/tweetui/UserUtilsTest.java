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

package com.twitter.sdk.android.tweetui;

import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.models.UserBuilder;

import io.fabric.sdk.android.FabricAndroidTestCase;

public class UserUtilsTest extends FabricAndroidTestCase {
    private User user;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        user = new UserBuilder()
                .setId(1)
                .setProfileImageUrlHttps(
                        "https://pbs.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3_normal.png"
                ).build();
    }

    public void testGetProfileImageUrlHttps_nullSize() {
        assertEquals(user.profileImageUrlHttps,
                UserUtils.getProfileImageUrlHttps(user, null));
    }

    public void testGetProfileImageUrlHttps_reasonablySmall() {
        final String reasonableSize = "https://pbs.twimg.com/profile_images/2284174872/" +
                  "7df3h38zabcvjylnyfe3_reasonably_small.png";
        assertEquals(reasonableSize,
                UserUtils.getProfileImageUrlHttps(user, UserUtils.AvatarSize.REASONABLY_SMALL));
    }

    public void testFormatScreenName_alreadyFormatted() {
        final String test = "@test";
        assertEquals(test, UserUtils.formatScreenName(test));
    }

    public void testFormatScreenName() {
        final String test = "@test";
        assertEquals("@test", UserUtils.formatScreenName(test));
    }
}
