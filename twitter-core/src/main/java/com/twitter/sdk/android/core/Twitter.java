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

import android.content.Context;

import com.twitter.sdk.android.core.internal.CommonUtils;
import com.twitter.sdk.android.core.internal.IdManager;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *  The {@link Twitter} class stores common configuration and state for TwitterKit SDK.
 */
public class Twitter {
    public static final String TAG = "Twitter";
    private static final String CONSUMER_KEY = "com.twitter.sdk.android.CONSUMER_KEY";
    private static final String CONSUMER_SECRET = "com.twitter.sdk.android.CONSUMER_SECRET";
    private static final String NOT_INITIALIZED_MESSAGE = "Must initialize Twitter before using getInstance()";
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 1L;
    static final Logger DEFAULT_LOGGER = new DefaultLogger();

    static volatile Twitter instance;

    private final Context context;
    private final IdManager idManager;
    private final ExecutorService executorService;
    private final TwitterAuthConfig twitterAuthConfig;
    private final Logger logger;
    private final boolean isDebug;

    private Twitter(TwitterConfig config) {
        this.context = config.context;
        this.idManager = new IdManager(context);

        if (config.twitterAuthConfig == null) {
            final String key = CommonUtils.getStringResourceValue(context, CONSUMER_KEY, "");
            final String secret = CommonUtils.getStringResourceValue(context, CONSUMER_SECRET, "");
            twitterAuthConfig = new TwitterAuthConfig(key, secret);
        } else {
            twitterAuthConfig = config.twitterAuthConfig;
        }

        if (config.executorService == null) {
            executorService = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        } else {
            executorService = config.executorService;
        }

        if (config.logger == null) {
            logger = DEFAULT_LOGGER;
        } else {
            logger = config.logger;
        }

        if (config.isDebug == null) {
            isDebug = false;
        } else {
            isDebug = config.isDebug;
        }
    }

    /**
     * Entry point to initialize the TwitterKit SDK.
     * <p>
     * Only the Application context is retained.
     * See http://developer.android.com/resources/articles/avoiding-memory-leaks.html
     * <p>
     * Should be called from {@code OnCreate()} method of custom {@code Application} class.
     * <pre>
     * public class SampleApplication extends Application {
     *   &#64;Override
     *   public void onCreate() {
     *     Twitter.initialize(this);
     *   }
     * }
     * </pre>
     *
     * @param context Android context used for initialization
     */
    public static void initialize(Context context) {
        final TwitterConfig config = new TwitterConfig
                .Builder(context)
                .build();
        createTwitter(config);
    }

    /**
     * Entry point to initialize the TwitterKit SDK.
     * <p>
     * Only the Application context is retained.
     * See http://developer.android.com/resources/articles/avoiding-memory-leaks.html
     * <p>
     * Should be called from {@code OnCreate()} method of custom {@code Application} class.
     * <pre>
     * public class SampleApplication extends Application {
     *   &#64;Override
     *   public void onCreate() {
     *     final TwitterConfig config = new TwitterConfig.Builder(this).build();
     *     Twitter.initialize(config);
     *   }
     * }
     * </pre>
     *
     * @param config {@link TwitterConfig} user for initialization
     */
    public static void initialize(TwitterConfig config) {
        createTwitter(config);
    }

    synchronized static Twitter createTwitter(TwitterConfig config) {
        if (instance == null) {
            instance = new Twitter(config);
            return instance;
        }

        return instance;
    }

    static void checkInitialized() {
        if (instance == null) {
            throw new IllegalStateException(NOT_INITIALIZED_MESSAGE);
        }
    }

    /**
     * @return Single instance of the {@link Twitter}.
     */
    public static Twitter getInstance() {
        checkInitialized();
        return instance;
    }

    /**
     * @param component the component name
     * @return A {@link TwitterContext} for specified component.
     */
    public Context getContext(String component) {
        return new TwitterContext(context, component, ".Fabric" + File.separator + component);
    }

    /**
     * @return the global IdManager.
     */
    public IdManager getIdManager() {
        return idManager;
    }

    /**
     * @return the global {@link TwitterAuthConfig}.
     */
    public TwitterAuthConfig getTwitterAuthConfig() {
        return twitterAuthConfig;
    }

    /**
     * @return the global {@link ExecutorService}.
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * @return the global value for debug mode.
     */
    public boolean isDebug() {
        return isDebug;
    }

    /**
     * @return the global {@link Logger}.
     */
    public static Logger getLogger() {
        if (instance == null) {
            return DEFAULT_LOGGER;
        }

        return instance.logger;
    }
}
