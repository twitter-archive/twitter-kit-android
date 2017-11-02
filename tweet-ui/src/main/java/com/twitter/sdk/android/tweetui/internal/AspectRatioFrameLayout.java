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

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.twitter.sdk.android.tweetui.R;

public class AspectRatioFrameLayout extends FrameLayout {

    private static final float DEFAULT_ASPECT_RATIO = 1.0f;
    private static final int DEFAULT_ADJUST_DIMENSION = 0;

    static final int ADJUST_DIMENSION_HEIGHT = 0;
    static final int ADJUST_DIMENSION_WIDTH = 1;

    protected double aspectRatio;
    private int dimensionToAdjust;      // ADJUST_DIMENSION_HEIGHT or ADJUST_DIMENSION_WIDTH

    public AspectRatioFrameLayout(Context context) {
        this(context, null);
    }

    public AspectRatioFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AspectRatioFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttributes(defStyle);
    }

    private void initAttributes(int styleResId) {
        final TypedArray a = getContext().getTheme().obtainStyledAttributes(styleResId,
                R.styleable.AspectRatioFrameLayout);
        try {
            aspectRatio = a.getFloat(
                    R.styleable.AspectRatioFrameLayout_tw__frame_layout_aspect_ratio,
                    DEFAULT_ASPECT_RATIO);
            dimensionToAdjust = a.getInt(
                    R.styleable.AspectRatioFrameLayout_tw__frame_layout_dimension_to_adjust,
                    DEFAULT_ADJUST_DIMENSION);
        } finally {
            a.recycle();
        }
    }

    public void setAspectRatio(final double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width;
        final int height;
        final int horizontalPadding = getPaddingLeft() + getPaddingRight();
        final int verticalPadding = getPaddingBottom() + getPaddingTop();

       if (dimensionToAdjust == ADJUST_DIMENSION_HEIGHT) {
           if (View.MeasureSpec.getMode(widthMeasureSpec) == View.MeasureSpec.EXACTLY) {
               width = View.MeasureSpec.getSize(widthMeasureSpec) - horizontalPadding;
           } else {
               super.onMeasure(widthMeasureSpec, heightMeasureSpec);
               width = getMeasuredWidth() - horizontalPadding;
           }
           height = (int) (width / aspectRatio);
       } else {
           if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
               height = MeasureSpec.getSize(heightMeasureSpec) - verticalPadding;
           } else {
               super.onMeasure(widthMeasureSpec, heightMeasureSpec);
               height = getMeasuredHeight() - verticalPadding;
           }
           width = (int) (height * aspectRatio);
       }

        super.onMeasure(
            View.MeasureSpec.makeMeasureSpec(width + horizontalPadding, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height + verticalPadding, View.MeasureSpec.EXACTLY));
    }
}
