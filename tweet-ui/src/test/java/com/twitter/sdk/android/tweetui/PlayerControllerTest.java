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

import android.net.Uri;
import android.view.View;

import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.VideoInfo;
import com.twitter.sdk.android.tweetui.internal.VideoControlView;
import com.twitter.sdk.android.tweetui.internal.VideoView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PlayerControllerTest {
    private static final String TEST_CONTENT_TYPE_MP4 = "video/mp4";
    private static final String TEST_CONTENT_URL = "https://example.com";
    private static final int TEST_SEEK_POSITION = 1000;

    @Mock
    VideoView videoView;
    @Mock
    VideoControlView videoControlView;
    @Captor
    private ArgumentCaptor<View.OnClickListener> clickListenerCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPrepare() {
        final Uri testUri = Uri.parse(TEST_CONTENT_URL);
        final VideoInfo.Variant variant =
                new VideoInfo.Variant(0, TEST_CONTENT_TYPE_MP4, testUri.toString());
        final VideoInfo videoInfo = TestFixtures.createVideoInfoWithVariant(variant);
        final MediaEntity entity = TestFixtures.createEntityWithVideo(videoInfo);

        final PlayerController playerController =
                spy(new PlayerController(videoView, videoControlView));
        doNothing().when(playerController).setUpMediaControl();
        playerController.prepare(entity);

        verify(playerController).setUpMediaControl(false);
        verify(videoView).setVideoURI(testUri, false);
        verify(videoView).requestFocus();
    }

    @Test
    public void testPrepare_withNullEntity() {
        final PlayerController playerController =
                spy(new PlayerController(videoView, videoControlView));
        doNothing().when(playerController).setUpMediaControl();
        playerController.prepare(null);
    }

    @Test
    public void testSetUpMediaControl_withLooping() {
        final PlayerController playerController =
                spy(new PlayerController(videoView, videoControlView));
        playerController.setUpMediaControl(true);

        verify(playerController).setUpLoopControl();
    }

    @Test
    public void testSetUpMediaControl_withOutLooping() {
        final PlayerController playerController =
                spy(new PlayerController(videoView, videoControlView));
        doNothing().when(playerController).setUpMediaControl();
        playerController.setUpMediaControl(false);

        verify(playerController).setUpMediaControl();
    }

    @Test
    public void testSetUpLoopControl() {
        final PlayerController playerController = new PlayerController(videoView, videoControlView);
        playerController.setUpLoopControl();

        verify(videoView).setOnClickListener(clickListenerCaptor.capture());
        final View.OnClickListener listener = clickListenerCaptor.getValue();

        when(videoView.isPlaying()).thenReturn(false);
        listener.onClick(null);
        verify(videoView).start();

        when(videoView.isPlaying()).thenReturn(true);
        listener.onClick(null);
        verify(videoView).pause();
    }

    @Test
    public void testOnDestroy() {
        final PlayerController playerController = new PlayerController(videoView, videoControlView);
        playerController.onDestroy();

        verify(videoView).stopPlayback();
    }

    @Test
    public void testOnPause() {
        when(videoView.getCurrentPosition()).thenReturn(TEST_SEEK_POSITION);
        when(videoView.isPlaying()).thenReturn(true);

        final PlayerController playerController = new PlayerController(videoView, videoControlView);
        playerController.onPause();

        verify(videoView).getCurrentPosition();
        verify(videoView).isPlaying();
        assertEquals(true, playerController.isPlaying);
        assertEquals(TEST_SEEK_POSITION, playerController.seekPosition);
    }

    @Test
    public void testOnResume() {
        final PlayerController playerController = new PlayerController(videoView, videoControlView);
        playerController.isPlaying = true;
        playerController.seekPosition = TEST_SEEK_POSITION;
        playerController.onResume();

        verify(videoView).start();
    }

    @Test
    public void testOnResume_withSeeekPosition() {
        final PlayerController playerController = new PlayerController(videoView, videoControlView);
        playerController.isPlaying = true;
        playerController.seekPosition = TEST_SEEK_POSITION;
        playerController.onResume();

        verify(videoView).seekTo(TEST_SEEK_POSITION);
        verify(videoView).start();
    }
}
