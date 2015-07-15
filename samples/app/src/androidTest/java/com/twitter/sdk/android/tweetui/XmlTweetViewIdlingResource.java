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

/**
 * XmlTweetViewIdlingResource represents an xml defined Tweet view which is asynchronously set
 * with a Tweet. When the Tweet and a field within the Tweet become non-null, the asynchronous
 * set is considered to have completed. The resource is then idle and ready ot use in tests.
 */
public class XmlTweetViewIdlingResource implements IdlingResource {

    private BaseTweetView view;
    private String name;
    private ResourceCallback callback;

    public XmlTweetViewIdlingResource(BaseTweetView view, String name) {
        this.view = view;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isIdleNow() {
        // TODO: BaseTweetView should provide an indication that rendering has completed to use here
        // XML TweetViews set a Tweet with only an id, before a Tweet is loaded from the network,
        // checking getTweet() is non-null is not sufficient.
        if (view.getTweet() != null && view.getTweet().text != null) {
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
