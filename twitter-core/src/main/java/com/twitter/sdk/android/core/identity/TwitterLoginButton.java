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

package com.twitter.sdk.android.core.identity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.R;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.CommonUtils;

import java.lang.ref.WeakReference;

/**
 * Log in button for logging into Twitter. When the button is clicked, an authorization request
 * is started and the user is presented with a screen requesting access to the user's Twitter
 * account. If successful, a {@link com.twitter.sdk.android.core.TwitterSession} is provided
 * in the {@link com.twitter.sdk.android.core.Callback#success(com.twitter.sdk.android.core.Result)}
 */
public class TwitterLoginButton extends Button {
    static final String TAG = TwitterCore.TAG;
    static final String ERROR_MSG_NO_ACTIVITY = "TwitterLoginButton requires an activity."
            + " Override getActivity to provide the activity for this button.";

    final WeakReference<Activity> activityRef;
    volatile TwitterAuthClient authClient;
    OnClickListener onClickListener;
    Callback<TwitterSession> callback;

    public TwitterLoginButton(Context context) {
        this(context, null);
    }

    public TwitterLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
    }

    public TwitterLoginButton(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, null);
    }

    TwitterLoginButton(Context context, AttributeSet attrs, int defStyle,
            TwitterAuthClient authClient) {
        super(context, attrs, defStyle);
        this.activityRef = new WeakReference<>(getActivity());
        this.authClient = authClient;
        setupButton();

        checkTwitterCoreAndEnable();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupButton() {
        final Resources res = getResources();
        super.setCompoundDrawablesWithIntrinsicBounds(
                res.getDrawable(R.drawable.tw__ic_logo_default), null, null, null);
        super.setCompoundDrawablePadding(
                res.getDimensionPixelSize(R.dimen.tw__login_btn_drawable_padding));
        super.setText(R.string.tw__login_btn_txt);
        super.setTextColor(res.getColor(R.color.tw__solid_white));
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                res.getDimensionPixelSize(R.dimen.tw__login_btn_text_size));
        super.setTypeface(Typeface.DEFAULT_BOLD);
        super.setPadding(res.getDimensionPixelSize(R.dimen.tw__login_btn_left_padding), 0,
                res.getDimensionPixelSize(R.dimen.tw__login_btn_right_padding), 0);
        super.setBackgroundResource(R.drawable.tw__login_btn);
        super.setOnClickListener(new LoginClickListener());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.setAllCaps(false);
        }
    }

    /**
     * Sets the {@link com.twitter.sdk.android.core.Callback} to invoke when login completes.
     *
     * @param callback The callback interface to invoke when login completes.
     * @throws java.lang.IllegalArgumentException if callback is null.
     */
    public void setCallback(Callback<TwitterSession> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }
        this.callback = callback;
    }

    /**
     * @return the current {@link com.twitter.sdk.android.core.Callback}
     */
    public Callback<TwitterSession> getCallback() {
        return callback;
    }

    /**
     * Call this method when {@link android.app.Activity#onActivityResult(int, int, Intent)}
     * is called to complete the authorization flow.
     *
     * @param requestCode the request code used for SSO
     * @param resultCode the result code returned by the SSO activity
     * @param data the result data returned by the SSO activity
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == getTwitterAuthClient().getRequestCode()) {
            getTwitterAuthClient().onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Gets the activity. Override this method if this button was created with a non-Activity
     * context.
     */
    protected Activity getActivity() {
        if (getContext() instanceof ContextThemeWrapper &&
                ((ContextThemeWrapper) getContext()).getBaseContext() instanceof Activity) {
            return (Activity) ((ContextThemeWrapper) getContext()).getBaseContext();
        } else if (getContext() instanceof Activity) {
            return (Activity) getContext();
        } else if (isInEditMode()) {
            return null;
        } else {
            throw new IllegalStateException(ERROR_MSG_NO_ACTIVITY);
        }
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private class LoginClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            checkCallback(callback);
            checkActivity(activityRef.get());

            getTwitterAuthClient().authorize(activityRef.get(), callback);

            if (onClickListener != null) {
                onClickListener.onClick(view);
            }
        }

        private void checkCallback(Callback callback) {
            if (callback == null) {
                CommonUtils.logOrThrowIllegalStateException(TwitterCore.TAG,
                        "Callback must not be null, did you call setCallback?");
            }
        }

        private void checkActivity(Activity activity) {
            if (activity == null || activity.isFinishing()) {
                CommonUtils.logOrThrowIllegalStateException(TwitterCore.TAG,
                        ERROR_MSG_NO_ACTIVITY);
            }
        }
    }

    TwitterAuthClient getTwitterAuthClient() {
        if (authClient == null) {
            synchronized (TwitterLoginButton.class) {
                if (authClient == null) {
                    authClient = new TwitterAuthClient();
                }
            }
        }
        return authClient;
    }

    private void checkTwitterCoreAndEnable() {
        //Default (Enabled) in edit mode
        if (isInEditMode()) return;

        try {
            TwitterCore.getInstance();
        } catch (IllegalStateException ex) {
            //Disable if TwitterCore hasn't started
            Twitter.getLogger().e(TAG, ex.getMessage());
            setEnabled(false);
        }
    }
}
