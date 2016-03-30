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
import android.view.View;
import android.widget.ProgressBar;

import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.tweetui.internal.TweetMediaUtils;
import com.twitter.sdk.android.tweetui.internal.VideoControlView;
import com.twitter.sdk.android.tweetui.internal.VideoView;

import io.fabric.sdk.android.Fabric;

class PlayerController {
    private static final String TAG = "PlayerController";
    final VideoView videoView;
    final VideoControlView videoControlView;
    final ProgressBar videoProgressView;

    int seekPosition = 0;
    boolean isPlaying = true;

    PlayerController(VideoView videoView, VideoControlView videoControlView,
            ProgressBar videoProgressView) {
        this.videoView = videoView;
        this.videoControlView = videoControlView;
        this.videoProgressView = videoProgressView;
    }

    void prepare(MediaEntity entity) {
        try {
            final boolean looping = TweetMediaUtils.isLooping(entity);
            final String url = TweetMediaUtils.getSupportedVariant(entity).url;
            final Uri uri = Uri.parse(url);

            setUpMediaControl(looping);
            videoView.setVideoURI(uri, looping);
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    videoProgressView.setVisibility(View.GONE);
                }
            });
            videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                    if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                        videoProgressView.setVisibility(View.GONE);
                        return true;
                    } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                        videoProgressView.setVisibility(View.VISIBLE);
                        return true;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            Fabric.getLogger().e(TAG, "Error occurred during video playback", e);
        }
    }

    void onResume() {
        if (seekPosition != 0) {
            videoView.seekTo(seekPosition);
        }
        if (isPlaying) {
            videoView.start();
            videoControlView.update();
        }
    }

    void onPause() {
        isPlaying = videoView.isPlaying();
        seekPosition = videoView.getCurrentPosition();
        videoView.pause();
    }

    void onDestroy() {
        videoView.stopPlayback();
    }

    void setUpMediaControl(boolean looping) {
        if (looping) {
            setUpLoopControl();
        } else {
            setUpMediaControl();
        }
    }

    void setUpLoopControl() {
        videoControlView.setVisibility(View.INVISIBLE);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                } else {
                    videoView.start();
                }
            }
        });
    }

    void setUpMediaControl() {
        videoView.setMediaController(videoControlView);
    }
}
