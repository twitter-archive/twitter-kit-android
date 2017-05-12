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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TwitterConfigTest {
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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(mockContext.getApplicationContext()).thenReturn(mockApplication);
    }

    @Test
    public void testBuilderConstructor_shouldStoreAppContext() {
        final TwitterConfig config = new TwitterConfig.Builder(mockContext).build();
        assertEquals(mockApplication, config.context);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderConstructor_shouldThrowException() {
        new TwitterConfig.Builder(null).build();
    }

    @Test
    public void testBuilder() {
        final TwitterConfig config = new TwitterConfig
                .Builder(mockContext)
                .executorService(mockExecutorService)
                .logger(mockLogger)
                .twitterAuthConfig(mockTwitterAuthConfig)
                .debug(true)
                .build();

        assertEquals(mockApplication, config.context);
        assertEquals(mockExecutorService, config.executorService);
        assertEquals(mockLogger, config.logger);
        assertEquals(mockTwitterAuthConfig, config.twitterAuthConfig);
        assertTrue(config.debug);
    }

    @Test
    public void testBuilder_withDefaults() {
        final TwitterConfig config = new TwitterConfig
                .Builder(mockContext)
                .build();

        assertEquals(mockApplication, config.context);
        assertNull(config.executorService);
        assertNull(config.logger);
        assertNull(config.twitterAuthConfig);
        assertNull(config.debug);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilder_withNullLogger_shouldThrowException() {
        new TwitterConfig.Builder(mockContext).logger(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilder_withNullTwitterAuthConfig_shouldThrowException() {
        new TwitterConfig.Builder(mockContext).twitterAuthConfig(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilder_withNullExecutorService_shouldThrowException() {
        new TwitterConfig.Builder(mockContext).executorService(null).build();
    }
}
