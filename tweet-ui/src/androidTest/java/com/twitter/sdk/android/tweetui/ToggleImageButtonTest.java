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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import io.fabric.sdk.android.FabricAndroidTestCase;

public class ToggleImageButtonTest extends FabricAndroidTestCase {
    private static final String CONTENT_DESCRIPTION_ON = "ContentDescriptionOn";
    private static final String CONTENT_DESCRIPTION_OFF = "ContentDescriptionOff";

    ToggleImageButton createDefaultButton() {
        return new ToggleImageButton(getContext());
    }

    ToggleImageButton createButtonFromXml() {
        return (ToggleImageButton) getInflatedLayout().findViewById(R.id.tw_toggle_image_button);
    }

    private View getInflatedLayout() {
        final ViewGroup group = new LinearLayout(getContext());
        return LayoutInflater.from(getContext()).inflate(
                R.layout.activity_toggle_image_button, group, true);
    }

    public void testInit() {
        final ToggleImageButton button = createDefaultButton();
        assertNull(button.contentDescriptionOn);
        assertNull(button.contentDescriptionOff);
        assertFalse(button.isToggledOn());
        assertTrue(button.toggleOnClick);
    }

    public void testPerformClick() {
        final ToggleImageButton button = createDefaultButton();
        assertTrue(button.toggleOnClick);
        assertFalse(button.isToggledOn());
        button.performClick();
        assertTrue(button.isToggledOn());
    }

    public void testSetToggledOn() {
        final ToggleImageButton button = createDefaultButton();
        assertFalse(button.isToggledOn());
        button.setToggledOn(true);
        assertTrue(button.isToggledOn());
    }

    public void testToggle() {
        final ToggleImageButton button = createDefaultButton();
        assertFalse(button.isToggledOn());
        button.toggle();
        assertTrue(button.isToggledOn());
    }

    public void testXmlInit() {
        final ToggleImageButton button = createButtonFromXml();
        assertEquals(CONTENT_DESCRIPTION_ON, button.contentDescriptionOn);
        assertEquals(CONTENT_DESCRIPTION_OFF, button.contentDescriptionOff);
        assertFalse(button.isToggledOn());
        assertEquals(CONTENT_DESCRIPTION_OFF, button.getContentDescription());
        assertFalse(button.toggleOnClick);
    }

    public void testPerformClick_toggleOnClickDisabled() {
        final ToggleImageButton button = createButtonFromXml();
        assertFalse(button.toggleOnClick);
        assertFalse(button.isToggledOn());
        button.performClick();
        assertFalse(button.isToggledOn());
    }

    public void testSetToggledOn_withContentDescription() {
        final ToggleImageButton button = createButtonFromXml();
        assertFalse(button.isToggledOn());
        assertEquals(CONTENT_DESCRIPTION_OFF, button.getContentDescription());
        button.setToggledOn(true);
        assertTrue(button.isToggledOn());
        assertEquals(CONTENT_DESCRIPTION_ON, button.getContentDescription());
    }
}
