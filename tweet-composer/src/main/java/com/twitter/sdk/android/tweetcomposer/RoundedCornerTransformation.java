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
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

import java.util.Arrays;

import static android.graphics.Bitmap.createBitmap;

class RoundedCornerTransformation implements Transformation {
    final float[] radii;

    RoundedCornerTransformation(float[] radii) {
        this.radii = radii;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        final RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());
        final Bitmap result = createBitmap(source.getWidth(), source.getHeight(),
                source.getConfig());
        final BitmapShader bitmapShader = new BitmapShader(source, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);

        final Path path = new Path();
        path.addRoundRect(rect, radii, Path.Direction.CCW);

        final Canvas canvas = new Canvas(result);
        canvas.drawPath(path, paint);

        source.recycle();

        return result;
    }

    @Override
    public String key() {
        return "RoundedCornerTransformation(" + Arrays.toString(radii) + ")";
    }

    public static class Builder {
        int topLeftRadius;
        int topRightRadius;
        int bottomRightRadius;
        int bottomLeftRadius;

        public Builder setRadius(int radius) {
            topLeftRadius = radius;
            topRightRadius = radius;
            bottomRightRadius = radius;
            bottomLeftRadius = radius;

            return this;
        }

        public Builder setRadii(int topLeftRadius, int topRightRadius, int bottomRightRadius,
                int bottomLeftRadius) {
            this.topLeftRadius = topLeftRadius;
            this.topRightRadius = topRightRadius;
            this.bottomRightRadius = bottomRightRadius;
            this.bottomLeftRadius = bottomLeftRadius;

            return this;
        }

        RoundedCornerTransformation build() {
            if (topLeftRadius < 0 || topRightRadius < 0 ||
                    bottomRightRadius < 0 || bottomLeftRadius < 0) {
                throw new IllegalStateException("Radius must not be negative");
            }

            final float[] radii = {topLeftRadius, topLeftRadius,
                    topRightRadius, topRightRadius,
                    bottomRightRadius, bottomRightRadius,
                    bottomLeftRadius, bottomLeftRadius};

            return new RoundedCornerTransformation(radii);
        }
    }
}
