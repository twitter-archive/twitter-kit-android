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

import android.database.DataSetObservable;
import android.database.DataSetObserver;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Identifiable;

import java.util.ArrayList;
import java.util.List;

/**
 * TimelineDelegate manages timeline data items and loads items from a Timeline.
 * @param <T> the item type
 */
class TimelineDelegate<T extends Identifiable> {
    // once capacity is exceeded, additional items will not be loaded
    static final long CAPACITY = 200L;
    // timeline that next and previous items are loaded from
    final Timeline<T> timeline;
    // Observable for Adapter DataSetObservers (for ListViews)
    final DataSetObservable listAdapterObservable;
    final TimelineStateHolder timelineStateHolder;
    List<T> itemList;

    /**
     * Constructs a TimelineDelegate with a timeline for requesting data.
     * @param timeline Timeline source
     * @throws java.lang.IllegalArgumentException if timeline is null
     */
    TimelineDelegate(Timeline<T> timeline) {
        this(timeline, null, null);
    }

    TimelineDelegate(Timeline<T> timeline, DataSetObservable observable, List<T> items) {
        if (timeline == null) {
            throw new IllegalArgumentException("Timeline must not be null");
        }
        this.timeline = timeline;
        this.timelineStateHolder = new TimelineStateHolder();
        if (observable == null) {
            listAdapterObservable = new DataSetObservable();
        } else {
            listAdapterObservable = observable;
        }

        if (items == null) {
            itemList = new ArrayList<>();
        } else {
            itemList = items;
        }
    }

    /**
     * Triggers loading the latest items and calls through to the developer callback. If items are
     * received, they replace existing items.
     */
    public void refresh(Callback<TimelineResult<T>> developerCb) {
        // reset scrollStateHolder cursors to be null, loadNext will get latest items
        timelineStateHolder.resetCursors();
        // load latest timeline items and replace existing items
        loadNext(timelineStateHolder.positionForNext(),
                new RefreshCallback(developerCb, timelineStateHolder));
    }

    /**
     * Triggers loading next items and calls through to the developer callback.
     */
    public void next(Callback<TimelineResult<T>> developerCb) {
        loadNext(timelineStateHolder.positionForNext(),
                new NextCallback(developerCb, timelineStateHolder));
    }

    /**
     * Triggers loading previous items.
     */
    public void previous() {
        loadPrevious(timelineStateHolder.positionForPrevious(),
                new PreviousCallback(timelineStateHolder));
    }

    /**
     * Returns the number of items in the data set.
     * @return Count of items.
     */
    public int getCount() {
        return itemList.size();
    }


    public Timeline getTimeline() {
        return timeline;
    }

    /**
     * Gets the data item associated with the specified position in the data set.
     * @param position The position of the item within the adapter's data set.
     * @return The data at the specified position.
     */
    public T getItem(int position) {
        if (isLastPosition(position)) {
            previous();
        }
        return itemList.get(position);
    }

    /**
     * Gets the row id associated with the specified position in the list.
     * @param position The position of the item within the adapter's data set.
     * @return The id of the item at the specified position.
     */
    public long getItemId(int position) {
        final Identifiable item = itemList.get(position);
        return item.getId();
    }

