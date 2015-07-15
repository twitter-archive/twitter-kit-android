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

import android.support.test.espresso.IdlingResource;
import android.view.View;

/**
 * AddViewIdlingResource represents a view that is added after an asynchronous operation.
 * When the view is found in the layout, the resource is then idle and ready to use.
 */
public class AddViewIdlingResource implements IdlingResource {

    private int viewId;
    private View parentView;
    private ResourceCallback callback;

    public AddViewIdlingResource(int viewId, View parentView) {
        this.viewId = viewId;
        this.parentView = parentView;
    }

    @Override
    public String getName() {
        return Integer.toString(viewId);
    }

    @Override
    public boolean isIdleNow() {
        final View view = parentView.findViewById(viewId);
        if (view != null && view.getId() == viewId) {
            callback.onTransitionToIdle();
            return true;
        }
        return false;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.callback = resourceCallback;
    }
}
