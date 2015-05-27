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

import java.util.Map;

/**
 * SessionManager for managing sessions.
 */
public interface SessionManager<T extends Session> {

    /**
     * @return the active session, restoring saved session if available
     */
    T getActiveSession();

    /**
     * Sets the active session.
     */
    void setActiveSession(T session);

    /**
     * Clears the active session.
     */
    void clearActiveSession();

    /**
     * @return the session associated with the id.
     */
    T getSession(long id);

    /**
     * Sets the session to associate with the id. If there is no active session, this session also
     * becomes the active session.
     */
    void setSession(long id, T session);

    /**
     * Clears the session associated with the id.
     */
    void clearSession(long id);

    /**
     * @return the session map containing all managed sessions
     */
    Map<Long, T> getSessionMap();
}
