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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;


import com.twitter.sdk.android.core.Twitter;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

class AdvertisingInfoServiceStrategy implements AdvertisingInfoStrategy {

    private static final String GOOGLE_PLAY_SERVICE_PACKAGE_NAME = "com.android.vending";
    private static final String GOOGLE_PLAY_SERVICES_INTENT
            = "com.google.android.gms.ads.identifier.service.START";
    private static final String GOOGLE_PLAY_SERVICES_INTENT_PACKAGE_NAME = "com.google.android.gms";
    private final Context context;

    AdvertisingInfoServiceStrategy(Context context) {
        this.context = context.getApplicationContext();
    }

    public AdvertisingInfo getAdvertisingInfo() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Twitter.getLogger().d(Twitter.TAG,
                    "AdvertisingInfoServiceStrategy cannot be called on the main thread");
            return null;
        }

        try {
            final PackageManager pm = context.getPackageManager();
            pm.getPackageInfo(GOOGLE_PLAY_SERVICE_PACKAGE_NAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            // Can happen if the device doesn't have Google play services
            // (Genymotion emulator is an example)
            Twitter.getLogger().d(Twitter.TAG, "Unable to find Google Play Services package name");
            return null;
        } catch (Exception e) {
            // android.os.TransactionTooLargeException can be thrown when there are many
            // transactions in progress even when most of the individual transactions are below the
            // 1MB buffer size.
            Twitter.getLogger().d(Twitter.TAG,
                    "Unable to determine if Google Play Services is available", e);
            return null;
        }

        final AdvertisingConnection connection = new AdvertisingConnection();
        final Intent intent = new Intent(GOOGLE_PLAY_SERVICES_INTENT);
        intent.setPackage(GOOGLE_PLAY_SERVICES_INTENT_PACKAGE_NAME);
        try {
            if (context.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
                try {
                    final AdvertisingInterface adInterface
                            = new AdvertisingInterface(connection.getBinder());
                    return new AdvertisingInfo(adInterface.getId(),
                            adInterface.isLimitAdTrackingEnabled());
                } catch (Exception e) {
                    Twitter.getLogger().w(Twitter.TAG,
                            "Exception in binding to Google Play Service to capture AdvertisingId",
                            e);
                } finally {
                    context.unbindService(connection);
                }
            } else {
                Twitter.getLogger().d(Twitter.TAG,
                        "Could not bind to Google Play Service to capture AdvertisingId");
            }
        } catch (Throwable t) {
            Twitter.getLogger().d(Twitter.TAG,
                    "Could not bind to Google Play Service to capture AdvertisingId", t);
        }

        return null;
    }

    /**
     * Connection used in binding to Google Play services
     */
    private static final class AdvertisingConnection implements ServiceConnection {
        private static final int QUEUE_TIMEOUT_IN_MS = 200;

        private boolean retrieved;
        // LinkedBlockingQueue(1) ensures that the connection only ever talks to 1 service at a time
        private final LinkedBlockingQueue<IBinder> queue = new LinkedBlockingQueue<IBinder>(1);

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                queue.put(service);
            } catch (InterruptedException e) {
                // no op
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name){
            queue.clear();
        }

        IBinder getBinder()  {
            if (retrieved) {
                Twitter.getLogger().e(Twitter.TAG, "getBinder already called");
            }
            retrieved = true;

            try {
                // The queue should be populated almost instantly (1-2ms) by onServiceConnected
                // However in the case of a case of a crash, onServiceConnected isn't always called
                // and queue.take would hang forever, so we'll give it 10ms before failing.
                return queue.poll(QUEUE_TIMEOUT_IN_MS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // no op
            }
            return null;
        }
    }

    /**
     * Interface for parsing the data from the Advertising Service Binder
     * This Parcel is undocumented. Example found here:
     * http://stackoverflow.com/questions/20097506/using-the-new-android-advertiser-id-inside-an-sdk
     */
    private static final class AdvertisingInterface implements IInterface {
        private static final int FLAGS_NONE = 0;
        // Transaction codes are defined by the (undocumented) service binder
        // Code 1 returns the Advertising id
        private static final int AD_TRANSACTION_CODE_ID = 1;
        // Code 2 returns the isLimitAdTrackingEnabled boolean property
        private static final int AD_TRANSACTION_CODE_LIMIT_AD_TRACKING = 2;

        private static final String ADVERTISING_ID_SERVICE_INTERFACE_TOKEN
                = "com.google.android.gms.ads.identifier.internal.IAdvertisingIdService";

        private final IBinder binder;

        private AdvertisingInterface(IBinder binder) {
            this.binder = binder;
        }

        @Override
        public IBinder asBinder() {
            return binder;
        }

        public String getId() throws RemoteException {
            final Parcel data = Parcel.obtain();
            final Parcel reply = Parcel.obtain();
            String id = null;
            try {
                data.writeInterfaceToken(ADVERTISING_ID_SERVICE_INTERFACE_TOKEN);
                binder.transact(AD_TRANSACTION_CODE_ID, data, reply, FLAGS_NONE);
                reply.readException();
                id = reply.readString();
            } catch (Exception e) {
                Twitter.getLogger().d(Twitter.TAG,
                        "Could not get parcel from Google Play Service to capture AdvertisingId");
            } finally {
                reply.recycle();
                data.recycle();
            }
            return id;
        }

        private boolean isLimitAdTrackingEnabled() throws RemoteException {
            final Parcel data = Parcel.obtain();
            final Parcel reply = Parcel.obtain();
            boolean limitAdTracking = false;
            try {
                data.writeInterfaceToken(ADVERTISING_ID_SERVICE_INTERFACE_TOKEN);
                data.writeInt(1);
                binder.transact(AD_TRANSACTION_CODE_LIMIT_AD_TRACKING, data, reply, FLAGS_NONE);
                reply.readException();
                limitAdTracking = 0 != reply.readInt();
            } catch (Exception e) {
                Twitter.getLogger().d(Twitter.TAG,
                        "Could not get parcel from Google Play Service"
                                + " to capture Advertising limitAdTracking");
            } finally {
                reply.recycle();
                data.recycle();
            }
            return limitAdTracking;
        }
    }
}
