# How to Contribute

We'd love to get patches from you! The core team at Twitter is very passionate about the direction of the product, and thus we are seeking fix contributions over external feature PRs.

If you have anything you'd like to contribute, we recommend discussing it with the core team before writing it.

## Workflow

The workflow that we support:

1.  Fork twitter-kit-android
2.  Check out the `master` branch
3.  Make a feature branch
4.  Make your cool new feature or bugfix on your branch
5.  Write a test for your change
6.  From your branch, make a pull request against `twitter/twitter-kit-android/master`
7.  Work with repo maintainers to get your change reviewed
8.  Wait for your change to be merged internally by staff
9.  Delete your feature branch

## Testing

We've use the standard [Android Testing tools](http://developer.android.com/tools/testing/testing_android.html). Most classes are currently using AndroidTestCase, but will slowly be migrated to JUnit 4.

```
Running ./gradlew test connectedCheck will perform the needed tests
```

## Styleguide

* We follow the android coding guidelines, followed by the java coding convention.

* checkstyle and lint will be used to help enforce code style.

### Android Style Guide
https://source.android.com/source/code-style.html

#### errata:

* the style guide is unclear on the naming of private static final fields, they should be SCREAMING_SNAKE_CASE

* AOSP uses Hungarian notation, however this is discouraged in modern open source libraries and applications. Instance variables should be named with CamelCase and without prefixes.

### Java Style Guide
http://google-styleguide.googlecode.com/svn/trunk/javaguide.html (MikeFu suggestion)

* Limit the use of horizontal alignment. While it may aid readability, it creates problems for future maintainers. More [here](http://google-styleguide.googlecode.com/svn/trunk/javaguide.html#s4.6.3-horizontal-alignment)
* For testing purposes, we allow static imports, as well, as wildcard imports. This exclusion is added to our checkstyle config.

### Javadoc Style Guide
http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html

See style guide, in particular, when documenting methods:

* Use 3rd person (descriptive) not 2nd person (prescriptive).
* Method descriptions begin with a verb phrase.
* Add description beyond the API name.
* Avoid descriptions that say nothing beyond what you know from reading the method name.

### Groovy / Gradle Style Guide
…

### Git Commit Message Style Guide
http://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html

http://who-t.blogspot.de/2009/12/on-commit-messages.html

* Commit message should describe why the change is being made with a high level overview of significant changes made.

* The first line of commit message should be no more than 65 characters long. This should be a short summary of your commit. Wrap subsequent lines to 80 columns.

* Write commit message in the imperative: "Fix bug" and not "Fixed bug"
or "Fixes bug."  This convention matches up with commit messages generated
by commands like git merge and git revert.

## Code Review

The twitter-kit-android repository on GitHub is kept in sync with an internal repository at
Twitter. For the most part this process should be transparent to twitter-kit-android users,
but it does have some implications for how pull requests are merged into the
codebase.

When you submit a pull request on GitHub, it will be reviewed by the
twitter-kit-android community (both inside and outside of Twitter), and once the changes are
approved, your commits will be brought into the internal system for additional
testing. Once the changes are merged internally, they will be pushed back to
GitHub with the next release.

This process means that the pull request will not be merged in the usual way.
Instead a member of the twitter-kit-android team will post a message in the pull request
thread when your changes have made their way back to GitHub, and the pull
request will be closed. The changes
in the pull request will be collapsed into a single commit, but the authorship
metadata will be preserved.

Please let us know if you have any questions about this process!