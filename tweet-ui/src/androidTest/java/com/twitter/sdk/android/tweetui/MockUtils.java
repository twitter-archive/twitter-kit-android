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

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.services.CollectionService;
import com.twitter.sdk.android.core.services.ListService;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.StatusesService;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class MockUtils {

    private MockUtils() {}

    public static Picasso mockPicasso(Picasso picasso, RequestCreator requestCreator) {
        when(picasso.load(anyString())).thenReturn(requestCreator);
        when(picasso.load(anyInt())).thenReturn(requestCreator);
        when(requestCreator.centerCrop()).thenReturn(requestCreator);
        when(requestCreator.error(anyInt())).thenReturn(requestCreator);
        when(requestCreator.fit()).thenReturn(requestCreator);
        when(requestCreator.placeholder(any(Drawable.class)))
                .thenReturn(requestCreator);
        doNothing().when(requestCreator).into(any(ImageView.class));
        return picasso;
    }

    public static void mockApiClient(TwitterApiClient apiClient) {
        final StatusesService statusesService = mock(StatusesService.class, new MockCallAnswer());
        final SearchService searchService = mock(SearchService.class, new MockCallAnswer());
        final ListService listService = mock(ListService.class, new MockCallAnswer());
        final CollectionService collectionService =
                mock(CollectionService.class, new MockCallAnswer());

        when(apiClient.getStatusesService()).thenReturn(statusesService);
        when(apiClient.getCollectionService()).thenReturn(collectionService);
        when(apiClient.getSearchService()).thenReturn(searchService);
        when(apiClient.getListService()).thenReturn(listService);
    }

    public static void mockClients(ConcurrentHashMap<Session, TwitterApiClient> clients,
                                   TwitterApiClient apiClient) {
        when(clients.get(anyObject())).thenReturn(apiClient);
        when(clients.contains(anyObject())).thenReturn(true);
    }
}
