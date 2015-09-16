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
import android.text.TextUtils;

import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.common.QueueFile;
import io.fabric.sdk.android.services.events.FilesSender;
import com.twitter.sdk.android.core.AuthenticatedClient;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.SSLSocketFactory;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.MainThreadExecutor;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;

class ScribeFilesSender implements FilesSender {

    private static final String SEND_FILE_FAILURE_ERROR = "Failed sending files";
    /**
     * The string "[" as a byte array.
     */
    private static final byte[] START_JSON_ARRAY = {'['};
    /**
     * The string "," as a byte array.
     */
    private static final byte[] COMMA = {','};
    /**
     * The string "]" as a byte array.
     */
    private static final byte[] END_JSON_ARRAY = {']'};

    private final Context context;
    private final ScribeConfig scribeConfig;
    private final long ownerId;
    private final TwitterAuthConfig authConfig;
    private final List<SessionManager<? extends Session>> sessionManagers;
    private final SSLSocketFactory sslSocketFactory;
    private final AtomicReference<RestAdapter> apiAdapter;
    private final ExecutorService executorService;
    private final IdManager idManager;

    public ScribeFilesSender(Context context, ScribeConfig scribeConfig, long ownerId,
            TwitterAuthConfig authConfig, List<SessionManager<? extends Session>> sessionManagers,
            SSLSocketFactory sslSocketFactory, ExecutorService executorService,
            IdManager idManager) {
        this.context = context;
        this.scribeConfig = scribeConfig;
        this.ownerId = ownerId;
        this.authConfig = authConfig;
        this.sessionManagers = sessionManagers;
        this.sslSocketFactory = sslSocketFactory;
        this.executorService = executorService;
        this.idManager = idManager;
        this.apiAdapter = new AtomicReference<>();
    }

    @Override
    public boolean send(List<File> files) {
        if (hasApiAdapter()) {
            try {
                final String scribeEvents = getScribeEventsAsJsonArrayString(files);
                CommonUtils.logControlled(context, scribeEvents);

                final Response response = upload(scribeEvents);
                if (response.getStatus() == HttpURLConnection.HTTP_OK) {
                    return true;
                } else {
                    CommonUtils.logControlledError(context, SEND_FILE_FAILURE_ERROR, null);
                }
            } catch (RetrofitError e) {
                CommonUtils.logControlledError(context, SEND_FILE_FAILURE_ERROR, e);
                if (e.getResponse() != null &&
                        (e.getResponse().getStatus() == HttpURLConnection.HTTP_INTERNAL_ERROR ||
                         e.getResponse().getStatus() == HttpURLConnection.HTTP_BAD_REQUEST)) {
                    return true;
                }
            } catch (IOException e) {
                CommonUtils.logControlledError(context, SEND_FILE_FAILURE_ERROR, e);
            }
        } else {
            CommonUtils.logControlled(context, "Cannot attempt upload at this time");
        }
        return false;
    }

    String getScribeEventsAsJsonArrayString(List<File> files) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        final boolean[] appendComma = new boolean[1];
        out.write(START_JSON_ARRAY);
        for (File f : files) {
            QueueFile qf = null;
            try {
                qf = new QueueFile(f);
                qf.forEach(new QueueFile.ElementReader() {
                    @Override
                    public void read(InputStream in, int length) throws IOException {
                        final byte[] buf = new byte[length];
                        in.read(buf);

                        if (appendComma[0]) {
                            out.write(COMMA);
                        } else {
                            // First time through we don't append comma, but subsequent times we do
                            appendComma[0] = true;
                        }
                        out.write(buf);
                    }
                });
            } finally {
                CommonUtils.closeQuietly(qf);
            }
        }
        out.write(END_JSON_ARRAY);

