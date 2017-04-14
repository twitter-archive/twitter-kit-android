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

package com.twitter.sdk.android.core.internal.oauth;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.TwitterApi;
import com.twitter.sdk.android.core.internal.network.UrlUtils;

import okio.ByteString;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * OAuth2.0 service. Provides methods for requesting guest auth tokens.
 */
public class OAuth2Service extends OAuthService {
    OAuth2Api api;

    interface OAuth2Api {
        @POST("/1.1/guest/activate.json")
        Call<GuestTokenResponse> getGuestToken(
                @Header(OAuthConstants.HEADER_AUTHORIZATION) String auth);

        @Headers("Content-Type: application/x-www-form-urlencoded;charset=UTF-8")
        @FormUrlEncoded
        @POST("/oauth2/token")
        Call<OAuth2Token> getAppAuthToken(@Header(OAuthConstants.HEADER_AUTHORIZATION) String auth,
                                          @Field(OAuthConstants.PARAM_GRANT_TYPE) String grantType);
    }

    public OAuth2Service(TwitterCore twitterCore, TwitterApi api) {
        super(twitterCore, api);
        this.api = getRetrofit().create(OAuth2Api.class);
    }

    /**
     * Requests a guest auth token.
     * @param callback The callback interface to invoke when when the request completes.
     */
    public void requestGuestAuthToken(final Callback<GuestAuthToken> callback) {
        final Callback<OAuth2Token> appAuthCallback = new Callback<OAuth2Token>() {
            @Override
            public void success(Result<OAuth2Token> result) {
                final OAuth2Token appAuthToken = result.data;
                // Got back an app auth token, now request a guest auth token.
                final Callback<GuestTokenResponse> guestTokenCallback
                        = new Callback<GuestTokenResponse>() {
                    @Override
                    public void success(Result<GuestTokenResponse> result) {
                        // Return a GuestAuthToken that includes the guestToken.
                        final GuestAuthToken guestAuthToken = new GuestAuthToken(
                                appAuthToken.getTokenType(), appAuthToken.getAccessToken(),
                                result.data.guestToken);
                        callback.success(new Result<>(guestAuthToken, null));
                    }

                    @Override
                    public void failure(TwitterException error) {
                        Twitter.getLogger().e(TwitterCore.TAG,
                                "Your app may not allow guest auth. Please talk to us "
                                        + "regarding upgrading your consumer key.", error);
                        callback.failure(error);
                    }
                };
                requestGuestToken(guestTokenCallback, appAuthToken);
            }

            @Override
            public void failure(TwitterException error) {
                Twitter.getLogger().e(TwitterCore.TAG, "Failed to get app auth token", error);
                if (callback != null) {
                    callback.failure(error);
                }
            }
        };
        requestAppAuthToken(appAuthCallback);
    }

    /**
     * Requests an application-only auth token.
     *
     * @param callback The callback interface to invoke when when the request completes.
     */
    void requestAppAuthToken(final Callback<OAuth2Token> callback) {
        api.getAppAuthToken(getAuthHeader(), OAuthConstants.GRANT_TYPE_CLIENT_CREDENTIALS)
                .enqueue(callback);
    }

    /**
     * Requests a guest token.
     *
     * @param callback The callback interface to invoke when when the request completes.
     * @param appAuthToken The application-only auth token.
     */
    void requestGuestToken(final Callback<GuestTokenResponse> callback,
            OAuth2Token appAuthToken) {
        api.getGuestToken(getAuthorizationHeader(appAuthToken)).enqueue(callback);
    }

    /**
     * Gets authorization header for inclusion in HTTP request headers.
     */
    private String getAuthorizationHeader(OAuth2Token token) {
        return OAuthConstants.AUTHORIZATION_BEARER + " " + token.getAccessToken();
    }

    private String getAuthHeader() {
        final TwitterAuthConfig authConfig = getTwitterCore().getAuthConfig();
        final ByteString string = ByteString.encodeUtf8(
                UrlUtils.percentEncode(authConfig.getConsumerKey())
                + ":"
                + UrlUtils.percentEncode(authConfig.getConsumerSecret()));

        return OAuthConstants.AUTHORIZATION_BASIC + " " + string.base64();
    }
}
