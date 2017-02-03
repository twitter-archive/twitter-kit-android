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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.twitter.sdk.android.tweetui.internal.SwipeToDismissTouchListener;
import com.twitter.sdk.android.tweetui.internal.VideoControlView;
import com.twitter.sdk.android.tweetui.internal.VideoView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class PlayerControllerTest {
    private static final String TEST_CONTENT_URL = "https://example.com";
    private static final String TEST_CALL_TO_ACTION_URL = "https://example.com";
    private static final String TEST_CALL_TO_ACTION_TEXT = "Open in";
    private static final int TEST_SEEK_POSITION = 1000;
    private static final Uri TEST_URI = Uri.parse(TEST_CONTENT_URL);

    @Mock
    VideoView videoView;
    @Mock
    VideoControlView videoControlView;
    @Mock
    ProgressBar videoProgressView;
    @Mock
    TextView callToActionView;
    @Mock
    View rootView;
    @Mock
    SwipeToDismissTouchListener.Callback callback;
    @Captor
    private ArgumentCaptor<View.OnClickListener> clickListenerCaptor;
    @Captor
    private ArgumentCaptor<MediaPlayer.OnPreparedListener> prepareListenerCaptor;
    @Captor
    private ArgumentCaptor<MediaPlayer.OnInfoListener> infoListenerCaptor;

    PlayerController subject;
    PlayerActivity.PlayerItem playerItem;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(videoView.getContext()).thenReturn(RuntimeEnvironment.application);
        subject = spy(new PlayerController(rootView, videoView, videoControlView,
                videoProgressView, callToActionView, callback));
        playerItem = new PlayerActivity.PlayerItem(TEST_CONTENT_URL, false, true, null, null);
    }

    @Test
    public void testPrepare() {
        doNothing().when(subject).setUpMediaControl();
        subject.prepare(playerItem);

        verify(subject).setUpMediaControl(false, true);
        verify(videoView).setOnTouchListener(any(View.OnTouchListener.class));
        verify(videoView).setVideoURI(TEST_URI, false);
        verify(videoView).requestFocus();
        verify(videoView).setOnPreparedListener(any(MediaPlayer.OnPreparedListener.class));
        verify(videoView).setOnInfoListener(any(MediaPlayer.OnInfoListener.class));
    }

    @Test
    public void testPrepare_verifyOnPreparedListener() {
        doNothing().when(subject).setUpMediaControl();
        subject.prepare(playerItem);

        verify(subject).setUpMediaControl(false, true);
        verify(videoView).setVideoURI(TEST_URI, false);
        verify(videoView).requestFocus();
        verify(videoView).setOnPreparedListener(prepareListenerCaptor.capture());
        verify(videoView).setOnInfoListener(any(MediaPlayer.OnInfoListener.class));
        verifyOnPreparedListener(prepareListenerCaptor.getValue());
    }

    @Test
    public void testPrepare_setUpCallToActionListener() {
        doNothing().when(subject).setUpMediaControl();

        final PlayerActivity.PlayerItem itemWithCallToActionUrl =
                new PlayerActivity.PlayerItem(TEST_CONTENT_URL, false, false,
                        TEST_CALL_TO_ACTION_TEXT, TEST_CALL_TO_ACTION_URL);
        subject.prepare(itemWithCallToActionUrl);

        verify(subject).setUpMediaControl(false, false);
        verify(videoView).setVideoURI(TEST_URI, false);
        verify(videoView).requestFocus();
        verify(videoView).setOnPreparedListener(any(MediaPlayer.OnPreparedListener.class));
        verify(videoView).setOnInfoListener(any(MediaPlayer.OnInfoListener.class));

        verify(callToActionView).setVisibility(View.VISIBLE);
        verify(callToActionView).setText(TEST_CALL_TO_ACTION_TEXT);
        verify(callToActionView).setOnClickListener(any(View.OnClickListener.class));
        verify(rootView).setOnClickListener(any(View.OnClickListener.class));
    }

    private void verifyOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
        listener.onPrepared(null);
        verify(videoProgressView).setVisibility(View.GONE);
    }

    @Test
    public void testPrepare_verifyOnInfoListener() {
        doNothing().when(subject).setUpMediaControl();
        subject.prepare(playerItem);

        verify(subject).setUpMediaControl(false, true);
        verify(videoView).setVideoURI(TEST_URI, false);
        verify(videoView).requestFocus();
        verify(videoView).setOnPreparedListener(any(MediaPlayer.OnPreparedListener.class));
        verify(videoView).setOnInfoListener(infoListenerCaptor.capture());
        verifyOnInfoListener(infoListenerCaptor.getValue());
    }

    private void verifyOnInfoListener(MediaPlayer.OnInfoListener listener) {
        listener.onInfo(null, MediaPlayer.MEDIA_INFO_BUFFERING_START, 0);
        verify(videoProgressView).setVisibility(View.VISIBLE);
        listener.onInfo(null, MediaPlayer.MEDIA_INFO_BUFFERING_END, 0);
        verify(videoProgressView).setVisibility(View.GONE);
    }

    @Test
    public void testPrepare_withNullEntity() {
        doNothing().when(subject).setUpMediaControl();
        subject.prepare(null);
    }

    @Test
    public void testSetUpMediaControl_withLooping() {
        subject.setUpMediaControl(true, false);

        verify(subject).setUpLoopControl();
    }

    @Test
    public void testSetUpMediaControl_withLoopingAndControls() {
        subject.setUpMediaControl(true, true);

        verify(subject).setUpMediaControl();
    }

    @Test
    public void testSetUpMediaControl_withOutLooping() {
        doNothing().when(subject).setUpMediaControl();
        subject.setUpMediaControl(false, true);

        verify(subject).setUpMediaControl();
    }

    @Test
    public void testSetUpLoopControl() {
        subject.setUpLoopControl();

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
        subject.onDestroy();

        verify(videoView).stopPlayback();
    }

    @Test
    public void testOnPause() {
        when(videoView.getCurrentPosition()).thenReturn(TEST_SEEK_POSITION);
        when(videoView.isPlaying()).thenReturn(true);

        subject.onPause();

        verify(videoView).getCurrentPosition();
        verify(videoView).isPlaying();
        assertEquals(true, subject.isPlaying);
        assertEquals(TEST_SEEK_POSITION, subject.seekPosition);
    }

    @Test
    public void testOnResume() {
        subject.isPlaying = true;
        subject.seekPosition = TEST_SEEK_POSITION;
        subject.onResume();

        verify(videoView).start();
    }

    @Test
    public void testOnResume_withSeeekPosition() {
        subject.isPlaying = true;
        subject.seekPosition = TEST_SEEK_POSITION;
        subject.onResume();

        verify(videoView).seekTo(TEST_SEEK_POSITION);
        verify(videoView).start();
    }
}
