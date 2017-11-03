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
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.app.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;


public class TweetPreviewActivity extends TweetUiActivity {
    private static final String TAG = "TweetPreviewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.preview_tweet);
        }
    }

    @Override
    int getLayout() {
        return R.layout.activity_frame;
    }

    @Override
    Fragment createFragment() {
        return TweetPreviewFragment.newInstance();
    }

    /**
     * Fragment showing a Tweet id input field, light/dark buttons, and a scrollable region which
     * renders light/dark previews of the requested Tweet for quick manual validation.
     */
    public static class TweetPreviewFragment extends Fragment {

        public static TweetPreviewFragment newInstance() {
            return new TweetPreviewFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {
            final View v = inflater.inflate(R.layout.tweetui_fragment_tweet_preview, container,
                    false);

            final ViewGroup tweetRegion = v.findViewById(R.id.tweet_region);
            final EditText selectorInput = v.findViewById(R.id.selector_input_tweet_id);
            final Button lightButton = v.findViewById(R.id.button_show_light);
            final Button darkButton = v.findViewById(R.id.button_show_dark);

            lightButton.setOnClickListener(v12 -> {
                final long tweetId = Long.parseLong(selectorInput.getText().toString());
                tweetRegion.removeAllViews();
                loadTweet(tweetId, tweetRegion, R.style.tw__TweetLightStyle);
            });

            darkButton.setOnClickListener(v1 -> {
                final long tweetId = Long.parseLong(selectorInput.getText().toString());
                tweetRegion.removeAllViews();
                loadTweet(tweetId, tweetRegion, R.style.tw__TweetDarkStyle);
            });

            return v;
        }

        /**
         * loadTweet wraps TweetUtils.loadTweet with a callback that ensures a compact and default
         * view with the correct style and spacing are inserted.
         */
        private void loadTweet(long tweetId, final ViewGroup container, final int style) {

            final Callback<Tweet> singleTweetCallback = new Callback<Tweet>() {

                @Override
                public void success(Result<Tweet> result) {
                    final Context context = getActivity();
                    if (context == null) return;

                    final Tweet tweet = result.data;
                    final CompactTweetView cv = new CompactTweetView(context, tweet, style);
                    container.addView(cv);

                    final View spacer = new View(context);
                    final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                            (int) context.getResources().getDimension(R.dimen.demo_tweet_space));
                    spacer.setLayoutParams(params);
                    container.addView(spacer);

                    final TweetView tv = new TweetView(context, tweet, style);
                    container.addView(tv);

                }

                @Override
                public void failure(TwitterException exception) {
                    final Activity activity = getActivity();
                    if (activity != null && !activity.isFinishing()) {
                        Toast.makeText(activity, R.string.tweet_load_error,
                                Toast.LENGTH_SHORT).show();
                    }
                    Log.e(TAG, "loadTweet failure", exception);
                }
            };
            TweetUtils.loadTweet(tweetId, singleTweetCallback);
        }
    }
}
