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

import com.twitter.sdk.android.core.GuestSession;
import com.twitter.sdk.android.core.GuestSessionProvider;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TestResources;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.CommonUtils;
import com.twitter.sdk.android.core.internal.IdManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.Calls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
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

    private SessionManager<TwitterSession> mockSessionMgr;
    private GuestSessionProvider mockGuestSessionProvider;
    private TwitterSession mockSession;
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
        mockGuestSessionProvider = mock(GuestSessionProvider.class);
        mockSession = mock(TwitterSession.class);
        when(mockSessionMgr.getSession(anyLong())).thenReturn(mockSession);
        when(mockSession.getAuthToken()).thenReturn(mock(TwitterAuthToken.class));

        mockService = mock(ScribeFilesSender.ScribeService.class);

        mockIdManager = mock(IdManager.class);

        final ScribeConfig scribeConfig = new ScribeConfig(true, ANY_URL, ANY_SCRIBE_PATH_VERSION,
                ANY_SCRIBE_PATH_TYPE, null, ANY_USER_AGENT, ScribeConfig.DEFAULT_MAX_FILES_TO_KEEP,
                ScribeConfig.DEFAULT_SEND_INTERVAL_SECONDS);
        filesSender = new ScribeFilesSender(context, scribeConfig,
                GuestSession.LOGGED_OUT_USER_ID, mock(TwitterAuthConfig.class), mockSessionMgr,
                mockGuestSessionProvider, mock(ExecutorService.class), mockIdManager);
        filesSender.setScribeService(mockService);

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

    private void setUpMockServiceResponse(Call<ResponseBody> response) {
        when(mockService.upload(anyString(), anyString(), anyString()))
                .thenReturn(response);
        when(mockService.uploadSequence(anyString(), anyString()))
                .thenReturn(response);
    }

    private void setUpScribeSequence(String sequence) {
        final ScribeConfig config = new ScribeConfig(true, ANY_URL, ANY_SCRIBE_PATH_VERSION,
                ANY_SCRIBE_PATH_TYPE, sequence, ANY_USER_AGENT,
                ScribeConfig.DEFAULT_MAX_FILES_TO_KEEP, ScribeConfig.DEFAULT_SEND_INTERVAL_SECONDS);

        filesSender = new ScribeFilesSender(context, config,
                GuestSession.LOGGED_OUT_USER_ID, mock(TwitterAuthConfig.class), mockSessionMgr,
                mockGuestSessionProvider, mock(ExecutorService.class), mock(IdManager.class));
        filesSender.setScribeService(mockService);
    }

    private Call<ResponseBody> successResponse() {
        final ResponseBody body = ResponseBody.create(MediaType.parse("application/json"), "");
        return Calls.response(body);
    }

    private Call<ResponseBody> errorResponse(int statusCode) {
        final ResponseBody body = ResponseBody.create(MediaType.parse("application/json"), "");
        return Calls.response(Response.<ResponseBody>error(statusCode, body));
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
    public void testGetApiAdapter_nullUserSession() {
        filesSender.setScribeService(null); // set api adapter to null since we pre-set it in setUp
        when(mockSessionMgr.getSession(anyLong())).thenReturn(null);
        assertNotNull(filesSender.getScribeService());
    }

    @Test
    public void testGetApiAdapter_validSession() {
        when(mockSessionMgr.getSession(anyLong())).thenReturn(mockSession);
        assertNotNull(filesSender.getScribeService());
    }

    @Test
    public void testGetApiAdapter_multipleCalls() {
        when(mockSessionMgr.getSession(anyLong())).thenReturn(mockSession);
        final ScribeFilesSender.ScribeService service = filesSender.getScribeService();
        assertEquals(service, filesSender.getScribeService());
    }

    @Test
    public void testUpload_noSequence() throws IOException{
        final String logs = TEST_LOGS;
        setUpMockServiceResponse(successResponse());
        setUpScribeSequence(null);
        filesSender.upload(logs);
        verify(mockService).upload(ANY_SCRIBE_PATH_VERSION, ANY_SCRIBE_PATH_TYPE, logs);
    }

    @Test
    public void testUpload_withSequence() throws IOException {
        final String sequence = "1";
        final String logs = TEST_LOGS;
        setUpMockServiceResponse(successResponse());
        setUpScribeSequence(sequence);
        filesSender.upload(logs);
        verify(mockService).uploadSequence(sequence, logs);
    }

    @Test
    public void testSend_uploadSucceeds() {
        setUpMockServiceResponse(successResponse());
        assertTrue(filesSender.send(tempFiles));
    }

    @Test
    public void testSend_uploadFailsInternalServerError() {
        setUpMockServiceResponse(errorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR));
        assertTrue(filesSender.send(tempFiles));
        verify(mockService, times(1)).upload(anyString(), anyString(), anyString());
    }

    @Test
    public void testSend_uploadFailsBadRequest() {
        setUpMockServiceResponse(errorResponse(HttpURLConnection.HTTP_BAD_REQUEST));
        assertTrue(filesSender.send(tempFiles));
        verify(mockService, times(1)).upload(anyString(), anyString(), anyString());
    }

    @Test
    public void testSend_uploadFailsForbidden() {
        setUpMockServiceResponse(errorResponse(HttpURLConnection.HTTP_FORBIDDEN));
        assertFalse(filesSender.send(tempFiles));
    }

    public Interceptor.Chain createMockChain() throws IOException{
        final Request request = new Request.Builder().url("https://dummy.com").build();
        final Interceptor.Chain chain  = mock(Interceptor.Chain.class);
        when(chain.request()).thenReturn(request);
        when(chain.proceed(any(Request.class))).thenAnswer(invocation -> {
            final Object[] args = invocation.getArguments();
            return new okhttp3.Response.Builder()
                    .protocol(Protocol.HTTP_1_1)
                    .code(HttpURLConnection.HTTP_OK)
                    .message("OK")
                    .request((Request) args[0])
                    .build();
        });
        return chain;
    }

    @Test
    public void testConfigRequestInterceptor_addsPollingHeader() throws IOException {
        final ScribeConfig config = mock(ScribeConfig.class);
        final Interceptor interceptor
                = new ScribeFilesSender.ConfigRequestInterceptor(config, mockIdManager);

        final Request request = interceptor.intercept(createMockChain()).request();

        assertEquals(REQUIRED_TWITTER_POLLING_HEADER_VALUE, request.header(TWITTER_POLLING_HEADER));
    }

    @Test
    public void testConfigRequestInterceptor_nullUserAgent() throws IOException {
        final ScribeConfig config = new ScribeConfig(true, ScribeConfig.BASE_URL,
                ANY_SCRIBE_PATH_VERSION, ANY_SCRIBE_PATH_TYPE, null, null,
                ScribeConfig.DEFAULT_MAX_FILES_TO_KEEP, ScribeConfig.DEFAULT_SEND_INTERVAL_SECONDS);
        final Interceptor interceptor
                = new ScribeFilesSender.ConfigRequestInterceptor(config, mockIdManager);

        final Request request = interceptor.intercept(createMockChain()).request();

        assertNull(request.header(USER_AGENT_HEADER));
    }

    @Test
    public void testConfigRequestInterceptor_anUserAgent() throws IOException {
        final ScribeConfig config = new ScribeConfig(true, ScribeConfig.BASE_URL,
                ANY_SCRIBE_PATH_VERSION, ANY_SCRIBE_PATH_TYPE, null, ANY_USER_AGENT,
                ScribeConfig.DEFAULT_MAX_FILES_TO_KEEP, ScribeConfig.DEFAULT_SEND_INTERVAL_SECONDS);
        final Interceptor interceptor
                = new ScribeFilesSender.ConfigRequestInterceptor(config, mockIdManager);

        final Request request = interceptor.intercept(createMockChain()).request();

        assertEquals(ANY_USER_AGENT, request.header(USER_AGENT_HEADER));
    }

    @Test
    public void testConfigRequestInterceptor_nullIdManager() throws IOException {
        final ScribeConfig config = mock(ScribeConfig.class);
        final Interceptor interceptor
                = new ScribeFilesSender.ConfigRequestInterceptor(config, mockIdManager);

        final Request request = interceptor.intercept(createMockChain()).request();

        assertNull(request.header(DEVICE_ID_HEADER));
    }

    @Test
    public void testConfigRequestInterceptor_anIdManager() throws IOException {
        final ScribeConfig config = mock(ScribeConfig.class);
        final Interceptor interceptor
                = new ScribeFilesSender.ConfigRequestInterceptor(config, mockIdManager);
        when(mockIdManager.getDeviceUUID()).thenReturn(ANY_DEVICE_ID);

        final Request request = interceptor.intercept(createMockChain()).request();

        assertEquals(ANY_DEVICE_ID, request.header(DEVICE_ID_HEADER));
    }
}
