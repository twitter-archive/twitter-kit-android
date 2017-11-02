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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.scribe.ScribeItem;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * TweetTimelineListAdapter is a ListAdapter which can provide Timeline Tweets to ListViews.
 */
public class TweetTimelineListAdapter extends TimelineListAdapter<Tweet> {
    protected Callback<Tweet> actionCallback;
    protected final int styleResId;
    protected TweetUi tweetUi;

    static final String TOTAL_FILTERS_JSON_PROP = "total_filters";
    static final String DEFAULT_FILTERS_JSON_MSG = "{\"total_filters\":0}";
    final Gson gson = new Gson();

    /**
     * Constructs a TweetTimelineListAdapter for the given Tweet Timeline.
     * @param context the context for row views.
     * @param timeline a Timeline&lt;Tweet&gt; providing access to Tweet data items.
     * @throws java.lang.IllegalArgumentException if context is null
     */
    public TweetTimelineListAdapter(Context context, Timeline<Tweet> timeline) {
        this(context, timeline, R.style.tw__TweetLightStyle, null);
    }

    TweetTimelineListAdapter(Context context, Timeline<Tweet> timeline, int styleResId,
                             Callback<Tweet> cb) {
        this(context, new TimelineDelegate<>(timeline), styleResId, cb, TweetUi.getInstance());
    }

    TweetTimelineListAdapter(Context context, TimelineDelegate<Tweet> delegate, int styleResId,
                             Callback<Tweet> cb, TweetUi tweetUi) {
        super(context, delegate);
        this.styleResId = styleResId;
        this.actionCallback = new ReplaceTweetCallback(delegate, cb);
        this.tweetUi = tweetUi;

        scribeTimelineImpression();
    }

    /**
     * Returns a CompactTweetView by default. May be overridden to provide another view for the
     * Tweet item. If Tweet actions are enabled, be sure to call setOnActionCallback(actionCallback)
     * on each new subclass of BaseTweetView to ensure proper success and failure handling
     * for Tweet actions (favorite, unfavorite).
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        final Tweet tweet = getItem(position);
        if (rowView == null) {
            final BaseTweetView tv = new CompactTweetView(context, tweet, styleResId);
            tv.setOnActionCallback(actionCallback);
            rowView = tv;
        } else {
            ((BaseTweetView) rowView).setTweet(tweet);
        }
        return rowView;
    }

    private void scribeTimelineImpression() {
        final String jsonMessage;
        if (delegate instanceof FilterTimelineDelegate) {
            final FilterTimelineDelegate filterTimelineDelegate = (FilterTimelineDelegate) delegate;
            final TimelineFilter timelineFilter = filterTimelineDelegate.timelineFilter;
            jsonMessage = getJsonMessage(timelineFilter.totalFilters());
        } else {
            jsonMessage = DEFAULT_FILTERS_JSON_MSG;
        }

        final ScribeItem scribeItem = ScribeItem.fromMessage(jsonMessage);
        final List<ScribeItem> items = new ArrayList<>();
        items.add(scribeItem);

        final String timelineType = getTimelineType(delegate.getTimeline());
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
     * TweetTimelineListAdapter Builder
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
        public Builder setTimeline(Timeline<Tweet> timeline) {
            this.timeline = timeline;
            return this;
        }

        /**
         * Sets the Tweet view style by resource id.
         * @param styleResId resource id of the Tweet view style
         */
        public Builder setViewStyle(int styleResId) {
            this.styleResId = styleResId;
            return this;
        }

        /**
         * Sets the callback to call when a Tweet action is performed on a Tweet view.
         * @param actionCallback called when a Tweet action is performed.
         */
        public Builder setOnActionCallback(Callback<Tweet> actionCallback) {
            this.actionCallback = actionCallback;
            return this;
        }

        /**
         * Sets the TimelineFilter used to filter tweets from timeline.
         * @param timelineFilter timelineFilter for timeline
         */
        public Builder setTimelineFilter(TimelineFilter timelineFilter) {
            this.timelineFilter = timelineFilter;
            return this;
        }

        /**
         * Builds a TweetTimelineListAdapter from Builder parameters.
         * @return a TweetTimelineListAdpater
         */
        public TweetTimelineListAdapter build() {
            if (timelineFilter == null) {
                return new TweetTimelineListAdapter(context, timeline, styleResId, actionCallback);
            } else {
                final FilterTimelineDelegate delegate =
                        new FilterTimelineDelegate(timeline, timelineFilter);
                return new TweetTimelineListAdapter(context, delegate, styleResId, actionCallback,
                        TweetUi.getInstance());
            }
        }
    }
}
