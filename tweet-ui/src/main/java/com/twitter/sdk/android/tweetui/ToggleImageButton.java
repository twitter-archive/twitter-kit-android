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

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Display on/off states (ie: Favorite or Retweet action buttons) as an {@link ImageButton}.
 *
 * The content description for the on and off states is defined by using the
 * {@code twitter:contentDescriptionOn} and {@code twitter:contentDescriptionOff} attributes.
 *
 * By default the button will be toggled when clicked. This behaviour can be prevented by setting
 * the {@code twitter:toggleOnClick} attribute to false.
 *
 * @attr ref android.R.styleable#ToggleImageButton_on
 * @attr ref android.R.styleable#ToggleImageButton_contentDescriptionOn
 * @attr ref android.R.styleable#ToggleImageButton_contentDescriptionff
 * @attr ref android.R.styleable#ToggleImageButton_toggleOnClick
 */
public class ToggleImageButton extends ImageButton {
    private static final int[] STATE_TOGGLED_ON = {R.attr.state_toggled_on};

    boolean isToggledOn;
    String contentDescriptionOn;
    String contentDescriptionOff;
    final boolean toggleOnClick;

    public ToggleImageButton(Context context) {
        this(context, null);
    }

    public ToggleImageButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = null;
        try {
            a = context.getTheme().obtainStyledAttributes(attrs,
                    R.styleable.ToggleImageButton, defStyle, 0);
            final String contentDescriptionOn =
                    a.getString(R.styleable.ToggleImageButton_contentDescriptionOn);
            final String contentDescriptionOff =
                    a.getString(R.styleable.ToggleImageButton_contentDescriptionOff);

            this.contentDescriptionOn = contentDescriptionOn == null ?
                    (String) getContentDescription() : contentDescriptionOn;
            this.contentDescriptionOff = contentDescriptionOff == null ?
                    (String) getContentDescription() : contentDescriptionOff;

            toggleOnClick = a.getBoolean(R.styleable.ToggleImageButton_toggleOnClick, true);

            setToggledOn(false);
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 2);
        if (isToggledOn) {
            mergeDrawableStates(drawableState, STATE_TOGGLED_ON);
        }
        return drawableState;
    }

    @Override
    public boolean performClick() {
        if (toggleOnClick) {
            toggle();
        }
        return super.performClick();
    }

    public void setToggledOn(boolean isToggledOn) {
        this.isToggledOn = isToggledOn;
        setContentDescription(isToggledOn ? contentDescriptionOn : contentDescriptionOff);
        refreshDrawableState();
    }

    public void toggle() {
        setToggledOn(!isToggledOn);
    }

    public boolean isToggledOn() {
        return isToggledOn;
    }
}
