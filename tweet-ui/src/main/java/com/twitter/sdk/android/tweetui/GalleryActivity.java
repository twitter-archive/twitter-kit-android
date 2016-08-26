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
import android.view.View;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.tweetui.internal.MultiTouchImageView;
import com.twitter.sdk.android.tweetui.internal.SwipeToDismissTouchListener;

public class GalleryActivity extends Activity {
    static final String MEDIA_ENTITY = "MEDIA_ENTITY";
    static final String TWEET_ID = "TWEET_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw__gallery_activity);

        final MediaEntity entity = (MediaEntity) getIntent().getSerializableExtra(MEDIA_ENTITY);
        final MultiTouchImageView imageView = (MultiTouchImageView) findViewById(R.id.image_view);

        final View.OnTouchListener touchListener =
                SwipeToDismissTouchListener.createFromView(imageView,
                        new SwipeToDismissTouchListener.Callback() {
            @Override
            public void onDismiss() {
                finish();
                overridePendingTransition(0, R.anim.tw__slide_out);
            }

            @Override
            public void onMove(float translationY) {

            }
        });
        imageView.setOnTouchListener(touchListener);

        Picasso.with(this).load(entity.mediaUrlHttps).into(imageView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.tw__slide_out);
    }
}
