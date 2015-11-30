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

import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.MediaController;

import com.twitter.sdk.android.tweetui.internal.VideoView;

import io.fabric.sdk.android.Fabric;

class PlayerController {
    private static final String TAG = "PlayerController";
    final VideoView videoView;

    PlayerController(VideoView videoView) {
        this.videoView = videoView;
    }

    void prepare(Uri uri) {
        try {
            final MediaController mediaController = new MediaController(videoView.getContext());
            mediaController.setAnchorView(videoView);

            videoView.setMediaController(mediaController);
            videoView.setVideoURI(uri);

        } catch (Exception e) {
            Fabric.getLogger().e(TAG, "Error occurred during video playback", e);
        }

        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });
    }

    void cleanup() {
        videoView.stopPlayback();
    }
}