        return out.toString("UTF-8");
    }

    /**
     * @return true if we have an api adapter for uploading
     */
    private boolean hasApiAdapter() {
        return getApiAdapter() != null;
    }

    /**
     * For testing purposes only.
     */
    void setApiAdapter(RestAdapter restAdapter) {
        apiAdapter.set(restAdapter);
    }

    /**
     * @return the api adapter, may be {@code null}
     */
    synchronized RestAdapter getApiAdapter() {
        if (apiAdapter.get() == null) {
            final Session session = getSession(ownerId);
            final RequestInterceptor interceptor
                    = new ConfigRequestInterceptor(scribeConfig, idManager);
            if (isValidSession(session)) {
                apiAdapter.compareAndSet(null,
                        new RestAdapter.Builder()
                                .setEndpoint(scribeConfig.baseUrl)
                                .setExecutors(executorService, new MainThreadExecutor())
                                .setRequestInterceptor(interceptor)
                                .setClient(new AuthenticatedClient(authConfig, session,
                                        sslSocketFactory))
                                .build()
                );
            } else {
                CommonUtils.logControlled(context, "No valid session at this time");
            }
        }
        return apiAdapter.get();
    }

    private Session getSession(long ownerId) {
        Session sessionToReturn = null;
        for (SessionManager<? extends Session> sessionManager : sessionManagers) {
            sessionToReturn = sessionManager.getSession(ownerId);
            if (sessionToReturn != null) {
                break;
            }
        }
        return sessionToReturn;
    }

    private boolean isValidSession(Session session) {
        return session != null && session.getAuthToken() != null;
    }

    /**
     * Uploads scribe events. Requires valid apiAdapter.
     */
    Response upload(String scribeEvents) {
        final ScribeService service = apiAdapter.get().create(ScribeService.class);
        if (!TextUtils.isEmpty(scribeConfig.sequence)) {
            return service.uploadSequence(scribeConfig.sequence, scribeEvents);
        } else {
            return service.upload(scribeConfig.pathVersion, scribeConfig.pathType, scribeEvents);
        }
    }

    interface ScribeService {

        @Headers("Content-Type: application/x-www-form-urlencoded;charset=UTF-8")
        @FormUrlEncoded
        @POST("/{version}/jot/{type}")
        Response upload(@Path("version") String version, @Path("type") String type,
                        @Field("log[]") String logs);

        @Headers("Content-Type: application/x-www-form-urlencoded;charset=UTF-8")
        @FormUrlEncoded
        @POST("/scribe/{sequence}")
        Response uploadSequence(@Path("sequence") String sequence, @Field("log[]") String logs);
    }

    // At a certain point we might need to allow either a custom RequestInterceptor to be set
    // by the user of the ScribeClient or a custom map of headers to be supplied.
    static class ConfigRequestInterceptor implements RequestInterceptor {
        private static final String USER_AGENT_HEADER = "User-Agent";
        private static final String CLIENT_UUID_HEADER = "X-Client-UUID";
        private static final String POLLING_HEADER = "X-Twitter-Polling";
        private static final String POLLING_HEADER_VALUE = "true";

        private final ScribeConfig scribeConfig;
        private final IdManager idManager;

        ConfigRequestInterceptor(ScribeConfig scribeConfig, IdManager idManager) {
            this.scribeConfig = scribeConfig;
            this.idManager = idManager;
        }

        @Override
        public void intercept(RequestFacade request) {
            if (!TextUtils.isEmpty(scribeConfig.userAgent)) {
                request.addHeader(USER_AGENT_HEADER, scribeConfig.userAgent);
            }

            /**
             * X-Client-UUID is used to populate the device-id field in the log base structure of
             * the event. It is read by the macaw-core UserTrackingFilter which places the value
             * into the deviceId field on the Request object.
             *
             * The Request object is then read by the RequestRecipient class which is the
             * ClientDataProvider implementation that LogBaseMarshaller uses in science's Scribelib
             *
             * Scribelib in turn is used by Rufous to marshall the data into the scribe structure.
             */
            if (!TextUtils.isEmpty(idManager.getDeviceUUID())) {
                request.addHeader(CLIENT_UUID_HEADER, idManager.getDeviceUUID());
            }

            /**
             * Add the polling header to help guarantee that our scribe request is properly
             * attributed on the backend.
             *
             * See: https://confluence.twitter.biz/display/PIE/Identifying+API+calls+associated+with+background+polling+events
             */
            request.addHeader(POLLING_HEADER, POLLING_HEADER_VALUE);
        }
    }
}
