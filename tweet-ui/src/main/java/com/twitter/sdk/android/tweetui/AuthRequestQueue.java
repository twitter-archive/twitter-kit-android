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

import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.tweetui.internal.ActiveSessionProvider;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * This queue transparently fetches guest auth tokens when no other signing mechanism is present
 * and then passes the requests off to the network layer.
 *
 * In order to solve concurrent access problems we have put synchronized around the public methods
 * in order to lock queue access so that we can avoid orphaning requests in the queue
 */
class AuthRequestQueue {
    final Queue<Callback<TwitterApiClient>> queue;
    // We use this to flag to mark that a session is either being restored from file or
    // requested from the server
    final AtomicBoolean awaitingSession;

    private final TwitterCore twitterCore;
    private final ActiveSessionProvider activeSessionProvider;

    AuthRequestQueue(TwitterCore twitterCore, ActiveSessionProvider activeSessionProvider) {
        this.twitterCore = twitterCore;
        this.activeSessionProvider = activeSessionProvider;
        queue = new ConcurrentLinkedQueue<>();
        awaitingSession = new AtomicBoolean(true);
    }

    /*
     * addRequest has 3 different branches
     * 1: if we are not waiting for a session (from file restoration or from network request)
     *    and there is an active session and an authConfig it will simply pass the request
     *    off to the network layer
     * 2: if we have already kicked off a request to get a guest auth token or otherwise just
     *    don't have an auth config in the form of an oauth2service (provided once TweetUi
     *    has been given a client id and secret we will queue the request
     * 3: otherwise we queue the request and start a request to the twitter api to get a guest
     *    auth token. We set the request flag to be active so that we don't end up kicking off
     *    duplicate requests to the api to get guest auth keys.
     */
    protected synchronized boolean addRequest(Callback<TwitterApiClient> callback) {
        if (callback == null) return false;

        // awaitingSession will be true until session restoration completes in the background.
        if (!awaitingSession.get()) {
            final Session session = getValidSession();
            if (session != null) {
                callback.success(new Result<>(twitterCore.getApiClient(session), null));
            } else {
                queue.add(callback);
                awaitingSession.set(true);
                requestAuth();
            }
        } else {
            queue.add(callback);
        }
        return true;
    }

    /*
     * We have 3 different outcomes:
     * 1. Valid session is restored, we need to flush requests
     * 2. No valid session and, there are pending requests, we need to initiate AuthRequest
     * 3. No valid session and nothing awaiting in the queue. We only need to remove the flag,
     * first request will trigger AuthRequest.
     */
    synchronized void sessionRestored(Session session) {
        if (session != null) {
            flushQueueOnSuccess(twitterCore.getApiClient(session));
        } else if (queue.size() > 0) {
            requestAuth();
        } else {
            // We can not find any session on the disk, future requests for Session
            awaitingSession.set(false);
        }
    }

    /*
     * Requests from the twitter api first an app auth token and then a guest auth token, only
     * is successful if both requests are successful.
     */
    void requestAuth() {
        twitterCore.logInGuest(getAppAuthTokenCallback());
    }

    /*
     * This is called only once we have a guest auth token and can pass these requests off to the
     * network layer to be signed with our new guest auth token.
     *
     * TODO: pass new token explicitly to the requests, currently this is only done by reference
     */
    synchronized void flushQueueOnSuccess(TwitterApiClient apiClient) {
        awaitingSession.set(false);

        while (!queue.isEmpty()) {
            final Callback<TwitterApiClient> request = queue.poll();
            request.success(new Result<>(apiClient, null));
        }
    }

    /*
     * If we weren't able to get a guest auth token we can't make these requests so we just call
     * back to the configured listener with an error
     */
    synchronized void flushQueueOnError(TwitterException error) {
        awaitingSession.set(false);

        while (!queue.isEmpty()) {
            final Callback request = queue.poll();
            request.failure(error);
        }
    }

    // not synchronized, only package protected for testing
    Session getValidSession() {
        final Session session = activeSessionProvider.getActiveSession();
        // Only use session if it has auth token.
        if (session != null && session.getAuthToken() != null &&
                !session.getAuthToken().isExpired()) {
            return session;
        } else {
            return null;
        }
    }

    Callback<AppSession> getAppAuthTokenCallback() {
        return new Callback<AppSession>() {
            @Override
            public void success(Result<AppSession> result) {
                flushQueueOnSuccess(twitterCore.getApiClient(result.data));
            }

            @Override
            public void failure(TwitterException exception) {
                flushQueueOnError(exception);
            }
        };
    }
}
