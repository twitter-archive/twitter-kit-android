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
import com.twitter.sdk.android.core.services.StatusesService;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public final class MockUtils {

    private MockUtils() {}

    public static Picasso mockPicasso(Picasso picasso, RequestCreator requestCreator) {
        when(picasso.load(anyString())).thenReturn(requestCreator);
        when(picasso.load(anyInt())).thenReturn(requestCreator);
        when(requestCreator.centerCrop()).thenReturn(requestCreator);
        when(requestCreator.fit()).thenReturn(requestCreator);
        when(requestCreator.placeholder(any(Drawable.class)))
                .thenReturn(requestCreator);
        doNothing().when(requestCreator).into(any(ImageView.class));
        return picasso;
    }

    public static void mockExecutorService(ExecutorService executorService) {
        final ArgumentCaptor<Runnable> runableArgument =
                ArgumentCaptor.forClass(Runnable.class);
        when(executorService.submit(runableArgument.capture())).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        return null;
                    }
                }
        );
    }

    public static void mockStatusesServiceClient(TwitterApiClient apiClient,
            StatusesService statusesService) {
        when(apiClient.getStatusesService()).thenReturn(statusesService);
    }

    public static void mockClients(ConcurrentHashMap<Session, TwitterApiClient> clients,
                                   TwitterApiClient apiClient) {
        when(clients.get(anyObject())).thenReturn(apiClient);
        when(clients.contains(anyObject())).thenReturn(true);
    }
}
