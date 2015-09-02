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

package com.example.app.tweetcomposer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.app.BaseActivity;
import com.example.app.twittercore.TwitterCoreMainActivity;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import com.example.app.R;

import java.net.MalformedURLException;
import java.net.URL;

public class TweetComposerMainActivity extends BaseActivity {
    private static final String TAG = "TweetComposer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tweetcomposer_activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.kit_tweetcomposer);
        }

        final Button tweetComposer = (Button) findViewById(R.id.tweet_composer);
        tweetComposer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    new TweetComposer.Builder(TweetComposerMainActivity.this)
                            .text("Tweet from Fabric!")
                            .url(new URL("http://www.twitter.com"))
                            .show();

                } catch (MalformedURLException e) {
                    Log.e(TAG, "error creating tweet intent", e);
                }
            }
        });

        final Button organicComposer = (Button) findViewById(R.id.organic_composer);
        organicComposer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                        .getActiveSession();
                final Intent intent;
                if (session == null) {
                    // session required to compose a Tweet
                    intent = TwitterCoreMainActivity.newIntent(TweetComposerMainActivity.this);
                } else {
                    intent = new ComposerActivity.Builder(TweetComposerMainActivity.this)
                            .session(session)
                            .tweetText("Hello World!")
                            .createIntent();
                }
                startActivity(intent);
            }
        });
    }
}
