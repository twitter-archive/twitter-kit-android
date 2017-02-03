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

package com.twitter.sdk.android.tweetcomposer;

import android.graphics.Bitmap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class RoundedCornerTransformationTest {
    static final float MAX_DELTA = 0.01f;
    static final int TEST_RADIUS = 10;
    static final int TEST_NEGATIVE_RADIUS = -10;
    static final float[] RESULT_RADII = {10, 10, 10, 10, 10, 10, 10, 10};

    @Test
    public void testTransform_verifyBitmapRecycled() {
        final Bitmap bm = mock(Bitmap.class);
        when(bm.getWidth()).thenReturn(4);
        when(bm.getHeight()).thenReturn(4);

        final RoundedCornerTransformation transformation = new RoundedCornerTransformation.Builder()
                .setRadius(TEST_RADIUS)
                .build();
        transformation.transform(bm);

        verify(bm).recycle();
    }

    @Test
    public void testKey() {
        final RoundedCornerTransformation transformation = new RoundedCornerTransformation.Builder()
                .setRadius(TEST_RADIUS)
                .build();

        final String key = "RoundedCornerTransformation(" + Arrays.toString(RESULT_RADII) + ")";
        assertEquals(key, transformation.key());
    }

    @Test
    public void testBuilder_setRadius() {
        final RoundedCornerTransformation transformation = new RoundedCornerTransformation.Builder()
                .setRadius(TEST_RADIUS)
                .build();

        assertArrayEquals(RESULT_RADII, transformation.radii, MAX_DELTA);
    }

    @Test
    public void testBuilder_setRadiusWithNegative() {
        try {
            new RoundedCornerTransformation.Builder()
                    .setRadius(TEST_NEGATIVE_RADIUS)
                    .build();
            fail();
        } catch (IllegalStateException ex) {
            assertEquals("Radius must not be negative", ex.getMessage());
        }
    }

    @Test
    public void testBuilder_setRadii() {
        final RoundedCornerTransformation transformation = new RoundedCornerTransformation.Builder()
                .setRadii(TEST_RADIUS, TEST_RADIUS, TEST_RADIUS, TEST_RADIUS)
                .build();

        assertArrayEquals(RESULT_RADII, transformation.radii, MAX_DELTA);
    }

    @Test
    public void testBuilder_setRadiiWithNegative() {
        try {
            new RoundedCornerTransformation.Builder()
                    .setRadii(TEST_RADIUS, TEST_NEGATIVE_RADIUS, TEST_RADIUS, TEST_RADIUS)
                    .build();
            fail();
        } catch (IllegalStateException ex) {
            assertEquals("Radius must not be negative", ex.getMessage());
        }
    }
}
