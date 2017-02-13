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

package com.twitter.sdk.android.core.internal.persistence;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

public class PreferenceStoreStrategy<T> implements PersistenceStrategy<T> {

    private final PreferenceStore store;
    private final SerializationStrategy<T> serializer;
    private final String key;


    public PreferenceStoreStrategy(PreferenceStore store, SerializationStrategy<T> serializer,
                                   String preferenceKey) {
        this.store = store;
        this.serializer = serializer;
        key = preferenceKey;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void save(T object) {
        store.save(store.edit().putString(key, serializer.serialize(object)));
    }

    @Override
    public T restore() {
        final SharedPreferences store = this.store.get();
        return serializer.deserialize(store.getString(key, null));
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void clear() {
        //TODO create a remove on the PreferenceStore
        store.edit().remove(key).commit();
    }
}
