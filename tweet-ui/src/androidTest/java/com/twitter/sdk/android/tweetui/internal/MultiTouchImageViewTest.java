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

package com.twitter.sdk.android.tweetui.internal;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.test.AndroidTestCase;

public class MultiTouchImageViewTest extends AndroidTestCase {
    static final RectF TEST_VIEW_RECT = new RectF(0, 0, 100, 100);
    static final Matrix TEST_BASE_MATRIX = new MatrixBuilder().postScale(2f).build();
    static final Matrix TEST_IDENTITY_MATRIX = new MatrixBuilder().build();
    static final float TEST_BASE_SCALE = 1f;
    static final Bitmap image = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
    MultiTouchImageView view;

    public void setUp() throws Exception {
        super.setUp();
        view = new MultiTouchImageView(getContext());
        view.setImageBitmap(image);
        view.layout(0, 0, 100, 100);
    }

    public void testInitialViewState() {
        assertEquals(TEST_BASE_MATRIX, view.baseMatrix);
        assertEquals(TEST_IDENTITY_MATRIX, view.updateMatrix);
        assertEquals(TEST_VIEW_RECT, view.viewRect);
        assertEquals(TEST_BASE_SCALE, view.getScale());
        assertEquals(TEST_BASE_MATRIX, view.getDrawMatrix());
    }

    public void testGetDrawRect() {
        final Matrix matrix = new MatrixBuilder()
                .postScale(2f)
                .postTranslate(10f, 10f)
                .build();
        final RectF result = view.getDrawRect(matrix);
        final RectF expected = new RectF(10f, 10f, 110f, 110f);
        assertEquals(expected, result);
    }

    public void testSetScale() {
        view.setScale(1.5f, 50f, 50f);

        final Matrix expected = new MatrixBuilder()
                .postScale(1.5f)
                .postTranslate(-25f, -25f)
                .build();
        assertEquals(expected, view.updateMatrix);
        assertEquals(1.5f, view.getScale());
        assertEquals(TEST_BASE_MATRIX, view.baseMatrix);
    }

    public void testReset() {
        view.setScale(1.5f, 50f, 50f);
        view.reset();

        assertEquals(TEST_IDENTITY_MATRIX, view.updateMatrix);
        assertEquals(TEST_BASE_SCALE, view.getScale());
        assertEquals(TEST_BASE_MATRIX, view.baseMatrix);
    }

    public void testSetTranslate() {
        view.setTranslate(10f, 10f);

        final Matrix expected = new MatrixBuilder()
                .postTranslate(10f, 10f)
                .build();
        assertEquals(expected, view.updateMatrix);
        assertEquals(TEST_BASE_SCALE, view.getScale());
        assertEquals(TEST_BASE_MATRIX, view.baseMatrix);
    }

    public void testCanBeSwiped_withScaleEqualOne() {
        assertTrue(view.canBeSwiped());
    }

    public void testCanBeSwiped_withScaleGreaterThanOne() {
        view.setScale(2, 0, 0);
        assertFalse(view.canBeSwiped());
    }

    static class MatrixBuilder {
        private final Matrix matrix = new Matrix();

        MatrixBuilder postScale(float scale) {
            matrix.postScale(scale, scale);
            return this;
        }

        MatrixBuilder postTranslate(float x, float y) {
            matrix.postTranslate(x, y);
            return this;
        }

        Matrix build() {
            return matrix;
        }
    }
}
