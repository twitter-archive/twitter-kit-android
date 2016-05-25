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
import android.os.Bundle;

import com.twitter.Regex;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;

public class ComposerActivity extends Activity {
    static final String EXTRA_USER_TOKEN = "EXTRA_USER_TOKEN";
    static final String EXTRA_CARD = "EXTRA_CARD";
    static final String EXTRA_THEME = "EXTRA_THEME";
    static final String EXTRA_HASHTAGS = "EXTRA_HASHTAGS";
    private static final int PLACEHOLDER_ID = -1;
    private static final String PLACEHOLDER_SCREEN_NAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        final TwitterAuthToken token = intent.getParcelableExtra(EXTRA_USER_TOKEN);
        final TwitterSession session = new TwitterSession(token, PLACEHOLDER_ID,
                PLACEHOLDER_SCREEN_NAME);
        final Card card = (Card) intent.getSerializableExtra(EXTRA_CARD);
        final String hashtags = intent.getStringExtra(EXTRA_HASHTAGS);
        final int themeResId = intent.getIntExtra(EXTRA_THEME, R.style.ComposerLight);

        setTheme(themeResId);
        setContentView(R.layout.tw__activity_composer);
        final ComposerView composerView = (ComposerView) findViewById(R.id.tw__composer_view);
        new ComposerController(composerView, session, card, hashtags, new FinisherImpl());
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

    public static class Builder {
        private final Context context;
        private TwitterAuthToken token;
        private int themeResId = R.style.ComposerLight;
        private Card card;
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

        public Builder card(Card card) {
            this.card = card;
            return this;
        }

        public Builder hashtags(String... hashtags) {
            if (hashtags == null) return this;

            final StringBuilder sb = new StringBuilder();
            for (String hashtag : hashtags) {
                final boolean isValid = Regex.VALID_HASHTAG.matcher(hashtag).find();
                if (isValid) {
                    sb.append(" ").append(hashtag);
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
            intent.putExtra(EXTRA_CARD, card);
            intent.putExtra(EXTRA_THEME, themeResId);
            intent.putExtra(EXTRA_HASHTAGS, hashtags);
            return intent;
        }
    }
}
