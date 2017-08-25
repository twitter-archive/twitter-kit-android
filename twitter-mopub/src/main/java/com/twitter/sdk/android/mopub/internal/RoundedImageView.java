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

package com.twitter.sdk.android.mopub.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import static android.graphics.Bitmap.createBitmap;

public class RoundedImageView extends ImageView {
    private float[] roundedCornerRadii;

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        setDefaultCornerRadii();
    }

    public RoundedImageView(Context context, AttributeSet attrs, int styleResId) {
        super(context, attrs, styleResId);
        setDefaultCornerRadii();
    }

    private void setDefaultCornerRadii() {
        setCornerRadii(0, 0, 0, 0);
    }

    /**
     * Set radius for each corner and override default behavior of no rounded corners.
     *
     * @param topLeftRadius     top left radius of view
     * @param topRightRadius    top right radius of view
     * @param bottomLeftRadius  bottom left radius of view
     * @param bottomRightRadius bottom right radius of view
     */
    public void setCornerRadii(int topLeftRadius, int topRightRadius,
                                        int bottomLeftRadius, int bottomRightRadius) {
        if (topLeftRadius < 0 || topRightRadius < 0 ||
                bottomRightRadius < 0 || bottomLeftRadius < 0) {
            throw new IllegalStateException("Radius must not be negative");
        }

        roundedCornerRadii = new float[]{
                topLeftRadius, topLeftRadius,
                topRightRadius, topRightRadius,
                bottomLeftRadius, bottomLeftRadius,
                bottomRightRadius, bottomRightRadius};
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            setImageDrawable(new BitmapDrawable(getResources(), transform(bitmap)));
        } else {
            setImageDrawable(null);
        }
    }

    private Bitmap transform(Bitmap source) {
        final RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());
        final Bitmap result = createBitmap(source.getWidth(), source.getHeight(),
                source.getConfig());
        final BitmapShader bitmapShader = new BitmapShader(source, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);

        final Path path = new Path();
        path.addRoundRect(rect, roundedCornerRadii, Path.Direction.CCW);

        final Canvas canvas = new Canvas(result);
        canvas.drawPath(path, paint);
        return result;
    }
}
