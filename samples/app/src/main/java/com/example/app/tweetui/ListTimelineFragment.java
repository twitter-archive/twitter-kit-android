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
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.R;
import com.example.app.twittercore.TwitterCoreMainActivity;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.BasicTimelineFilter;
import com.twitter.sdk.android.tweetui.FilterValues;
import com.twitter.sdk.android.tweetui.TimelineFilter;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TwitterListTimeline;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * ListTimelineFragment demonstrates a TimelineListAdapter with a TwitterListTimeline.
 */
public class ListTimelineFragment extends ListFragment {

    public static ListTimelineFragment newInstance() {
        return new ListTimelineFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // launch the app login activity when a guest user tries to favorite a Tweet
        final Callback<Tweet> actionCallback = new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                // Intentionally blank
            }
            @Override
            public void failure(TwitterException exception) {
                if (exception instanceof TwitterAuthException) {
                    startActivity(TwitterCoreMainActivity.newIntent(getActivity()));
                }
            }
        };

        final TwitterListTimeline timeline = new TwitterListTimeline.Builder()
                .slugWithOwnerScreenName("twitter-bots", "dghubble")
                .build();

        final TweetTimelineListAdapter adapter =
                new TweetTimelineListAdapter.Builder(getActivity())
                .setTimelineFilter(getBasicTimelineFilter())
                .setTimeline(timeline)
                .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                .setOnActionCallback(actionCallback)
                .build();

        setListAdapter(adapter);
    }

    private TimelineFilter getBasicTimelineFilter() {
        final InputStream inputStream = getContext().getResources().
                openRawResource(R.raw.filter_values);
        final JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
        final FilterValues filterValues = new Gson().fromJson(reader, FilterValues.class);
        return new BasicTimelineFilter(filterValues);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tweetui_timeline, container, false);
    }
}
