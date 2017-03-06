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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mopub.nativeads.BaseNativeAd;
import com.mopub.nativeads.MoPubAdRenderer;
import com.mopub.nativeads.NativeImageHelper;
import com.mopub.nativeads.NativeRendererHelper;
import com.mopub.nativeads.StaticNativeAd;

public class TwitterStaticNativeAdRenderer implements MoPubAdRenderer<StaticNativeAd> {
    private static final int DEFAULT_STYLE = R.style.tw__ad_LightStyle;

    private final int styleResId;

    public TwitterStaticNativeAdRenderer() {
        this.styleResId = DEFAULT_STYLE;
    }

    public TwitterStaticNativeAdRenderer(int styleResId) {
        this.styleResId = styleResId;
    }

    @Override
    public View createAdView(final Context context, final ViewGroup parent) {
        return new TwitterStaticNativeAd(context, null, styleResId);
    }

    @Override
    public void renderAdView(final View view, final StaticNativeAd staticNativeAd) {
        update((TwitterStaticNativeAd) view, staticNativeAd);
    }

    @Override
    public boolean supports(final BaseNativeAd nativeAd) {
        return nativeAd instanceof StaticNativeAd;
    }

    private void update(final TwitterStaticNativeAd staticNativeView,
            final StaticNativeAd staticNativeAd) {
        NativeRendererHelper.addTextView(staticNativeView.adTitleView,
                staticNativeAd.getTitle());
        NativeRendererHelper.addTextView(staticNativeView.adTextView, staticNativeAd.getText());
        NativeRendererHelper.addTextView(staticNativeView.callToActionView,
                staticNativeAd.getCallToAction());
        NativeImageHelper.loadImageView(staticNativeAd.getMainImageUrl(),
                staticNativeView.mainImageView);
        NativeImageHelper.loadImageView(staticNativeAd.getIconImageUrl(),
                staticNativeView.adIconView);
        NativeRendererHelper.addPrivacyInformationIcon(
                staticNativeView.privacyInfoView,
                staticNativeAd.getPrivacyInformationIconImageUrl(),
                staticNativeAd.getPrivacyInformationIconClickThroughUrl());
    }
}
