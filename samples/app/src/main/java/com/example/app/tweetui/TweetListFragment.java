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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.LoadCallback;
import com.twitter.sdk.android.tweetui.TweetViewFetchAdapter;

import com.example.app.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Displays a layout with a ListView widget bound to a TweetViewAdapter data source.
 */
public class TweetListFragment extends ListFragment {

    final List<Long> tweetIds = new ArrayList<>();

    public static TweetListFragment newInstance() {
        return new TweetListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweetIds.addAll(Arrays.asList(574000939800993792L, 503435417459249153L, 510908133917487104L,
                473514864153870337L, 477788140900347904L, 20L, 484816434313195520L,
                466041861774114819L, 448250020773380096L));
        final TweetViewFetchAdapter adapter = new TweetViewFetchAdapter<CompactTweetView>(
                getActivity()) {
            @Override
            public CompactTweetView getTweetView(Context context, Tweet tweet) {
                return new CompactTweetView(context, tweet, R.style.tw__TweetLightStyle);
            }
        };
        setListAdapter(adapter);
        adapter.setTweetIds(tweetIds, new LoadCallback<List<Tweet>>() {
            @Override
            public void success(List<Tweet> tweets) {}

            @Override
            public void failure(TwitterException exception) {
                final Activity activity = getActivity();
                if (activity != null && !activity.isFinishing()) {
                    Toast.makeText(activity, R.string.tweet_load_error,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tweetui_timeline, container, false);
        return view;
    }
}
