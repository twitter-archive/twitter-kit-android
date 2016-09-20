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

public class GalleryActivity extends Activity {
    static final String MEDIA_ENTITY = "MEDIA_ENTITY";
    static final String MEDIA_ENTITIES = "MEDIA_ENTITIES";
    static final String TWEET_ID = "TWEET_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw__gallery_activity);

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
        adapter.addAll(getEntities());
        viewPager.setAdapter(adapter);
    }

    // For backwards compatibility we need to support single entity or list of entities.
    List<MediaEntity> getEntities() {
        final MediaEntity entity = (MediaEntity) getIntent().getSerializableExtra(MEDIA_ENTITY);
        if (entity != null) {
            return Collections.singletonList(entity);
        }

        return (List<MediaEntity>) getIntent().getSerializableExtra(MEDIA_ENTITIES);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.tw__slide_out);
    }
}
