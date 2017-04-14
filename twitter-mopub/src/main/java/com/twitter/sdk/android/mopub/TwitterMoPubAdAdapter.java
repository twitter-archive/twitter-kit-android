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

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Adapter;

import com.mopub.nativeads.MoPubAdAdapter;
import com.mopub.nativeads.MoPubNativeAdPositioning;
import com.mopub.nativeads.RequestParameters;

public class TwitterMoPubAdAdapter extends MoPubAdAdapter{
    private static final String TWITTERKIT_KEYWORD = "src:twitterkit";

    /**
     * Creates a new TwitterMoPubAdAdapter object.
     *
     * By default, the adapter will contact the server to determine ad positions. If you
     * wish to hard-code positions in your app, see {@link TwitterMoPubAdAdapter( Activity,
     * MoPubNativeAdPositioning.MoPubClientPositioning )}.
     *
     * @param activity The activity.
     * @param originalAdapter Your original adapter.
     */
    public TwitterMoPubAdAdapter(Activity activity, Adapter originalAdapter) {
        super(activity, originalAdapter);
    }

    /**
     * Creates a new TwitterMoPubAdAdapter object, using server positioning.
     *
     * @param activity The activity.
     * @param originalAdapter Your original adapter.
     * @param adPositioning A positioning object for specifying where ads will be placed in your
     * stream. See {@link MoPubNativeAdPositioning#serverPositioning()}.
     */
    public TwitterMoPubAdAdapter(Activity activity, Adapter originalAdapter,
            MoPubNativeAdPositioning.MoPubServerPositioning adPositioning) {
        super(activity, originalAdapter, adPositioning);
    }

    /**
     * Creates a new TwitterMoPubAdAdapter object, using client positioning.
     *
     * @param activity The activity.
     * @param originalAdapter Your original adapter.
     * @param adPositioning A positioning object for specifying where ads will be placed in your
     * stream. See {@link MoPubNativeAdPositioning#clientPositioning()}.
     */
    public TwitterMoPubAdAdapter(Activity activity, Adapter originalAdapter,
            MoPubNativeAdPositioning.MoPubClientPositioning adPositioning) {
        super(activity, originalAdapter, adPositioning);
    }

    @Override
    public void loadAds(@NonNull final String adUnitId) {
        loadAds(adUnitId, null);
    }

    @Override
    public void loadAds(@NonNull final String adUnitId,
            @Nullable final RequestParameters requestParams) {

        final RequestParameters.Builder builder = new RequestParameters.Builder();
        if (requestParams != null) {
            final String keywords = TextUtils.isEmpty(requestParams.getKeywords())
                    ? TWITTERKIT_KEYWORD : requestParams.getKeywords() + "," + TWITTERKIT_KEYWORD;
            builder.keywords(keywords);
            builder.location(requestParams.getLocation());
        } else {
            builder.keywords(TWITTERKIT_KEYWORD);
        }

        super.loadAds(adUnitId, builder.build());
    }
}
