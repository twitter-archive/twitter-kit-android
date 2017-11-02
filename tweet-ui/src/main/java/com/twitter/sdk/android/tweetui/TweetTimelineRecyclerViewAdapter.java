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
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.scribe.ScribeItem;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * TweetTimelineRecyclerViewAdapter is a RecyclerView adapter which can provide Timeline Tweets to
 * RecyclerViews.
 */
public class TweetTimelineRecyclerViewAdapter extends
        RecyclerView.Adapter<TweetTimelineRecyclerViewAdapter.TweetViewHolder> {

    protected final Context context;
    protected final TimelineDelegate<Tweet> timelineDelegate;
    protected Callback<Tweet> actionCallback;
    protected final int styleResId;
    protected TweetUi tweetUi;
    private int previousCount;

    static final String TOTAL_FILTERS_JSON_PROP = "total_filters";
    static final String DEFAULT_FILTERS_JSON_MSG = "{\"total_filters\":0}";
    final Gson gson = new Gson();

    /**
     * Constructs a TweetTimelineRecyclerViewAdapter for a RecyclerView implementation of a timeline
     *
     * @param context the context for row views.
     * @param timeline a Timeline&lt;Tweet&gt; providing access to Tweet data items.
     * @throws java.lang.IllegalArgumentException if context is null
     */
    public TweetTimelineRecyclerViewAdapter(Context context, Timeline<Tweet> timeline) {
        this(context, timeline, R.style.tw__TweetLightStyle, null);
    }

    protected TweetTimelineRecyclerViewAdapter(Context context, Timeline<Tweet> timeline,
                                               int styleResId, Callback<Tweet> cb) {
        this(context, new TimelineDelegate<>(timeline), styleResId, cb, TweetUi.getInstance());
    }

    TweetTimelineRecyclerViewAdapter(Context context, TimelineDelegate<Tweet> timelineDelegate,
                                     int styleResId, Callback<Tweet> cb, TweetUi tweetUi) {
        this(context, timelineDelegate, styleResId);
        actionCallback = new ReplaceTweetCallback(timelineDelegate, cb);
        this.tweetUi = tweetUi;
        scribeTimelineImpression();
    }

    TweetTimelineRecyclerViewAdapter(Context context,
                                     final TimelineDelegate<Tweet> timelineDelegate,
                                     int styleResId) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        this.context = context;
        this.timelineDelegate = timelineDelegate;
        this.styleResId = styleResId;

        this.timelineDelegate.refresh(new Callback<TimelineResult<Tweet>>() {
            @Override
            public void success(Result<TimelineResult<Tweet>> result) {
                notifyDataSetChanged();
                previousCount = TweetTimelineRecyclerViewAdapter.this.timelineDelegate.getCount();
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });

        final DataSetObserver dataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (previousCount == 0) {
                    notifyDataSetChanged();
                } else {
                    notifyItemRangeInserted(previousCount,
                            TweetTimelineRecyclerViewAdapter.this.timelineDelegate.getCount()
                                    - previousCount);
                }
                previousCount = TweetTimelineRecyclerViewAdapter.this.timelineDelegate.getCount();
            }

            @Override
            public void onInvalidated() {
                notifyDataSetChanged();
                super.onInvalidated();
            }
        };

        this.timelineDelegate.registerDataSetObserver(dataSetObserver);
    }

    public void refresh(Callback<TimelineResult<Tweet>> cb) {
        timelineDelegate.refresh(cb);
        previousCount = 0;
    }

    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Tweet tweet = new TweetBuilder().build();
        final CompactTweetView compactTweetView = new CompactTweetView(context, tweet, styleResId);
        compactTweetView.setOnActionCallback(actionCallback);
        return new TweetViewHolder(compactTweetView);
    }

    @Override
    public void onBindViewHolder(TweetViewHolder holder, int position) {
        final Tweet tweet = timelineDelegate.getItem(position);
        final CompactTweetView compactTweetView = (CompactTweetView) holder.itemView;
        compactTweetView.setTweet(tweet);
    }

    @Override
    public int getItemCount() {
        return timelineDelegate.getCount();
    }

    protected static final class TweetViewHolder extends RecyclerView.ViewHolder {
        public TweetViewHolder(CompactTweetView itemView) {
            super(itemView);
        }
    }

    private void scribeTimelineImpression() {
        final String jsonMessage;
        if (timelineDelegate instanceof FilterTimelineDelegate) {
            final FilterTimelineDelegate filterTimelineDelegate =
                    (FilterTimelineDelegate) timelineDelegate;
            final TimelineFilter timelineFilter = filterTimelineDelegate.timelineFilter;
            jsonMessage = getJsonMessage(timelineFilter.totalFilters());
        } else {
            jsonMessage = DEFAULT_FILTERS_JSON_MSG;
        }

        final ScribeItem scribeItem = ScribeItem.fromMessage(jsonMessage);
        final List<ScribeItem> items = new ArrayList<>();
        items.add(scribeItem);

        final String timelineType = getTimelineType(timelineDelegate.getTimeline());
        tweetUi.scribe(ScribeConstants.getSyndicatedSdkTimelineNamespace(timelineType));
        tweetUi.scribe(ScribeConstants.getTfwClientTimelineNamespace(timelineType), items);
    }

    private String getJsonMessage(int totalFilters) {
        final JsonObject message = new JsonObject();
        message.addProperty(TOTAL_FILTERS_JSON_PROP, totalFilters);
        return gson.toJson(message);
    }

    static String getTimelineType(Timeline timeline) {
        if (timeline instanceof BaseTimeline) {
            return ((BaseTimeline) timeline).getTimelineType();
        }
        return "other";
    }

    /*
     * On success, sets the updated Tweet in the TimelineDelegate to replace any old copies
     * of the same Tweet by id.
     */
    static class ReplaceTweetCallback extends Callback<Tweet> {
        TimelineDelegate<Tweet> delegate;
        Callback<Tweet> cb;

        ReplaceTweetCallback(TimelineDelegate<Tweet> delegate, Callback<Tweet> cb) {
            this.delegate = delegate;
            this.cb = cb;
        }

        @Override
        public void success(Result<Tweet> result) {
            delegate.setItemById(result.data);
            if (cb != null) {
                cb.success(result);
            }
        }

        @Override
        public void failure(TwitterException exception) {
            if (cb != null) {
                cb.failure(exception);
            }
        }
    }

    /**
     * TweetTimelineRecyclerViewAdapter Builder
     */
    public static class Builder {
        private Context context;
        private Timeline<Tweet> timeline;
        private Callback<Tweet> actionCallback;
        private TimelineFilter timelineFilter;
        private int styleResId = R.style.tw__TweetLightStyle;

        /**
         * Constructs a Builder.
         * @param context Context for Tweet views.
         */
        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Sets the Tweet timeline data source.
         * @param timeline Timeline of Tweets
         */
        public TweetTimelineRecyclerViewAdapter.Builder setTimeline(Timeline<Tweet> timeline) {
            this.timeline = timeline;
            return this;
        }

        /**
         * Sets the Tweet view style by resource id.
         * @param styleResId resource id of the Tweet view style
         */
        public TweetTimelineRecyclerViewAdapter.Builder setViewStyle(int styleResId) {
            this.styleResId = styleResId;
            return this;
        }

        /**
         * Sets the callback to call when a Tweet action is performed on a Tweet view.
         * @param actionCallback called when a Tweet action is performed.
         */
        public TweetTimelineRecyclerViewAdapter.Builder setOnActionCallback(
                Callback<Tweet> actionCallback) {
            this.actionCallback = actionCallback;
            return this;
        }

        /**
         * Sets the TimelineFilter used to filter tweets from timeline.
         * @param timelineFilter timelineFilter for timeline
         */
        public TweetTimelineRecyclerViewAdapter.Builder setTimelineFilter(
                TimelineFilter timelineFilter) {
            this.timelineFilter = timelineFilter;
            return this;
        }

        /**
         * Builds a TweetTimelineRecyclerViewAdapter from Builder parameters.
         * @return a TweetTimelineListAdpater
         */
        public TweetTimelineRecyclerViewAdapter build() {
            if (timelineFilter == null) {
                return new TweetTimelineRecyclerViewAdapter(context, timeline, styleResId,
                        actionCallback);
            } else {
                final FilterTimelineDelegate delegate = new FilterTimelineDelegate(timeline,
                        timelineFilter);
                return new TweetTimelineRecyclerViewAdapter(context, delegate, styleResId,
                        actionCallback, TweetUi.getInstance());
            }
        }
    }
}
