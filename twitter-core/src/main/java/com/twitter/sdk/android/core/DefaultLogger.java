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

import android.util.Log;

/**
 * Default logger that logs to android.util.Log.
 */
public class DefaultLogger implements Logger {
    private int logLevel;

    public DefaultLogger(int logLevel) {
        this.logLevel = logLevel;
    }

    public DefaultLogger() {
        this.logLevel = Log.INFO;
    }

    @Override
    public boolean isLoggable(String tag, int level) {
        return logLevel <= level;
    }

    @Override
    public int getLogLevel() {
        return logLevel;
    }

    @Override
    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public void d(String tag, String text, Throwable throwable) {
        if (isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, text, throwable);
        }
    }

    @Override
    public void v(String tag, String text, Throwable throwable) {
        if (isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, text, throwable);
        }
    }

    @Override
    public void i(String tag, String text, Throwable throwable) {
        if (isLoggable(tag, Log.INFO)) {
            Log.i(tag, text, throwable);
        }
    }

    @Override
    public void w(String tag, String text, Throwable throwable) {
        if (isLoggable(tag, Log.WARN)) {
            Log.w(tag, text, throwable);
        }
    }

    @Override
    public void e(String tag, String text, Throwable throwable) {
        if (isLoggable(tag, Log.ERROR)) {
            Log.e(tag, text, throwable);
        }
    }

    @Override
    public void d(String tag, String text) {
        d(tag, text, null);
    }

    @Override
    public void v(String tag, String text) {
        v(tag, text, null);
    }

    @Override
    public void i(String tag, String text) {
        i(tag, text, null);
    }

    @Override
    public void w(String tag, String text) {
        w(tag, text, null);
    }

    @Override
    public void e(String tag, String text) {
        e(tag, text, null);
    }

    @Override
    public void log(int priority, String tag, String msg) {
        log(priority, tag, msg, false);
    }

    @Override
    public void log(int priority, String tag, String msg, boolean forceLog) {
        if (forceLog || isLoggable(tag, priority)) {
            Log.println(priority, tag, msg);
        }
    }
}
