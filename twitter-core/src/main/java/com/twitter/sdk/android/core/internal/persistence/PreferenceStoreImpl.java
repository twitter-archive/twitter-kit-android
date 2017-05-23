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

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceStoreImpl implements PreferenceStore {
    private final SharedPreferences sharedPreferences;

    public PreferenceStoreImpl(Context context, String name) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * @return {@link android.content.SharedPreferences} name spaced to Kit
     */
    @Override
    public SharedPreferences get() {
        return sharedPreferences;
    }

    /**
     * @return {@link android.content.SharedPreferences.Editor} name spaced to Kit
     */
    @Override
    public SharedPreferences.Editor edit() {
        return sharedPreferences.edit();
    }

    /**
     * Apply thread safe saves based on Android API level
     * @param editor
     * @return boolean success
     */
    @Override
    public boolean save(SharedPreferences.Editor editor) {
        editor.apply();
        return true;
    }
}
