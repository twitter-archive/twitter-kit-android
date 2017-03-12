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

/**
 * Interface to support custom logger.
 */
public interface Logger {

    boolean isLoggable(String tag, int level);
    int getLogLevel();
    void setLogLevel(int logLevel);

    void d(String tag, String text, Throwable throwable);
    void v(String tag, String text, Throwable throwable);
    void i(String tag, String text, Throwable throwable);
    void w(String tag, String text, Throwable throwable);
    void e(String tag, String text, Throwable throwable);

    void d(String tag, String text);
    void v(String tag, String text);
    void i(String tag, String text);
    void w(String tag, String text);
    void e(String tag, String text);

    void log(int priority, String tag, String msg);
    void log(int priority, String tag, String msg, boolean forceLog);
}
