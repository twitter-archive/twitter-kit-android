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

import android.content.Context;

import io.fabric.sdk.android.services.network.PinningInfoProvider;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;

class TwitterPinningInfoProvider implements PinningInfoProvider {

    private static final String[] PINS;

    static {
        final HashMap<String, String> pinMap = new HashMap<>();
        pinMap.put("VERISIGN_CLASS1", "2343d148a255899b947d461a797ec04cfed170b7");
        pinMap.put("VERISIGN_CLASS1_G3", "5519b278acb281d7eda7abc18399c3bb690424b5");
        pinMap.put("VERISIGN_CLASS2_G2", "1237ba4517eead2926fdc1cdfebeedf2ded9145c");
        pinMap.put("VERISIGN_CLASS2_G3", "5abec575dcaef3b08e271943fc7f250c3df661e3");
        pinMap.put("VERISIGN_CLASS3_G2", "1a21b4952b6293ce18b365ec9c0e934cb381e6d4");
        pinMap.put("VERISIGN_CLASS3_G3", "22f19e2ec6eaccfc5d2346f4c2e8f6c554dd5e07");
        pinMap.put("VERISIGN_CLASS3_G4", "ed663135d31bd4eca614c429e319069f94c12650");
        pinMap.put("VERISIGN_CLASS3_G5", "b181081a19a4c0941ffae89528c124c99b34acc7");
        pinMap.put("VERISIGN_CLASS4_G3", "3c03436868951cf3692ab8b426daba8fe922e5bd");
        pinMap.put("VERISIGN_UNIVERSAL", "bbc23e290bb328771dad3ea24dbdf423bd06b03d");
        pinMap.put("GEOTRUST_GLOBAL", "c07a98688d89fbab05640c117daa7d65b8cacc4e");
        pinMap.put("GEOTRUST_GLOBAL2", "713836f2023153472b6eba6546a9101558200509");
        pinMap.put("GEOTRUST_PRIMARY", "b01989e7effb4aafcb148f58463976224150e1ba");
        pinMap.put("GEOTRUST_PRIMARY_G2", "bdbea71bab7157f9e475d954d2b727801a822682");
        pinMap.put("GEOTRUST_PRIMARY_G3", "9ca98d00af740ddd8180d21345a58b8f2e9438d6");
        pinMap.put("GEOTRUST_UNIVERAL", "87e85b6353c623a3128cb0ffbbf551fe59800e22");
        pinMap.put("GEOTRUST_UNIVERSAL2", "5e4f538685dd4f9eca5fdc0d456f7d51b1dc9b7b");
        pinMap.put("DIGICERT_GLOBAL_ROOT", "d52e13c1abe349dae8b49594ef7c3843606466bd");
        pinMap.put("DIGICERT_EV_ROOT", "83317e62854253d6d7783190ec919056e991b9e3");
        pinMap.put("DIGICERT_ASSUREDID_ROOT", "68330e61358521592983a3c8d2d2e1406e7ab3c1");
        pinMap.put("TWITTER1", "56fef3c2147d4ed38837fdbd3052387201e5778d");

        final Collection<String> values = pinMap.values();
        PINS = values.toArray(new String[values.size()]);
    }

    private final Context appContext;

    public TwitterPinningInfoProvider(Context context) {
        appContext = context.getApplicationContext();
    }

    @Override
    public InputStream getKeyStoreStream() {
        return appContext.getResources().openRawResource(R.raw.tw__cacerts);
    }

    @Override
    public String getKeyStorePassword() {
        // keystore required to have a password, but these certificates are public
        return "changeit";
    }

    @Override
    public String[] getPins() {
        return PINS;
    }

    @Override
    public long getPinCreationTimeInMillis() {
        return BuildConfig.BUILD_TIME;
    }
}
