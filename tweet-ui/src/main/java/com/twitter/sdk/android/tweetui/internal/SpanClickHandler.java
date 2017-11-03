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

import android.text.Layout;
import android.text.Spanned;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * A helper class that enables support for clicks on spans in a the text of a
 * {@link android.widget.TextView} or a {@link android.text.Layout}. The text in the layout must
 * be of type Spanned, and the spans need to implement {@link HighlightedClickableSpan}.
 */
public class SpanClickHandler {
    private final View view;
    private Layout layout;
    private float left;
    private float top;

    private HighlightedClickableSpan highlightedClickableSpan;

    public static void enableClicksOnSpans(TextView textView) {
        final SpanClickHandler helper = new SpanClickHandler(textView, null);
        textView.setOnTouchListener((view, event) -> {
            final TextView textView1 = (TextView) view;
            final Layout layout = textView1.getLayout();
            if (layout != null) {
                helper.layout = layout;
                helper.left = textView1.getTotalPaddingLeft() + textView1.getScrollX();
                helper.top = textView1.getTotalPaddingTop() + textView1.getScrollY();
                return helper.handleTouchEvent(event);
            }
            return false;
        });
    }

    /**
     * Creates an instance of this helper for a layout and its containing view.
     */
    public SpanClickHandler(View view, Layout layout) {
        this.view = view;
        this.layout = layout;
    }

    /**
     * Takes a motion event from the processing view and check for clicks on
     * any of the clickable spans in the layout.
     * @param event The motion event.
     * @return true if the event has been handled.
     */
    public boolean handleTouchEvent(MotionEvent event) {
        final CharSequence text = layout.getText();
        final Spanned spannedText = text instanceof Spanned ? (Spanned) text : null;
        if (spannedText == null) {
            return false;
        }

        final int action = (event.getAction() & MotionEvent.ACTION_MASK);
        final int x = (int) (event.getX() - left);
        final int y = (int) (event.getY() - top);

        if (x < 0 || x >= layout.getWidth() || y < 0 || y >= layout.getHeight()) {
            deselectSpan();
            return false;
        }

        // Get the clicked line and check x is within the text on this line.
        final int line = layout.getLineForVertical(y);
        if (x < layout.getLineLeft(line) || x > layout.getLineRight(line)) {
            deselectSpan();
            return false;
        }

        if (action == MotionEvent.ACTION_DOWN) {
            final int offset = layout.getOffsetForHorizontal(line, x);
            final HighlightedClickableSpan[] span = spannedText.getSpans(offset, offset,
                    HighlightedClickableSpan.class);
            if (span.length > 0) {
                selectSpan(span[0]);
                return true;
            }
        } else if (action == MotionEvent.ACTION_UP) {
            final HighlightedClickableSpan selectedSpan = highlightedClickableSpan;
            if (selectedSpan != null) {
                selectedSpan.onClick(view);
                deselectSpan();
                return true;
            }
        }
        return false;
    }

    /**
     * Selects the given span.
     */
    private void selectSpan(HighlightedClickableSpan span) {
        span.select(true);
        highlightedClickableSpan = span;
        invalidate();
    }

    /**
     * Deselects the currently selected link, if there is one.
     */
    private void deselectSpan() {
        final HighlightedClickableSpan selectedSpan = highlightedClickableSpan;
        if (selectedSpan != null && selectedSpan.isSelected()) {
            selectedSpan.select(false);
            highlightedClickableSpan = null;
            invalidate();
        }
    }

    private void invalidate() {
        view.invalidate((int) left, (int) top, (int) left + layout.getWidth(),
                (int) top + layout.getHeight());
    }
}
