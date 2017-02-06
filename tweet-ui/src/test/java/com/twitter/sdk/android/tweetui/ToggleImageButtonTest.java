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

import android.util.AttributeSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class ToggleImageButtonTest {
    private static final String CONTENT_DESCRIPTION_ON = "ContentDescriptionOn";
    private static final String CONTENT_DESCRIPTION_OFF = "ContentDescriptionOff";

    ToggleImageButton createDefaultButton() {
        return new ToggleImageButton(RuntimeEnvironment.application);
    }

    ToggleImageButton createButtonWithAttributes() {
        final AttributeSet attrs = Robolectric.buildAttributeSet()
                .addAttribute(R.attr.contentDescriptionOff, CONTENT_DESCRIPTION_OFF)
                .addAttribute(R.attr.contentDescriptionOn, CONTENT_DESCRIPTION_ON)
                .addAttribute(R.attr.toggleOnClick, "false")
                .build();

        return new ToggleImageButton(RuntimeEnvironment.application, attrs);
    }

    @Test
    public void testInit() {
        final ToggleImageButton button = createDefaultButton();
        assertNull(button.contentDescriptionOn);
        assertNull(button.contentDescriptionOff);
        assertFalse(button.isToggledOn());
        assertTrue(button.toggleOnClick);
    }

    @Test
    public void testPerformClick() {
        final ToggleImageButton button = createDefaultButton();
        assertTrue(button.toggleOnClick);
        assertFalse(button.isToggledOn());
        button.performClick();
        assertTrue(button.isToggledOn());
    }

    @Test
    public void testSetToggledOn() {
        final ToggleImageButton button = createDefaultButton();
        assertFalse(button.isToggledOn());
        button.setToggledOn(true);
        assertTrue(button.isToggledOn());
    }

    @Test
    public void testToggle() {
        final ToggleImageButton button = createDefaultButton();
        assertFalse(button.isToggledOn());
        button.toggle();
        assertTrue(button.isToggledOn());
    }

    @Test
    public void testXmlInit() {
        final ToggleImageButton button = createButtonWithAttributes();
        assertEquals(CONTENT_DESCRIPTION_ON, button.contentDescriptionOn);
        assertEquals(CONTENT_DESCRIPTION_OFF, button.contentDescriptionOff);
        assertFalse(button.isToggledOn());
        assertEquals(CONTENT_DESCRIPTION_OFF, button.getContentDescription());
        assertFalse(button.toggleOnClick);
    }

    @Test
    public void testPerformClick_toggleOnClickDisabled() {
        final ToggleImageButton button = createButtonWithAttributes();
        assertFalse(button.toggleOnClick);
        assertFalse(button.isToggledOn());
        button.performClick();
        assertFalse(button.isToggledOn());
    }

    @Test
    public void testSetToggledOn_withContentDescription() {
        final ToggleImageButton button = createButtonWithAttributes();
        assertFalse(button.isToggledOn());
        assertEquals(CONTENT_DESCRIPTION_OFF, button.getContentDescription());
        button.setToggledOn(true);
        assertTrue(button.isToggledOn());
        assertEquals(CONTENT_DESCRIPTION_ON, button.getContentDescription());
    }
}
