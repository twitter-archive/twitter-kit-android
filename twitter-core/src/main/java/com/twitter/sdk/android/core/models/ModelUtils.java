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

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Util class for twitter-core models
 */
public final class ModelUtils {

    private ModelUtils() {}

    public static <T> List<T> getSafeList(List<T> entities) {
        if (entities == null) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(entities);
        }
    }

    public static <K, V> Map<K, V> getSafeMap(Map<K, V> entities) {
        if (entities == null) {
            return Collections.emptyMap();
        } else {
            return Collections.unmodifiableMap(entities);
        }
    }
}
