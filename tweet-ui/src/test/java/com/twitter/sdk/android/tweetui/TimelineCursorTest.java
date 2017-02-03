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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class TimelineCursorTest {
    private static final Long TEST_MAX_POSITION = 200L;
    private static final Long TEST_MIN_POSITION = 100L;

    @Test
    public void testConstructor() {
        final TimelineCursor cursor = new TimelineCursor(TEST_MIN_POSITION, TEST_MAX_POSITION);
        assertEquals(TEST_MIN_POSITION, cursor.minPosition);
        assertEquals(TEST_MAX_POSITION, cursor.maxPosition);
    }

    @Test
    public void testConstructor_withList() {
        final List<TestItem> testItems = new ArrayList<>();
        testItems.add(new TestItem(TEST_MAX_POSITION));
        testItems.add(new TestItem(TEST_MIN_POSITION));
        final TimelineCursor cursor = new TimelineCursor(testItems);
        assertEquals(TEST_MIN_POSITION, cursor.minPosition);
        assertEquals(TEST_MAX_POSITION, cursor.maxPosition);
    }
}
