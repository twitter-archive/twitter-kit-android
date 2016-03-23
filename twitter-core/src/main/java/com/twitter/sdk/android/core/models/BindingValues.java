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

package com.twitter.sdk.android.core.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Map of key/value pairs representing card data.
 */
public class BindingValues {
    private final Map<String, Object> bindingValues = new HashMap<>(32);

    void add(String key, Object value) {
        bindingValues.put(key, value);
    }

    /**
     * Returns {@code true} if specified key exists.
     */
    public boolean containsKey(String key) {
        return bindingValues.containsKey(key);
    }

    /**
     * Returns the value for the specified key. Returns {@code null} if key does not exist, or
     * object cannot be cast to return type.
     */
    public <T> T get(String key) {
        try {
            return (T) bindingValues.get(key);
        } catch (ClassCastException ex){
            return null;
        }
    }
}
