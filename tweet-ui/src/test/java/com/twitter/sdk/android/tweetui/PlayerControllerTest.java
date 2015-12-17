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

import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;

import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.VideoInfo;
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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PlayerControllerTest {
    private static final String TEST_CONTENT_TYPE_MP4 = "video/mp4";
    private static final String TEST_CONTENT_URL = "https://example.com";
    @Mock
    VideoView videoView;
    @Captor
    private ArgumentCaptor<View.OnClickListener> clickListenerCaptor;
    @Captor
    private ArgumentCaptor<MediaPlayer.OnPreparedListener> preparedListenerCaptor;

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
        final MediaEntity entity = TestFixtures.createEntityWithVideoInfo(videoInfo);

        final PlayerController playerController = spy(new PlayerController(videoView));
        doNothing().when(playerController).setUpMediaControl();
        playerController.prepare(entity);

        verify(playerController).setUpMediaControl(false);
        verify(videoView).setVideoURI(testUri, false);
        verify(videoView).requestFocus();
        verify(videoView).setOnPreparedListener(preparedListenerCaptor.capture());

        final MediaPlayer.OnPreparedListener listener = preparedListenerCaptor.getValue();
        assertNotNull(listener);
        listener.onPrepared(null);
        verify(videoView).start();
    }

    @Test
    public void testPrepare_withNullEntity() {
        final PlayerController playerController = spy(new PlayerController(videoView));
        doNothing().when(playerController).setUpMediaControl();
        playerController.prepare(null);
    }

    @Test
    public void testSetUpMediaControl_withLooping() {
        final PlayerController playerController = spy(new PlayerController(videoView));
        playerController.setUpMediaControl(true);

        verify(playerController).setUpLoopControl();
    }

    @Test
    public void testSetUpMediaControl_withOutLooping() {
        final PlayerController playerController = spy(new PlayerController(videoView));
        doNothing().when(playerController).setUpMediaControl();
        playerController.setUpMediaControl(false);

        verify(playerController).setUpMediaControl();
    }

    @Test
    public void testSetUpLoopControl() {
        final PlayerController playerController = new PlayerController(videoView);
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
    public void testCleanup() {
        final PlayerController playerController = new PlayerController(videoView);
        playerController.cleanup();

        verify(videoView).stopPlayback();
    }
}
