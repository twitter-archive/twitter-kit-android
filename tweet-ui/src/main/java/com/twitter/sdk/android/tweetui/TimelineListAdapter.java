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
import android.widget.BaseAdapter;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.Identifiable;

/**
 * TimelineListAdapter is a ListAdapter providing timeline items for ListViews.
 * Concrete subclasses must define a type parameter and implement getView.
 */
abstract class TimelineListAdapter<T extends Identifiable> extends BaseAdapter {
    protected final Context context;
    protected final TimelineDelegate<T> delegate;

    /**
     * Constructs a TimelineListAdapter for the given Timeline.
     * @param context the context for row views.
     * @param timeline a Timeline providing access to timeline data items.
     * @throws java.lang.IllegalArgumentException if context or timeline is null
     */
    TimelineListAdapter(Context context, Timeline<T> timeline) {
        this(context, new TimelineDelegate<>(timeline));
    }

    TimelineListAdapter(Context context, TimelineDelegate<T> delegate) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        this.context = context;
        this.delegate = delegate;
        delegate.refresh(null);
    }

    /**
     * Clears the items and loads the latest Timeline items.
     */
    public void refresh(Callback<TimelineResult<T>> cb) {
        delegate.refresh(cb);
    }

    @Override
    public int getCount() {
        return delegate.getCount();
    }

    @Override
    public T getItem(int position) {
        return delegate.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return delegate.getItemId(position);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        delegate.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        delegate.unregisterDataSetObserver(observer);
    }

    @Override
    public void notifyDataSetChanged() {
        delegate.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        delegate.notifyDataSetInvalidated();
    }
}
