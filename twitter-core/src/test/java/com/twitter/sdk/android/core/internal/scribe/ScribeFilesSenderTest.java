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

package com.twitter.sdk.android.core.internal.scribe;

import android.content.Context;

import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.IdManager;

import com.twitter.sdk.android.core.BuildConfig;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TestResources;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLSocketFactory;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class ScribeFilesSenderTest {

    private static final int NUM_SCRIBE_EVENTS = 9;
    private static final String TEST_LOGS = "testlogs";
    private static final String ANY_URL = "http://example.com/";
    private static final String ANY_REASON = "reason";
    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String ANY_SCRIBE_PATH_VERSION = "version";
    private static final String ANY_SCRIBE_PATH_TYPE = "type";
    private static final String ANY_USER_AGENT = "ua";
    private static final String DEVICE_ID_HEADER = "X-Client-UUID";
    private static final String ANY_DEVICE_ID = "id";
    private static final String TWITTER_POLLING_HEADER = "X-Twitter-Polling";
    private static final String REQUIRED_TWITTER_POLLING_HEADER_VALUE = "true";

    private List<SessionManager<? extends Session>> sessionManagers;
    private SessionManager<Session> mockSessionMgr;
    private Session mockSession;
    private RestAdapter mockAdapter;
    private ScribeFilesSender.ScribeService mockService;
    private IdManager mockIdManager;
    private Context context;

    private ScribeFilesSender filesSender;
    private String[] filenames;
    private List<File> tempFiles;

    @Rule
    public final TestResources testResources = new TestResources();

    @Before
    public void setUp() throws Exception {

        context = RuntimeEnvironment.application;
        mockSessionMgr = mock(SessionManager.class);
        mockSession = mock(Session.class);
        when(mockSessionMgr.getSession(anyLong())).thenReturn(mockSession);
        when(mockSession.getAuthToken()).thenReturn(mock(TwitterAuthToken.class));

        mockAdapter = mock(RestAdapter.class);
        mockService = mock(ScribeFilesSender.ScribeService.class);
        when(mockAdapter.create(ScribeFilesSender.ScribeService.class)).thenReturn(mockService);

        mockIdManager = mock(IdManager.class);

        final ScribeConfig scribeConfig = new ScribeConfig(true, ANY_URL, ANY_SCRIBE_PATH_VERSION,
                ANY_SCRIBE_PATH_TYPE, null, ANY_USER_AGENT, ScribeConfig.DEFAULT_MAX_FILES_TO_KEEP,
                ScribeConfig.DEFAULT_SEND_INTERVAL_SECONDS);
        sessionManagers = new ArrayList<>();
        sessionManagers.add(mockSessionMgr);
        filesSender = new ScribeFilesSender(context, scribeConfig,
                ScribeConstants.LOGGED_OUT_USER_ID, mock(TwitterAuthConfig.class), sessionManagers,
                mock(SSLSocketFactory.class), mock(ExecutorService.class), mockIdManager);
        filesSender.setApiAdapter(mockAdapter);

        filenames = new String[] {
                "se_c9666213-d768-45a1-a3ca-5941e4c35f26_1404423214376.tap",
                "se_f6a58964-88aa-4e52-8bf8-d1d461b64392_1404423154382.tap"
        };

        // Read asset files into temporary files that can be passed to the ScribeFilesSender.
        final File outputDir = context.getCacheDir();
        tempFiles = new ArrayList<>(filenames.length);
        final byte[] buffer = new byte[1024];

        for (int i = 0; i < filenames.length; i++) {
            tempFiles.add(File.createTempFile("temp_" + i, ScribeFilesManager.FILE_EXTENSION,
                    outputDir));

            InputStream is = null;
            OutputStream os = null;
            try {
                is = testResources.getAsStream(filenames[i]);
                os = new FileOutputStream(tempFiles.get(i));
                CommonUtils.copyStream(is, os, buffer);
            } finally {
                CommonUtils.closeQuietly(is);
                CommonUtils.closeQuietly(os);
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        for (File f : tempFiles) {
            f.delete();
        }
    }

    private void setUpMockServiceResponse(Response response) {
        when(mockService.upload(anyString(), anyString(), anyString())).thenReturn(response);
        when(mockService.uploadSequence(anyString(), anyString())).thenReturn(response);
    }

    private void setUpMockServiceErrorResponse(Response response) {
        final RetrofitError error = mock(RetrofitError.class);
        when(error.getResponse()).thenReturn(response);

        when(mockService.upload(anyString(), anyString(), anyString())).thenThrow(error);
    }

    private void setUpScribeSequence(String sequence) {
        final ScribeConfig config = new ScribeConfig(true, ANY_URL, ANY_SCRIBE_PATH_VERSION,
                ANY_SCRIBE_PATH_TYPE, sequence, ANY_USER_AGENT,
                ScribeConfig.DEFAULT_MAX_FILES_TO_KEEP, ScribeConfig.DEFAULT_SEND_INTERVAL_SECONDS);

        filesSender = new ScribeFilesSender(context, config,
                ScribeConstants.LOGGED_OUT_USER_ID, mock(TwitterAuthConfig.class), sessionManagers,
                mock(SSLSocketFactory.class), mock(ExecutorService.class), mock(IdManager.class));
        filesSender.setApiAdapter(mockAdapter);
    }

    private Response newResponse(int statusCode) {
        return new Response(ANY_URL, statusCode, ANY_REASON, new ArrayList<Header>(), null);
    }

    // tests follow
    @Test
    public void testGetScribeEventsAsJsonArrayString() throws IOException, JSONException {
        final String jsonArrayString = filesSender.getScribeEventsAsJsonArrayString(tempFiles);

        // Assert that we got back valid json
        final JSONArray jsonArray = new JSONArray(jsonArrayString);
        assertNotNull(jsonArray);
        assertEquals(NUM_SCRIBE_EVENTS, jsonArray.length());
    }

    @Test
    public void testGetApiAdapter_nullSession() {
        filesSender.setApiAdapter(null); // set api adapter to null since we pre-set it in setUp
        when(mockSessionMgr.getSession(anyLong())).thenReturn(null);
        assertNull(filesSender.getApiAdapter());
    }

    @Test
    public void testGetApiAdapter_validSession() {
        when(mockSessionMgr.getSession(anyLong())).thenReturn(mockSession);
        assertNotNull(filesSender.getApiAdapter());
    }

    @Test
    public void testGetApiAdapter_multipleCalls() {
        when(mockSessionMgr.getSession(anyLong())).thenReturn(mockSession);
        final RestAdapter apiAdapter = filesSender.getApiAdapter();
        assertEquals(apiAdapter, filesSender.getApiAdapter());
    }

    @Test
    public void testUpload_noSequence() {
        final String logs = TEST_LOGS;
        setUpScribeSequence(null);
        filesSender.upload(logs);
        verify(mockService).upload(ANY_SCRIBE_PATH_VERSION, ANY_SCRIBE_PATH_TYPE, logs);
    }

    @Test
    public void testUpload_withSequence() {
        final String sequence = "1";
        final String logs = TEST_LOGS;
        setUpScribeSequence(sequence);
        filesSender.upload(logs);
        verify(mockService).uploadSequence(sequence, logs);
    }

    @Test
    public void testSend_nullSession() {
        // Send should fail when we don't have a valid session.
        filesSender.setApiAdapter(null); // set api adapter to null since we pre-set it in setUp
        when(mockSessionMgr.getSession(anyLong())).thenReturn(null);
        assertFalse(filesSender.send(tempFiles));
        verifyZeroInteractions(mockAdapter);
    }

    @Test
    public void testSend_uploadSucceeds() {
        setUpMockServiceResponse(newResponse(HttpStatus.SC_OK));
        assertTrue(filesSender.send(tempFiles));
    }

    @Test
    public void testSend_uploadFailsInternalServerError() {
        setUpMockServiceErrorResponse(newResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR));
        assertTrue(filesSender.send(tempFiles));
        verify(mockService, times(1)).upload(anyString(), anyString(), anyString());
    }

    @Test
    public void testSend_uploadFailsBadRequest() {
        setUpMockServiceErrorResponse(newResponse(HttpStatus.SC_BAD_REQUEST));
        assertTrue(filesSender.send(tempFiles));
        verify(mockService, times(1)).upload(anyString(), anyString(), anyString());
    }

    @Test
    public void testSend_uploadFailsForbidden() {
        setUpMockServiceErrorResponse(newResponse(HttpStatus.SC_FORBIDDEN));
        assertFalse(filesSender.send(tempFiles));
    }

    @Test
    public void testConfigRequestInterceptor_addsPollingHeader() {
        final ScribeConfig config = mock(ScribeConfig.class);
        final RequestInterceptor.RequestFacade facade
                = mock(RequestInterceptor.RequestFacade.class);
        final RequestInterceptor interceptor
                = new ScribeFilesSender.ConfigRequestInterceptor(config, mockIdManager);
        interceptor.intercept(facade);
        verify(facade, times(1)).addHeader(TWITTER_POLLING_HEADER,
                REQUIRED_TWITTER_POLLING_HEADER_VALUE);
    }

    @Test
    public void testConfigRequestInterceptor_nullUserAgent() {
        final ScribeConfig config = new ScribeConfig(true, ScribeConfig.BASE_URL,
                ANY_SCRIBE_PATH_VERSION, ANY_SCRIBE_PATH_TYPE, null, null,
                ScribeConfig.DEFAULT_MAX_FILES_TO_KEEP, ScribeConfig.DEFAULT_SEND_INTERVAL_SECONDS);
        final RequestInterceptor.RequestFacade facade
                = mock(RequestInterceptor.RequestFacade.class);
        final RequestInterceptor interceptor
                = new ScribeFilesSender.ConfigRequestInterceptor(config, mockIdManager);
        interceptor.intercept(facade);
        verify(facade, times(0)).addHeader(eq(USER_AGENT_HEADER), anyString());
    }

    @Test
    public void testConfigRequestInterceptor_anUserAgent() {
        final ScribeConfig config = new ScribeConfig(true, ScribeConfig.BASE_URL,
                ANY_SCRIBE_PATH_VERSION, ANY_SCRIBE_PATH_TYPE, null, ANY_USER_AGENT,
                ScribeConfig.DEFAULT_MAX_FILES_TO_KEEP, ScribeConfig.DEFAULT_SEND_INTERVAL_SECONDS);
        final RequestInterceptor.RequestFacade facade
                = mock(RequestInterceptor.RequestFacade.class);
        final RequestInterceptor interceptor
                = new ScribeFilesSender.ConfigRequestInterceptor(config, mockIdManager);
        interceptor.intercept(facade);
        verify(facade, times(1)).addHeader(USER_AGENT_HEADER, ANY_USER_AGENT);
    }

    @Test
    public void testConfigRequestInterceptor_nullIdManager() {
        final ScribeConfig config = mock(ScribeConfig.class);
        final RequestInterceptor.RequestFacade facade
                = mock(RequestInterceptor.RequestFacade.class);
        final RequestInterceptor interceptor
                = new ScribeFilesSender.ConfigRequestInterceptor(config, mockIdManager);
        interceptor.intercept(facade);
        verify(facade, times(0)).addHeader(eq(DEVICE_ID_HEADER) , anyString());
    }

    @Test
    public void testConfigRequestInterceptor_anIdManager() {
        final ScribeConfig config = mock(ScribeConfig.class);
        final RequestInterceptor.RequestFacade facade
                = mock(RequestInterceptor.RequestFacade.class);
        when(mockIdManager.getDeviceUUID()).thenReturn(ANY_DEVICE_ID);
        final RequestInterceptor interceptor
                = new ScribeFilesSender.ConfigRequestInterceptor(config, mockIdManager);
        interceptor.intercept(facade);
        verify(facade, times(1)).addHeader(DEVICE_ID_HEADER, ANY_DEVICE_ID);
    }
}
