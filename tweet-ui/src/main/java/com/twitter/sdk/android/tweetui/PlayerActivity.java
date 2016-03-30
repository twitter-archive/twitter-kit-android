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
import android.widget.ProgressBar;

import com.twitter.sdk.android.tweetui.internal.VideoControlView;
import com.twitter.sdk.android.tweetui.internal.VideoView;
import com.twitter.sdk.android.core.models.MediaEntity;

public class PlayerActivity extends Activity {
    static final String MEDIA_ENTITY = "MEDIA_ENTITY";
    static final String TWEET_ID = "TWEET_ID";

    PlayerController playerController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw__player_activity);

        final ProgressBar videoProgressView = (ProgressBar) findViewById(R.id.video_progress_view);
        final VideoView videoView = (VideoView) findViewById(R.id.video_view);
        final VideoControlView videoControlView =
                (VideoControlView) findViewById(R.id.video_control_view);

        final long tweetId = getIntent().getLongExtra(TWEET_ID, 0);
        final MediaEntity entity = (MediaEntity) getIntent().getSerializableExtra(MEDIA_ENTITY);

        final VideoScribeClient scribeClient = new VideoScribeClientImpl(TweetUi.getInstance());
        scribeClient.play(tweetId, entity);

        playerController = new PlayerController(videoView, videoControlView, videoProgressView);
        playerController.prepare(entity);
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
}
