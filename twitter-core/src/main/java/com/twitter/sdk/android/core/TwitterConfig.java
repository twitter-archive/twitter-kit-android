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

import java.util.concurrent.ExecutorService;

/**
 * Configurable Twitter options
 */
public class TwitterConfig {
    final Context context;
    final Logger logger;
    final TwitterAuthConfig twitterAuthConfig;
    final ExecutorService executorService;
    final Boolean debug;

    private TwitterConfig(Context context, Logger logger, TwitterAuthConfig twitterAuthConfig,
            ExecutorService executorService, Boolean debug) {
        this.context = context;
        this.logger = logger;
        this.twitterAuthConfig = twitterAuthConfig;
        this.executorService = executorService;
        this.debug = debug;
    }

    /**
     * Builder for creating {@link TwitterConfig} instances.
     * */
    public static class Builder {
        private final Context context;
        private Logger logger;
        private TwitterAuthConfig twitterAuthConfig;
        private ExecutorService executorService;
        private Boolean debug;

        /**
         * Start building a new {@link TwitterConfig} instance.
         */
        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }

            this.context = context.getApplicationContext();
        }

        /**
         * Sets the {@link Logger} to build with.
         */
        public Builder logger(Logger logger) {
            if (logger == null) {
                throw new IllegalArgumentException("Logger must not be null.");
            }

            this.logger = logger;

            return this;
        }

        /**
         * Sets the {@link TwitterAuthConfig} to build with.
         */
        public Builder twitterAuthConfig(TwitterAuthConfig authConfig) {
            if (authConfig == null) {
                throw new IllegalArgumentException("TwitterAuthConfig must not be null.");
            }

            this.twitterAuthConfig = authConfig;

            return this;
        }

        /**
         * Sets the {@link ExecutorService} to build with.
         */
        public Builder executorService(ExecutorService executorService) {
            if (executorService == null) {
                throw new IllegalArgumentException("ExecutorService must not be null.");
            }

            this.executorService = executorService;

            return this;
        }

        /**
         * Enable debug mode
         */
        public Builder debug(boolean debug) {
            this.debug = debug;

            return this;
        }

        /**
         * Build the {@link TwitterConfig} instance
         */
        public TwitterConfig build() {
            return new TwitterConfig(context, logger, twitterAuthConfig, executorService, debug);
        }
    }
}
