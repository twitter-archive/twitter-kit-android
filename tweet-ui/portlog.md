#*Files ported from twitter-android and twitter-android-internal*#

* sdk/kits/tweetui/src/main/java/com/twitter/one/android/kits/tweetui/Linkifier.java
  - from: twitter-android/library/src/main/java/com/twitter/library/view/Linkifier.java
  - copied tests for methods that we did copy
  - significantly modified, main entity offset loop preserved
* sdk/kits/tweetui/src/main/java/com/twitter/one/android/kits/tweetui/TweetUtils.java
  - from: twitter-android/library/src/main/java/com/twitter/library/util/Util.java - methods for date parsing and number formatting
  - from: twitter-android-internal/src/main/java/com/twitter/internal/util/Time.java - constants
  - from: library/src/main/java/com/twitter/library/api/TwitterParsers.java - html escaping and entity offsets
  - from: ./library/src/main/java/com/twitter/library/util/I18nUtils.java - emoji entity adjustments
  - some minor amount of added code and refactorings
  - we wrote our own tests, none to copy
  - tests for html escaping
  - copied some html escaping and entity adjustment tests
* sdk/kits/tweetui/src/main/java/com/twitter/sdk/android/tweetui/internal/util/HtmlEntities.java
  from: cobalt/internal/src/main/java/com/twitter/internal/util/Entities.java
  - no modification
  - no tests to copy
  - from Apache Contrib (with Apache 2.0 license)
* sdk/kits/tweetui/src/main/java/com/twitter/sdk/android/tweetui/internal/util/IntHashMap.java
  from: cobalt/internal/src/main/java/com/twitter/internal/util/IntHashMap.java
  - no modification
  - no tests to copy
  - from Apache Contrib (with Apache 2.0 license)
