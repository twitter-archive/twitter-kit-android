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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.R;
import com.example.app.twittercore.TwitterCoreMainActivity;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.BaseTweetView;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import java.util.Arrays;
import java.util.List;

public class TweetActivity extends TweetUiActivity {
    private static final String TAG = "TweetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.tweets_activity);
        }
    }

    @Override
    int getLayout() {
        return R.layout.activity_frame;
    }

    @Override
    Fragment createFragment() {
        return TweetsFragment.newInstance();
    }

    public static class TweetsFragment extends Fragment {

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

        public static TweetsFragment newInstance() {
            return new TweetsFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final View v = inflater.inflate(R.layout.tweetui_fragment_tweet, container, false);

            final ViewGroup tweetRegion = v.findViewById(R.id.tweet_region);

            // load single Tweets and construct TweetViews
            loadTweet(20L, tweetRegion, R.id.jack_regular_tweet);
            loadTweet(510908133917487104L, tweetRegion, R.id.bike_regular_tweet);

            // load multiple Tweets and construct CompactTweetViews
            final List<Long> tweetIds = Arrays.asList(20L, 510908133917487104L);
            final List<Integer> viewIds = Arrays.asList(R.id.jack_compact_tweet,
                    R.id.bike_compact_tweet);
            loadTweets(tweetIds, tweetRegion, viewIds);

            return v;
        }

        /**
         * loadTweet wraps TweetUtils.loadTweet with a callback that ensures the view is given a
         * known id to simplify UI automation testing.
         */
        private void loadTweet(long tweetId, final ViewGroup container, final int viewId) {
            final Callback<Tweet> singleTweetCallback = new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    final Context context = getActivity();
                    if (context == null) return;
                    final Tweet tweet = result.data;
                    final BaseTweetView tv = new TweetView(context, tweet,
                            R.style.tw__TweetLightWithActionsStyle);
                    tv.setOnActionCallback(actionCallback);
                    tv.setId(viewId);
                    container.addView(tv);
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.e(TAG, "loadTweet failure", exception);
                }
            };
            TweetUtils.loadTweet(tweetId, singleTweetCallback);
        }

        /**
         * loadTweets wraps TweetUtils.loadTweets to use a callback that ensures each view is given
         * a known id to simplify UI automation testing.
         */
        private void loadTweets(final List<Long> tweetIds, final ViewGroup container,
                                final List<Integer> viewIds) {
            TweetUtils.loadTweets(tweetIds, new Callback<List<Tweet>>() {
                @Override
                public void success(Result<List<Tweet>> result) {
                    final Context context = getActivity();
                    if (context == null) return;
                    for (int i = 0; i < result.data.size(); i++) {
                        final BaseTweetView tv = new CompactTweetView(context, result.data.get(i),
                                R.style.tw__TweetDarkWithActionsStyle);
                        tv.setOnActionCallback(actionCallback);
                        tv.setId(viewIds.get(i));
                        container.addView(tv);
                    }
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.e(TAG, "loadTweets failure " + tweetIds, exception);
                }
            });
        }
    }
}
