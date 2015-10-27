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

import android.widget.ImageButton;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.Tweet;

import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class TweetActionBarViewTest extends TweetUiTestCase {

    public void testSetOnActionCallback() {
        final TweetActionBarView view = createView();
        final Callback<Tweet> actionCallback = mock(Callback.class);
        view.setOnActionCallback(actionCallback);
        assertEquals(actionCallback, view.actionCallback);
    }

    public void testSetLike() {
        final TweetRepository tweetRepository = TweetUi.getInstance().getTweetRepository();
        final TweetActionBarView view = createView();
        view.setLike(TestFixtures.TEST_TWEET);

        final ArgumentCaptor<LikeTweetAction> likeCaptor
                = ArgumentCaptor.forClass(LikeTweetAction.class);
        verify(view.likeButton).setToggledOn(TestFixtures.TEST_TWEET.favorited);
        verify(view.likeButton).setOnClickListener(likeCaptor.capture());
        final LikeTweetAction likeAction = likeCaptor.getValue();
        assertNotNull(likeAction);
        assertEquals(TestFixtures.TEST_TWEET, likeAction.tweet);
        assertEquals(tweetRepository, likeAction.tweetRepository);
    }

    public void testSetLike_handlesNullTweet() {
        final TweetActionBarView view = createView();
        view.setLike(null);
        verifyZeroInteractions(view.likeButton);
    }

    public void testSetShare() {
        final TweetActionBarView view = createView();
        view.setShare(TestFixtures.TEST_TWEET);

        final ArgumentCaptor<ShareTweetAction> shareCaptor
                = ArgumentCaptor.forClass(ShareTweetAction.class);
        verify(view.shareButton).setOnClickListener(shareCaptor.capture());
        final ShareTweetAction shareAction = shareCaptor.getValue();
        assertNotNull(shareAction);
        assertEquals(TestFixtures.TEST_TWEET, shareAction.tweet);
    }

    public void testSetShare_handlesNullTweet() {
        final TweetActionBarView view = createView();
        view.setShare(null);
        verifyZeroInteractions(view.shareButton);
    }

    private TweetActionBarView createView() {
        final TweetActionBarView view = new TweetActionBarView(getContext());
        view.likeButton = mock(ToggleImageButton.class);
        view.shareButton = mock(ImageButton.class);
        return view;
    }
}
