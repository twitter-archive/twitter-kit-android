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

package com.twitter.sdk.android.tweetui.internal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.IntentUtils;
import com.twitter.sdk.android.core.internal.VineCardUtils;
import com.twitter.sdk.android.core.internal.scribe.ScribeItem;
import com.twitter.sdk.android.core.models.Card;
import com.twitter.sdk.android.core.models.ImageValue;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.VideoInfo;
import com.twitter.sdk.android.tweetui.GalleryActivity;
import com.twitter.sdk.android.tweetui.PlayerActivity;
import com.twitter.sdk.android.tweetui.R;
import com.twitter.sdk.android.tweetui.TweetMediaClickListener;
import com.twitter.sdk.android.tweetui.TweetUi;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public class TweetMediaView extends ViewGroup implements View.OnClickListener {

    static final int MAX_IMAGE_VIEW_COUNT = 4;
    static final String SIZED_IMAGE_SMALL = ":small";

    private final OverlayImageView[] imageViews = new OverlayImageView[MAX_IMAGE_VIEW_COUNT];
    private List<MediaEntity> mediaEntities = Collections.emptyList();
    private final Path path = new Path();
    private final RectF rect = new RectF();
    private final int mediaDividerSize;
    private int imageCount;
    final float [] radii = new float[8];
    int mediaBgColor = Color.BLACK;
    int photoErrorResId;
    final DependencyProvider dependencyProvider;
    boolean internalRoundedCornersEnabled;
    TweetMediaClickListener tweetMediaClickListener;
    Tweet tweet;

    public TweetMediaView(Context context) {
        this(context, null);
    }

    public TweetMediaView(Context context, AttributeSet attrs) {
        this(context, attrs, new DependencyProvider());
    }

    TweetMediaView(Context context, AttributeSet attrs, DependencyProvider dependencyProvider) {
        super(context, attrs);

        this.dependencyProvider =  dependencyProvider;
        mediaDividerSize = getResources().getDimensionPixelSize
                (R.dimen.tw__media_view_divider_size);
        photoErrorResId = R.drawable.tw__ic_tweet_photo_error_dark;
    }

    public void setRoundedCornersRadii(int topLeft, int topRight, int bottomRight, int bottomLeft) {
        radii[0] = topLeft;
        radii[1] = topLeft;
        radii[2] = topRight;
        radii[3] = topRight;
        radii[4] = bottomRight;
        radii[5] = bottomRight;
        radii[6] = bottomLeft;
        radii[7] = bottomLeft;

        requestLayout();
    }

    public void setMediaBgColor(int mediaBgColor) {
        this.mediaBgColor = mediaBgColor;
    }

    public void setTweetMediaClickListener(TweetMediaClickListener tweetMediaClickListener) {
        this.tweetMediaClickListener = tweetMediaClickListener;
    }

    public void setPhotoErrorResId(int photoErrorResId) {
        this.photoErrorResId = photoErrorResId;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (imageCount > 0) {
            layoutImages();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final Size size;
        if (imageCount > 0) {
            size = measureImages(widthMeasureSpec, heightMeasureSpec);
        } else {
            size = Size.EMPTY;
        }
        setMeasuredDimension(size.width, size.height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        path.reset();
        rect.set(0, 0, w, h);
        path.addRoundRect(rect, radii, Path.Direction.CW);
        path.close();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (internalRoundedCornersEnabled &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final int saveState = canvas.save();
            canvas.clipPath(path);
            super.dispatchDraw(canvas);
            canvas.restoreToCount(saveState);
        } else {
            super.dispatchDraw(canvas);
        }
    }

    @Override
    public void onClick(View view) {
        final Integer mediaEntityIndex = (Integer) view.getTag(R.id.tw__entity_index);
        if (tweetMediaClickListener != null) {
            final MediaEntity mediaEntity;
            if (!mediaEntities.isEmpty()) {
                mediaEntity = mediaEntities.get(mediaEntityIndex);
            } else {
                mediaEntity = null;
            }

            tweetMediaClickListener.onMediaEntityClick(tweet, mediaEntity);
        } else if (!mediaEntities.isEmpty()) {
            final MediaEntity mediaEntity = mediaEntities.get(mediaEntityIndex);
            if (TweetMediaUtils.isVideoType(mediaEntity)) {
                launchVideoPlayer(mediaEntity);
            } else if (TweetMediaUtils.isPhotoType(mediaEntity)) {
                launchPhotoGallery(mediaEntityIndex);
            }
        } else {
            launchVideoPlayer(tweet);
        }
    }

    public void launchVideoPlayer(MediaEntity entity) {
        final VideoInfo.Variant variant = TweetMediaUtils.getSupportedVariant(entity);
        if (variant != null) {
            final Intent intent = new Intent(getContext(), PlayerActivity.class);
            final boolean looping = TweetMediaUtils.isLooping(entity);
            final boolean showControls = TweetMediaUtils.showVideoControls(entity);
            final String url = TweetMediaUtils.getSupportedVariant(entity).url;
            final PlayerActivity.PlayerItem item =
                    new PlayerActivity.PlayerItem(url, looping, showControls, null, null);
            intent.putExtra(PlayerActivity.PLAYER_ITEM, item);

            IntentUtils.safeStartActivity(getContext(), intent);
        }
    }

    public void launchVideoPlayer(Tweet tweet) {
        final Card card = tweet.card;
        final Intent intent = new Intent(getContext(), PlayerActivity.class);
        final String playerStreamUrl = VineCardUtils.getStreamUrl(card);

        final PlayerActivity.PlayerItem playerItem =
                new PlayerActivity.PlayerItem(playerStreamUrl, true, false, null, null);
        intent.putExtra(PlayerActivity.PLAYER_ITEM, playerItem);

        final ScribeItem scribeItem = ScribeItem.fromTweetCard(tweet.id, card);
        intent.putExtra(PlayerActivity.SCRIBE_ITEM, scribeItem);

        IntentUtils.safeStartActivity(getContext(), intent);
    }

    public void launchPhotoGallery(int mediaEntityIndex) {
        final Intent intent = new Intent(getContext(), GalleryActivity.class);
        final GalleryActivity.GalleryItem item =
                new GalleryActivity.GalleryItem(tweet.id, mediaEntityIndex, mediaEntities);
        intent.putExtra(GalleryActivity.GALLERY_ITEM, item);
        IntentUtils.safeStartActivity(getContext(), intent);
    }

    public void setTweetMediaEntities(Tweet tweet, List<MediaEntity> mediaEntities) {
        if (tweet == null || mediaEntities == null || mediaEntities.isEmpty() ||
                mediaEntities.equals(this.mediaEntities)) {
            return;
        }

        this.tweet = tweet;
        this.mediaEntities = mediaEntities;

        clearImageViews();
        initializeImageViews(mediaEntities);

        internalRoundedCornersEnabled = TweetMediaUtils.isPhotoType(mediaEntities.get(0));

        requestLayout();
    }

    public void setVineCard(Tweet tweet) {
        if (tweet == null || tweet.card == null || !VineCardUtils.isVine(tweet.card)) {
            return;
        }

        this.tweet = tweet;
        this.mediaEntities = Collections.emptyList();

        clearImageViews();
        initializeImageViews(tweet.card);

        internalRoundedCornersEnabled = false;

        requestLayout();
    }

    Size measureImages(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        final int halfWidth = (width - mediaDividerSize) / 2;
        final int halfHeight = (height - mediaDividerSize) / 2;
        switch (imageCount) {
            case 1:
                measureImageView(0, width, height);
                break;
            case 2:
                measureImageView(0, halfWidth, height);
                measureImageView(1, halfWidth, height);
                break;
            case 3:
                measureImageView(0, halfWidth, height);
                measureImageView(1, halfWidth, halfHeight);
                measureImageView(2, halfWidth, halfHeight);
                break;
            case 4:
                measureImageView(0, halfWidth, halfHeight);
                measureImageView(1, halfWidth, halfHeight);
                measureImageView(2, halfWidth, halfHeight);
                measureImageView(3, halfWidth, halfHeight);
                break;
            default:
                break;
        }
        return Size.fromSize(width, height);
    }

    void measureImageView(int i, int width, int height) {
        imageViews[i].measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    void layoutImages() {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        final int halfWidth = (width - mediaDividerSize) / 2;
        final int halfHeight = (height - mediaDividerSize) / 2;
        final int middle = halfWidth + mediaDividerSize;
        switch (imageCount) {
            case 1:
                layoutImage(0, 0, 0, width, height);
                break;
            case 2:
                layoutImage(0, 0, 0, halfWidth, height);
                layoutImage(1, halfWidth + mediaDividerSize, 0, width, height);
                break;
            case 3:
                layoutImage(0, 0, 0, halfWidth, height);
                layoutImage(1, middle, 0, width, halfHeight);
                layoutImage(2, middle, halfHeight + mediaDividerSize, width, height);
                break;
            case 4:
                layoutImage(0, 0, 0, halfWidth, halfHeight);
                layoutImage(2, 0, halfHeight + mediaDividerSize, halfWidth, height);
                layoutImage(1, middle, 0, width, halfHeight);
                layoutImage(3, middle, halfHeight + mediaDividerSize, width, height);
                break;
            default:
                break;
        }
    }

    void layoutImage(int i, int left, int top, int right, int bottom) {
        final ImageView view = imageViews[i];
        if (view.getLeft() == left && view.getTop() == top && view.getRight() == right
                && view.getBottom() == bottom) {
            return;
        }

        view.layout(left, top, right, bottom);
    }

    void clearImageViews() {
        for (int index = 0; index < imageCount; index++) {
            final ImageView imageView = imageViews[index];
            if (imageView != null) {
                imageView.setVisibility(GONE);
            }
        }
        imageCount = 0;
    }

    void initializeImageViews(List<MediaEntity> mediaEntities) {
        imageCount = Math.min(MAX_IMAGE_VIEW_COUNT, mediaEntities.size());

        for (int index = 0; index < imageCount; index++) {
            final OverlayImageView imageView = getOrCreateImageView(index);

            final MediaEntity mediaEntity = mediaEntities.get(index);
            setAltText(imageView, mediaEntity.altText);
            setMediaImage(imageView, getSizedImagePath(mediaEntity));
            setOverlayImage(imageView, TweetMediaUtils.isVideoType(mediaEntity));
        }
    }

    void initializeImageViews(Card card) {
        imageCount = 1;

        final OverlayImageView imageView = getOrCreateImageView(0);

        final ImageValue imageValue = VineCardUtils.getImageValue(card);
        setAltText(imageView, imageValue.alt);
        setMediaImage(imageView, imageValue.url);
        setOverlayImage(imageView, true);
    }

    OverlayImageView getOrCreateImageView(int index) {
        OverlayImageView imageView = imageViews[index];
        if (imageView == null) {
            imageView = new OverlayImageView(getContext());
            imageView.setLayoutParams(generateDefaultLayoutParams());
            imageView.setOnClickListener(this);
            imageViews[index] = imageView;
            addView(imageView, index);
        } else {
            measureImageView(index, 0, 0);
            layoutImage(index, 0, 0, 0, 0);
        }

        imageView.setVisibility(VISIBLE);
        imageView.setBackgroundColor(mediaBgColor);
        imageView.setTag(R.id.tw__entity_index, index);

        return imageView;
    }


    String getSizedImagePath(MediaEntity mediaEntity) {
        if (imageCount > 1) {
           return mediaEntity.mediaUrlHttps + SIZED_IMAGE_SMALL;
        }
        return mediaEntity.mediaUrlHttps;   // defaults to :medium
    }

    void setAltText(ImageView imageView, String description) {
        if (!TextUtils.isEmpty(description)) {
            imageView.setContentDescription(description);
        } else {
            imageView.setContentDescription(getResources().getString(R.string.tw__tweet_media));
        }
    }

    void setOverlayImage(OverlayImageView imageView, boolean isVideo) {
        if (isVideo) {
            imageView.setOverlayDrawable(getContext().getResources()
                    .getDrawable(R.drawable.tw__player_overlay));
        } else {
            imageView.setOverlayDrawable(null);
        }
    }

    void setMediaImage(ImageView imageView, String imagePath) {
        final Picasso imageLoader = dependencyProvider.getImageLoader();
        if (imageLoader == null) return;

        imageLoader.load(imagePath)
                .fit()
                .centerCrop()
                .error(photoErrorResId)
                .into(imageView, new PicassoCallback(imageView));
    }

    /**
     * Picasso Callback which clears the ImageView's background onSuccess. This is done to reduce
     * overdraw. A weak reference is used to avoid leaking the Activity context because the Callback
     * will be strongly referenced by Picasso.
     */
    static class PicassoCallback implements com.squareup.picasso.Callback {
        final WeakReference<ImageView> imageViewWeakReference;

        PicassoCallback(ImageView imageView) {
            imageViewWeakReference = new WeakReference<>(imageView);
        }

        @Override
        public void onSuccess() {
            final ImageView imageView = imageViewWeakReference.get();
            if (imageView != null) {
                imageView.setBackgroundResource(android.R.color.transparent);
            }
        }

        @Override
        public void onError() { /* intentionally blank */ }
    }

    static class Size {
        static final Size EMPTY = new Size();
        final int width;
        final int height;

        private Size() {
            this(0, 0);
        }

        private Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        static Size fromSize(int w, int h) {
            final int boundedWidth = Math.max(w, 0);
            final int boundedHeight = Math.max(h, 0);
            return boundedWidth != 0 || boundedHeight != 0 ?
                    new Size(boundedWidth, boundedHeight) : EMPTY;
        }
    }

    static class DependencyProvider {
        /**
         * Can be null if run before TweetUi#doInBackground completes
         */
        Picasso getImageLoader() {
            return TweetUi.getInstance().getImageLoader();
        }
    }
}
