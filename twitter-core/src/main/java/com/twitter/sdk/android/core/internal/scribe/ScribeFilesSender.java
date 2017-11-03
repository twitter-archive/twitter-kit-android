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

import com.twitter.sdk.android.core.GuestSessionProvider;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.internal.CommonUtils;
import com.twitter.sdk.android.core.internal.IdManager;
import com.twitter.sdk.android.core.internal.network.GuestAuthInterceptor;
import com.twitter.sdk.android.core.internal.network.OAuth1aInterceptor;
import com.twitter.sdk.android.core.internal.network.OkHttpClientHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

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
    private final SessionManager<? extends Session<TwitterAuthToken>> sessionManager;
    private final GuestSessionProvider guestSessionProvider;
    private final AtomicReference<ScribeService> scribeService;
    private final ExecutorService executorService;
    private final IdManager idManager;

    ScribeFilesSender(Context context, ScribeConfig scribeConfig, long ownerId,
            TwitterAuthConfig authConfig,
            SessionManager<? extends Session<TwitterAuthToken>> sessionManager,
            GuestSessionProvider guestSessionProvider, ExecutorService executorService,
            IdManager idManager) {
        this.context = context;
        this.scribeConfig = scribeConfig;
        this.ownerId = ownerId;
        this.authConfig = authConfig;
        this.sessionManager = sessionManager;
        this.guestSessionProvider = guestSessionProvider;
        this.executorService = executorService;
        this.idManager = idManager;
        this.scribeService = new AtomicReference<>();
    }

    @Override
    public boolean send(List<File> files) {
        if (hasApiAdapter()) {
            try {
                final String scribeEvents = getScribeEventsAsJsonArrayString(files);
                CommonUtils.logControlled(context, scribeEvents);

                final Response<ResponseBody> response = upload(scribeEvents);
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    return true;
                } else {
                    CommonUtils.logControlledError(context, SEND_FILE_FAILURE_ERROR, null);
                    if (response.code() == HttpURLConnection.HTTP_INTERNAL_ERROR ||
                            response.code() == HttpURLConnection.HTTP_BAD_REQUEST) {
                        return true;
                    }
                }
            } catch (Exception e) {
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
                qf.forEach((in, length) -> {
                    final byte[] buf = new byte[length];
                    in.read(buf);

                    if (appendComma[0]) {
                        out.write(COMMA);
                    } else {
                        // First time through we don't append comma, but subsequent times we do
                        appendComma[0] = true;
                    }
                    out.write(buf);
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
        return getScribeService() != null;
    }

    /**
     * For testing purposes only.
     */
    void setScribeService(ScribeService restAdapter) {
        scribeService.set(restAdapter);
    }

    /**
     * @return the api adapter, may be {@code null}
     */
    synchronized ScribeService getScribeService() {
        if (scribeService.get() == null) {
            final Session session = getSession(ownerId);
            final OkHttpClient client;
            if (isValidSession(session)) {
                client = new OkHttpClient.Builder()
                        .certificatePinner(OkHttpClientHelper.getCertificatePinner())
                        .addInterceptor(new ConfigRequestInterceptor(scribeConfig, idManager))
                        .addInterceptor(new OAuth1aInterceptor(session, authConfig))
                        .build();
            } else {
                client = new OkHttpClient.Builder()
                        .certificatePinner(OkHttpClientHelper.getCertificatePinner())
                        .addInterceptor(new ConfigRequestInterceptor(scribeConfig, idManager))
                        .addInterceptor(new GuestAuthInterceptor(guestSessionProvider))
                        .build();
            }

            final Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(scribeConfig.baseUrl)
                    .client(client)
                    .build();

            scribeService.compareAndSet(null, retrofit.create(ScribeService.class));
        }

        return scribeService.get();
    }

    private Session getSession(long ownerId) {
        return sessionManager.getSession(ownerId);
    }

    private boolean isValidSession(Session session) {
        return session != null && session.getAuthToken() != null;
    }

    /**
     * Uploads scribe events. Requires valid scribeService.
     */
    Response<ResponseBody> upload(String scribeEvents) throws IOException {
        final ScribeService service = getScribeService();
        if (!TextUtils.isEmpty(scribeConfig.sequence)) {
            return service.uploadSequence(scribeConfig.sequence, scribeEvents).execute();
        } else {
            return service.upload(scribeConfig.pathVersion, scribeConfig.pathType, scribeEvents)
                    .execute();
        }
    }

    interface ScribeService {

        @Headers("Content-Type: application/x-www-form-urlencoded;charset=UTF-8")
        @FormUrlEncoded
        @POST("/{version}/jot/{type}")
        Call<ResponseBody> upload(@Path("version") String version,
                                  @Path("type") String type,
                                  @Field("log[]") String logs);

        @Headers("Content-Type: application/x-www-form-urlencoded;charset=UTF-8")
        @FormUrlEncoded
        @POST("/scribe/{sequence}")
        Call<ResponseBody> uploadSequence(@Path("sequence") String sequence,
                                          @Field("log[]") String logs);
    }

    // At a certain point we might need to allow either a custom RequestInterceptor to be set
    // by the user of the ScribeClient or a custom map of headers to be supplied.
    static class ConfigRequestInterceptor implements Interceptor {
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
        public okhttp3.Response intercept(Chain chain) throws IOException {
            final Request.Builder builder = chain.request().newBuilder();
            if (!TextUtils.isEmpty(scribeConfig.userAgent)) {
                builder.header(USER_AGENT_HEADER, scribeConfig.userAgent);
            }

            /*
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
                builder.header(CLIENT_UUID_HEADER, idManager.getDeviceUUID());
            }

            /*
             * Add the polling header to help guarantee that our scribe request is properly
             * attributed on the backend.
             *
             * See: https://confluence.twitter.biz/display/PIE/Identifying+API+calls+associated+with+background+polling+events
             */
            builder.header(POLLING_HEADER, POLLING_HEADER_VALUE);

            return chain.proceed(builder.build());
        }
    }
}
