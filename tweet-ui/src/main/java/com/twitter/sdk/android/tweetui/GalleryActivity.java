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

import com.twitter.sdk.android.core.internal.scribe.ScribeItem;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.tweetui.internal.SwipeToDismissTouchListener;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class GalleryActivity extends Activity {
    public static final String GALLERY_ITEM = "GALLERY_ITEM";
    static final String MEDIA_ENTITY = "MEDIA_ENTITY";
    GalleryItem galleryItem;

    final GalleryScribeClient galleryScribeClient =
            new GalleryScribeClientImpl(TweetUi.getInstance());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw__gallery_activity);

        galleryItem = getGalleryItem();

        // Only scribe show event when view is first created
        if (savedInstanceState == null) {
            scribeShowEvent();
        }

        final GalleryAdapter adapter = new GalleryAdapter(this, getSwipeToDismissCallback());
        adapter.addAll(galleryItem.mediaEntities);

        final ViewPager viewPager = findViewById(R.id.tw__view_pager);
        final int marginPixels =
                getResources().getDimensionPixelSize(R.dimen.tw__gallery_page_margin);
        viewPager.setPageMargin(marginPixels);
        viewPager.addOnPageChangeListener(getOnPageChangeListener());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(galleryItem.mediaEntityIndex);
    }

    ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            int galleryPosition = -1;

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                // Initial on tap of entity at position 0, which is not invoked by onPageSelected()
                if (galleryPosition == -1 && position == 0 && positionOffset == 0.0) {
                    scribeImpressionEvent(position);
                    galleryPosition++;
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (galleryPosition >= 0) {
                    scribeNavigateEvent();
                }
                galleryPosition++;

                scribeImpressionEvent(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) { /* intentionally blank */ }
        };
    }

    SwipeToDismissTouchListener.Callback getSwipeToDismissCallback() {
       return new SwipeToDismissTouchListener.Callback() {
           @Override
           public void onDismiss() {
               scribeDismissEvent();
               finish();
               overridePendingTransition(0, R.anim.tw__slide_out);
           }

           @Override
           public void onMove(float translationY) { /* intentionally blank */ }
       };
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
        scribeDismissEvent();
        super.onBackPressed();
        overridePendingTransition(0, R.anim.tw__slide_out);
    }

    void scribeShowEvent() {
        galleryScribeClient.show();
    }

    void scribeDismissEvent() {
        galleryScribeClient.dismiss();
    }

    void scribeImpressionEvent(int mediaEntityPosition) {
        final MediaEntity mediaEntity = galleryItem.mediaEntities.get(mediaEntityPosition);
        final ScribeItem scribeItem = ScribeItem.fromMediaEntity(galleryItem.tweetId, mediaEntity);
        galleryScribeClient.impression(scribeItem);
    }

    void scribeNavigateEvent() {
        galleryScribeClient.navigate();
    }

    public static class GalleryItem implements Serializable {
        public final long tweetId;
        public final int mediaEntityIndex;
        public final List<MediaEntity> mediaEntities;

        public GalleryItem(int mediaEntityIndex, List<MediaEntity> mediaEntities) {
            this(0L, mediaEntityIndex, mediaEntities);
        }

        public GalleryItem(long tweetId, int mediaEntityIndex, List<MediaEntity> mediaEntities) {
            this.tweetId = tweetId;
            this.mediaEntityIndex = mediaEntityIndex;
            this.mediaEntities = mediaEntities;
        }
    }
}
