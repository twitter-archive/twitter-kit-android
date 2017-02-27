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

import com.twitter.sdk.android.core.internal.scribe.ScribeItem;
import com.twitter.sdk.android.tweetui.internal.SwipeToDismissTouchListener;

import java.io.Serializable;

public class PlayerActivity extends Activity {

    public static final String PLAYER_ITEM = "PLAYER_ITEM";
    public static final String SCRIBE_ITEM = "SCRIBE_ITEM";

    static final VideoScribeClient videoScribeClient =
            new VideoScribeClientImpl(TweetUi.getInstance());

    PlayerController playerController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw__player_activity);

        final PlayerItem item = (PlayerItem) getIntent().getSerializableExtra(PLAYER_ITEM);
        final View rootView = findViewById(android.R.id.content);
        playerController = new PlayerController(rootView,
                new SwipeToDismissTouchListener.Callback(){

            @Override
            public void onDismiss() {
                PlayerActivity.this.finish();
                overridePendingTransition(0, R.anim.tw__slide_out);
            }

            @Override
            public void onMove(float translationY) {

            }
        });
        playerController.prepare(item);

        final ScribeItem scribeItem = (ScribeItem) getIntent().getSerializableExtra(SCRIBE_ITEM);
        scribeCardPlayImpression(scribeItem);
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerController.onResume();
    }

    @Override
    protected void onPause() {
        playerController.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        playerController.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.tw__slide_out);
    }

    private void scribeCardPlayImpression(ScribeItem scribeItem) {
        videoScribeClient.play(scribeItem);
    }

    public static class PlayerItem implements Serializable {
        public final String url;
        public final boolean looping;
        public final boolean showVideoControls;
        public final String callToActionUrl;
        public final String callToActionText;

        public PlayerItem(String url, boolean looping, boolean showVideoControls,
                          String callToActionText, String callToActionUrl) {
            this.url = url;
            this.looping = looping;
            this.showVideoControls = showVideoControls;
            this.callToActionText = callToActionText;
            this.callToActionUrl = callToActionUrl;
        }
    }
}
