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

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

public class MultiTouchImageView extends ImageView
        implements SwipeToDismissTouchListener.SwipeableViewProvider {
    private static final long SCALE_ANIMATION_DURATION = 300L;
    private static final float DOUBLE_TAP_SCALE_FACTOR = 2.0f;
    private static final float MINIMUM_SCALE_FACTOR = 1.0f;

    final ScaleGestureDetector scaleGestureDetector;
    final GestureDetector gestureDetector;

    final Matrix drawMatrix = new Matrix();
    final Matrix baseMatrix = new Matrix();
    final Matrix updateMatrix = new Matrix();
    final RectF viewRect = new RectF();

    // Used to avoid allocating new objects
    final RectF drawRect = new RectF();
    final float[] matrixValues = new float[9];

    boolean allowIntercept;

    public MultiTouchImageView(Context context) {
        this(context, null);
    }

    public MultiTouchImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiTouchImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector
                .SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                setScale(scaleGestureDetector.getScaleFactor(), scaleGestureDetector.getFocusX(),
                        scaleGestureDetector.getFocusY());
                setImageMatrix();
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                if (getScale() < MINIMUM_SCALE_FACTOR) {
                    reset();
                    setImageMatrix();
                }
            }
        });

        gestureDetector = new GestureDetector(context, new GestureDetector
                .SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
                setTranslate(-dx, -dy);
                setImageMatrix();

                if (allowIntercept && !scaleGestureDetector.isInProgress()) {
                    requestDisallowInterceptTouchEvent(false);
                }

                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (getScale() > MINIMUM_SCALE_FACTOR) {
                    animateScale(getScale(), MINIMUM_SCALE_FACTOR, e.getX(), e.getY());
                } else {
                    animateScale(getScale(), DOUBLE_TAP_SCALE_FACTOR, e.getX(), e.getY());
                }
                return true;
            }
        });
    }

    boolean isInitializationComplete() {
        final Drawable drawable = getDrawable();
        return drawable != null && drawable.getIntrinsicWidth() > 0;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (isInitializationComplete()) {
            initializeViewRect();
            initializeBaseMatrix(getDrawable());
            setImageMatrix();
        }
    }

    void initializeViewRect() {
        viewRect.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(),
                getHeight() - getPaddingBottom());
    }

    void initializeBaseMatrix(Drawable drawable) {
        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        final RectF srcRect = new RectF(0, 0, drawableWidth, drawableHeight);

        baseMatrix.reset();
        baseMatrix.setRectToRect(srcRect, viewRect, Matrix.ScaleToFit.CENTER);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isInitializationComplete()) {
            return false;
        }

        // Do not allow touch events to be intercepted (usually for gallery swipes) by default
        requestDisallowInterceptTouchEvent(true);

        boolean retVal = scaleGestureDetector.onTouchEvent(event);
        retVal = gestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }

    void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        final ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    void setScale(float ds, float px, float py) {
        updateMatrix.postScale(ds, ds, px, py);
    }

    float getScale() {
        updateMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    void setTranslate(float dx, float dy) {
        updateMatrix.postTranslate(dx, dy);
    }

    void reset() {
        updateMatrix.reset();
    }

    void updateMatrixBounds() {
        final RectF rect = getDrawRect(getDrawMatrix());
        float dy = 0;
        float dx = 0;

        if (rect.height() <= viewRect.height()) {
            dy = (viewRect.height() - rect.height()) / 2 - rect.top;
        } else if (rect.top > 0) {
            dy = -rect.top;
        } else if (rect.bottom < viewRect.height()) {
            dy = viewRect.height() - rect.bottom;
        }

        if (rect.width() <= viewRect.width()) {
            allowIntercept = true;
            dx = (viewRect.width() - rect.width()) / 2 - rect.left;
        } else if (rect.left > 0) {
            allowIntercept = true;
            dx = -rect.left;
        } else if (rect.right < viewRect.width()) {
            allowIntercept = true;
            dx = viewRect.width() - rect.right;
        } else {
            allowIntercept = false;
        }

        setTranslate(dx, dy);
    }

    RectF getDrawRect(Matrix matrix) {
        final Drawable drawable = getDrawable();
        if (drawable != null) {
            drawRect.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            matrix.mapRect(drawRect);
        }

        return drawRect;
    }

    Matrix getDrawMatrix() {
        drawMatrix.set(baseMatrix);
        drawMatrix.postConcat(updateMatrix);

        return drawMatrix;
    }

    void setImageMatrix() {
        updateMatrixBounds();
        setScaleType(ScaleType.MATRIX);
        setImageMatrix(getDrawMatrix());
    }

    void animateScale(float start, float end, final float px, final float py) {
        final ValueAnimator animator = ValueAnimator.ofFloat(start, end);

        animator.setDuration(SCALE_ANIMATION_DURATION);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(valueAnimator -> {
            final float scale = (float) valueAnimator.getAnimatedValue();
            final float ds = scale / getScale();

            setScale(ds, px, py);
            setImageMatrix();
        });
        animator.start();
    }

    @Override
    public boolean canBeSwiped() {
        return getScale() == 1f;
    }
}
