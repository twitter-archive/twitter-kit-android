package com.twitter.sdk.android.tweetui;

// Make AuthRequestQueue public so we can mock it using Mockito
public class TestAuthRequestQueue extends AuthRequestQueue {
    public TestAuthRequestQueue() {
        super(null, null);
    }
}
