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

package com.twitter.sdk.android.core.services.params;

import io.fabric.sdk.android.FabricAndroidTestCase;

public class GeocodeTest extends FabricAndroidTestCase {

    public void testToString() {
        final Geocode geocode = new Geocode(1.0, 1.0, 1, Geocode.Distance.MILES);
        assertEquals("1.0,1.0,1mi", geocode.toString());
    }
}
