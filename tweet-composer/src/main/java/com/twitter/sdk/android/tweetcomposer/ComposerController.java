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
import android.text.TextUtils;
import android.view.View;

import com.twitter.Validator;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.TwitterApiConstants;
import com.twitter.sdk.android.core.models.User;

class ComposerController {
    final ComposerView composerView;
    final TwitterSession session;
    final Card card;
    final ComposerActivity.Finisher finisher;
    final DependencyProvider dependencyProvider;

    ComposerController(final ComposerView composerView, TwitterSession session, Card card,
                       String hashtags, ComposerActivity.Finisher finisher) {
        this(composerView, session, card, hashtags, finisher, new DependencyProvider());
    }

    // testing purposes
    ComposerController(final ComposerView composerView, TwitterSession session, Card card,
                       String hashtags, ComposerActivity.Finisher finisher,
            DependencyProvider dependencyProvider) {
        this.composerView = composerView;
        this.session = session;
        this.card = card;
        this.finisher = finisher;
        this.dependencyProvider = dependencyProvider;

        composerView.setCallbacks(new ComposerCallbacksImpl());
        composerView.setTweetText(hashtags);
        setProfilePhoto();
        setCardView(card);
        dependencyProvider.getScribeClient().impression(card);
    }

    void setProfilePhoto() {
        dependencyProvider.getApiClient(session).getAccountService().verifyCredentials(false, true)
                .enqueue(new Callback<User>() {
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

    void setCardView(Card card) {
        if (card != null) {
            final CardViewFactory cardViewFactory = dependencyProvider.getCardViewFactory();
            final View view = cardViewFactory.createCard(composerView.getContext(), card);
            composerView.setCardView(view);
        }
    }

    public interface ComposerCallbacks {
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
            dependencyProvider.getScribeClient().click(card, ScribeConstants.SCRIBE_TWEET_ELEMENT);
            final Intent intent = new Intent(composerView.getContext(), TweetUploadService.class);
            intent.putExtra(TweetUploadService.EXTRA_USER_TOKEN, session.getAuthToken());
            intent.putExtra(TweetUploadService.EXTRA_TWEET_TEXT, text);
            intent.putExtra(TweetUploadService.EXTRA_TWEET_CARD, card);
            composerView.getContext().startService(intent);
            finisher.finish();
        }

        @Override
        public void onCloseClick() {
            dependencyProvider.getScribeClient().click(card, ScribeConstants.SCRIBE_CANCEL_ELEMENT);
            finisher.finish();
        }
    }

    int tweetTextLength(String text) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }

        return dependencyProvider.getTweetValidator().getTweetLength(text);
    }

    static int remainingCharCount(int charCount) {
        return TwitterApiConstants.MAX_TWEET_CHARS - charCount;
    }

    /*
     * @return true if the Tweet text is a valid length, false otherwise.
     */
    static boolean isPostEnabled(int charCount) {
        return charCount > 0 && charCount <= TwitterApiConstants.MAX_TWEET_CHARS;
    }

    /*
     * @return true if the Tweet text is too long, false otherwise.
     */
    static boolean isTweetTextOverflow(int charCount) {
        return charCount > TwitterApiConstants.MAX_TWEET_CHARS;
    }

    /*
     * Mockable class that provides ComposerController dependencies.
     */
    static class DependencyProvider {
        final CardViewFactory cardViewFactory = new CardViewFactory();
        final Validator tweetValidator = new Validator();

        TwitterApiClient getApiClient(TwitterSession session) {
            return TwitterCore.getInstance().getApiClient(session);
        }

        CardViewFactory getCardViewFactory() {
            return cardViewFactory;
        }

        Validator getTweetValidator() {
            return tweetValidator;
        }

        ComposerScribeClient getScribeClient() {
            return new ComposerScribeClientImpl(TweetComposer.getInstance().getScribeClient());
        }
    }
}
