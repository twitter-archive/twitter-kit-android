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

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TwitterTest {
    private static final String CONSUMER_KEY = "com.twitter.sdk.android.CONSUMER_KEY";
    private static final String CONSUMER_SECRET = "com.twitter.sdk.android.CONSUMER_SECRET";
    private static final String TEST_PACKAGE_NAME = "com.twitter.sdk.android.test";

    @Mock
    Context mockContext;
    @Mock
    Application mockApplication;
    @Mock
    ExecutorService mockExecutorService;
    @Mock
    TwitterAuthConfig mockTwitterAuthConfig;
    @Mock
    Logger mockLogger;
    @Mock
    Resources mockResources;

    @Before
    @SuppressWarnings("ResourceType")
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(mockResources.getIdentifier(CONSUMER_KEY, "string", TEST_PACKAGE_NAME)).thenReturn(1);
        when(mockResources.getIdentifier(CONSUMER_SECRET, "string", TEST_PACKAGE_NAME))
                .thenReturn(2);
        when(mockResources.getString(1)).thenReturn(TestFixtures.KEY);
        when(mockResources.getString(2)).thenReturn(TestFixtures.SECRET);

        when(mockApplication.getResources()).thenReturn(mockResources);
        when(mockApplication.getApplicationInfo()).thenReturn(new ApplicationInfo());
        when(mockApplication.getApplicationContext()).thenReturn(mockApplication);
        when(mockApplication.getPackageName()).thenReturn(TEST_PACKAGE_NAME);

        when(mockContext.getApplicationContext()).thenReturn(mockApplication);
    }

    @After
    public void tearDown() {
        // Reset static instance for next test
        Twitter.instance = null;
    }

    @Test
    public void testInitialize_withConfig() {
        final TwitterConfig config = new TwitterConfig
                .Builder(mockContext)
                .executorService(mockExecutorService)
                .logger(mockLogger)
                .twitterAuthConfig(mockTwitterAuthConfig)
                .isDebug(true)
                .build();

        Twitter.initialize(config);

        assertEquals(mockExecutorService, Twitter.getInstance().getExecutorService());
        assertEquals(mockLogger, Twitter.getLogger());
        assertEquals(mockTwitterAuthConfig, Twitter.getInstance().getTwitterAuthConfig());
        assertNotNull(Twitter.getInstance().getIdManager());
        assertTrue(Twitter.getInstance().isDebug());
        assertTrue(Twitter.getInstance().getContext("") instanceof TwitterContext);
    }

    @Test
    public void testInitialize_withDefaults() {
        Twitter.initialize(mockContext);

        assertNotNull(Twitter.getInstance().getExecutorService());
        assertNotNull(Twitter.getInstance().getIdManager());
        assertEquals(Twitter.DEFAULT_LOGGER, Twitter.getLogger());
        assertFalse(Twitter.getInstance().isDebug());
        assertTrue(Twitter.getInstance().getContext("") instanceof TwitterContext);

        final TwitterAuthConfig authConfig = Twitter.getInstance().getTwitterAuthConfig();
        assertNotNull(authConfig);
        assertEquals(TestFixtures.KEY, authConfig.getConsumerKey());
        assertEquals(TestFixtures.SECRET, authConfig.getConsumerSecret());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetInstance_shouldThrowWhenNotInitialized() {
        Twitter.getInstance();
    }
}
