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
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class PlayerActivity extends Activity {
    static final String VIDEO_URL = "VIDEO_URL";
    PlayerController playerController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw__player_activity);

        final VideoView videoView = (VideoView) findViewById(R.id.video_view);
        final String url = getIntent().getStringExtra(VIDEO_URL);
        final Uri uri = Uri.parse(url);

        playerController = new PlayerController(videoView);
        playerController.prepare(uri);
    }

    @Override
    public void onDestroy() {
        playerController.cleanup();
        super.onDestroy();
    }
}
