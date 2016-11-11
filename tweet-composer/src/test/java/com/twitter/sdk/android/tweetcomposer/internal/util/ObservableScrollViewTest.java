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

package com.twitter.sdk.android.tweetcomposer.internal.util;

import com.twitter.sdk.android.tweetcomposer.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ObservableScrollViewTest {
    static final int TEST_SCROLL_X = 10;

    @Test
    public void testOnScrollChanged() {
        final ObservableScrollView scrollView =
                new ObservableScrollView(RuntimeEnvironment.application);
        final ObservableScrollView.ScrollViewListener listener =
                mock(ObservableScrollView.ScrollViewListener.class);
        scrollView.setScrollViewListener(listener);

        scrollView.onScrollChanged(0, TEST_SCROLL_X, 0, 0);

        verify(listener).onScrollChanged(TEST_SCROLL_X);
    }
}
