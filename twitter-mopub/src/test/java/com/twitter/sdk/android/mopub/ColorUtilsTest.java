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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class ColorUtilsTest {

    @Test
    public void testIsLightColor_black() {
        assertFalse(ColorUtils.isLightColor(Color.BLACK));
    }

    @Test
    public void testIsLightColor_white() {
        assertTrue(ColorUtils.isLightColor(Color.WHITE));
    }

    @Test
    public void testDefaultCtaButtonIsDarkColor() {
        assertFalse(ColorUtils.isLightColor(R.color.tw__ad_cta_default));
    }

    @Test
    public void testCtaTextColorIsLightForDarkBgColor() {
        assertEquals(Color.WHITE, ColorUtils.calculateCtaTextColor(R.color.tw__ad_cta_default));
        assertEquals(Color.WHITE, ColorUtils.calculateCtaTextColor(Color.BLACK));
        assertEquals(Color.WHITE, ColorUtils.calculateCtaTextColor(Color.DKGRAY));
    }

    @Test
    public void testCtaTextColorIsDarkForLightBgColor() {
        assertTrue(Color.WHITE != ColorUtils.calculateCtaTextColor(Color.WHITE));
        assertTrue(Color.WHITE != ColorUtils.calculateCtaTextColor(Color.LTGRAY));
    }

    @Test
    public void testCTAOnTapColorIsLighterForDarkBgColor() {
        final int darkColor = Color.BLACK;
        final int originalRed = Color.red(darkColor);
        final int originalGreen = Color.green(darkColor);
        final int originalBlue = Color.blue(darkColor);

        final int lighterColor = ColorUtils.calculateCtaOnTapColor(darkColor);
        final int lighterRed = Color.red(lighterColor);
        final int lighterGreen = Color.green(lighterColor);
        final int lighterBlue = Color.blue(lighterColor);

        assertTrue(lighterRed > originalRed
                && lighterGreen > originalGreen
                && lighterBlue > originalBlue);
    }

    @Test
    public void testCTAOnTapColorIsDarkerForLightBgColor() {
        final int lightColor = Color.WHITE;
        final int originalRed = Color.red(lightColor);
        final int originalGreen = Color.green(lightColor);
        final int originalBlue = Color.blue(lightColor);

        final int darkerColor = ColorUtils.calculateCtaOnTapColor(lightColor);
        final int darkerRed = Color.red(darkerColor);
        final int darkerGreen = Color.green(darkerColor);
        final int darkerBlue = Color.blue(darkerColor);

        assertTrue(originalRed > darkerRed
                && originalGreen > darkerGreen
                && originalBlue > darkerBlue);
    }

    @Test
    public void testContrastColorForDarkColor() {
        final int darkColor = Color.BLACK;
        final int contrastingLightColor = ColorUtils.calculateContrastingColor(darkColor);
        assertTrue(ColorUtils.isLightColor(contrastingLightColor));
    }

    @Test
    public void testContrastColorForLightColor() {
        final int lightColor = Color.WHITE;
        final int contrastingDarkColor = ColorUtils.calculateContrastingColor(lightColor);
        assertFalse(ColorUtils.isLightColor(contrastingDarkColor));
    }
}
