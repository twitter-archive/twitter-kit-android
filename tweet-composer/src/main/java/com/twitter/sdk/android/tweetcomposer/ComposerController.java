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

import android.text.TextUtils;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.TwitterApiConstants;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

import io.fabric.sdk.android.Fabric;

class ComposerController {
    ComposerView composerView;
    TwitterSession session;
    ComposerActivity.Finisher finisher;
    final DependencyProvider dependencyProvider;

    ComposerController(final ComposerView composerView, TwitterSession session,
                       String initialText, ComposerActivity.Finisher finisher) {
        this(composerView, session, initialText, finisher, new DependencyProvider());
    }

    // testing purposes
    ComposerController(final ComposerView composerView, TwitterSession session,
            String initialText, ComposerActivity.Finisher finisher,
            DependencyProvider dependencyProvider) {
        this.composerView = composerView;
        this.session = session;
        this.finisher = finisher;
        this.dependencyProvider = dependencyProvider;

        composerView.setCallbacks(new ComposerCallbacksImpl());
        composerView.setTweetText(initialText);
        composerView.setCursorAtEnd();
        setProfilePhoto();
    }

    void setProfilePhoto() {
        dependencyProvider.getApiClient(session).getAccountService().verifyCredentials(false, true,
            new Callback<User>() {
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
            dependencyProvider.getApiClient(session).getStatusesService().update(text,
                null, null, null, null, null, null, true, null, new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
                        finisher.finish();
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Fabric.getLogger().d(ComposerActivity.TAG, "Post Tweet failed",
                                exception);
                    }
                });
        }

        @Override
        public void onCloseClick() {
            finisher.finish();
        }
    }

    static int tweetTextLength(String text) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        // TODO use the Twitter validator to factor t.co links into counting.
        return text.codePointCount(0, text.length());
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

        TwitterApiClient getApiClient(TwitterSession session) {
            return TwitterCore.getInstance().getApiClient(session);
        }
    }
}
