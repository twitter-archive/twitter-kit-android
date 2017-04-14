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
    public void testIsLightColor_blue() {
        assertFalse(ColorUtils.isLightColor(Color.BLUE));
    }

    @Test
    public void testIsLightColor_black() {
        assertFalse(ColorUtils.isLightColor(Color.BLACK));
    }

    @Test
    public void testIsLightColor_white() {
        assertTrue(ColorUtils.isLightColor(Color.WHITE));
    }

    @Test
    public void testCalculateOpacityTransform_zeroOpacity() {
        assertEquals(Color.WHITE, ColorUtils.calculateOpacityTransform(0, Color.BLUE, Color.WHITE));
    }

    @Test
    public void testCalculateOpacityTransform_fullOpacity() {
        assertEquals(Color.BLUE, ColorUtils.calculateOpacityTransform(1, Color.BLUE, Color.WHITE));
    }

    @Test
    public void testCalculateOpacityTransform_returnsFullOpacity() {
        final int color = ColorUtils.calculateOpacityTransform(0, Color.BLUE, Color.WHITE);
        assertEquals(0xFF000000, color & 0xFF000000);
    }
}
