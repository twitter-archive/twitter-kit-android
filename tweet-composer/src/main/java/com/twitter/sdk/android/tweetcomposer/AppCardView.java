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

package com.twitter.sdk.android.tweetcomposer;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class AppCardView extends LinearLayout {
    ImageView appImageView;
    ViewGroup appInfoLayout;
    TextView appInstallButton;
    TextView appNameView;
    TextView appStoreNameView;

    public AppCardView(Context context) {
        this(context, null);
    }

    public AppCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AppCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        inflate(context, R.layout.tw__app_card, this);
        findSubviews();
        setButtonColor();
    }

    void findSubviews() {
        appImageView = (ImageView) findViewById(R.id.tw__app_image);
        appNameView = (TextView) findViewById(R.id.tw__app_name);
        appStoreNameView = (TextView) findViewById(R.id.tw__app_store_name);
        appInstallButton = (TextView) findViewById(R.id.tw__app_install_button);
        appInfoLayout = (ViewGroup) findViewById(R.id.tw__app_info_layout);
    }

    void setCard(Card card) {
        setImage(Uri.parse(card.imageUri));
        setAppName(card.appName);
    }

    void setImage(Uri uri) {
        final int radius = getResources().getDimensionPixelSize(R.dimen.tw__card_radius_medium);
        final Transformation transformation = new RoundedCornerTransformation.Builder()
                .setRadii(radius, radius, 0, 0)
                .build();
        Picasso.with(getContext())
                .load(uri)
                .transform(transformation)
                .fit()
                .centerCrop()
                .into(appImageView);
    }

    void setAppName(String name) {
        appNameView.setText(name);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int maxWidth = getResources().getDimensionPixelSize(R.dimen.tw__card_maximum_width);

        // Adjust width if required
        final int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (maxWidth > 0 && maxWidth < measuredWidth) {
            final int measureMode = MeasureSpec.getMode(widthMeasureSpec);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidth, measureMode);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    void setButtonColor() {
        final int buttonTextColor = getResources().getColor(R.color.tw__composer_blue_text);
        appInstallButton.setTextColor(buttonTextColor);
    }
}
