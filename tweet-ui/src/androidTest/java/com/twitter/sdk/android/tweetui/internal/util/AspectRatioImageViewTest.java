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

package com.twitter.sdk.android.tweetui.internal.util;

import android.test.AndroidTestCase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.twitter.sdk.android.tweetui.R;

public class AspectRatioImageViewTest extends AndroidTestCase {

    private static final float DELTA = 0.001f;

    private AspectRatioImageView getHeightAdjustedView() {
        return (AspectRatioImageView) getInflatedLayout().findViewById(R.id.height_adjusted_view);
    }

    private AspectRatioImageView getWidthAdjustedView() {
        return (AspectRatioImageView) getInflatedLayout().findViewById(R.id.width_adjusted_view);
    }

    public void testHeightAdjusted() {
        final AspectRatioImageView imageView = getHeightAdjustedView();
        assertEquals(1.6, imageView.getAspectRatio(), DELTA);
        assertEquals(AspectRatioImageView.ADJUST_DIMENSION_HEIGHT,
                imageView.getDimensionToAdjust());
    }

    public void testWidthAdjusted() {
        final AspectRatioImageView imageView = getWidthAdjustedView();
        assertEquals(1.2, imageView.getAspectRatio(), DELTA);
        assertEquals(AspectRatioImageView.ADJUST_DIMENSION_WIDTH,
                imageView.getDimensionToAdjust());
    }

    private View getInflatedLayout() {
        final ViewGroup group = new LinearLayout(getContext());
        return LayoutInflater.from(getContext()).inflate(
                R.layout.activity_aspect_ratio_image_view_test, group, true);
    }
}
