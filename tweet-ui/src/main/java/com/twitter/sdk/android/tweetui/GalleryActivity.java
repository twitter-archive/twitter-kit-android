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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.tweetui.internal.SwipeToDismissTouchListener;

import java.util.Collections;
import java.util.List;

import java.io.Serializable;

public class GalleryActivity extends Activity {
    public static final String GALLERY_ITEM = "GALLERY_ITEM";
    static final String MEDIA_ENTITY = "MEDIA_ENTITY";
    static final String TWEET_ID = "TWEET_ID";
    GalleryItem galleryItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw__gallery_activity);

        galleryItem = getGalleryItem();

        final ViewPager viewPager = (ViewPager) findViewById(R.id.tw__view_pager);
        final int marginPixels =
                getResources().getDimensionPixelSize(R.dimen.tw__gallery_page_margin);
        viewPager.setPageMargin(marginPixels);

        final GalleryAdapter adapter =
                new GalleryAdapter(this, new SwipeToDismissTouchListener.Callback() {
            @Override
            public void onDismiss() {
                finish();
                overridePendingTransition(0, R.anim.tw__slide_out);
            }

            @Override
            public void onMove(float translationY) {

            }
        });

        adapter.addAll(galleryItem.mediaEntities);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(galleryItem.mediaEntityIndex);
    }

    // For backwards compatibility we need to support single entity or list of entities.
    GalleryItem getGalleryItem() {
        final MediaEntity entity = (MediaEntity) getIntent().getSerializableExtra(MEDIA_ENTITY);
        if (entity != null) {
            return new GalleryItem(0, Collections.singletonList(entity));
        }

        return (GalleryItem) getIntent().getSerializableExtra(GALLERY_ITEM);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.tw__slide_out);
    }

    public static class GalleryItem implements Serializable {
        public long tweetId;
        public int mediaEntityIndex;
        public List<MediaEntity> mediaEntities;

        public GalleryItem(int mediaEntityIndex, List<MediaEntity> mediaEntities) {
            this.mediaEntityIndex = mediaEntityIndex;
            this.mediaEntities = mediaEntities;
        }

        public GalleryItem(long tweetId, int mediaEntityIndex, List<MediaEntity> mediaEntities) {
            this.tweetId = tweetId;
            this.mediaEntityIndex = mediaEntityIndex;
            this.mediaEntities = mediaEntities;
        }
    }
}
