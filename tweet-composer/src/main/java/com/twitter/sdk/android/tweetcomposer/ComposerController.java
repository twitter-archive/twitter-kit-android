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
import android.widget.Toast;

import com.twitter.sdk.android.core.internal.TwitterApiConstants;

class ComposerController {
    ComposerView composerView;

    ComposerController(ComposerView composerView) {
        this.composerView = composerView;
        composerView.setCallbacks(new ComposerCallbacksImpl());
        composerView.setTweetText("");
    }

    public interface ComposerCallbacks {
        void onTextChanged(String text);
        void onTweetPost();
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
        public void onTweetPost() {
            Toast.makeText(composerView.getContext(), "Mock post Tweet", Toast.LENGTH_SHORT).show();
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
}
