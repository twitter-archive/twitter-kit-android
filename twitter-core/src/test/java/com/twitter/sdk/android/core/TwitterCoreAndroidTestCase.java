package com.twitter.sdk.android.core;

import android.test.AndroidTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class TwitterCoreAndroidTestCase extends AndroidTestCase {

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();

    setContext(RuntimeEnvironment.application);
  }

  @Test
  public void test() {

  }
}
