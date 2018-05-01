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

package com.example.app.tweetui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.Button;

import com.example.app.BaseActivity;
import com.example.app.R;

public class TweetUiMainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tweetui_activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.kit_tweetui);
        }

        final Button xmlTweetButton = findViewById(R.id.button_xml_tweet_activity);
        xmlTweetButton.setOnClickListener(v -> startActivity(new Intent(TweetUiMainActivity.this, XmlTweetActivity.class)));

        final Button tweetActivityButton = findViewById(R.id.button_tweet_activity);
        tweetActivityButton.setOnClickListener(v -> startActivity(new Intent(TweetUiMainActivity.this, TweetActivity.class)));

        final Button unqiueTweetButton = findViewById(R.id.button_unique_tweet_activity);
        unqiueTweetButton.setOnClickListener(v -> startActivity(new Intent(TweetUiMainActivity.this, UniqueTweetActivity.class)));

        final Button tweetListButton = findViewById(R.id.button_fixed_timeline_activity);
        tweetListButton.setOnClickListener(v -> startActivity(new Intent(TweetUiMainActivity.this, FixedTimelineActivity.class)));

        final Button timelineButton = findViewById(R.id.button_refresh_timeline_activity);
        timelineButton.setOnClickListener(v -> startActivity(new Intent(TweetUiMainActivity.this, TimelineActivity.class)));

        final Button timelinesButton = findViewById(R.id.button_timelines_activity);
        timelinesButton.setOnClickListener(view -> startActivity(new Intent(TweetUiMainActivity.this, TimelinesActivity.class)));

        final Button tweetSelectorButton = findViewById(
                R.id.button_tweet_preview_activity);
        tweetSelectorButton.setOnClickListener(v -> startActivity(new Intent(TweetUiMainActivity.this, TweetPreviewActivity.class)));

        final Button tweetPojoButton = findViewById(
                R.id.button_tweet_pojo_activity);
        tweetPojoButton.setOnClickListener(v -> startActivity(new Intent(TweetUiMainActivity.this, TweetPojoActivity.class)));

        final Button rtlTimelineButton = findViewById(R.id.button_rtl_timeline_activity);
        rtlTimelineButton.setOnClickListener(v -> startActivity(new Intent(TweetUiMainActivity.this, RtlTimelineActivity.class)));
    }
}
