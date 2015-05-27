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

package com.twitter.sdk.android.core.identity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import io.fabric.sdk.android.Fabric;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * Single Sign On implementation of an {@link AuthHandler}
 */
class SSOAuthHandler extends AuthHandler {

    /**
     * Package name of the Twitter for Android application.
     */
    static final String PACKAGE_NAME = "com.twitter.android";

    /**
     * Class name of the Activity responsible for Single sign-on flow.
     */
    static final String SSO_CLASS_NAME = PACKAGE_NAME + ".SingleSignOnActivity";

    static final ComponentName SSO_ACTIVITY = new ComponentName(PACKAGE_NAME, SSO_CLASS_NAME);

    /**
     * The Twitter for Android application signature.
     */
    static final String APP_SIGNATURE = "3082025d308201c6a00302010202044bd76cce300d06092" +
            "a864886f70d01010505003073310b3009060355040613025553310b3009060355040813024341311630" +
            "140603550407130d53616e204672616e636973636f31163014060355040a130d547769747465722c204" +
            "96e632e310f300d060355040b13064d6f62696c65311630140603550403130d4c656c616e6420526563" +
            "686973301e170d3130303432373233303133345a170d3438303832353233303133345a3073310b30090" +
            "60355040613025553310b3009060355040813024341311630140603550407130d53616e204672616e63" +
            "6973636f31163014060355040a130d547769747465722c20496e632e310f300d060355040b13064d6f6" +
            "2696c65311630140603550403130d4c656c616e642052656368697330819f300d06092a864886f70d01" +
            "0101050003818d003081890281810086233c2e51c62232d49cc932e470713d63a6a1106b38f9e442e01" +
            "bc79ca4f95c72b2cb3f1369ef7dea6036bff7c4b2828cb3787e7657ad83986751ced5b131fcc6f413ef" +
            "b7334e32ed9787f9e9a249ae108fa66009ac7a7932c25d37e1e07d4f9f66aa494c270dbac87d261c966" +
            "8d321c2fba4ef2800e46671a597ff2eac5d7f0203010001300d06092a864886f70d0101050500038181" +
            "003e1f01cb6ea8be8d2cecef5cd2a64c97ba8728aa5f08f8275d00508d64d139b6a72c5716b40a040df" +
            "0eeeda04de9361107e123ee8d3dc05e70c8a355f46dbadf1235443b0b214c57211afd4edd147451c443" +
            "d49498d2a7ff27e45a99c39b9e47429a1dae843ba233bf8ca81296dbe1dc5c5434514d995b027924680" +
            "9392a219b";

    private static final String EXTRA_CONSUMER_KEY = "ck";
    private static final String EXTRA_CONSUMER_SECRET = "cs";

    public SSOAuthHandler(TwitterAuthConfig authConfig, Callback<TwitterSession> callback,
            int requestCode) {
        super(authConfig, callback, requestCode);
    }

    @Override
    public boolean authorize(Activity activity) {
        return startAuthActivityForResult(activity);
    }


    private boolean startAuthActivityForResult(Activity activity) {
        final PackageManager pm = activity.getPackageManager();
        if (!checkAppSignature(pm)) {
            Fabric.getLogger().e(TwitterCore.TAG, "SSO app signature check failed", null);
            return false;
        }

        try {
            final ActivityInfo activityInfo = pm.getActivityInfo(SSO_ACTIVITY, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Fabric.getLogger().e(TwitterCore.TAG, "SSO auth activity not found", null);
            return false;
        }

        final TwitterAuthConfig authConfig = getAuthConfig();
        final Intent intent = new Intent().setComponent(SSO_ACTIVITY);
        intent.putExtra(EXTRA_CONSUMER_KEY, authConfig.getConsumerKey())
                .putExtra(EXTRA_CONSUMER_SECRET, authConfig.getConsumerSecret());

        try {
            activity.startActivityForResult(intent, requestCode);
            return true;
        } catch (Exception e) {
            Fabric.getLogger().e(TwitterCore.TAG, "SSO exception occurred", e);
            return false;
        }
    }

    /**
     * Returns true if Twitter for Android is installed.
     *
     * @param context a context
     * @return true if Twitter is installed; otherwise, false.
     */
    public static boolean isAvailable(Context context) {
        return checkAppSignature(context.getPackageManager());
    }

    private static boolean checkAppSignature(PackageManager pm) {
        PackageInfo p;
        try {
            p = pm.getPackageInfo(PACKAGE_NAME, PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

        for (Signature s : p.signatures) {
            if (APP_SIGNATURE.equals(s.toCharsString())) {
                return true;
            }
        }
        return false;
    }
}
