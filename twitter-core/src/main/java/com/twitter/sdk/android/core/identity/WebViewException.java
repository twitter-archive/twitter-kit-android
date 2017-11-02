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

package com.twitter.sdk.android.core.identity;

/**
 * Exception thrown when a WebDialog error occurs.
 */
class WebViewException extends Exception {
    private static final long serialVersionUID = -7397331487240298819L;

    private final int errorCode;
    private final String failingUrl;

    WebViewException(int errorCode, String description, String failingUrl) {
        super(description);
        this.errorCode = errorCode;
        this.failingUrl = failingUrl;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return getMessage();
    }

    public String getFailingUrl() {
        return failingUrl;
    }
}
