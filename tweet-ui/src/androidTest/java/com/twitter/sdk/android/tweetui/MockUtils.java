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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.services.StatusesService;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.ConcurrentHashMap;

import retrofit2.Call;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class MockUtils {

    private MockUtils() {}

    static Picasso mockPicasso(Picasso picasso, RequestCreator requestCreator) {
        when(picasso.load(anyString())).thenReturn(requestCreator);
        when(picasso.load(anyInt())).thenReturn(requestCreator);
        when(picasso.load(isNull(String.class))).thenReturn(requestCreator);
        when(requestCreator.centerCrop()).thenReturn(requestCreator);
        when(requestCreator.error(anyInt())).thenReturn(requestCreator);
        when(requestCreator.fit()).thenReturn(requestCreator);
        when(requestCreator.placeholder(any(Drawable.class)))
                .thenReturn(requestCreator);
        doNothing().when(requestCreator).into(any(ImageView.class));
        return picasso;
    }

    static void mockApiClient(TwitterApiClient apiClient) {
        final StatusesService statusesService = mock(StatusesService.class, new MockCallAnswer());

        when(apiClient.getStatusesService()).thenReturn(statusesService);
    }

    static void mockClients(ConcurrentHashMap<Session, TwitterApiClient> clients,
                                   TwitterApiClient apiClient) {
        when(clients.get(anyObject())).thenReturn(apiClient);
        when(clients.contains(anyObject())).thenReturn(true);
    }

    static class MockCallAnswer implements Answer<Object> {
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            if (invocation.getMethod().getReturnType().equals(Call.class)) {
                return mock(Call.class);
            } else {
                return Mockito.RETURNS_DEFAULTS.answer(invocation);
            }
        }
    }
}
