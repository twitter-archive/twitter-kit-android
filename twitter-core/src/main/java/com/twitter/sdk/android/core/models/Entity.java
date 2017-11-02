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

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides metadata and additional contextual information about content posted on Twitter
 */
class Entity implements Serializable {
    private static final int START_INDEX = 0;
    private static final int END_INDEX = 1;

    /**
     * An array of integers indicating the offsets.
     */
    @SerializedName("indices")
    public final List<Integer> indices;

    Entity(int start, int end) {
        final List<Integer> temp = new ArrayList<>(2);
        temp.add(START_INDEX, start);
        temp.add(END_INDEX, end);

        indices = Collections.unmodifiableList(temp);
    }

    public int getStart() {
        return indices.get(START_INDEX);
    }

    public int getEnd() {
        return indices.get(END_INDEX);
    }
}
