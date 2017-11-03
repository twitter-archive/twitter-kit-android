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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class ScribeEventFactoryTest {
    private static long ANY_TIMESTAMP = 123;
    private static String ANY_ADVERTISING_ID = "id";
    private static String ANY_LANGUAGE = "lang";

    private static final String TFW_CLIENT_NAME = "tfw";
    private static final String SDK_CLIENT_NAME = "android";
    private static final String OTHER_CLIENT_NAME = "other";

    @Test
    public void testNewScribeEvent_tfwEvent() {
        final EventNamespace ns
                = new EventNamespace.Builder().setClient(TFW_CLIENT_NAME).builder();
        final ScribeEvent event = ScribeEventFactory.newScribeEvent(ns, ANY_TIMESTAMP,
                ANY_LANGUAGE, ANY_ADVERTISING_ID);
        assertEquals(SyndicationClientEvent.class, event.getClass());
    }

    @Test
    public void testNewScribeEvent_sdkEvent() {
        final EventNamespace ns
                = new EventNamespace.Builder().setClient(SDK_CLIENT_NAME).builder();
        final ScribeEvent event = ScribeEventFactory.newScribeEvent(ns, ANY_TIMESTAMP,
                ANY_LANGUAGE, ANY_ADVERTISING_ID);
        assertEquals(SyndicatedSdkImpressionEvent.class, event.getClass());
    }

    @Test
    public void testNewScribeEvent_otherEvent() {
        final EventNamespace ns
                = new EventNamespace.Builder().setClient(OTHER_CLIENT_NAME).builder();
        final ScribeEvent event = ScribeEventFactory.newScribeEvent(ns, ANY_TIMESTAMP,
                ANY_LANGUAGE, ANY_ADVERTISING_ID);
        assertEquals(SyndicatedSdkImpressionEvent.class, event.getClass());
    }

    @Test
    public void testNewScribeEvent_withEventInfo() {
        final EventNamespace ns
                = new EventNamespace.Builder().setClient(OTHER_CLIENT_NAME).builder();
        final String eventInfo = "any info";
        final ScribeEvent event = ScribeEventFactory.newScribeEvent(ns, eventInfo, ANY_TIMESTAMP,
                ANY_LANGUAGE, ANY_ADVERTISING_ID, Collections.emptyList());
        assertEquals(SyndicatedSdkImpressionEvent.class, event.getClass());
    }
}
