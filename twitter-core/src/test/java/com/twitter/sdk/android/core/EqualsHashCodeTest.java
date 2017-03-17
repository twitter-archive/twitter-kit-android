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

package com.twitter.sdk.android.core;


import com.twitter.sdk.android.core.internal.oauth.GuestAuthToken;
import com.twitter.sdk.android.core.internal.oauth.OAuth2Token;
import com.twitter.sdk.android.core.internal.scribe.EventNamespace;
import com.twitter.sdk.android.core.internal.scribe.ScribeEvent;
import com.twitter.sdk.android.core.internal.scribe.ScribeItem;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetBuilder;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class EqualsHashCodeTest {

    @Test
    public void testTwitterAuthToken() throws Exception {
        EqualsVerifier.forClass(TwitterAuthToken.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .withIgnoredFields("createdAt")
                .verify();
    }

    @Test
    public void testOAuth2Token() throws Exception {
        EqualsVerifier.forClass(OAuth2Token.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .withIgnoredFields("createdAt")
                .usingGetClass()
                .verify();
    }

    @Test
    public void testGuestAuthToken() throws Exception {
        EqualsVerifier.forClass(GuestAuthToken.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .withIgnoredFields("createdAt")
                .usingGetClass()
                .verify();
    }

    @Test
    public void testSession() throws Exception {
        EqualsVerifier.forClass(Session.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .usingGetClass()
                .verify();
    }

    @Test
    public void testGuestSession() throws Exception {
        EqualsVerifier.forClass(GuestSession.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .usingGetClass()
                .verify();
    }

    @Test
    public void testTwitterSession() throws Exception {
        EqualsVerifier.forClass(TwitterSession.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .usingGetClass()
                .verify();
    }

    @Test
    public void testEventNamespace() throws Exception {
        EqualsVerifier.forClass(EventNamespace.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .usingGetClass()
                .verify();
    }

    @Test
    public void testScribeEvent() throws Exception {
        EqualsVerifier.forClass(ScribeEvent.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .usingGetClass()
                .verify();
    }

    @Test
    public void testScribeItem() throws Exception {
        EqualsVerifier.forClass(ScribeItem.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .usingGetClass()
                .verify();
    }

    @Test
    public void testMediaDetails() throws Exception {
        EqualsVerifier.forClass(ScribeItem.MediaDetails.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .usingGetClass()
                .verify();
    }

    @Test
    public void testCardEvent() throws Exception {
        EqualsVerifier.forClass(ScribeItem.CardEvent.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .usingGetClass()
                .verify();
    }

    @Test
    public void testTweet() throws Exception {
        final Tweet tweet01 = new TweetBuilder().setId(123456).build();
        final Tweet tweet02 = new TweetBuilder().setId(654321).build();
        EqualsVerifier.forClass(Tweet.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .withPrefabValues(Tweet.class, tweet01, tweet02)
                .withOnlyTheseFields("id")
                .verify();
    }
}