    /**
     * Sets all items in the itemList with the item id to be item. If no items with the same id
     * are found, no changes are made.
     * @param item the updated item to set in the itemList
     */
    public void setItemById(T item) {
        for (int i = 0; i < itemList.size(); i++) {
            if (item.getId() == itemList.get(i).getId()) {
                itemList.set(i, item);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Returns true if the itemList size is below the MAX_ITEMS capacity, false otherwise.
     */
    boolean withinMaxCapacity() {
        return itemList.size() < CAPACITY;
    }

    /**
     * Returns true if the position is for the last item in itemList, false otherwise.
     */
    boolean isLastPosition(int position) {
        return position == (itemList.size() - 1);
    }

    /**
     * Checks the capacity and sets requestInFlight before calling timeline.next.
     */
    void loadNext(Long minPosition, Callback<TimelineResult<T>> cb) {
        if (withinMaxCapacity()) {
            if (timelineStateHolder.startTimelineRequest()) {
                timeline.next(minPosition, cb);
            } else {
                cb.failure(new TwitterException("Request already in flight"));
            }
        } else {
            cb.failure(new TwitterException("Max capacity reached"));
        }
    }

    /**
     * Checks the capacity and sets requestInFlight before calling timeline.previous.
     */
    void loadPrevious(Long maxPosition, Callback<TimelineResult<T>> cb) {
        if (withinMaxCapacity()) {
            if (timelineStateHolder.startTimelineRequest()) {
                timeline.previous(maxPosition, cb);
            } else {
                cb.failure(new TwitterException("Request already in flight"));
            }
        } else {
            cb.failure(new TwitterException("Max capacity reached"));
        }
    }

    /**
     * TimelineDelegate.DefaultCallback is a Callback which handles setting requestInFlight to
     * false on both success and failure and calling through to a wrapped developer Callback.
     * Subclass methods must call through to the parent method after their custom implementation.
     */
    class DefaultCallback extends Callback<TimelineResult<T>> {
        final Callback<TimelineResult<T>> developerCallback;
        final TimelineStateHolder timelineStateHolder;

        DefaultCallback(Callback<TimelineResult<T>> developerCb,
                        TimelineStateHolder timelineStateHolder) {
            this.developerCallback = developerCb;
            this.timelineStateHolder = timelineStateHolder;
        }

        @Override
        public void success(Result<TimelineResult<T>> result) {
            timelineStateHolder.finishTimelineRequest();
            if (developerCallback != null) {
                developerCallback.success(result);
            }
        }

        @Override
        public void failure(TwitterException exception) {
            timelineStateHolder.finishTimelineRequest();
            if (developerCallback != null) {
                developerCallback.failure(exception);
            }
        }
    }

    /**
     * Handles receiving next timeline items. Prepends received items to listItems, updates the
     * scrollStateHolder nextCursor, and calls notifyDataSetChanged.
     */
    class NextCallback extends DefaultCallback {

        NextCallback(Callback<TimelineResult<T>> developerCb,
                TimelineStateHolder timelineStateHolder) {
            super(developerCb, timelineStateHolder);
        }

        @Override
        public void success(Result<TimelineResult<T>> result) {
            if (result.data.items.size() > 0) {
                final ArrayList<T> receivedItems = new ArrayList<>(result.data.items);
                receivedItems.addAll(itemList);
                itemList = receivedItems;
                notifyDataSetChanged();
                timelineStateHolder.setNextCursor(result.data.timelineCursor);
            }
            // do nothing when zero items are received. Subsequent 'next' call does not change.
            super.success(result);
        }
    }

    /**
     * Handles receiving latest timeline items. If timeline items are received, clears listItems,
     * sets received items, updates the scrollStateHolder nextCursor, and calls
     * notifyDataSetChanged. If the results have no items, does nothing.
     */
    class RefreshCallback extends NextCallback {

        RefreshCallback(Callback<TimelineResult<T>> developerCb,
                TimelineStateHolder timelineStateHolder) {
            super(developerCb, timelineStateHolder);
        }

        @Override
        public void success(Result<TimelineResult<T>> result) {
            if (result.data.items.size() > 0) {
                itemList.clear();
            }
            super.success(result);
        }
    }

    /**
     * Handles appending listItems and updating the scrollStateHolder previousCursor.
     */
    class PreviousCallback extends DefaultCallback {

        PreviousCallback(TimelineStateHolder timelineStateHolder) {
            super(null, timelineStateHolder);
        }

        @Override
        public void success(Result<TimelineResult<T>> result) {
            if (result.data.items.size() > 0) {
                itemList.addAll(result.data.items);
                notifyDataSetChanged();
                timelineStateHolder.setPreviousCursor(result.data.timelineCursor);
            }
            // do nothing when zero items are received. Subsequent 'next' call does not change.
            super.success(result);
        }
    }

    /* Support Adapter DataSetObservers, based on BaseAdapter */

    /**
     * Registers an observer that is called when changes happen to the managed data items.
     * @param observer The object that will be notified when the data set changes.
     */
    public void registerDataSetObserver(DataSetObserver observer) {
        listAdapterObservable.registerObserver(observer);
    }

    /**
     * Unregister an observer that has previously been registered via
     * registerDataSetObserver(DataSetObserver).
     * @param observer The object to unregister.
     */
    public void unregisterDataSetObserver(DataSetObserver observer) {
        listAdapterObservable.unregisterObserver(observer);
    }

    /**
     * Notifies the attached observers that the underlying data has been changed and any View
     * reflecting the data set should refresh itself.
     */
    public void notifyDataSetChanged() {
        listAdapterObservable.notifyChanged();
    }

    /**
     * Notifies the attached observers that the underlying data is not longer valid or available.
     * Once invoked, this adapter is no longer valid and should not report further data set changes.
     */
    public void notifyDataSetInvalidated() {
        listAdapterObservable.notifyInvalidated();
    }
}
