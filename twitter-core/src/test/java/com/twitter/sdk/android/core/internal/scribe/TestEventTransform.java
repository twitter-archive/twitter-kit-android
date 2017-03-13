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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

class TestEventTransform implements EventTransform<TestEvent> {

    static final String ID = "id";
    static final String MSG = "msg";

    @Override
    public byte[] toBytes(TestEvent event) throws IOException {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(ID, event.id);
            jsonObject.put(MSG, event.msg);

            return jsonObject.toString().getBytes("UTF-8");
        } catch (JSONException e) {
            throw new IOException(e.getMessage());
        }
    }

    TestEvent fromBytes(byte[] bytes) throws IOException {
        final String bytesString = new String(bytes, "UTF-8");
        try {
            final JSONObject jsonObject = new JSONObject(bytesString);
            final String id = jsonObject.getString(ID);
            final String msg = jsonObject.getString(MSG);
            return new TestEvent(id, msg);
        } catch (JSONException e) {
            throw new IOException(e.getMessage());
        }
    }
}
