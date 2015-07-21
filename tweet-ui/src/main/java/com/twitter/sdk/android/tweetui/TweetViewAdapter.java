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

package com.twitter.sdk.android.tweetui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to provide a collection of TweetViews to AdapterViews (such as ListView).
 */
public class TweetViewAdapter<T extends BaseTweetView> extends BaseAdapter {
    protected final Context context;
    protected List<Tweet> tweets;

    /**
     * Constructs a TweetViewAdapter with an empty collection of Tweets.
     * @param context the context of the views
     */
    public TweetViewAdapter(Context context) {
        this.context = context;
        tweets = new ArrayList<>();
    }

    /**
     * Constructs a TweetViewAdapter for the given collection of Tweets.
     * @param context the context of the views
     * @param tweets collection of Tweets
     */
    public TweetViewAdapter(Context context, List<Tweet> tweets) {
        super();
        this.context = context;
        this.tweets = tweets;
    }

    /**
     * Override to customize the Tweet view that should be used in the list.
     */
    public T getTweetView(Context context, Tweet tweet) {
        return (T) new CompactTweetView(context, tweet);
    }


    @Override
    public int getCount() {
        return (tweets == null) ? 0 : tweets.size();
    }

    @Override
    public Tweet getItem(int position) {
        return tweets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Sets all Tweets with id matching the given Tweet id to the updated Tweet.
     * @param tweet the updated Tweet to set in the list
     */
    public void setTweetById(Tweet tweet) {
        for (int i = 0; i < tweets.size(); i++) {
            if (tweet.getId() == tweets.get(i).getId()) {
                tweets.set(i, tweet);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        final Tweet tweet = getItem(position);
        if (rowView == null) {
            rowView = getTweetView(context, tweet);
        } else {
            ((BaseTweetView) rowView).setTweet(tweet);
        }
        return rowView;
    }

    /**
     * Get the collection of Tweets.
     */
    public List<Tweet> getTweets() {
        return tweets;
    }

    /**
     * Set the collection of Tweets.
     */
    public void setTweets(List<Tweet> tweets) {
        if (tweets == null) {
            this.tweets = new ArrayList<>();
        } else {
            this.tweets = tweets;
        }
        notifyDataSetChanged();
    }
}
