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

public class TweetViewXmlTest extends BaseTweetViewXmlTest {
    @Override
    TweetView getView() {
        return (TweetView) getInflatedLayout().findViewById(R.id.tweet_view);
    }

    @Override
    TweetView getViewDark() {
        return (TweetView) getInflatedLayout().findViewById(R.id.tweet_view_dark);
    }

    // Layout

    public void testLayout() {
        final TweetView view = getView();
        assertNotNull(view);
        assertEquals(R.layout.tw__tweet, view.getLayout());
    }

    // Styling

    public void testActionColorDefault() {
        final TweetView view = getView();
        final int color = getResources().getColor(R.color.tw__tweet_action_color);
        assertEquals(color, view.shareButton.getCurrentTextColor());
    }

    public void testActionColorDark() {
        final TweetView view = getViewDark();
        final int color = getResources().getColor(R.color.tw__tweet_action_color);
        assertEquals(color, view.shareButton.getCurrentTextColor());
    }
}
