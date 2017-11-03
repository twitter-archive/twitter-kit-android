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
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.app.R;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetBuilder;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.models.UserBuilder;
import com.twitter.sdk.android.tweetui.TweetView;

public class UniqueTweetActivity extends TweetUiActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.unqiue_tweets);
        }
    }

    @Override
    int getLayout() {
        return R.layout.activity_frame;
    }

    @Override
    Fragment createFragment() {
        return UniqueTweetFragment.newInstance();
    }

    /**
     * Fragment showing unique Tweet view cases.
     */
    public static class UniqueTweetFragment extends Fragment {

        public static UniqueTweetFragment newInstance() {
            return new UniqueTweetFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View v = inflater.inflate(R.layout.tweetui_fragment_unique_tweet, container,
                    false);

            final LinearLayout tweetRegion = v.findViewById(R.id.tweet_region);

            // Tweet object already present, construct a TweetView
            final Tweet knownTweet = new TweetBuilder()
                    .setId(3L)
                    .setUser(new UserBuilder()
                                    .setId(User.INVALID_ID)
                                    .setName("name")
                                    .setScreenName("namename")
                                    .setVerified(false)
                                    .build()
                    )
                    .setText("Preloaded text of a Tweet that couldn't be loaded.")
                    .setCreatedAt("Wed Jun 06 20:07:10 +0000 2012")
                    .build();
            final TweetView knownTweetView = new TweetView(getActivity(), knownTweet);
            tweetRegion.addView(knownTweetView);

            return v;
        }
    }
}
