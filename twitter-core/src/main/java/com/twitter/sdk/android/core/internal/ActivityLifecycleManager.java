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

package com.twitter.sdk.android.core.internal;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.HashSet;
import java.util.Set;

/**
 * This is a convenience class that wraps the ActivityLifecycleCallbacks registration. It provides
 * an abstract Callbacks class that reduces required boilerplate code in your callbacks as well as
 * OS Version checks that make it compatible with Android versions less than Ice Cream Sandwich.
 */
public class ActivityLifecycleManager {
    private final ActivityLifecycleCallbacksWrapper callbacksWrapper;

    /**
     * Override the methods corresponding to the activity.
     */
    public abstract static class Callbacks {
        public void onActivityCreated(Activity activity, Bundle bundle) {}
        public void onActivityStarted(Activity activity) {}
        public void onActivityResumed(Activity activity) {}
        public void onActivityPaused(Activity activity) {}
        public void onActivityStopped(Activity activity) {}
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}
        public void onActivityDestroyed(Activity activity) {}
    }

    /**
     * @param context Any context object, it is not stored
     */
    public ActivityLifecycleManager(Context context) {
        final Application application = (Application) context.getApplicationContext();
        callbacksWrapper = new ActivityLifecycleCallbacksWrapper(application);
    }

    /**
     * @param callbacks The callbacks
     * @return true if the version of the application context supports registering lifecycle
     * callbacks
     */
    public boolean registerCallbacks(Callbacks callbacks) {
        return callbacksWrapper != null &&
                callbacksWrapper.registerLifecycleCallbacks(callbacks);
    }

    /**
     * Unregisters all previously registered callbacks on the application context.
     */
    public void resetCallbacks() {
        if (callbacksWrapper != null) {
            callbacksWrapper.clearCallbacks();
        }
    }

    private static class ActivityLifecycleCallbacksWrapper {
        private final Set<Application.ActivityLifecycleCallbacks> registeredCallbacks =
                new HashSet<>();
        private final Application application;

        ActivityLifecycleCallbacksWrapper(Application application) {
            this.application = application;
        }

        private void clearCallbacks() {
            for (Application.ActivityLifecycleCallbacks callback : registeredCallbacks) {
                application.unregisterActivityLifecycleCallbacks(callback);
            }
        }

        private boolean registerLifecycleCallbacks(final Callbacks callbacks) {

            if (application != null) {
                final Application.ActivityLifecycleCallbacks callbackWrapper =
                        new Application.ActivityLifecycleCallbacks() {

                            @Override
                            public void onActivityCreated(Activity activity, Bundle bundle) {
                                callbacks.onActivityCreated(activity, bundle);
                            }

                            @Override
                            public void onActivityStarted(Activity activity) {
                                callbacks.onActivityStarted(activity);
                            }

                            @Override
                            public void onActivityResumed(Activity activity) {
                                callbacks.onActivityResumed(activity);
                            }

                            @Override
                            public void onActivityPaused(Activity activity) {
                                callbacks.onActivityPaused(activity);
                            }

                            @Override
                            public void onActivityStopped(Activity activity) {
                                callbacks.onActivityStopped(activity);
                            }

                            @Override
                            public void onActivitySaveInstanceState(Activity activity,
                                    Bundle bundle) {
                                callbacks.onActivitySaveInstanceState(activity, bundle);
                            }

                            @Override
                            public void onActivityDestroyed(Activity activity) {
                                callbacks.onActivityDestroyed(activity);
                            }
                        };
                application.registerActivityLifecycleCallbacks(callbackWrapper);
                this.registeredCallbacks.add(callbackWrapper);
                return true;
            }
            return false;
        }
    }
}
