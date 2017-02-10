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

package com.twitter.sdk.android.tweetcomposer.internal.util;

import android.util.AttributeSet;

import com.twitter.sdk.android.tweetcomposer.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class AspectRatioImageViewTest {
    private static final double TEST_ASPECT_RATIO = 2.0;
    private static final float DELTA = 0.001f;

    private AspectRatioImageView getHeightAdjustedView() {
        final AttributeSet attrs = Robolectric.buildAttributeSet()
                .addAttribute(R.attr.tw__image_dimension_to_adjust, "height")
                .addAttribute(R.attr.tw__image_aspect_ratio, "1.6")
                .build();

        return new AspectRatioImageView(RuntimeEnvironment.application, attrs);
    }

    private AspectRatioImageView getWidthAdjustedView() {
        final AttributeSet attrs = Robolectric.buildAttributeSet()
                .addAttribute(R.attr.tw__image_dimension_to_adjust, "width")
                .addAttribute(R.attr.tw__image_aspect_ratio, "1.2")
                .build();

        return new AspectRatioImageView(RuntimeEnvironment.application, attrs);
    }

    @Test
    public void testHeightAdjusted() {
        final AspectRatioImageView imageView = getHeightAdjustedView();
        assertEquals(1.6, imageView.getAspectRatio(), DELTA);
        assertEquals(AspectRatioImageView.ADJUST_DIMENSION_HEIGHT,
                imageView.getDimensionToAdjust());
    }

    @Test
    public void testWidthAdjusted() {
        final AspectRatioImageView imageView = getWidthAdjustedView();
        assertEquals(1.2, imageView.getAspectRatio(), DELTA);
        assertEquals(AspectRatioImageView.ADJUST_DIMENSION_WIDTH,
                imageView.getDimensionToAdjust());
    }

    @Test
    public void testSetAspectRatio() {
        final AspectRatioImageView av = new AspectRatioImageView(RuntimeEnvironment.application);
        av.setAspectRatio(TEST_ASPECT_RATIO);
        assertEquals(TEST_ASPECT_RATIO, av.getAspectRatio(), DELTA);
    }

    @Test
    public void testSetAspectRatio_withAttributeSet() {
        final AspectRatioImageView av = getHeightAdjustedView();
        av.setAspectRatio(TEST_ASPECT_RATIO);
        assertEquals(TEST_ASPECT_RATIO, av.getAspectRatio(), DELTA);
    }

    @Test
    public void testCalculateHeight() {
        final AspectRatioImageView av = new AspectRatioImageView(RuntimeEnvironment.application);
        assertEquals(400, av.calculateHeight(600, 1.5));
        assertEquals(600, av.calculateHeight(300, 0.5));
        assertEquals(300, av.calculateHeight(300, 1.0));
        assertEquals(0, av.calculateHeight(0, 1.3));
        assertEquals(0, av.calculateHeight(100, 0));
        // sub-pixel space for images mean aspect ratios cannot be respected
        assertEquals(1, av.calculateHeight(10, 15.0));
    }

    @Test
    public void testCalculateWidth() {
        final AspectRatioImageView av = new AspectRatioImageView(RuntimeEnvironment.application);
        assertEquals(300, av.calculateWidth(200, 1.5));
        assertEquals(201, av.calculateWidth(401, 0.5));
        assertEquals(200, av.calculateWidth(200, 1.0));
        assertEquals(0, av.calculateWidth(0, 1.3));
        assertEquals(0, av.calculateWidth(100, 0));
        // sub-pixel space for images mean aspect ratios cannot be respected
        assertEquals(1, av.calculateWidth(10, 0.05));
    }
}
