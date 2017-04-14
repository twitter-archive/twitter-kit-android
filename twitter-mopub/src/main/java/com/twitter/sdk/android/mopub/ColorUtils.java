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

package com.twitter.sdk.android.mopub;

import android.graphics.Color;

final class ColorUtils {
    private static final int RGB_TOTAL_COLORS = 256;

    private static final float DEFAULT_LIGHTNESS_THRESHOLD = .6f;
    private static final float ON_TAP_LIGHTNESS_THRESHOLD = .3f;

    private static final float CTA_ON_TAP_DARKNESS_FACTOR = 0.1f;
    private static final float CTA_ON_TAP_LIGHTNESS_FACTOR = 0.2f;
    private static final float CTA_TEXT_LIGHTNESS_FACTOR = .6f;

    private static final int OPAQUE_ALPHA = Math.round(255 * 1.0f);
    private static final int TRANSPARENT_ALPHA = Math.round(255 * 0.9f);
    private static final int COLOR_FULLY_WHITE = Math.round(255 * 1.0f);
    private static final int COLOR_PARTIALLY_BLACK = Math.round(255 * 0.4f);

    private ColorUtils() {}

    public static int calculateCtaTextColor(final int ctaBackgroundColor) {
        if (isLightColor(ctaBackgroundColor)) {
            return calculateDarkerColor(ctaBackgroundColor, CTA_TEXT_LIGHTNESS_FACTOR);
        } else {
            return Color.WHITE;
        }
    }

    public static int calculateCtaOnTapColor(final int ctaBackgroundColor) {
        if (isLightColor(ctaBackgroundColor, ON_TAP_LIGHTNESS_THRESHOLD)) {
            return calculateDarkerColor(ctaBackgroundColor, CTA_ON_TAP_DARKNESS_FACTOR);
        } else {
            return calculateLighterColor(ctaBackgroundColor, CTA_ON_TAP_LIGHTNESS_FACTOR);
        }
    }

    public static boolean isLightColor(final int color) {
        return isLightColor(color, DEFAULT_LIGHTNESS_THRESHOLD);
    }

    /**
     * This method calculates a darker color provided a factor of reduction in lightness.
     *
     * @param color The original color value
     * @param factor Factor of lightness reduction, range can be between 0 - 1.0
     * @return  The calculated darker color
     */
    public static int calculateDarkerColor(final int color, final float factor) {
        final int a = Color.alpha(color);
        final int r = Color.red(color);
        final int g = Color.green(color);
        final int b = Color.blue(color);

        final int lightnessLevel = Math.round(RGB_TOTAL_COLORS * factor);

        return Color.argb(a,
                Math.max(r - lightnessLevel, 0),
                Math.max(g - lightnessLevel, 0),
                Math.max(b - lightnessLevel, 0));
    }

    /**
     * This method calculates a lighter color provided a factor of increase in lightness.
     *
     * @param color A color value
     * @param factor Factor of increase in lightness, range can be between 0 - 1.0
     * @return  The calculated darker color
     */
    public static int calculateLighterColor(final int color, final float factor) {
        final int a = Color.alpha(color);
        final int r = Color.red(color);
        final int g = Color.green(color);
        final int b = Color.blue(color);

        final int lightnessLevel = Math.round(RGB_TOTAL_COLORS * factor);

        return Color.argb(a,
                Math.min(r + lightnessLevel, 255),
                Math.min(g + lightnessLevel, 255),
                Math.min(b + lightnessLevel, 255));
    }

    /**
     * This method calculates the suitable contrasting color that is viewable.
     *
     * @param color A color value.
     * @return  The calculated contrasting color that is viewable.
     */
    public static int calculateContrastingColor(final int color) {
        final boolean isLightColor = isLightColor(color);
        final int alpha = isLightColor ? OPAQUE_ALPHA : TRANSPARENT_ALPHA;
        final int rgbColor = isLightColor ? COLOR_PARTIALLY_BLACK : COLOR_FULLY_WHITE;
        return Color.argb(alpha, rgbColor, rgbColor, rgbColor);
    }

    /**
     * This method uses HSL to determine in a human eyesight terms if a color is light or not.
     * See: http://en.wikipedia.org/wiki/HSL_and_HSV. The threshold values are from ITU Rec. 709
     * http://en.wikipedia.org/wiki/Rec._709#Luma_coefficients
     *
     *
     * @param  color A color value
     * @param  factor A factor of lightness measured between 0-1.0
     * @return Whether or not the color is considered light
     */
    public static boolean isLightColor(final int color, final float factor) {
        final int r = Color.red(color);
        final int g = Color.green(color);
        final int b = Color.blue(color);

        final double threshold = 0.21 * r + 0.72 * g + 0.07 * b;
        return threshold > (RGB_TOTAL_COLORS * factor);
    }
}
