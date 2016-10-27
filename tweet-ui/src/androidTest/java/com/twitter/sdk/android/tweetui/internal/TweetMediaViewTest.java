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
import android.widget.ImageView;

import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.tweetui.R;
import com.twitter.sdk.android.tweetui.TestFixtures;
import com.twitter.sdk.android.tweetui.TweetUiTestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TweetMediaViewTest extends TweetUiTestCase {

    TweetMediaView tweetMediaView;
    CharSequence contentDescription;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        tweetMediaView = new TweetMediaView(getContext());
        contentDescription = getContext().getResources().getString(R.string.tw__tweet_media);
    }

    public void testInitialViewState() {
        for (int index = 0; index < TweetMediaView.MAX_IMAGE_VIEW_COUNT; index++) {
            final ImageView imageView = (ImageView) tweetMediaView.getChildAt(index);
            assertNull(imageView);
        }
    }

    public void testCreateImageView() {
        final ImageView imageView = tweetMediaView.createImageView(2);

        assertNotNull(imageView);
        assertEquals(2, imageView.getTag(R.id.tw__entity_index));
    }

    public void testSetMediaEntities_withEmptyList() {
        final List<MediaEntity> emptyMediaEntities = Collections.EMPTY_LIST;
        tweetMediaView.setTweetMediaEntities(TestFixtures.TEST_TWEET, emptyMediaEntities);

        for (int index = 0; index < TweetMediaView.MAX_IMAGE_VIEW_COUNT; index++) {
            final ImageView imageView = (ImageView) tweetMediaView.getChildAt(index);
            assertNull(imageView);
        }
    }

    public void testSetMediaEntities_withSingleEntity() {
        final MediaEntity entity = TestFixtures.createMediaEntityWithPhoto(100, 100);
        final List<MediaEntity> mediaEntities = new ArrayList<>();
        mediaEntities.add(entity);
        tweetMediaView.setTweetMediaEntities(TestFixtures.TEST_TWEET, mediaEntities);

        final ImageView imageView = (ImageView) tweetMediaView.getChildAt(0);
        assertEquals(View.VISIBLE, imageView.getVisibility());
        assertNull(tweetMediaView.getChildAt(1));
        assertNull(tweetMediaView.getChildAt(2));
        assertNull(tweetMediaView.getChildAt(3));
    }

    public void testSetMediaEntities_withMultipleEntities() {
        final List<MediaEntity> mediaEntities = TestFixtures.createMultipleMediaEntitiesWithPhoto
                (TweetMediaView.MAX_IMAGE_VIEW_COUNT, 100, 100);
        tweetMediaView.setTweetMediaEntities(TestFixtures.TEST_TWEET, mediaEntities);

        for (int index = 0; index < TweetMediaView.MAX_IMAGE_VIEW_COUNT; index++) {
            final ImageView imageView = (ImageView) tweetMediaView.getChildAt(index);
            assertEquals(View.VISIBLE, imageView.getVisibility());
            assertEquals(index, imageView.getTag(R.id.tw__entity_index));
            assertEquals(contentDescription, imageView.getContentDescription());
        }
    }

    public void testClearImageViews() {
        final List<MediaEntity> mediaEntities = TestFixtures.createMultipleMediaEntitiesWithPhoto
                (TweetMediaView.MAX_IMAGE_VIEW_COUNT, 100, 100);
        tweetMediaView.setTweetMediaEntities(TestFixtures.TEST_TWEET, mediaEntities);
        tweetMediaView.clearImageViews();
        for (int index = 0; index < TweetMediaView.MAX_IMAGE_VIEW_COUNT; index++) {
            final ImageView imageView = (ImageView) tweetMediaView.getChildAt(index);
            assertEquals(View.GONE, imageView.getVisibility());
        }
    }
}
