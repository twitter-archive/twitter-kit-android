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

import com.twitter.sdk.android.core.TwitterException;

/**
 * Callback for making an API request through a load utility. Logic in
 * callbacks is executed on the main thread.
 * @param <T> type of requested item (e.g. Tweet, List<Tweet>)
 */
public interface LoadCallback<T> {
    void success(T loadedItem);
    void failure(TwitterException exception);
}
