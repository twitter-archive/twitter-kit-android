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
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mopub.nativeads.BaseNativeAd;
import com.mopub.nativeads.StaticNativeAd;
import com.mopub.network.MaxWidthImageLoader;
import com.mopub.network.Networking;
import com.mopub.volley.toolbox.ImageLoader;
import com.twitter.sdk.android.mopub.internal.RoundedImageView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class TwitterStaticNativeAdRendererTest {
    private static final String TEST_TITLE = "title";
    private static final String TEST_TEXT = "text";
    private static final String TEST_CTA = "cta";
    private static final String TEST_URL = "https://twitter.com";

    private TwitterStaticNativeAdRenderer twitterStaticNativeAdRenderer;
    private StaticNativeAd staticNativeAd;
    private TwitterStaticNativeAd twitterStaticNativeAd;
    @Mock
    private ViewGroup viewGroup;
    @Mock
    private MaxWidthImageLoader mockImageLoader;
    @Mock
    private ImageLoader.ImageContainer mockImageContainer;
    @Mock
    private Bitmap mockBitmap;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Networking.setImageLoaderForTesting(mockImageLoader);
        doReturn(mockBitmap).when(mockImageContainer).getBitmap();

        doAnswer(invocation -> {
            final Object[] args = invocation.getArguments();
            ((ImageLoader.ImageListener) args[1]).onResponse(mockImageContainer, true);
            return null;
        }).when(mockImageLoader).get(anyString(), any(ImageLoader.ImageListener.class));

        twitterStaticNativeAdRenderer = new TwitterStaticNativeAdRenderer();

        twitterStaticNativeAd = new TwitterStaticNativeAd(RuntimeEnvironment.application);
        twitterStaticNativeAd.adTextView = mock(TextView.class);
        twitterStaticNativeAd.adTitleView = mock(TextView.class);
        twitterStaticNativeAd.callToActionView = mock(TextView.class);
        twitterStaticNativeAd.mainImageView = mock(RoundedImageView.class);
        twitterStaticNativeAd.adIconView = mock(ImageView.class);
        twitterStaticNativeAd.privacyInfoView = mock(ImageView.class);

        staticNativeAd = new StaticNativeAd() { };
        staticNativeAd.setTitle(TEST_TITLE);
        staticNativeAd.setText(TEST_TEXT);
        staticNativeAd.setCallToAction(TEST_CTA);
        staticNativeAd.setClickDestinationUrl(TEST_URL);
        staticNativeAd.setMainImageUrl(TEST_URL);
        staticNativeAd.setIconImageUrl(TEST_URL);
        staticNativeAd.setPrivacyInformationIconClickThroughUrl(TEST_URL);
        staticNativeAd.setPrivacyInformationIconImageUrl(TEST_URL);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateAdView_withNullContext_shouldThrowNPE() {
        twitterStaticNativeAdRenderer.createAdView(null, viewGroup);
    }

    @Test(expected = NullPointerException.class)
    public void testRenderAdView_withNullView_shouldThrowNPE() {
        twitterStaticNativeAdRenderer.renderAdView(null, staticNativeAd);
    }

    @Test(expected = NullPointerException.class)
    public void testRenderAdView_withNullNativeAd_shouldThrowNPE() {
        twitterStaticNativeAdRenderer.renderAdView(twitterStaticNativeAd, null);
    }

    @Test
    public void testRenderAdView_shouldReturnPopulatedView() {
        when(twitterStaticNativeAd.privacyInfoView.getContext()).thenReturn(mock(Context.class));
        twitterStaticNativeAdRenderer.renderAdView(twitterStaticNativeAd, staticNativeAd);

        verify(twitterStaticNativeAd.adTitleView).setText(TEST_TITLE);
        verify(twitterStaticNativeAd.adTextView).setText(TEST_TEXT);
        verify(twitterStaticNativeAd.callToActionView).setText(TEST_CTA);
        verify(twitterStaticNativeAd.mainImageView).setImageBitmap(mockBitmap);
        verify(twitterStaticNativeAd.adIconView).setImageBitmap(mockBitmap);
        verify(twitterStaticNativeAd.privacyInfoView).setImageBitmap(mockBitmap);
        verify(twitterStaticNativeAd.privacyInfoView)
                .setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void testSupports_withCorrectInstanceOfBaseNativeAd_shouldReturnTrue() throws Exception {
        assertTrue(twitterStaticNativeAdRenderer.supports(new StaticNativeAd() {}));
        assertFalse(twitterStaticNativeAdRenderer.supports(mock(BaseNativeAd.class)));
    }
}
