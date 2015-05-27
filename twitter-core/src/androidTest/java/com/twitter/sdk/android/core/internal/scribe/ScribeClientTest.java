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

package com.twitter.sdk.android.core.internal.scribe;

import io.fabric.sdk.android.FabricAndroidTestCase;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.KitStub;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.events.DisabledEventsStrategy;
import io.fabric.sdk.android.services.events.EventsStrategy;

import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import javax.net.ssl.SSLSocketFactory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ScribeClientTest extends FabricAndroidTestCase {

    private static final long TEST_USER_ID = 12L;
    private static final String ANY_PATH_VERSION = "version";
    private static final String ANY_PATH_TYPE = "type";

    private KitStub kitStub;
    private ScribeClient scribeClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        kitStub = new KitStub();
        kitStub.init(getContext());

        scribeClient = new ScribeClient(kitStub, mock(ScheduledExecutorService.class),
                mock(ScribeConfig.class), mock(ScribeEvent.Transform.class),
                mock(TwitterAuthConfig.class), mock(List.class),
                mock(SSLSocketFactory.class), mock(IdManager.class));
    }

    public void testScribe() {
        final ScribeHandler mockHandler = mock(TestScribeHandler.class);
        final ScribeEvent mockScribeEvent = mock(ScribeEvent.class);

        scribeClient.scribeHandlers.put(ScribeConstants.LOGGED_OUT_USER_ID, mockHandler);
        scribeClient.scribe(mockScribeEvent, ScribeConstants.LOGGED_OUT_USER_ID);

        verify(mockHandler).scribe(mockScribeEvent);
    }

    public void testScribeAndFlush() {
        final ScribeHandler mockHandler = mock(TestScribeHandler.class);
        final ScribeEvent mockScribeEvent = mock(ScribeEvent.class);

        scribeClient.scribeHandlers.put(ScribeConstants.LOGGED_OUT_USER_ID, mockHandler);
        scribeClient.scribeAndFlush(mockScribeEvent, ScribeConstants.LOGGED_OUT_USER_ID);

        verify(mockHandler).scribeAndFlush(mockScribeEvent);
    }

    public void testGetScribeHandler() throws IOException {
        final ScribeHandler loggedOutScribeHandler
                = scribeClient.getScribeHandler(ScribeConstants.LOGGED_OUT_USER_ID);
        assertNotNull(loggedOutScribeHandler);
        // Verify that asking for a scribe handler for the same owner id results in the same one
        // being returned.
        assertEquals(loggedOutScribeHandler,
                scribeClient.getScribeHandler(ScribeConstants.LOGGED_OUT_USER_ID));

        // Verify that different scribe handlers are returned for the different user ids.
        final ScribeHandler testUserScribeHandler
                = scribeClient.getScribeHandler(TEST_USER_ID);
        assertNotNull(testUserScribeHandler);
        assertNotSame(loggedOutScribeHandler, testUserScribeHandler);
    }

    public void testGetScribeStrategy_scribeEnabled() {
        final ScribeConfig config = new ScribeConfig(true, ScribeConfig.BASE_URL, ANY_PATH_VERSION,
                ANY_PATH_TYPE, null, null, ScribeConfig.DEFAULT_MAX_FILES_TO_KEEP,
                ScribeConfig.DEFAULT_SEND_INTERVAL_SECONDS);
        scribeClient = new ScribeClient(mock(Kit.class), mock(ScheduledExecutorService.class),
                config, mock(ScribeEvent.Transform.class), mock(TwitterAuthConfig.class),
                mock(List.class), mock(SSLSocketFactory.class), mock(IdManager.class));

        final EventsStrategy<ScribeEvent> scribeStrategy
                = scribeClient.getScribeStrategy(ScribeConstants.LOGGED_OUT_USER_ID, null);
        assertTrue(scribeStrategy instanceof EnabledScribeStrategy);
    }

    public void testGetScribeStrategy_scribeDisabled() {
        final ScribeConfig config = new ScribeConfig(false, ScribeConfig.BASE_URL, ANY_PATH_VERSION,
                ANY_PATH_TYPE, null, null, ScribeConfig.DEFAULT_MAX_FILES_TO_KEEP,
                ScribeConfig.DEFAULT_SEND_INTERVAL_SECONDS);
        scribeClient = new ScribeClient(mock(Kit.class), mock(ScheduledExecutorService.class),
                config, mock(ScribeEvent.Transform.class), mock(TwitterAuthConfig.class),
                mock(List.class), mock(SSLSocketFactory.class), mock(IdManager.class));

        final EventsStrategy<ScribeEvent> scribeStrategy
                = scribeClient.getScribeStrategy(ScribeConstants.LOGGED_OUT_USER_ID, null);
        assertTrue(scribeStrategy instanceof DisabledEventsStrategy);
    }

    public void testGetWorkingFileNameForOwner() {
        assertTrue(scribeClient.getWorkingFileNameForOwner(ScribeConstants.LOGGED_OUT_USER_ID)
                .startsWith(Long.toString(ScribeConstants.LOGGED_OUT_USER_ID)));

        assertTrue(scribeClient.getWorkingFileNameForOwner(TEST_USER_ID)
                .startsWith(Long.toString(TEST_USER_ID)));
    }

    public void testGetStorageDirForOwner() {
        assertTrue(scribeClient.getStorageDirForOwner(ScribeConstants.LOGGED_OUT_USER_ID)
                .startsWith(Long.toString(ScribeConstants.LOGGED_OUT_USER_ID)));

        assertTrue(scribeClient.getStorageDirForOwner(TEST_USER_ID)
                .startsWith(Long.toString(TEST_USER_ID)));
    }
}
