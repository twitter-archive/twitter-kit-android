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

package com.twitter.sdk.android.tweetcomposer;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ComposerView extends LinearLayout {
    EditText tweetEditView;
    TextView charCountView;
    Button tweetButton;
    ComposerController.ComposerCallbacks callbacks;

    public ComposerView(Context context) {
        this(context, null);
    }

    public ComposerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ComposerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.tw__composer_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findSubviews();

        tweetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callbacks.onTweetPost();
            }
        });

        tweetEditView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                callbacks.onTweetPost();
                return true;
            }
        });

        tweetEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                callbacks.onTextChanged(tweetEditView.getText().toString());
            }
        });
    }

    void findSubviews() {
        tweetEditView = (EditText) findViewById(R.id.tw__edit_tweet);
        charCountView = (TextView) findViewById(R.id.tw__char_count);
        tweetButton = (Button) findViewById(R.id.tw__post_tweet);
    }

    void setCallbacks(ComposerController.ComposerCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    String getTweetText() {
        return tweetEditView.getText().toString();
    }

    void setTweetText(String text) {
        tweetEditView.setText(text);
    }

    void setCharCount(int remainingCount) {
        charCountView.setText(Integer.toString(remainingCount));
    }

    void setCharCountTextStyle(int textStyleResId) {
        charCountView.setTextAppearance(getContext(), textStyleResId);
    }

    void postTweetEnabled(boolean enabled) {
        tweetButton.setEnabled(enabled);
    }
}
