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

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.InputStream;

/**
 * A rule for accessing test resources needed by unit tests.
 *
 * Add this as a public member variable annotated with @Rule. For example:
 *
 * @Rule
 * public final TestResources mTestResources = new TestResources();
 */
public class TestResources implements TestRule {
    @Override
    public Statement apply(final Statement base, Description description) {
        return base;
    }

    /**
     * Open a resource under src/resources/test as an InputStream.
     *
     * @throws ResourceNotFound if the resource is not found
     */
    public InputStream getAsStream(String resourceName) {
        final InputStream stream = getClass().getClassLoader().getResourceAsStream(resourceName);
        if (stream == null) {
            throw new ResourceNotFound(resourceName);
        }
        return stream;
    }

    public static class ResourceNotFound extends RuntimeException {
        public final String resourceName;

        public ResourceNotFound(String resourceName) {
            this.resourceName = resourceName;
        }
    }
}
