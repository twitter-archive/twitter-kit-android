# Android SDK TweetComposer Kit changelog

*Non-trivial pull requests should include an entry below. Entries must be suitable for inclusion in public-facing materials such as release notes and blog posts. Keep them short, sweet, and in the past tense. New entries go on top. When merging to deploy, add the version number and date.*

## Unreleased

## v3.2.0

* Moved build to Java 8

## v3.1.1

* Send broadcast when Tweet compose is cancelled.

## v3.1.0

* Bumped version number

## v3.0.0

* App card creation removed from composer

## v2.3.1

* Restricted Broadcast Intents to current application to avoid leaking sensitive information.

## v2.3.0

* Updated Twitter Core dependency.

## v2.2.0

* Updated Twitter Core dependency.

## v2.1.0

* Updated Twitter Core dependency to version 2.1.0.

## v2.0.1

* Added translations.
* Removed pseudo locales from translations.

## v2.0.0

* Dropped support for API versions before API 14 (ICS).
* Updated Twitter Core dependency

## v1.0.5

* Updated Twitter Core dependency

## v1.0.4

* Updated Fabric Base dependency
* Updated composer to accept #hashtags

## v1.0.3

## v1.0.2

## v1.0.1

* Renamed drawables to avoid name collisions.

## v1.0.0

* Added composer with support for app install cards.

## v0.9.0

* Raised Min SDK version from 8 to 9.

## v0.8.0

* (DH) Removed tweet-composer dependency on twitter-core.

## v0.7.4

* (DH) Allow tweet-composer dependency on twitter-core to be excluded.

## v0.7.3
**Jan 30 2015**

* (EF) Removed targetSdkVersion because it should not be specified on libraries.

## v0.7.2
**Nov 20 2014**

* (TS) Moved to Java 7

## v0.7.1
**Oct 30 2014**

* (TY) Removed Apache 2.0 License from pom files.

## v0.7.0
**Oct 15 2014**

* (LTM) Removed allowBackup=true attribute from application element in AndroidManifest.xml.
* Create TweetComposer Builder to assist in building intent for Twitter for Android and will fallback to a Browser
