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

package com.twitter.sdk.android.tweetcomposer;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Media;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetcomposer.internal.CardCreate;
import com.twitter.sdk.android.tweetcomposer.internal.CardData;

import java.io.File;

import io.fabric.sdk.android.Fabric;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TweetUploadService extends IntentService {
    public static final String UPLOAD_SUCCESS
            = "com.twitter.sdk.android.tweetcomposer.UPLOAD_SUCCESS";
    public static final String UPLOAD_FAILURE
            = "com.twitter.sdk.android.tweetcomposer.UPLOAD_FAILURE";
    public static final String EXTRA_TWEET_ID = "EXTRA_TWEET_ID";
    public static final String EXTRA_RETRY_INTENT = "EXTRA_RETRY_INTENT";

    static final String TAG = "TweetUploadService";
    static final String EXTRA_USER_TOKEN = "EXTRA_USER_TOKEN";
    static final String EXTRA_TWEET_TEXT = "EXTRA_TWEET_TEXT";
    static final String EXTRA_TWEET_CARD = "EXTRA_TWEET_CARD";
    private static final int PLACEHOLDER_ID = -1;
    private static final String PLACEHOLDER_SCREEN_NAME = "";
    DependencyProvider dependencyProvider;

    TwitterSession twitterSession;
    String tweetText;
    Card tweetCard;
    Intent intent;

    public TweetUploadService() {
        this(new DependencyProvider());
    }

    // testing purposes
    TweetUploadService(DependencyProvider dependencyProvider) {
        super("TweetUploadService");
        this.dependencyProvider = dependencyProvider;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final TwitterAuthToken token = intent.getParcelableExtra(EXTRA_USER_TOKEN);
        this.intent = intent;
        twitterSession = new TwitterSession(token, PLACEHOLDER_ID, PLACEHOLDER_SCREEN_NAME);
        tweetText = intent.getStringExtra(EXTRA_TWEET_TEXT);
        tweetCard = (Card) intent.getSerializableExtra(EXTRA_TWEET_CARD);

        if (Card.isAppCard(tweetCard)) {
            uploadAppCardTweet(twitterSession, tweetText, tweetCard);
        } else {
            uploadTweet(twitterSession, tweetText);
        }
    }

    void uploadTweet(TwitterSession session, final String text) {
        final ComposerApiClient client = dependencyProvider.getComposerApiClient(session);
        client.getComposerStatusesService().update(text, null).enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                sendSuccessBroadcast(result.data.getId());
                stopSelf();
            }

            @Override
            public void failure(TwitterException exception) {
                fail(exception);
            }
        });
    }

    void uploadAppCardTweet(TwitterSession session, final String text, final Card card) {
        final ComposerApiClient client = dependencyProvider.getComposerApiClient(session);

        final Uri uri = Uri.parse(card.imageUri);
        final String path = FileUtils.getPath(TweetUploadService.this, uri);
        if (path == null) {
            fail(new TwitterException("Uri file path resolved to null"));
            return;
        }
        final File file = new File(path);
        final String mimeType = FileUtils.getMimeType(file);
        final RequestBody media = RequestBody.create(MediaType.parse(mimeType), file);

        client.getMediaService().upload(media, null, null).enqueue(new Callback<Media>() {
            @Override
            public void success(Result<Media> result) {
                final CardData cardData = CardDataFactory.createAppCardData(card,
                        result.data.mediaId, dependencyProvider.getAdvertisingId());
                client.getCardService().create(cardData).enqueue(new Callback<CardCreate>() {
                    @Override
                    public void success(Result<CardCreate> result) {
                        final String cardUri = result.data.cardUri;
                        client.getComposerStatusesService().update(text, cardUri).enqueue(
                                new Callback<Tweet>() {
                                    @Override
                                    public void success(Result<Tweet> result) {
                                        sendSuccessBroadcast(result.data.getId());
                                        stopSelf();
                                    }

                                    @Override
                                    public void failure(TwitterException exception) {
                                        fail(exception);
                                    }
                                });
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        fail(exception);
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                fail(exception);
            }
        });
    }

    void fail(TwitterException e) {
        sendFailureBroadcast(intent);
        Fabric.getLogger().e(TAG, "Post Tweet failed", e);
        stopSelf();
    }

    void sendSuccessBroadcast(long tweetId) {
        final Intent intent = new Intent(UPLOAD_SUCCESS);
        intent.putExtra(EXTRA_TWEET_ID, tweetId);
        intent.setPackage(getApplicationContext().getPackageName());
        sendBroadcast(intent);
    }

    void sendFailureBroadcast(Intent original) {
        final Intent intent = new Intent(UPLOAD_FAILURE);
        intent.putExtra(EXTRA_RETRY_INTENT, original);
        intent.setPackage(getApplicationContext().getPackageName());
        sendBroadcast(intent);
    }

    /*
     * Mockable class that provides ComposerController dependencies.
     */
    static class DependencyProvider {

        ComposerApiClient getComposerApiClient(TwitterSession session) {
            return TweetComposer.getInstance().getApiClient(session);
        }

        String getAdvertisingId() {
            return TweetComposer.getInstance().getAdvertisingId();
        }
    }
}
