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

package com.twitter.sdk.android.tweetui;

import android.view.View;
import android.view.ViewGroup;

import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.tweetui.internal.GalleryImageView;
import com.twitter.sdk.android.tweetui.internal.SwipeToDismissTouchListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class GalleryAdapterTest {
    @Mock
    SwipeToDismissTouchListener.Callback callback;
    @Mock
    MediaEntity entity;
    @Mock
    View view;
    @Mock
    ViewGroup container;
    GalleryAdapter subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        subject = new GalleryAdapter(RuntimeEnvironment.application, callback);
    }

    @Test
    public void testGetCount_withZeroItems() {
        assertEquals(0, subject.getCount());
    }

    @Test
    public void testGetCount_withOneItems() {
        subject.addAll(Collections.singletonList(entity));
        assertEquals(1, subject.getCount());
    }

    @Test
    public void testIsViewFromObject_withSameObject() {
        assertTrue(subject.isViewFromObject(view, view));
    }

    @Test
    public void testIsViewFromObject_withDifferentObject() {
        assertFalse(subject.isViewFromObject(view, entity));
    }

    @Test
    public void testInstantiateItem() {
        subject.addAll(Collections.singletonList(entity));
        final GalleryImageView result = (GalleryImageView) subject.instantiateItem(container, 0);

        assertNotNull(result);
        verify(container).addView(result);
    }

    @Test
    public void testDestroyItem() {
        subject.destroyItem(container, 0, view);

        verify(container).removeView(view);
    }
}
