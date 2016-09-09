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

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;

import java.util.Locale;

public final class TestUtils {

    private TestUtils() {}

    /**
     * Sets global locale
     * @param locale Locale to set
     */
    public static Locale setLocale(Context context, Locale locale) {
        final Resources res = context.getResources();
        final Configuration config = res.getConfiguration();
        final Locale originalLocale = config.locale;

        Locale.setDefault(locale);
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
        return originalLocale;
    }


    /**
     * Gets the color of the ImageView's ColorDrawable or 0 for API &lt; 11.
     * @param imageView an ImageView with a ColorDrawable
     * @return int color of the ImageView
     */
    public static int getDrawableColor(ImageView imageView) {
        final ColorDrawable drawable = (ColorDrawable) imageView.getDrawable();
        return drawable.getColor();
    }

    public static int getBackgroundColor(ImageView imageView) {
        final ColorDrawable drawable = (ColorDrawable) imageView.getBackground();
        return drawable.getColor();
    }
}
