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

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;

/**
 * A span that can be clicked. Use with {@link SpanClickHandler}.
 */
public abstract class ClickableLinkSpan extends ClickableSpan implements HighlightedClickableSpan {

    public final int linkColor;
    private final int selectedColor;
    private final boolean colored;
    private final boolean underlined;
    private boolean selected;

    public ClickableLinkSpan(int selectedColor, int linkColor, boolean underlined) {
        this(selectedColor, linkColor, true, underlined);
    }

    ClickableLinkSpan(int selectedColor, int linkColor, boolean colored, boolean underlined) {
        this.selectedColor = selectedColor;
        this.linkColor = linkColor;
        this.colored = colored;
        this.underlined = underlined;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        if (colored) {
            ds.setColor(linkColor);
        } else {
            ds.setColor(ds.linkColor);
        }

        if (selected) {
            ds.bgColor = selectedColor;
        } else {
            ds.bgColor = Color.TRANSPARENT;
        }

        if (underlined) {
            ds.setUnderlineText(true);
        }
    }

    @Override
    public void select(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }
}
