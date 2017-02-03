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

package com.twitter.sdk.android.mopub;

import android.test.AndroidTestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class TwitterStaticNativeAdTest extends AndroidTestCase {

    @Test
    public void testStyleAttrsForDefaultTheme() {
        final TwitterStaticNativeAd nativeAd =
                new TwitterStaticNativeAd(RuntimeEnvironment.application);
        final int expectedContainerBgColor =
                nativeAd.getResources().getColor(R.color.tw__ad_light_container_bg_color);
        final int expectedCardBgColor =
                nativeAd.getResources().getColor(R.color.tw__ad_light_card_bg_color);
        final int expectedPrimaryTextColor =
                nativeAd.getResources().getColor(R.color.tw__ad_light_text_primary_color);
        final int ctaBackgroundColor =
                nativeAd.getResources().getColor(R.color.tw__ad_cta_default);
        final int cardBorderColor =
                nativeAd.getResources().getColor(R.color.tw__ad_light_card_border_color);

        assertEquals(expectedContainerBgColor, nativeAd.containerBackgroundColor);
        assertEquals(expectedCardBgColor, nativeAd.cardBackgroundColor);
        assertEquals(expectedPrimaryTextColor, nativeAd.primaryTextColor);
        assertEquals(ctaBackgroundColor, nativeAd.ctaBackgroundColor);
        assertEquals(cardBorderColor, nativeAd.cardBorderColor);
    }

    @Test
    public void testStyleAttrsForDarkTheme() {
        final TwitterStaticNativeAd nativeAd =
                new TwitterStaticNativeAd(RuntimeEnvironment.application, null,
                        R.style.tw__ad_DarkStyle);

        final int expectedContainerBgColor =
                nativeAd.getResources().getColor(R.color.tw__ad_dark_container_bg_color);
        final int expectedCardBgColor =
                nativeAd.getResources().getColor(R.color.tw__ad_dark_card_bg_color);
        final int expectedPrimaryTextColor =
                nativeAd.getResources().getColor(R.color.tw__ad_dark_text_primary_color);
        final int ctaBackgroundColor =
                nativeAd.getResources().getColor(R.color.tw__ad_cta_default);
        final int cardBorderColor =
                nativeAd.getResources().getColor(R.color.tw__ad_dark_card_border_color);

        assertEquals(expectedContainerBgColor, nativeAd.containerBackgroundColor);
        assertEquals(expectedCardBgColor, nativeAd.cardBackgroundColor);
        assertEquals(expectedPrimaryTextColor, nativeAd.primaryTextColor);
        assertEquals(ctaBackgroundColor, nativeAd.ctaBackgroundColor);
        assertEquals(cardBorderColor, nativeAd.cardBorderColor);
    }
}
