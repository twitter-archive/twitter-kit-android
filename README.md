# Twitter Kit for Android

Twitter Kit is a multi-module gradle project containing several Twitter SDKs including TweetComposer, TwitterCore, and TweetUi. It is built on the Fabric platform and uses many shared components.

Using Twitter Kit from source in production applications is not officially supported by Fabric. Please utilize the available binaries.

## Download


Define via Gradle:
```groovy

buildscript {
  repositories {
    mavenCentral()
    maven { url 'https://maven.fabric.io/public' }
  }
  dependencies {
    classpath 'io.fabric.tools:gradle:1.+'
  }
}

apply plugin: 'io.fabric'

repositories {
  mavenCentral()
  maven { url 'https://maven.fabric.io/public' }
}

dependencies {
  compile('com.twitter.sdk.android:twitter:2.3.2@aar') {
    transitive = true
  }
}

```

Check out [more details and other build tool integrations](https://fabric.io/downloads/build-tools)

## Getting Started

* Sign up for a [Fabric account](https://fabric.io) and follow onboarding instructions to get your Fabric API Key and build secret, found under the organization settings of the Fabric web dashboard.
* Either create your Twitter app by through [this portal](https://apps.twitter.com/) or by using the Fabric IDE plugin.
* Rename samples/app/fabric.properties.sample to samples/app/fabric.properties and populate information.
* Run Sample app to verify build.
* For extensive documentation, please see the [official documentation](http://docs.fabric.io/android/twitter/index.html).

## Code of Conduct

This, and all github.com/twitter projects, are under the [Twitter Open Source Code of Conduct](https://engineering.twitter.com/opensource/code-of-conduct). Additionally, see the [Typelevel Code of Conduct](http://typelevel.org/conduct) for specific examples of harassing behavior that are not tolerated.

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

For usage questions post on [Twitter Community](https://twittercommunity.com/c/fabric/twitter).

Please report any bugs as [issues](https://github.com/twitter/twitter-kit-android/issues).

Follow [@Fabric](http://twitter.com/fabric) on Twitter for updates.

## Authors

* [Andre Pinter](https://twitter.com/endform)
* [Dalton Hubble](https://twitter.com/dghubble)
* [Eric Frohnhoefer](https://twitter.com/ericfrohnhoefer)
* [Lien Mamitsuka](https://twitter.com/lientm)
* [Ty Smith](https://twitter.com/tsmith)
* [Vamsi Kancharla](https://twitter.com/vam_si)

Thanks for assistance and contributions:

* [Israel Camacho](https://twitter.com/rallat)
* [Justin Starry](https://twitter.com/sirstarry)
* [Yohan Hartanto](https://twitter.com/yohan)

## License

Copyright 2015 Twitter, Inc.

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
