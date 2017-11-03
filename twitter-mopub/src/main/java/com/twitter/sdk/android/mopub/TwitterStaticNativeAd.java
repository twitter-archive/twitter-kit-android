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
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twitter.sdk.android.mopub.internal.RoundedImageView;

public class TwitterStaticNativeAd extends FrameLayout {
    LinearLayout containerLayout;
    RoundedImageView mainImageView;
    RelativeLayout cardLayout;
    ImageView adIconView;
    TextView adTitleView;
    TextView adTextView;
    TextView callToActionView;
    ImageView privacyInfoView;

    // style colors
    int containerBackgroundColor;
    int cardBackgroundColor;
    int primaryTextColor;
    int ctaBackgroundColor;
    int cardBorderColor;

    private static final int DEFAULT_AD_STYLE = R.style.tw__ad_LightStyle;

    public TwitterStaticNativeAd(Context context) {
        this(context, null);
    }

    public TwitterStaticNativeAd(Context context, AttributeSet attrs) {
        this(context, attrs, DEFAULT_AD_STYLE);
    }

    public TwitterStaticNativeAd(Context context, AttributeSet attrs, int styleResId) {
        super(context, attrs);
        findSubviews();
        initAttributes(styleResId);
        setStyleAttributes();
    }

    private void findSubviews() {
        LayoutInflater.from(getContext()).inflate(R.layout.tw__native_ad, this, true);
        containerLayout = findViewById(R.id.tw__ad_mopub_layout);
        mainImageView = findViewById(R.id.native_ad_main_image);
        cardLayout = findViewById(R.id.native_ad_card);
        adIconView = findViewById(R.id.native_ad_icon_image);
        adTitleView = findViewById(R.id.native_ad_title);
        adTextView = findViewById(R.id.native_ad_text);
        callToActionView = findViewById(R.id.native_ad_cta);
        privacyInfoView = findViewById(R.id.native_ad_privacy_info_icon_image);
    }

    private void initAttributes(int styleResId) {
        final TypedArray a = getContext().getTheme().obtainStyledAttributes(styleResId,
                R.styleable.tw__native_ad);
        try {
            readStyleAttributes(a);
        } finally {
            a.recycle();
        }
    }

    private void readStyleAttributes(TypedArray typedArray) {
        containerBackgroundColor = typedArray.getColor(
                R.styleable.tw__native_ad_tw__ad_container_bg_color,
                getResources().getColor(R.color.tw__ad_light_container_bg_color));

        cardBackgroundColor = typedArray.getColor(R.styleable.tw__native_ad_tw__ad_card_bg_color,
                getResources().getColor(R.color.tw__ad_light_card_bg_color));

        primaryTextColor = typedArray.getColor(
                R.styleable.tw__native_ad_tw__ad_text_primary_color,
                getResources().getColor(R.color.tw__ad_light_text_primary_color));

        ctaBackgroundColor = typedArray.getColor(
                R.styleable.tw__native_ad_tw__ad_cta_button_color,
                getResources().getColor(R.color.tw__ad_cta_default));
    }

    private void setStyleAttributes() {
        containerLayout.setBackgroundColor(containerBackgroundColor);
        adTitleView.setTextColor(primaryTextColor);
        adTextView.setTextColor(primaryTextColor);
        final int adViewRadius = (int) getResources().getDimension(R.dimen.tw__ad_view_radius);
        mainImageView.setCornerRadii(adViewRadius, adViewRadius, 0, 0);

        final TextView privacyTextView = findViewById(R.id.native_ad_privacy_text);
        privacyTextView.setTextColor(
                ColorUtils.calculateContrastingColor(containerBackgroundColor));

        setCardStyling();
        setCallToActionStyling();
    }

    private void setCardStyling() {
        final boolean isLightBg = ColorUtils.isLightColor(containerBackgroundColor);
        if (isLightBg) {
            cardBorderColor = getResources().getColor(R.color.tw__ad_light_card_border_color);
        } else {
            cardBorderColor = getResources().getColor(R.color.tw__ad_dark_card_border_color);
        }

        final ShapeDrawable bgDrawable = new ShapeDrawable(new RectShape());
        bgDrawable.getPaint().setColor(cardBackgroundColor);
        final ShapeDrawable borderDrawable = new ShapeDrawable(new RectShape());
        borderDrawable.getPaint().setColor(cardBorderColor);

        final Drawable[] layers = new Drawable[2];
        layers[0] = borderDrawable;
        layers[1] = bgDrawable;

        final LayerDrawable layerDrawable = new LayerDrawable(layers);
        layerDrawable.setLayerInset(0, 0, 0, 0, 0);
        layerDrawable.setLayerInset(1, 1, 0, 1, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            cardLayout.setBackground(layerDrawable);
        } else {
            cardLayout.setBackgroundDrawable(layerDrawable);
        }
    }

    private void setCallToActionStyling() {
        final int calculatedCTATextColor = ColorUtils.calculateCtaTextColor(ctaBackgroundColor);
        callToActionView.setTextColor(calculatedCTATextColor);

        // Setup StateListDrawable obj with two gradient drawables:
        // First is the selected item with lighter/darker bg color of original
        // Second is unselected item with the call to action background color
        // Also set the default ad view radius for bottomLeft and bottomRight corners
        final StateListDrawable stateListDrawable = new StateListDrawable();
        final int adViewRadius = (int) getResources().getDimension(R.dimen.tw__ad_view_radius);
        final float[] ctaViewRadii = new float[]{
                0, 0,
                0, 0,
                adViewRadius, adViewRadius,
                adViewRadius, adViewRadius};

        final GradientDrawable selectedItem = new GradientDrawable();
        selectedItem.setCornerRadii(ctaViewRadii);
        final int ctaPressedBgColor = ColorUtils.calculateCtaOnTapColor(ctaBackgroundColor);
        selectedItem.setColor(ctaPressedBgColor);
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, selectedItem);

        final GradientDrawable unselectedItem = new GradientDrawable();
        unselectedItem.setCornerRadii(ctaViewRadii);
        unselectedItem.setColor(ctaBackgroundColor);
        stateListDrawable.addState(new int[]{}, unselectedItem);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            callToActionView.setBackground(stateListDrawable);
        } else {
            callToActionView.setBackgroundDrawable(stateListDrawable);
        }
    }
}
