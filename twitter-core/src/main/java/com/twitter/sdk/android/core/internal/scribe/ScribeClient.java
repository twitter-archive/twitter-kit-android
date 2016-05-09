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

import android.content.Context;

import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.common.SystemCurrentTimeProvider;
import io.fabric.sdk.android.services.events.DisabledEventsStrategy;
import io.fabric.sdk.android.services.events.EventsStrategy;
import io.fabric.sdk.android.services.events.QueueFileEventStorage;
import io.fabric.sdk.android.services.persistence.FileStoreImpl;

import com.twitter.sdk.android.core.GuestSessionProvider;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

import javax.net.ssl.SSLSocketFactory;

/**
 * ScribeClient for executing scribe requests, on a best effort basis. If the application crashes
 * while a scribe request is being processed, the scribe event may be lost.
 *
 * To scribe an event, call {@link ScribeClient#scribe(ScribeEvent, long)}. This records the event
 * to local storage for uploading sometime in the near future. Scribe settings will determine when
 * the actual upload occurs. For example, scribe events may be uploaded every 60 seconds, or
 * every 100 KB of events. Upon successful upload, scribe events are removed from local storage.
 *
 * To scribe an event and have it uploaded immediately, call {@link ScribeClient#scribeAndFlush(ScribeEvent, long)}.
 */
public class ScribeClient {

    private static final String WORKING_FILENAME_BASE = "_se.tap";
    private static final String STORAGE_DIR_BASE = "_se_to_send";

    /**
     * Map of user ids to ScribeHandlers so that scribe events are stored and uploaded on per user
     * id basis.
     */
    final ConcurrentHashMap<Long, ScribeHandler> scribeHandlers;
    /**
     * The kit.
     */
    private final Kit kit;
    /**
     * The scheduled executor service for performing background operations and scheduling uploads.
     */
    private final ScheduledExecutorService executor;
    /**
     * The scribe configuration.
     */
    private final ScribeConfig scribeConfig;
    /**
     * The ScribeEvent.Transform for serializing and de-serializing scribe data.
     */
    private final ScribeEvent.Transform transform;

    private final TwitterAuthConfig authConfig;
    private final SessionManager<? extends Session<TwitterAuthToken>> sessionManager;
    private final GuestSessionProvider guestSessionProvider;
    private final SSLSocketFactory sslSocketFactory;
    private final IdManager idManager;

    /**
     * Constructor.
     *
     * @param kit the kit
     * @param executor scheduled executor service for executing scribe requests on background thread
     * @param scribeConfig the scribe configuration
     * @param transform the scribe event transform for serializing and deserializing scribe events
     * flush of all queued events as long as a network connection is available.
     * @param authConfig the auth configuration
     * @param sessionManager the session manager
     * @param sslSocketFactory the SSL socket factory
     * @param idManager the id manager used to provide the device id
     */
    public ScribeClient(Kit kit, ScheduledExecutorService executor, ScribeConfig scribeConfig,
            ScribeEvent.Transform transform, TwitterAuthConfig authConfig,
            SessionManager<? extends Session<TwitterAuthToken>> sessionManager,
            GuestSessionProvider guestSessionProvider, SSLSocketFactory sslSocketFactory,
            IdManager idManager) {
        this.kit = kit;
        this.executor = executor;
        this.scribeConfig = scribeConfig;
        this.transform = transform;
        this.authConfig = authConfig;
        this.sessionManager = sessionManager;
        this.guestSessionProvider = guestSessionProvider;
        this.sslSocketFactory = sslSocketFactory;
        this.idManager = idManager;

        // Set initial capacity to 2 to handle one logged in user and one logged out user.
        scribeHandlers = new ConcurrentHashMap<>(2);
    }

    /**
     * Scribes an event.
     */
    public boolean scribe(ScribeEvent event, long ownerId) {
        try {
            getScribeHandler(ownerId).scribe(event);
            return true;
        } catch (IOException e) {
            CommonUtils.logControlledError(kit.getContext(), "Failed to scribe event", e);
            return false;
        }
    }

    /**
     * Scribes an event and immediately flushes the event.
     */
    public boolean scribeAndFlush(ScribeEvent event, long ownerId) {
        try {
            getScribeHandler(ownerId).scribeAndFlush(event);
            return true;
        } catch (IOException e) {
            CommonUtils.logControlledError(kit.getContext(), "Failed to scribe event", e);
            return false;
        }
    }

    ScribeHandler getScribeHandler(long ownerId) throws IOException {
        if (!scribeHandlers.containsKey(ownerId)) {
            scribeHandlers.putIfAbsent(ownerId, newScribeHandler(ownerId));
        }
        return scribeHandlers.get(ownerId);
    }

    private ScribeHandler newScribeHandler(long ownerId) throws IOException {
        final Context context = kit.getContext();
        final QueueFileEventStorage storage = new QueueFileEventStorage(context,
                new FileStoreImpl(kit).getFilesDir(), getWorkingFileNameForOwner(ownerId),
                getStorageDirForOwner(ownerId));
        final ScribeFilesManager filesManager = new ScribeFilesManager(context,
                transform, new SystemCurrentTimeProvider(), storage, scribeConfig.maxFilesToKeep);
        return new ScribeHandler(context, getScribeStrategy(ownerId, filesManager), filesManager,
                executor);
    }

    EventsStrategy<ScribeEvent> getScribeStrategy(long ownerId, ScribeFilesManager filesManager) {
        final Context context = kit.getContext();
        if (scribeConfig.isEnabled) {
            CommonUtils.logControlled(context, "Scribe enabled");
            return new EnabledScribeStrategy(context, executor, filesManager, scribeConfig,
                    new ScribeFilesSender(context, scribeConfig, ownerId, authConfig,
                            sessionManager, guestSessionProvider, sslSocketFactory, executor,
                            idManager));
        } else {
            CommonUtils.logControlled(context, "Scribe disabled");
            return new DisabledEventsStrategy<>();
        }
    }

    String getWorkingFileNameForOwner(long ownerId) {
        return ownerId + WORKING_FILENAME_BASE;
    }

    String getStorageDirForOwner(long ownerId) {
        return ownerId + STORAGE_DIR_BASE;
    }
}
