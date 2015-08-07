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

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.app.BaseActivity;
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
    }

    public void onTweet(View view) {
        try {
            new TweetComposer.Builder(this)
                    .text("Tweet from Fabric!")
                    .url(new URL("http://www.twitter.com"))
                    .show();

        } catch (MalformedURLException e) {
            Log.e(TAG, "error creating tweet intent", e);
        }
    }
}
