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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.twitter.sdk.android.core.internal.CommonUtils;
import com.twitter.sdk.android.core.models.SafeListAdapter;
import com.twitter.sdk.android.core.models.SafeMapAdapter;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.FixedTweetTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Example code showing how to load Tweets from JSON.
 */
public class TweetPojoActivity extends TweetUiActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.tweet_pojo);
        }
    }

    @Override
    int getLayout() {
        return R.layout.activity_frame;
    }

    @Override
    Fragment createFragment() {
        return TweetPojoFragment.newInstance();
    }

    public static class TweetPojoFragment extends ListFragment {

        public static TweetPojoFragment newInstance() {
            return new TweetPojoFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Note: Load should normally be called from background thread.
            final List<Tweet> tweets = loadTweets();
            final FixedTweetTimeline fixedTimeline = new FixedTweetTimeline.Builder()
                    .setTweets(tweets).build();
            final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter(getActivity(),
                    fixedTimeline);
            setListAdapter(adapter);
        }

        List<Tweet> loadTweets() {
            final Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(new SafeListAdapter())
                    .registerTypeAdapterFactory(new SafeMapAdapter())
                    .create();

            InputStreamReader reader = null;
            try {
                reader = new InputStreamReader(getResources().openRawResource(R.raw.tweets));
                return gson.fromJson(reader, new TypeToken<ArrayList<Tweet>>() {}.getType());
            }finally {
                CommonUtils.closeQuietly(reader);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.tweetui_timeline, container, false);
        }
    }
}
