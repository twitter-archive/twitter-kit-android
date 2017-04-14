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
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * An ImageView subclass that take a {@link android.graphics.drawable.Drawable} and draws it on top
 * the ImageView content.
 */
public class OverlayImageView extends ImageView {
    Overlay overlay = new Overlay(null);

    public OverlayImageView(Context context) {
        super(context);
    }

    public OverlayImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        overlay.draw(canvas);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        overlay.setDrawableState(getDrawableState());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        overlay.setDrawableBounds(getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        overlay.setDrawableBounds(width, height);
    }

    @Override
    public void invalidateDrawable(Drawable drawable) {
        if (drawable == overlay.drawable) {
            invalidate();
        } else {
            super.invalidateDrawable(drawable);
        }
    }

    /*
     * Sets the drawable to be drawn on top the ImageView content.
     *
     * @param drawable The drawable
     */
    public void setOverlayDrawable(Drawable drawable) {
        if (drawable != overlay.drawable) {
            overlay.cleanupDrawable(this);
            if (drawable != null) {
                drawable.setCallback(this);
            }

            overlay = new Overlay(drawable);
            overlay.setDrawableState(getDrawableState());
            requestLayout();
        }
    }

    /**
     * Takes a {@link android.graphics.drawable.Drawable} and draws it on top the ImageView content.
     * The overlay drawable will respect the view's current state so a selector can be passed in.
     */
    protected static class Overlay {
        final Drawable drawable;

        Overlay(Drawable drawable) {
            this.drawable = drawable;
        }

        protected void cleanupDrawable(ImageView imageView) {
            if (drawable != null) {
                drawable.setCallback(null);
                imageView.unscheduleDrawable(drawable);
            }
        }

        protected void setDrawableBounds(int width, int height) {
            if (drawable != null) {
                drawable.setBounds(0, 0, width, height);
            }
        }

        protected void setDrawableState(int[] state) {
            if (drawable != null && drawable.isStateful()) {
                drawable.setState(state);
            }
        }

        protected void draw(Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }
    }
}
