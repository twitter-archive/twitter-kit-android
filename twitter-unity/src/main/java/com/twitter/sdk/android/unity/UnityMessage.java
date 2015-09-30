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

package com.twitter.sdk.android.unity;

import com.unity3d.player.UnityPlayer;

class UnityMessage {
    final String data;
    final String method;

    UnityMessage(String method, String data) {
        this.method = method;
        this.data = data;
    }

    public static class Builder {
        String data = "";
        String method;

        public Builder setData(String data) {
            this.data = data;
            return this;
        }

        public Builder setMethod(String method) {
            this.method = method;
            return this;
        }

        public UnityMessage build() {
            return new UnityMessage(method, data);
        }
    }

    public void send() {
        UnityPlayer.UnitySendMessage(TwitterKit.GAME_OBJECT_NAME, method, data);
    }
}
