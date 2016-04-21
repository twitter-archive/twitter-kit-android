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

import android.content.Context;

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import io.fabric.sdk.android.FabricTestUtils;
import io.fabric.sdk.android.KitStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TweetComposerTest {
    private static final String TWITTER_NOT_INIT_ERROR_MSG
            = "Must start Twitter Kit with Fabric.with() first";
    private Context context = RuntimeEnvironment.application;
    private TweetComposer tweetComposer;

    @Before
    public void setUp() throws Exception {
        tweetComposer = new TweetComposer();
    }

    @After
    public void tearDown() throws Exception {
        FabricTestUtils.resetFabric();
    }

    @Test
    public void testGetVersion() {
        final TweetComposer composer = new TweetComposer();
        final String version = BuildConfig.VERSION_NAME + "." + BuildConfig.BUILD_NUMBER;
        assertEquals(version, composer.getVersion());
    }

    public void testGetIdentifier() {
        final TweetComposer composer = new TweetComposer();
        final String identifier = BuildConfig.GROUP + ":" + BuildConfig.ARTIFACT_ID;
        assertEquals(identifier, composer.getIdentifier());
    }

    @Test
    public void testGetInstance_fabricNotInitialized() throws Exception {
        try {
            TweetComposer.getInstance();
            fail("Should fail if Fabric is not initialized");
        } catch (IllegalStateException e) {
            assertEquals("Must Initialize Fabric before using singleton()", e.getMessage());
        }
    }

    @Test
    public void testGetInstance_twitterNotInitialized() throws Exception {
        FabricTestUtils.with(context, new KitStub<Result>());
        try {
            TweetComposer.getInstance();
            fail("Should fail if Fabric is not initialized");
        } catch (IllegalStateException e) {
            assertEquals(TWITTER_NOT_INIT_ERROR_MSG, e.getMessage());
        }
    }

    @Test
    public void testGetApiClient_fabricNotInitialized() throws Exception {
        try {
            tweetComposer.getApiClient(mock(TwitterSession.class));
            fail("Should fail if Twitter is not initialized with Fabric");
        } catch (IllegalStateException e) {
            assertEquals("Must Initialize Fabric before using singleton()", e.getMessage());
        }
    }

    @Test
    public void testGetApiClient_twitterNotInitialized() throws Exception {
        FabricTestUtils.with(context, new KitStub<Result>());
        try {
            tweetComposer.getApiClient(mock(TwitterSession.class));
            fail("Should fail if Twitter is not initialized with Fabric");
        } catch (IllegalStateException e) {
            assertEquals(TWITTER_NOT_INIT_ERROR_MSG, e.getMessage());
        }
    }
}
