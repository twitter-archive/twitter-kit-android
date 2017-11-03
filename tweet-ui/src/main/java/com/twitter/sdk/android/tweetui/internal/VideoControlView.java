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

package com.twitter.sdk.android.tweetui.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.twitter.sdk.android.tweetui.R;

public class VideoControlView extends FrameLayout {
    static final long PROGRESS_BAR_TICKS = 1000L;
    static final int FADE_DURATION_MS = 150;
    private static final int SHOW_PROGRESS_MSG = 1001;

    MediaPlayerControl player;
    ImageButton stateControl;
    TextView currentTime;
    TextView duration;
    SeekBar seekBar;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SHOW_PROGRESS_MSG) {
                if (player == null) {
                    return;
                }
                updateProgress();
                updateStateControl();
                if (isShowing() && player.isPlaying()) {
                    msg = obtainMessage(SHOW_PROGRESS_MSG);
                    sendMessageDelayed(msg, 500);
                }
            }
        }
    };

    public VideoControlView(Context context) {
        super(context);
    }

    public VideoControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMediaPlayer(MediaPlayerControl player) {
        this.player = player;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initSubviews();
    }

    void initSubviews() {
        final LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.tw__video_control, this);

        stateControl = findViewById(R.id.tw__state_control);
        currentTime = findViewById(R.id.tw__current_time);
        duration = findViewById(R.id.tw__duration);
        seekBar = findViewById(R.id.tw__progress);

        seekBar.setMax((int) PROGRESS_BAR_TICKS);
        seekBar.setOnSeekBarChangeListener(createProgressChangeListener());
        stateControl.setOnClickListener(createStateControlClickListener());

        setDuration(0);
        setCurrentTime(0);
        setProgress(0, 0, 0);
    }

    OnClickListener createStateControlClickListener() {
        return view -> {
            if (player.isPlaying()) {
                player.pause();
            } else {
                player.start();
            }
            show();
        };
    }

    SeekBar.OnSeekBarChangeListener createProgressChangeListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }

                final int duration = player.getDuration();
                final long position = (duration * progress) / PROGRESS_BAR_TICKS;
                player.seekTo((int) position);
                setCurrentTime((int) position);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(SHOW_PROGRESS_MSG);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessage(SHOW_PROGRESS_MSG);
            }
        };
    }

    void updateProgress() {
        final int duration = player.getDuration();
        final int currentTime = player.getCurrentPosition();
        final int bufferPercentage = player.getBufferPercentage();
        setDuration(duration);
        setCurrentTime(currentTime);
        setProgress(currentTime, duration, bufferPercentage);
    }

    void setDuration(int durationMillis) {
        duration.setText(MediaTimeUtils.getPlaybackTime(durationMillis));
    }

    void setCurrentTime(int currentTimeMillis) {
        currentTime.setText(MediaTimeUtils.getPlaybackTime(currentTimeMillis));
    }

    void setProgress(int currentTimeMillis, int durationMillis, int bufferPercentage) {
        final long pos = durationMillis > 0 ?
                PROGRESS_BAR_TICKS * currentTimeMillis / durationMillis : 0;
        seekBar.setProgress((int) pos);
        seekBar.setSecondaryProgress(bufferPercentage * 10);
    }

    void updateStateControl() {
        if (player.isPlaying()) {
            setPauseDrawable();
        } else if (player.getCurrentPosition() > Math.max(player.getDuration() - 500, 0)) {
            setReplayDrawable();
        } else {
            setPlayDrawable();
        }
    }

    void setPlayDrawable() {
        stateControl.setImageResource(R.drawable.tw__video_play_btn);
        stateControl.setContentDescription(getContext().getString(R.string.tw__play));
    }

    void setPauseDrawable() {
        stateControl.setImageResource(R.drawable.tw__video_pause_btn);
        stateControl.setContentDescription(getContext().getString(R.string.tw__pause));
    }

    void setReplayDrawable() {
        stateControl.setImageResource(R.drawable.tw__video_replay_btn);
        stateControl.setContentDescription(getContext().getString(R.string.tw__replay));
    }

    void hide() {
        handler.removeMessages(SHOW_PROGRESS_MSG);
        AnimationUtils.fadeOut(this, FADE_DURATION_MS);
    }

    void show() {
        handler.sendEmptyMessage(SHOW_PROGRESS_MSG);
        AnimationUtils.fadeIn(this, FADE_DURATION_MS);
    }

    public boolean isShowing() {
        return getVisibility() == View.VISIBLE;
    }

    public void update() {
        handler.sendEmptyMessage(SHOW_PROGRESS_MSG);
    }

    public interface MediaPlayerControl {
        void start();

        void pause();

        int getDuration();

        int getCurrentPosition();

        void seekTo(int position);

        boolean isPlaying();

        int getBufferPercentage();
    }
}
