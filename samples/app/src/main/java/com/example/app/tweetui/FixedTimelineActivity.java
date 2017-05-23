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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.app.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.FixedTweetTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TweetUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FixedTimelineActivity extends TweetUiActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.fixed_timeline);
        }
    }

    @Override
    int getLayout() {
        return R.layout.activity_frame;
    }

    @Override
    Fragment createFragment() {
        return FixedTimelineFragment.newInstance();
    }

    /**
     * Fragment showing a Timeline with a fixed list of Tweets.
     */
    public static class FixedTimelineFragment extends ListFragment {

        final List<Long> tweetIds = new ArrayList<>();

        public static FixedTimelineFragment newInstance() {
            return new FixedTimelineFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            tweetIds.addAll(Arrays.asList(574000939800993792L, 503435417459249153L, 510908133917487104L,
                    473514864153870337L, 477788140900347904L, 20L, 484816434313195520L,
                    466041861774114819L, 448250020773380096L));

            TweetUtils.loadTweets(tweetIds, new Callback<List<Tweet>>() {
                @Override
                public void success(Result<List<Tweet>> result) {
                    final FixedTweetTimeline fixedTimeline = new FixedTweetTimeline.Builder()
                            .setTweets(result.data).build();
                    final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter(getActivity(),
                            fixedTimeline);
                    setListAdapter(adapter);
                }

                @Override
                public void failure(TwitterException exception) {
                    final Activity activity = getActivity();
                    if (activity != null && !activity.isFinishing()) {
                        Toast.makeText(activity, R.string.multi_tweet_view_error,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.tweetui_timeline, container, false);
        }
    }
}
