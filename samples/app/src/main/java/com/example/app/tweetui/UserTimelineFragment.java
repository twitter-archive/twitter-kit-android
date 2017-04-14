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

import com.example.app.BuildConfig;
import com.example.app.R;
import com.example.app.twittercore.TwitterCoreMainActivity;
import com.mopub.nativeads.MoPubAdAdapter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.mopub.TwitterMoPubAdAdapter;
import com.twitter.sdk.android.mopub.TwitterStaticNativeAdRenderer;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

/**
 * UserTimelineFragment demonstrates a TimelineListAdapter with a UserTimeline.
 */
public class UserTimelineFragment extends ListFragment {

    public static UserTimelineFragment newInstance() {
        return new UserTimelineFragment();
    }

    private MoPubAdAdapter moPubAdAdapter;

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

        final UserTimeline userTimeline = new UserTimeline.Builder().screenName("twitterdev").build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(getActivity())
                .setTimeline(userTimeline)
                .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                .setOnActionCallback(actionCallback)
                .build();

        moPubAdAdapter = new TwitterMoPubAdAdapter(getActivity(), adapter);
        final TwitterStaticNativeAdRenderer adRenderer = new TwitterStaticNativeAdRenderer();
        moPubAdAdapter.registerAdRenderer(adRenderer);
        moPubAdAdapter.loadAds(BuildConfig.MOPUB_AD_UNIT_ID);

        setListAdapter(moPubAdAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tweetui_timeline, container, false);
    }

    @Override
    public void onDestroy() {
        // You must call this or the ad adapter may cause a memory leak
        moPubAdAdapter.destroy();
        super.onDestroy();
    }
}
