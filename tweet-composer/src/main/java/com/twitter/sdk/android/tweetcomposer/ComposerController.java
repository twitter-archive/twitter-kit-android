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

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.twitter.Validator;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import static com.twitter.sdk.android.tweetcomposer.TweetUploadService.TWEET_COMPOSE_CANCEL;

class ComposerController {
    final ComposerView composerView;
    final TwitterSession session;
    final Uri imageUri;
    final ComposerActivity.Finisher finisher;
    final DependencyProvider dependencyProvider;

    ComposerController(final ComposerView composerView, TwitterSession session, Uri imageUri,
            String text, String hashtags, ComposerActivity.Finisher finisher) {
        this(composerView, session, imageUri, text, hashtags, finisher, new DependencyProvider());
    }

    // testing purposes
    ComposerController(final ComposerView composerView, TwitterSession session, Uri imageUri,
            String text, String hashtags, ComposerActivity.Finisher finisher,
            DependencyProvider dependencyProvider) {
        this.composerView = composerView;
        this.session = session;
        this.imageUri = imageUri;
        this.finisher = finisher;
        this.dependencyProvider = dependencyProvider;

        composerView.setCallbacks(new ComposerCallbacksImpl());
        composerView.setTweetText(generateText(text, hashtags));
        setProfilePhoto();
        setImageView(imageUri);
        dependencyProvider.getScribeClient().impression();
    }

    String generateText(String text, String hashtags) {
        final StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(text)) {
            sb.append(text);
        }
        if (!TextUtils.isEmpty(hashtags)) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(hashtags);
        }
        return sb.toString();
    }

    void setProfilePhoto() {
        dependencyProvider.getApiClient(session).getAccountService()
                .verifyCredentials(false, true, false).enqueue(new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        composerView.setProfilePhotoView(result.data);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        // show placeholder background color
                        composerView.setProfilePhotoView(null);
                    }
                });
    }

    void setImageView(Uri imageUri) {
        if (imageUri != null) {
            composerView.setImageView(imageUri);
        }
    }

    interface ComposerCallbacks {
        void onTextChanged(String text);
        void onTweetPost(String text);
        void onCloseClick();
    }

    class ComposerCallbacksImpl implements ComposerCallbacks {

        @Override
        public void onTextChanged(String text) {
            final int charCount = tweetTextLength(text);
            composerView.setCharCount(remainingCharCount(charCount));
            // character count overflow red color
            if (isTweetTextOverflow(charCount)) {
                composerView.setCharCountTextStyle(R.style.tw__ComposerCharCountOverflow);
            } else {
                composerView.setCharCountTextStyle(R.style.tw__ComposerCharCount);
            }
            // Tweet post button enable/disable
            composerView.postTweetEnabled(isPostEnabled(charCount));
        }

        @Override
        public void onTweetPost(String text) {
            dependencyProvider.getScribeClient().click(ScribeConstants.SCRIBE_TWEET_ELEMENT);
            final Intent intent = new Intent(composerView.getContext(), TweetUploadService.class);
            intent.putExtra(TweetUploadService.EXTRA_USER_TOKEN, session.getAuthToken());
            intent.putExtra(TweetUploadService.EXTRA_TWEET_TEXT, text);
            intent.putExtra(TweetUploadService.EXTRA_IMAGE_URI, imageUri);
            composerView.getContext().startService(intent);
            finisher.finish();
        }

        @Override
        public void onCloseClick() {
            onClose();
        }
    }

    void onClose() {
        dependencyProvider.getScribeClient().click(ScribeConstants.SCRIBE_CANCEL_ELEMENT);
        sendCancelBroadcast();
        finisher.finish();
    }

    int tweetTextLength(String text) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }

        return dependencyProvider.getTweetValidator().getTweetLength(text);
    }

    void sendCancelBroadcast() {
        final Intent intent = new Intent(TWEET_COMPOSE_CANCEL);
        intent.setPackage(composerView.getContext().getPackageName());
        composerView.getContext().sendBroadcast(intent);
    }

    static int remainingCharCount(int charCount) {
        return Validator.MAX_TWEET_LENGTH - charCount;
    }

    /*
     * @return true if the Tweet text is a valid length, false otherwise.
     */
    static boolean isPostEnabled(int charCount) {
        return charCount > 0 && charCount <= Validator.MAX_TWEET_LENGTH;
    }

    /*
     * @return true if the Tweet text is too long, false otherwise.
     */
    static boolean isTweetTextOverflow(int charCount) {
        return charCount > Validator.MAX_TWEET_LENGTH;
    }

    /*
     * Mockable class that provides ComposerController dependencies.
     */
    static class DependencyProvider {
        final Validator tweetValidator = new Validator();

        TwitterApiClient getApiClient(TwitterSession session) {
            return TwitterCore.getInstance().getApiClient(session);
        }

        Validator getTweetValidator() {
            return tweetValidator;
        }

        ComposerScribeClient getScribeClient() {
            return new ComposerScribeClientImpl(TweetComposer.getInstance().getScribeClient());
        }
    }
}
