# Twitter Kit for Android

Twitter Kit is a multi-module gradle project containing several Twitter SDKs including TweetComposer, TwitterCore, and TweetUi. Twitter Kit is designed to make interacting with Twitter seamless and efficient.

Using Twitter Kit from source in production applications is not officially supported. Please utilize the available binaries.

## Download


Define via Gradle:
```groovy

repositories {
  jcenter()
}

dependencies {
  compile('com.twitter.sdk.android:twitter:3.1.1@aar') {
    transitive = true
  }
}

```

## Getting Started

* Create your Twitter app through [this portal](https://apps.twitter.com/).
* Rename samples/app/twitter.properties.sample to samples/app/twitter.properties and populate the consumer key and secret.
* Run Sample app to verify build.
* For extensive documentation, please see the [official documentation](http://dev.twitter.com/twitterkit/overview.html).

## Code of Conduct

This, and all github.com/twitter projects, are under the [Twitter Open Source Code of Conduct](https://github.com/twitter/code-of-conduct/blob/master/code-of-conduct.md). Additionally, see the [Typelevel Code of Conduct](http://typelevel.org/conduct) for specific examples of harassing behavior that are not tolerated.

## Building

Please use the provided gradle wrapper to build the project.

```
./gradlew assemble
```

Run all automated tests on device to verify.

```
./gradlew connectedCheck
```

To run the sample app

```
./gradlew :samples:app:installDebug
```


Contributing

The master branch of this repository contains the latest stable release of Twitter Kit. See [CONTRIBUTING.md](https://github.com/twitter/twitter-kit-android/blob/master/CONTRIBUTING.md) for more details about how to contribute.

## Contact

For usage questions post on [Twitter Community](https://twittercommunity.com/tags/c/publisher/twitter/android).

Please report any bugs as [issues](https://github.com/twitter/twitter-kit-android/issues).

Follow [@TwitterDev](http://twitter.com/twitterdev) on Twitter for updates.

## License

Copyright 2017 Twitter, Inc.

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
