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

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.twitter.sdk.android.core.IntentUtils;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.tweetui.internal.SwipeToDismissTouchListener;
import com.twitter.sdk.android.tweetui.internal.VideoControlView;
import com.twitter.sdk.android.tweetui.internal.VideoView;

class PlayerController {
    private static final String TAG = "PlayerController";
    final VideoView videoView;
    final VideoControlView videoControlView;
    final ProgressBar videoProgressView;
    final TextView callToActionView;
    final View rootView;
    int seekPosition;
    boolean isPlaying = true;
    final SwipeToDismissTouchListener.Callback callback;

    PlayerController(View rootView, SwipeToDismissTouchListener.Callback callback) {
        this.rootView = rootView;
        this.videoView = rootView.findViewById(R.id.video_view);
        this.videoControlView = rootView.findViewById(R.id.video_control_view);
        this.videoProgressView = rootView.findViewById(R.id.video_progress_view);
        this.callToActionView = rootView.findViewById(R.id.call_to_action_view);
        this.callback = callback;
    }

    // Unit testing purposes
    PlayerController(View rootView, VideoView videoView, VideoControlView videoControlView,
            ProgressBar videoProgressView, TextView callToActionView,
            SwipeToDismissTouchListener.Callback callback) {
        this.rootView = rootView;
        this.videoView = videoView;
        this.videoControlView = videoControlView;
        this.videoProgressView = videoProgressView;
        this.callToActionView = callToActionView;
        this.callback = callback;
    }

    void prepare(PlayerActivity.PlayerItem item) {
        try {
            setUpCallToAction(item);
            setUpMediaControl(item.looping, item.showVideoControls);
            final View.OnTouchListener listener = SwipeToDismissTouchListener
                    .createFromView(videoView, callback);
            videoView.setOnTouchListener(listener);
            videoView.setOnPreparedListener(mediaPlayer -> videoProgressView.setVisibility(View.GONE));
            videoView.setOnInfoListener((mediaPlayer, what, extra) -> {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    videoProgressView.setVisibility(View.GONE);
                    return true;
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    videoProgressView.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            });
            final Uri uri = Uri.parse(item.url);
            videoView.setVideoURI(uri, item.looping);
            videoView.requestFocus();
        } catch (Exception e) {
            Twitter.getLogger().e(TAG, "Error occurred during video playback", e);
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

    void setUpMediaControl(boolean looping, boolean showVideoControls) {
        if (looping && !showVideoControls) {
            setUpLoopControl();
        } else {
            setUpMediaControl();
        }
    }

    void setUpLoopControl() {
        videoControlView.setVisibility(View.INVISIBLE);
        videoView.setOnClickListener(view -> {
            if (videoView.isPlaying()) {
                videoView.pause();
            } else {
                videoView.start();
            }
        });
    }

    void setUpMediaControl() {
        videoView.setMediaController(videoControlView);
    }

    void setUpCallToAction(PlayerActivity.PlayerItem item) {
        if (item.callToActionText != null && item.callToActionUrl != null) {
            callToActionView.setVisibility(View.VISIBLE);
            callToActionView.setText(item.callToActionText);
            setUpCallToActionListener(item.callToActionUrl);
            setUpRootViewOnClickListener();
        }
    }

    void setUpCallToActionListener(final String callToActionUrl) {
        callToActionView.setOnClickListener(v -> {
            final Uri uri = Uri.parse(callToActionUrl);
            final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            IntentUtils.safeStartActivity(callToActionView.getContext(), intent);
        });
    }

    void setUpRootViewOnClickListener() {
        rootView.setOnClickListener(v -> {
            if (callToActionView.getVisibility() == View.VISIBLE) {
                callToActionView.setVisibility(View.GONE);
            } else {
                callToActionView.setVisibility(View.VISIBLE);
            }
        });
    }


}
