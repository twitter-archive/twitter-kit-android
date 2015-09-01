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

package com.twitter.sdk.android.core.internal;

import android.text.TextUtils;

import com.twitter.sdk.android.core.models.User;

public final class UserUtils {
    private UserUtils() {}

    // see https://dev.twitter.com/overview/general/user-profile-images-and-banners
    // see also: https://confluence.twitter.biz/display/PLATFORM/Image+Types+and+Sizes
    public enum AvatarSize {
        NORMAL("_normal"),
        BIGGER("_bigger"),
        MINI("_mini"),
        ORIGINAL("_original"),
        REASONABLY_SMALL("_reasonably_small");

        private final String suffix;

        AvatarSize(String suffix) {
            this.suffix = suffix;
        }

        String getSuffix() {
            return suffix;
        }
    }

    public static String getProfileImageUrlHttps(User user, AvatarSize size) {
        if (user != null && user.profileImageUrlHttps != null) {
            final String url = user.profileImageUrlHttps;
            if (size == null || url == null) {
                return url;
            }

            switch (size) {
                case NORMAL:
                case BIGGER:
                case MINI:
                case ORIGINAL:
                case REASONABLY_SMALL:
                    return url
                            .replace(AvatarSize.NORMAL.getSuffix(), size.getSuffix());
                default:
                    return url;
            }
        } else {
            return null;
        }
    }

    /**
     * @return the given screenName, prepended with an "@"
     */
    public static CharSequence formatScreenName(CharSequence screenName) {
        if (TextUtils.isEmpty(screenName)) {
            return "";
        }

        if (screenName.charAt(0) == '@') {
            return screenName;
        }
        return "@" + screenName;
    }
}
