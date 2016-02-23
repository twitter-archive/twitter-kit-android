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

import android.view.View;

/**
 * An interface for spans that can be clicked and highlighted on selection. In order to respond to actions
 * on spans that implement this interface, use {@link SpanClickHandler}.
 */
public interface HighlightedClickableSpan {
    /**
     * Triggered if the span is clicked.
     */
    void onClick(View view);

    /**
     * Called when the span is clicked and released.
     */
    void select(boolean selected);

    /**
     * Returns the selection status of the span.
     */
    boolean isSelected();
}
