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

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;

import com.twitter.sdk.android.core.IntentUtils;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.models.Tweet;

class ShareTweetAction implements View.OnClickListener {
    final Tweet tweet;
    final TweetUi tweetUi;
    final TweetScribeClient tweetScribeClient;

    ShareTweetAction(Tweet tweet, TweetUi tweetUi) {
        this(tweet, tweetUi, new TweetScribeClientImpl(tweetUi));
    }

    // For testing only
    ShareTweetAction(Tweet tweet, TweetUi tweetUi, TweetScribeClient tweetScribeClient) {
        super();
        this.tweet = tweet;
        this.tweetUi = tweetUi;
        this.tweetScribeClient = tweetScribeClient;
    }

    @Override
    public void onClick(View v) {
        onClick(v.getContext(), v.getResources());
    }

    void scribeShareAction() {
        tweetScribeClient.share(tweet);
    }

    void onClick(Context context, Resources resources) {
        if (tweet == null || tweet.user == null) return;

        scribeShareAction();

        final String shareSubject = getShareSubject(resources);
        final String shareContent = getShareContent(resources);
        final Intent shareIntent = getShareIntent(shareSubject, shareContent);
        final String shareText = resources.getString(R.string.tw__share_tweet);
        final Intent chooser = Intent.createChooser(shareIntent, shareText);
        launchShareIntent(chooser, context);
    }

    String getShareContent(Resources resources) {
        return resources.getString(R.string.tw__share_content_format,
                tweet.user.screenName, Long.toString(tweet.id));
    }

    String getShareSubject(Resources resources) {
        return resources.getString(R.string.tw__share_subject_format, tweet.user.name,
                tweet.user.screenName);
    }

    void launchShareIntent(Intent chooser, Context context) {
        if (!IntentUtils.safeStartActivity(context, chooser)) {
            Twitter.getLogger()
                    .e(TweetUi.LOGTAG, "Activity cannot be found to handle share intent");
        }
    }

    Intent getShareIntent(String subject, String content) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.setType("text/plain");
        return intent;
    }
}
