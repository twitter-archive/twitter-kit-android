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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.twitter.Regex;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;

public class ComposerActivity extends Activity {
    static final String EXTRA_USER_TOKEN = "EXTRA_USER_TOKEN";
    static final String EXTRA_IMAGE_URI = "EXTRA_IMAGE_URI";
    static final String EXTRA_THEME = "EXTRA_THEME";
    static final String EXTRA_TEXT = "EXTRA_TEXT";
    static final String EXTRA_HASHTAGS = "EXTRA_HASHTAGS";
    private static final int PLACEHOLDER_ID = -1;
    private static final String PLACEHOLDER_SCREEN_NAME = "";
    private ComposerController composerController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        final TwitterAuthToken token = intent.getParcelableExtra(EXTRA_USER_TOKEN);
        final TwitterSession session = new TwitterSession(token, PLACEHOLDER_ID,
                PLACEHOLDER_SCREEN_NAME);
        final Uri imageUri = intent.getParcelableExtra(EXTRA_IMAGE_URI);
        final String text = intent.getStringExtra(EXTRA_TEXT);
        final String hashtags = intent.getStringExtra(EXTRA_HASHTAGS);
        final int themeResId = intent.getIntExtra(EXTRA_THEME, R.style.ComposerLight);

        setTheme(themeResId);
        setContentView(R.layout.tw__activity_composer);
        final ComposerView composerView = findViewById(R.id.tw__composer_view);
        composerController = new ComposerController(composerView, session, imageUri, text, hashtags,
                new FinisherImpl());
    }

    interface Finisher {
        void finish();
    }

    // FinisherImpl allows sub-components to finish the host Activity.
    class FinisherImpl implements Finisher {
        @Override
        public void finish() {
            ComposerActivity.this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        composerController.onClose();
    }

    public static class Builder {
        private final Context context;
        private TwitterAuthToken token;
        private int themeResId = R.style.ComposerLight;
        private Uri imageUri;
        private String text;
        private String hashtags;

        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null");
            }
            this.context = context;
        }

        public Builder session(TwitterSession session) {
            if (session == null) {
                throw new IllegalArgumentException("TwitterSession must not be null");
            }
            final TwitterAuthToken token = session.getAuthToken();
            if (token == null) {
                throw new IllegalArgumentException("TwitterSession token must not be null");
            }
            // session passed via the parcelable auth token
            this.token = token;
            return this;
        }

        public Builder image(Uri imageUri) {
            this.imageUri = imageUri;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder hashtags(String... hashtags) {
            if (hashtags == null) return this;

            final StringBuilder sb = new StringBuilder();
            for (String hashtag : hashtags) {
                if (Regex.VALID_HASHTAG.matcher(hashtag).find()) {
                    if (sb.length() > 0) {
                        sb.append(" ");
                    }
                    sb.append(hashtag);
                }
            }

            this.hashtags = sb.length() == 0 ? null : sb.toString();

            return this;
        }

        public Builder darkTheme() {
            themeResId = R.style.ComposerDark;
            return this;
        }

        public Intent createIntent() {
            if (token == null) {
                throw new IllegalStateException("Must set a TwitterSession");
            }
            final Intent intent = new Intent(context, ComposerActivity.class);
            intent.putExtra(EXTRA_USER_TOKEN, token);
            intent.putExtra(EXTRA_IMAGE_URI, imageUri);
            intent.putExtra(EXTRA_THEME, themeResId);
            intent.putExtra(EXTRA_TEXT, text);
            intent.putExtra(EXTRA_HASHTAGS, hashtags);
            return intent;
        }
    }
}
