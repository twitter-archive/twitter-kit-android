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

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.tweetui.R;

public class MediaBadgeView extends FrameLayout {
    TextView videoDuration;
    ImageView gifBadge;

    public MediaBadgeView(Context context) {
        this(context, null);
    }

    public MediaBadgeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaBadgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initSubViews(context);
    }

    void initSubViews(Context context) {
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.tw__media_badge, this, true);

        videoDuration = (TextView) view.findViewById(R.id.tw__video_duration);
        gifBadge = (ImageView) view.findViewById(R.id.tw__gif_badge);
    }

    public void setMediaEntity(MediaEntity entity) {
        if (TweetMediaUtils.GIF_TYPE.equals(entity.type)) {
            gifBadge.setVisibility(View.VISIBLE);
            videoDuration.setVisibility(View.GONE);
        } else if (TweetMediaUtils.VIDEO_TYPE.equals(entity.type)) {
            videoDuration.setVisibility(View.VISIBLE);
            gifBadge.setVisibility(View.GONE);

            final long duration = entity.videoInfo == null ? 0 : entity.videoInfo.durationMillis;
            videoDuration.setText(MediaTimeUtils.getPlaybackTime(duration));
        } else {
            videoDuration.setVisibility(View.GONE);
            gifBadge.setVisibility(View.GONE);
        }
    }
}
