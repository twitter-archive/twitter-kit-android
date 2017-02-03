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
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(RobolectricTestRunner.class)
public class GalleryImageViewTest {
    @Mock
    SwipeToDismissTouchListener.Callback callback;
    @Mock
    Drawable drawable;
    MultiTouchImageView imageView;
    ProgressBar progressBar;
    Bitmap bitmap;
    GalleryImageView subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        imageView = spy(new MultiTouchImageView(RuntimeEnvironment.application));
        progressBar = spy(new ProgressBar(RuntimeEnvironment.application));
        bitmap = Bitmap.createBitmap(10, 10, null);
        subject = new GalleryImageView(RuntimeEnvironment.application, imageView, progressBar);
    }

    @Test
    public void testConstructor() {
        assertNotNull(subject.imageView);
        assertNotNull(subject.progressBar);

        FrameLayout.LayoutParams params;
        params = (FrameLayout.LayoutParams) subject.imageView.getLayoutParams();
        assertEquals(FrameLayout.LayoutParams.MATCH_PARENT, params.height);
        assertEquals(FrameLayout.LayoutParams.MATCH_PARENT, params.width);
        assertEquals(Gravity.CENTER, params.gravity);

        params = (FrameLayout.LayoutParams) subject.progressBar.getLayoutParams();
        assertEquals(FrameLayout.LayoutParams.WRAP_CONTENT, params.height);
        assertEquals(FrameLayout.LayoutParams.WRAP_CONTENT, params.width);
        assertEquals(Gravity.CENTER, params.gravity);
    }

    @Test
    public void testSetSwipeToDismissCallback() {
        subject.setSwipeToDismissCallback(callback);

        verify(subject.imageView).setOnTouchListener(any(View.OnTouchListener.class));
    }

    @Test
    public void testOnBitmapLoaded() {
        subject.onBitmapLoaded(bitmap, null);

        verify(subject.imageView).setImageBitmap(bitmap);
        verify(subject.progressBar).setVisibility(View.GONE);
    }

    @Test
    public void testOnBitmapFailed() {
        reset(subject.imageView, subject.progressBar);

        subject.onBitmapFailed(drawable);

        verifyZeroInteractions(subject.imageView, subject.progressBar);
    }

    @Test
    public void testOnPrepareLoad() {
        subject.onPrepareLoad(drawable);

        verify(subject.imageView).setImageResource(android.R.color.transparent);
        verify(subject.progressBar).setVisibility(View.VISIBLE);
    }
}
