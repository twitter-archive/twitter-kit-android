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

package com.twitter.sdk.android.core.internal.scribe;

import com.twitter.sdk.android.core.models.TweetBuilder;
import com.twitter.sdk.android.core.models.UserBuilder;

import junitx.extensions.EqualsHashCodeTestCase;

public class ScribeItemEqualsHashCodeTest extends EqualsHashCodeTestCase {

    public ScribeItemEqualsHashCodeTest(String name) {
        super(name);
    }

    @Override
    protected Object createInstance() throws Exception {
        return ScribeItem.fromTweet(new TweetBuilder().setId(1234).build());
    }

    @Override
    protected Object createNotEqualInstance() throws Exception {
        return ScribeItem.fromUser(new UserBuilder().setId(1234).build());
    }
}
