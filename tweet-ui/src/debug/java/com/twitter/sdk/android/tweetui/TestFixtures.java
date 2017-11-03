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

import android.net.Uri;

import com.twitter.sdk.android.core.internal.VineCardUtils;
import com.twitter.sdk.android.core.models.BindingValues;
import com.twitter.sdk.android.core.models.Card;
import com.twitter.sdk.android.core.models.HashtagEntity;
import com.twitter.sdk.android.core.models.ImageValue;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.MentionEntity;
import com.twitter.sdk.android.core.models.SymbolEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetBuilder;
import com.twitter.sdk.android.core.models.TweetEntities;
import com.twitter.sdk.android.core.models.UrlEntity;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.models.UserBuilder;
import com.twitter.sdk.android.core.models.UserValue;
import com.twitter.sdk.android.core.models.VideoInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TestFixtures {
    public static final String CONSUMER_KEY = "consumer_key";
    public static final String CONSUMER_SECRET = "test_secret";
    public static final String EMPTY_STRING = "";
    public static final long TEST_TWEET_ID = 467360037434712064L;

    public static final String TEST_NAME = "Alfred Verbose Named";
    public static final String TEST_RETWEETER_NAME = "Mr Retweets";

    public static final String TEST_SCREEN_NAME = "longestusername";
    public static final String TEST_FORMATTED_SCREEN_NAME = "@longestusername";
    public static final String TEST_RETWEETER_SCREEN_NAME = "retweets";
    public static final String TEST_PROFILE_IMAGE_URL = "https://twimg.twitter.com/image";

    public static final User TEST_USER = new UserBuilder()
            .setId(12L)
            .setName(TEST_NAME)
            .setScreenName(TEST_SCREEN_NAME)
            .setVerified(false)
            .setProfileImageUrlHttps(TEST_PROFILE_IMAGE_URL)
            .build();

    public static final User TEST_RETWEETER = new UserBuilder()
            .setId(13L)
            .setName(TEST_RETWEETER_NAME)
            .setScreenName(TEST_RETWEETER_SCREEN_NAME)
            .setProfileImageUrlHttps(TEST_PROFILE_IMAGE_URL)
            .build();

    public static final User EMPTY_USER = new UserBuilder()
            .setId(-1L)
            .setName("")
            .setScreenName("")
            .setVerified(false)
            .build();

    public static final String TEST_STATUS = "A test Tweet status message.";
    public static final String EMPTY_STATUS = "";

    public static final String TEST_TIMESTAMP = "Wed Jun 06 20:07:10 +0000 2012";
    public static final String TIMESTAMP_RENDERED = "• 06/06/12";
    public static final String EMPTY_TIMESTAMP = "";
    public static final String INVALID_TIMESTAMP_INPUT = "Dec 12, 2013";

    public static final String TEST_PHOTO_URL = "https://pbs.twimg.com/media/someimage.jpg";
    public static final String TEST_URL = "https://twitter.com/";
    public static final String TEST_HASHTAG = "https://twitter.com/search?q=" + Uri.encode("#") + "TwitterForGood";
    public static final String TEST_STATUS_WITH_LINK = "A test Tweet status message. " + TEST_URL;
    public static final String TEST_STATUS_WITH_HASHTAG = "A test Tweet status message. " + TEST_HASHTAG;

    public static final String TEST_CONTENT_DESCRIPTION
            = "Alfred Verbose Named. A test Tweet status message.. Jun 6, 2012.";

    public static final String TEST_PERMALINK_ONE =
            "https://twitter.com/longestusername/status/1?ref_src=twsrc%5Etwitterkit";
    public static final String TEST_PERMALINK_TWO =
            "https://twitter.com/longestusername/status/2?ref_src=twsrc%5Etwitterkit";
    public static final String TEST_PERMALINK_UNKNOWN_USER
            = "https://twitter.com/twitter_unknown/status/1?ref_src=twsrc%5Etwitterkit";

    public static final Tweet TEST_TWEET = createTweet(1L, TEST_USER, TEST_STATUS, TEST_TIMESTAMP,
            false);
    public static final Tweet TEST_TWEET_LINK = createTweet(1L, TEST_USER, TEST_STATUS_WITH_LINK, TEST_TIMESTAMP,
            false);
    public static final Tweet TEST_TWEET_HASHTAG = createTweet(1L, TEST_USER, TEST_STATUS_WITH_HASHTAG, TEST_TIMESTAMP,
            false);
    public static final Tweet TEST_FAVORITED_TWEET = createTweet(1L, TEST_USER, TEST_STATUS,
            TEST_TIMESTAMP, true);
    public static final Tweet TEST_PHOTO_TWEET = createPhotoTweet(2L, TEST_USER, TEST_STATUS,
            TEST_TIMESTAMP, TEST_PHOTO_URL);
    public static final Tweet TEST_MULTIPLE_PHOTO_TWEET = createMultiplePhotosTweet(4, 2L, TEST_USER, TEST_STATUS,
            TEST_TIMESTAMP, TEST_PHOTO_URL);
    // Empty Tweet has empty string name, username, status, and timestamp fields
    public static final Tweet EMPTY_TWEET = createTweet(-1L, EMPTY_USER, EMPTY_STATUS,
            EMPTY_TIMESTAMP, false);
    public static final Tweet INVALID_TIMESTAMP_TWEET = createTweet(3L, EMPTY_USER, EMPTY_STATUS,
            INVALID_TIMESTAMP_INPUT, false);
    public static final Tweet TEST_RETWEET = createRetweet(4L, TEST_RETWEETER, TEST_TWEET);

    public static final List<Long> TWEET_IDS = new ArrayList<>();
    public static final List<Long> DUPLICATE_TWEET_IDS = new ArrayList<>();
    public static final List<Tweet> UNORDERED_TWEETS = new ArrayList<>();
    public static final List<Tweet> ORDERED_TWEETS = new ArrayList<>();
    public static final List<Tweet> ORDERED_DUPLICATE_TWEETS = new ArrayList<>();
    public static final List<Tweet> UNORDERED_DUPLICATE_TWEETS = new ArrayList<>();
    public static final List<Tweet> UNORDERED_MISSING_TWEETS = new ArrayList<>();
    public static final List<Tweet> ORDERED_MISSING_TWEETS = new ArrayList<>();

    public static final BindingValues TEST_BINDING_VALUES =
            new BindingValues(Collections.emptyMap());
    public static final String PLAYER_CARD_VINE = VineCardUtils.VINE_CARD;
    public static final String TEST_VINE_USER_ID = "586671909";

    static {
        TWEET_IDS.addAll(Arrays.asList(100L, 101L, 102L));
        DUPLICATE_TWEET_IDS.addAll(Arrays.asList(100L, 101L, 102L, 101L));
        addTweetsWithId(UNORDERED_TWEETS, Arrays.asList(101L, 102L, 100L));
        addTweetsWithId(ORDERED_TWEETS, Arrays.asList(100L, 101L, 102L));
        addTweetsWithId(ORDERED_DUPLICATE_TWEETS, Arrays.asList(100L, 101L, 102L, 101L));
        addTweetsWithId(UNORDERED_DUPLICATE_TWEETS, Arrays.asList(101L, 102L, 101L, 102L, 100L));
        addTweetsWithId(UNORDERED_MISSING_TWEETS, Arrays.asList(102L, 101L));
        addTweetsWithId(ORDERED_MISSING_TWEETS, Arrays.asList(101L, 102L));
    }

    private TestFixtures() {}

    public static Tweet createTweet(long id) {
        return createTweet(id, null, "", "", false);
    }

    static Tweet createTweet(long id, User user, String text, String timestamp,
            boolean isFavorited) {
        return new TweetBuilder()
                .setId(id)
                .setUser(user)
                .setText(text)
                .setCreatedAt(timestamp)
                .setFavorited(isFavorited)
                .setEntities(new TweetEntities(null, null, null, null, null))
                .build();
    }

    static Tweet createPhotoTweet(long id, User user, String text, String timestamp,
            String photoUrlHttps) {
        final MediaEntity photoEntity = new MediaEntity("", "", "", 0, 0, 0L, null, null,
                photoUrlHttps, createMediaEntitySizes(100, 100), 0L, null, "photo", null, "");
        final ArrayList<MediaEntity> mediaEntities = new ArrayList<>();
        mediaEntities.add(photoEntity);
        final TweetEntities entities = new TweetEntities(null, null, mediaEntities, null, null);
        return new TweetBuilder()
                .setId(id)
                .setUser(user)
                .setText(text)
                .setCreatedAt(timestamp)
                .setEntities(entities)
                .setExtendedEntities(entities)
                .build();
    }

    static Tweet createMultiplePhotosTweet(int count, long id, User user, String text,
                                                  String timestamp, String photoUrlHttps) {
        final ArrayList<MediaEntity> mediaEntities = new ArrayList<>();
        for (int x = 0; x < count; x++) {
            final MediaEntity photoEntity = new MediaEntity("", "", "", 0, 0, 0L, null, null,
                    photoUrlHttps, createMediaEntitySizes(100, 100), 0L, null, "photo", null, "");
            mediaEntities.add(photoEntity);
        }
        final TweetEntities entities = new TweetEntities(null, null, mediaEntities, null, null);
        return new TweetBuilder()
                .setId(id)
                .setUser(user)
                .setText(text)
                .setCreatedAt(timestamp)
                .setEntities(entities)
                .setExtendedEntities(entities)
                .build();
    }

    static Tweet createRetweet(long id, User retweeter, Tweet retweetedStatus) {
        return new TweetBuilder()
                .setId(id)
                .setUser(retweeter)
                .setRetweetedStatus(retweetedStatus)
                .build();
    }

    public static Tweet createTweetWithVineCard(long id, User user, String text, Card card) {
        return new TweetBuilder()
                .setId(id)
                .setCard(card)
                .setText(text)
                .setUser(user)
                .build();
    }

    static void addTweetsWithId(List<Tweet> tweets, List<Long> tweetIds) {
        for (long tweetId : tweetIds) {
            tweets.add(createTweet(tweetId));
        }
    }

    public static UrlEntity newUrlEntity(int start, int end) {
        return new UrlEntity("url", "expandedUrl", "displayUrl", start, end);
    }

    public static MediaEntity newMediaEntity(int start, int end, String type) {
        return newMediaEntity(start, end, type, 0);
    }

    public static MediaEntity newMediaEntity(int start, int end, String type,
                                             int durationInMillis) {
        final VideoInfo videoInfo =
                new VideoInfo(Collections.EMPTY_LIST, durationInMillis, Collections.EMPTY_LIST);
        return new MediaEntity("url", "expandedUrl", "displayUrl", start, end, 0L, "0", "mediaUrl",
                "mediaUrlHttps", null, 0L, "0", type, videoInfo, "");
    }

    public static HashtagEntity newHashtagEntity(String text, int start, int end) {
        return new HashtagEntity(text, start, end);
    }

    public static MentionEntity newMentionEntity(String screenName, int start, int end) {
        return new MentionEntity(100, "100", screenName, screenName, start, end);
    }

    public static SymbolEntity newSymbolEntity(String symbol, int start, int end) {
        return new SymbolEntity(symbol, start, end);
    }

    public static List<Tweet> getTweetList(long count) {
        final List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            // add 1000 just so ids are clearly distinct from position
            tweets.add(TestFixtures.createTweet(1000 + i));
        }
        return tweets;
    }

    public static List<MediaEntity> createMultipleMediaEntitiesWithPhoto(int count, int w, int h) {
        final List<MediaEntity> mediaEntities = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final MediaEntity mediaEntityWithPhoto = createMediaEntityWithPhoto(w, h);
            mediaEntities.add(mediaEntityWithPhoto);
        }
        return mediaEntities;
    }

    public static MediaEntity createMediaEntityWithPhoto(int width, int height) {
        return createMediaEntityWithPhoto(createMediaEntitySizes(width, height));
    }

    public static MediaEntity createMediaEntityWithPhoto(MediaEntity.Sizes sizes) {
        return new MediaEntity(null, null, null, 0, 0, 0L, null, null, null, sizes, 0L, null,
                "photo", null, "");
    }

    public static MediaEntity.Sizes createMediaEntitySizes(int width, int height) {
        final MediaEntity.Size medium = new MediaEntity.Size(width, height, "fit");
        return new MediaEntity.Sizes(null, null, medium, null);
    }

    public static MediaEntity createEntityWithVideo(VideoInfo videoInfo) {
        return new MediaEntity(null, null, null, 0, 0, 0L, null, null, null, null, 0L, null,
                "video", videoInfo, "");
    }

    public static MediaEntity createEntityWithAnimatedGif(VideoInfo videoInfo) {
        return new MediaEntity(null, null, null, 0, 0, 0L, null, null, null, null, 0L, null,
                "animated_gif", videoInfo, "");
    }

    public static VideoInfo createVideoInfoWithVariant(VideoInfo.Variant variant) {
        return new VideoInfo(null, 0, Collections.singletonList(variant));
    }

    public static Card sampleInvalidVineCard() {
        return new Card(TEST_BINDING_VALUES, "invalid");
    }

    public static Card sampleValidVineCard() {
        return new Card(createBindingValuesForCard(), PLAYER_CARD_VINE);
    }

    public static BindingValues createBindingValuesForCard() {
        final UserValue testUser = new UserValue(TEST_VINE_USER_ID);
        final Map<String, Object> testValues = new HashMap<>();
        testValues.put("site", testUser);

        final ImageValue imageValue = new ImageValue(10, 10, TEST_PHOTO_URL, "");
        testValues.put("player_image", imageValue);
        testValues.put("player_stream_url", TEST_URL);

        return new BindingValues(testValues);
    }
}
