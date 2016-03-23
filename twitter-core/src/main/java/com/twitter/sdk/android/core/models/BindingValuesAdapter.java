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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BindingValuesAdapter implements JsonSerializer<BindingValues>,
        JsonDeserializer<BindingValues> {
    private static final String STRING_TYPE = "STRING";
    private static final String IMAGE_TYPE = "IMAGE";
    private static final String USER_TYPE = "USER";
    private static final String BOOLEAN_TYPE = "BOOLEAN";

    private static final String TYPE_MEMBER = "type";
    private static final String TYPE_VALUE_MEMBER = "string_value";
    private static final String IMAGE_VALUE_MEMBER = "image_value";
    private static final String USER_VALUE_MEMBER = "user_value";
    private static final String BOOLEAN_MEMBER = "boolean_value";

    @Override
    public JsonElement serialize(BindingValues src, Type typeOfSrc,
            JsonSerializationContext context) {
        return null;
    }

    @Override
    public BindingValues deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            return new BindingValues();
        }

        final JsonObject obj = json.getAsJsonObject();
        final Set<Map.Entry<String, JsonElement>> members = obj.entrySet();

        final Map<String, Object> bindingHash = new HashMap<>(32);
        for (Map.Entry<String, JsonElement> member : members) {
            final String key = member.getKey();
            final JsonObject memberObj = member.getValue().getAsJsonObject();
            final Object value = getValue(memberObj, context);

            bindingHash.put(key, value);
        }

        return new BindingValues(bindingHash);
    }

    Object getValue(JsonObject obj, JsonDeserializationContext context) {
        final JsonElement typeObj = obj.get(TYPE_MEMBER);
        if (typeObj == null || !typeObj.isJsonPrimitive()) {
            return null;
        }

        switch (typeObj.getAsString()) {
            case STRING_TYPE:
                return context.deserialize(obj.get(TYPE_VALUE_MEMBER), String.class);
            case IMAGE_TYPE:
                return context.deserialize(obj.get(IMAGE_VALUE_MEMBER), ImageValue.class);
            case USER_TYPE:
                return context.deserialize(obj.get(USER_VALUE_MEMBER), UserValue.class);
            case BOOLEAN_TYPE:
                return context.deserialize(obj.get(BOOLEAN_MEMBER), Boolean.class);
            default:
                return null;
        }
    }
}
