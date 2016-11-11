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

package com.twitter.sdk.android.core.internal.network;

import com.twitter.sdk.android.core.GuestSessionProvider;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;

public class OkHttpClientHelper {
    public static OkHttpClient getOkHttpClient(GuestSessionProvider guestSessionProvider) {
        return addGuestAuth(new OkHttpClient.Builder(), guestSessionProvider).build();
    }

    public static OkHttpClient getOkHttpClient(Session<? extends TwitterAuthToken> session,
            TwitterAuthConfig authConfig) {
        if (session == null) {
            throw new IllegalArgumentException("Session must not be null.");
        }

        return addSessionAuth(new OkHttpClient.Builder(), session, authConfig).build();
    }

    public static OkHttpClient getCustomOkHttpClient(OkHttpClient httpClient,
            GuestSessionProvider guestSessionProvider) {
        if (httpClient == null) {
            throw new IllegalArgumentException("HttpClient must not be null.");
        }

        return addGuestAuth(httpClient.newBuilder(), guestSessionProvider)
                .build();
    }

    public static OkHttpClient getCustomOkHttpClient(
            OkHttpClient httpClient,
            Session<? extends TwitterAuthToken> session,
            TwitterAuthConfig authConfig) {
        if (session == null) {
            throw new IllegalArgumentException("Session must not be null.");
        }

        if (httpClient == null) {
            throw new IllegalArgumentException("HttpClient must not be null.");
        }

        return addSessionAuth(httpClient.newBuilder(), session, authConfig)
                .build();
    }

    static OkHttpClient.Builder addGuestAuth(OkHttpClient.Builder builder,
                                             GuestSessionProvider guestSessionProvider) {
        return builder
                .certificatePinner(getCertificatePinner())
                .authenticator(new GuestAuthenticator(guestSessionProvider))
                .addInterceptor(new GuestAuthInterceptor(guestSessionProvider))
                .addNetworkInterceptor(new GuestAuthNetworkInterceptor());
    }

    static OkHttpClient.Builder addSessionAuth(OkHttpClient.Builder builder,
                                               Session<? extends TwitterAuthToken> session,
                                               TwitterAuthConfig authConfig) {
        return builder
                .certificatePinner(getCertificatePinner())
                .addInterceptor(new OAuth1aInterceptor(session, authConfig));
    }

    public static CertificatePinner getCertificatePinner() {
        return new CertificatePinner.Builder()
                .add("*.twitter.com", "sha1/I0PRSKJViZuUfUYaeX7ATP7RcLc=") //VERISIGN_CLASS1
                .add("*.twitter.com", "sha1/VRmyeKyygdftp6vBg5nDu2kEJLU=") //VERISIGN_CLASS1_G3
                .add("*.twitter.com", "sha1/Eje6RRfurSkm/cHN/r7t8t7ZFFw=") //VERISIGN_CLASS2_G2
                .add("*.twitter.com", "sha1/Wr7Fddyu87COJxlD/H8lDD32YeM=") //VERISIGN_CLASS2_G3
                .add("*.twitter.com", "sha1/GiG0lStik84Ys2XsnA6TTLOB5tQ=") //VERISIGN_CLASS3_G2
                .add("*.twitter.com", "sha1/IvGeLsbqzPxdI0b0wuj2xVTdXgc=") //VERISIGN_CLASS3_G3
                .add("*.twitter.com", "sha1/7WYxNdMb1OymFMQp4xkGn5TBJlA=") //VERISIGN_CLASS3_G4
                .add("*.twitter.com", "sha1/sYEIGhmkwJQf+uiVKMEkyZs0rMc=") //VERISIGN_CLASS3_G5
                .add("*.twitter.com", "sha1/PANDaGiVHPNpKri0Jtq6j+ki5b0=") //VERISIGN_CLASS4_G3
                .add("*.twitter.com", "sha1/u8I+KQuzKHcdrT6iTb30I70GsD0=") //VERISIGN_UNIVERSAL
                .add("*.twitter.com", "sha1/wHqYaI2J+6sFZAwRfap9ZbjKzE4=") //GEOTRUST_GLOBAL
                .add("*.twitter.com", "sha1/cTg28gIxU0crbrplRqkQFVggBQk=") //GEOTRUST_GLOBAL2
                .add("*.twitter.com", "sha1/sBmJ5+/7Sq/LFI9YRjl2IkFQ4bo=") //GEOTRUST_PRIMARY
                .add("*.twitter.com", "sha1/vb6nG6txV/nkddlU0rcngBqCJoI=") //GEOTRUST_PRIMARY_G2
                .add("*.twitter.com", "sha1/nKmNAK90Dd2BgNITRaWLjy6UONY=") //GEOTRUST_PRIMARY_G3
                .add("*.twitter.com", "sha1/h+hbY1PGI6MSjLD/u/VR/lmADiI=") //GEOTRUST_UNIVERAL
                .add("*.twitter.com", "sha1/Xk9ThoXdT57KX9wNRW99UbHcm3s=") //GEOTRUST_UNIVERSAL2
                .add("*.twitter.com", "sha1/1S4TwavjSdrotJWU73w4Q2BkZr0=") //DIGICERT_GLOBAL_ROOT
                .add("*.twitter.com", "sha1/gzF+YoVCU9bXeDGQ7JGQVumRueM=") //DIGICERT_EV_ROOT
                .add("*.twitter.com", "sha1/aDMOYTWFIVkpg6PI0tLhQG56s8E=") //DIGICERT_ASSUREDID_ROOT
                .add("*.twitter.com", "sha1/Vv7zwhR9TtOIN/29MFI4cgHld40=") //TWITTER1
                .build();
    }
}
