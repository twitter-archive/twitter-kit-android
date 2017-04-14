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

package com.twitter.sdk.android.tweetui;

import android.test.AndroidTestCase;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Incorrectly translated dates can cause our process to crash, so here we exercise all of our
 * translations.
 */
public class DateTranslationsTest extends AndroidTestCase {
    private Locale defaultLocale;

    static final SimpleDateFormat RELATIVE_DATE_FORMAT =
            new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        defaultLocale = TestUtils.setLocale(getContext(), Locale.ENGLISH);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestUtils.setLocale(getContext(), defaultLocale);
    }

    public void testDateLocales() {
        final List<Locale> locales = Arrays.asList(new Locale("ar"), new Locale("da"),
                new Locale("de"), new Locale("en", "GB"), new Locale("en", "SS"),
                new Locale("en", "XX"), new Locale("es"), new Locale("fa"), new Locale("fi"),
                new Locale("fr"), new Locale("hi"), new Locale("hu"), new Locale("in"),
                new Locale("it"), new Locale("iw"), new Locale("ja"), new Locale("ko"),
                new Locale("ms"), new Locale("nb"), new Locale("nl"), new Locale("pl"),
                new Locale("pt"), new Locale("ru"), new Locale("sv"), new Locale("th"),
                new Locale("tl"), new Locale("tr"), new Locale("ur"), new Locale("zh", "CN"),
                new Locale("zh", "TW"));
        for (Locale locale: locales) {
            TestUtils.setLocale(getContext(), locale);
            final String shortStr = getContext().getResources()
                    .getString(R.string.tw__relative_date_format_short);
            final String longStr = getContext().getResources()
                    .getString(R.string.tw__relative_date_format_long);
            // incorrect localized format strings would throw an exception
            RELATIVE_DATE_FORMAT.applyPattern(shortStr);
            RELATIVE_DATE_FORMAT.applyPattern(longStr);
        }
    }
}
