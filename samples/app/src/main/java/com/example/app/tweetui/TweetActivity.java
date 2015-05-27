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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.R;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.LoadCallback;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import java.util.Arrays;
import java.util.List;

public class TweetActivity extends TweetUiActivity {
    private static final String TAG = "TweetActivity";

    @Override
    int getLayout() {
        return R.layout.activity_frame;
    }

    @Override
    Fragment createFragment() {
        return TweetsFragment.newInstance();
    }

    public static class TweetsFragment extends Fragment {

        public static TweetsFragment newInstance() {
            return new TweetsFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final View v = inflater.inflate(R.layout.tweetui_fragment_tweet, container, false);

            final ViewGroup tweetRegion = (ViewGroup) v.findViewById(R.id.tweet_region);

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
            final LoadCallback<Tweet> singleTweetCallback = new LoadCallback<Tweet>() {
                @Override
                public void success(Tweet tweet) {
                    final Context context = getActivity();
                    if (context == null) return;
                    final View view = new TweetView(context, tweet);
                    view.setId(viewId);
                    container.addView(view);
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
            TweetUtils.loadTweets(tweetIds, new LoadCallback<List<Tweet>>() {
                @Override
                public void success(List<Tweet> tweets) {
                    final Context context = getActivity();
                    if (context == null) return;
                    for (int i = 0; i < tweets.size(); i++) {
                        final View view = new CompactTweetView(context, tweets.get(i));
                        view.setId(viewIds.get(i));
                        container.addView(view);
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
