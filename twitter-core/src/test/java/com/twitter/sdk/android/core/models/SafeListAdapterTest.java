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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class SafeListAdapterTest  {

    private static final String TEST_JSON_LIST_NULL = "{\"list\":null}";
    private static final String TEST_JSON_LIST_EMPTY = "{\"list\":[]}";
    private static final String TEST_JSON_LIST_VALUES = "{\"list\":[32,36]}";

    private static final int TEST_ANY_NUMBER = 100;

    private Gson gson;

    @Before
    public void setUp() throws Exception {

        gson = new GsonBuilder().registerTypeAdapterFactory(new SafeListAdapter()).create();
    }

    @Test
    public void testDeserialization_nullListModel1() {
        final Model1 model = gson.fromJson(TEST_JSON_LIST_NULL, Model1.class);
        assertEquals(Collections.EMPTY_LIST, model.listOfIntegers);
    }

    @Test
    public void testDeserialization_emptyListModel1() {
        final Model1 model = gson.fromJson(TEST_JSON_LIST_EMPTY, Model1.class);
        assertEquals(Collections.EMPTY_LIST, model.listOfIntegers);
    }

    @Test
    public void testDeserialization_validListModel1() {
        final Model1 model = gson.fromJson(TEST_JSON_LIST_VALUES, Model1.class);
        try {
            model.listOfIntegers.add(Integer.valueOf(TEST_ANY_NUMBER));
        } catch (Exception e) {
            assertTrue(e instanceof UnsupportedOperationException);
        }
    }

    @Test
    public void testDeserialization_nullListModel2() {
        final Model2 model = gson.fromJson(TEST_JSON_LIST_NULL, Model2.class);
        assertEquals(Collections.EMPTY_LIST, model.listOfLongs);
    }

    @Test
    public void testDeserialization_emptyList() {
        final Model2 model = gson.fromJson(TEST_JSON_LIST_EMPTY, Model2.class);
        assertEquals(Collections.EMPTY_LIST, model.listOfLongs);
    }

    @Test
    public void testDeserialization_validListModel2() {
        final Model2 model = gson.fromJson(TEST_JSON_LIST_VALUES, Model2.class);
        try {
            model.listOfLongs.add(Long.valueOf(TEST_ANY_NUMBER));
        } catch (Exception e) {
            assertTrue(e instanceof UnsupportedOperationException);
        }
    }

    private static class Model1 {

        @SerializedName("list")
        public final List<Integer> listOfIntegers;

        // Not used in testing, but needed because of final.
        Model1(List<Integer> listOfLongs) {
            this.listOfIntegers = listOfLongs;
        }
    }

    private static class Model2 {

        @SerializedName("list")
        public final List<Long> listOfLongs;

        // Not used in testing, but needed because of final.
        Model2(List<Long> listOfLongs) {
            this.listOfLongs = listOfLongs;
        }
    }
}
