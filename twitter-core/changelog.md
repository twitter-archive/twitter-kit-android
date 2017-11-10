# Twitter Core Android SDK changelog
*Non-trivial pull requests should include an entry below. Entries must be suitable for inclusion in public-facing materials such as release notes and blog posts. Keep them short, sweet, and in the past tense. New entries go on top. When merging to deploy, add the version number and date.*

## Unreleased

## v3.2.0

* Fixed issue that caused startup delays
* Moved build to Java 8

## v3.1.1

* Bumped version number

## v3.1.0

* Add private constructor for GSON
* Add support to linkify hashtags

## v3.0.0

* Removed dependency on Fabric
* Updated Retrofit and OkHttp dependencies
* Request email permission screen removed

## v2.3.1

* Removed unused Vine utility method.

## v2.3.0

* Normalized user-agent to avoid errors with non-ascii characters.
* Added symbols to Tweet entities.

## v2.2.0

* Added support for user defined OkHttpClient.

## v2.1.0

* Fixed IllegalArgumentException in GuestAuthenticator.

## v2.0.1

* Updated proguard rules for Okhttp3 and Retrofit2.
* Removed pseudo locales from translations.
* Moved TwitterCollection from internal package to models.
* Minor bug fixes.

## v2.0.0

* Dropped support for API versions before API 14 (ICS).
* Migrated to Retrofit 2.0 and OkHttp 3.2.
* TwitterApiClient now automatically refreshes expired guest tokens.
* Removed previously deprecated methods and classes.
* Removed all public reference to Application Authentication.
* Fixed issue parsing withheldInCountries field in User object.
* Added altText field to MediaEntity object.
* Added Quote Tweet to Tweet object.

## v1.7.0

* Added support for Vine in Tweets
* Enabled extended Tweet display

## v1.6.8

* Fixed Fake ID exploit

## v1.6.7

* Updated Fabric Base dependency

## v1.6.6

* Fixed security issue where certificate pinning wasn't happening for some requests.

## v1.6.5

 * Removed Verisign Class 3 Certificate from pinning list.
 * Fixed JavaDocs.

## v1.6.4

* Fixed retrieving auth token when using OkHttp 2.3+.

## v1.6.3

## v1.6.2

* Added VideoInfo field to MediaEntity to support video and animated gif playback.

## v1.6.1

* Updated translations.

## v1.6.0
* Removed usage of deprecated Apache HTTP Client constants.

## v1.5.0

* Raised Min SDK version from 8 to 9 to support APIs switching to SHA2 TLS certificates.
* Added a StatusService update endpoint which accepts mediaIds.
* Added MediaService to support media upload.

## v1.4.3

* Fixed bug in guest auth queue which cached application-only auth fallback tokens indefinitely.
Change queue to disallow and clear application-only auth tokens.
* Removed logInGuest fallback to application-only auth so logInGuest consistently provides an
AppSession with a GuestAuthToken. Previously, logInGuest provided an AppSession which could fall
back to a non-expiring app auth token when guest token requests failed.
* Fixed bug in TwitterApiException's TwitterRateLimit.getRemainingTime().

## v1.4.2

* Made minor changes to support tweet-ui Tweet actions and error states.

## v1.4.1

* (EF) (internal) Moved AuthRequestQueue to twitter-core.
* (EF) Fixed critical issue where Twitter sessions are lost when using Proguard.
* (DH) (internal) Added support for Twitter Single Signon with the Twitter Android Dogfood App.
* (EF) Added unretweet endpoint to StatusesService.
* (DH) Switched CollectionService from the beta collections endpoint to the public collections endpoint.

## v1.4.0

* (DH) Improved security of WebView used by OAuthActivity by disabling form data saving.
* (DH) (internal) Added session verify credential scribing on daily ping.
* (DH) Added ListService, with lists/statuses endpoint, to TwitterApiClient.

## v1.3.4

* (IC) (internal) OAuth1 refactoring and improvements.

## v1.3.3

* (DH) (internal) Added CollectionService to TwitterApiClient and added TwitterCollection model.
* (DH) (internal) Added GuestCallback to wrap Guest Auth callbacks and simplify token expiration handling.

## v1.3.2

* (DH) Added Identifiable interface to abstract models with getId() method. Made Tweet and User models implement Identifiable.
* (AP) Disabled scribing strategy when BUILD_TYPE is debug to reduce memory usage during testing.

## v1.3.1

* (MCF) Refactored file management to support respecting the maximum pending file count obtained from settings.

## v1.3.0
**Jan 30 2015**

* (DH) Moved AppSession into com.twitter.sdk.android.core package from the internal.oauth subpackage

## v1.2.0
**Jan 29 2015**

* (AP) Use SessionMonitor to automatically call verify_credentials with active user sessions
* (TS) Removed URL logging in OAuth Webview
* (EF) Removed targetSdkVersion because it should not be specified on libraries.
* (DH) Add logInGuest method to TwitterCore to request guest authentication

## v1.1.1
**Dec 15 2014**

* (AP) Scribe impressions of ShareEmail and Twitter Login
* (DH) Disabled AllCaps Login with Twitter button text on API 21
* (LTM) Fixed crash that could occur when passing RetrofitError in Intent or Bundle, which could happen if login or share email flows failed with specific RetrofitError (like gson conversion).
* (TS) Added Consumer Proguard Config file to be bundled with AAR

## v1.1.0
**Nov 20 2014**

* (EF) Created new OAuthSigning class to generate OAuth headers.
* (IC) Handled Exception on TwitterApiException to avoid crashes and minimize the impact of changes in the endpoints
* (LTM) Improved developer experience for TwitterAuthClient#authorize method.
* (AP) Fixed handling of scribe server errors
* (LTM) Changed text on login button from "Sign in with Twitter" to "Log in with Twitter".
* (TS) Moved to Java 7
* (AP) Fixed AuthenticatedClient dropping existing headers
* (DH) Add 'createdAt' field to AuthTokens
* (DH) Add isExpired method to AuthTokens to indicate if a token is known to have expired

## v1.0.1
**Oct 30 2014**

* (TY) Removed Apache 2.0 License from pom files.

## v1.0.0
**Oct 15 2014**

* (LTM) Removed allowBackup=true attribute from application element in AndroidManifest.xml.
* (LTM) Fixed bug where we were failing to close QueueFile used in scribing.
* (YH) SSLSocket is initialized on the background or on demand.
* (AP) Added required ScribeConfig arguments for scribe path components
* (LTM) Updated logging to use TwitterCore.TAG everywhere.
* (LTM) Introduced AppSession for holding app tokens. TwitterSession now holds only user tokens.
* (LTM) Introduced new SessionManager that manages the app sessions. Updated TwitterApiClient to accept Session instead of TwitterSession.
* (LTM) Refactored scribe to allow kit developers to set their own scribe configuration.
* (AP) Updated ScribeFilesSender to indicate to whether to clean up scribe files in case of internal service error
* (TS) Added TwitterLoginButton graceful failing when Twitter hasn't started yet
* (AP) Updated ScribeFilesSender to send device id header
* (LTM) Updated Twitter logIn API to explicitly require Activity contexts.
* (LTM) Added Share Email API for requesting access to a user's email address.
* (TS) Rename Twitter to TwitterCore
* (AP) Updated ScribeConfig with configurable User-Agent
* (LTM) Refactor common styles, colors, dimens from Digits to Twitter.
* (LTM) Updated Twitter API Tweet model to be immutable model. (breaking change)
* (LTM) Updated Twitter API User model to be immutable model. (breaking change)
* (LTM) Updated Twitter API entity models to be immutable models. (breaking change)

## v0.13.0

* (LTM) Fixed bug where Tweet id was not deserialized due to missing annotation.
* (LTM) Improved security of WebView used by OAuthActivity by disabling javascript and file access.
* (IC) Added get callback method for TwitterLoginButton

## v0.12.0
**Sep 15 2014**

* (TS) Added fluent API builder to Fabric Class and cleaned up Fabric API
* (LTM) Moved TwitterAuthResponse from core package to internal/oauth. Also renamed it to OAuthResponse.
* (LTM) Renamed twittercore to core
* (TS) Renamed Foundation to Fabric
* (LTM) Updated SessionManager to support multiple sessions. This is a breaking API change.

## v0.11.0
** Sep 4 2014**

* (LTM) Updated error logging when we fail to get a guest auth to match the one used on iOS.
* (LTM) Fixed bug where scribe events were not signed.
* (TS) Fixed bug in parsing POST params for AuthenticatedTwitterClient.
* (TS) Added SearchService and related models.
* (LTM) Always enable scribing.

## v0.10.0
**Aug 18 2014**

* (DH) Updated TwitterApiException to provide twitter API error.
* (TS) Replaced Kit#getName with Kit#getIdentifier, fixed issue where kit names could be reported incorrectly after proguarding.
* (TS) Moved Services out of Twitter class and into TwitterApiClient.
* (LTM) Switched to using Twitter Callback interface, which matches callbacks we're using for API services. Removed OnAuthCompleteListener.
* (LTM) Cleaned up Twitter interface. Removed unnecessary methods, introduced SessionManager for managing session, and added log in and log out.
* (LTM) Removed deprecated methods from TwitterAuthClient.
* (LTM) Added support for scribing. Scribing is disabled by default.
* (TS) Added API stack based on retrofit
* (TS) Added Rate Limiting

## v0.9.0
**July 24 2014**

* (LTM) Switched to using Sdk.getLogger for all logging.

## v0.8.0
**June 26 2014**

* (LTM) Move identity package into twittercore package. Rename TwitterIdentity to Twitter.
* (LTM) Added new method TwitterLoginButton#setOnAuthCompleteListener.
* (LTM) Added new method TwitterAuthClient#authorize that accepts OnAuthCompleteListener callback. Deprecated single parameter TwitterAuthClient#authorize.
* (LTM) Removed all abstract methods from TwitterAuthClient. Defined and documented default request code for use for Single Sign On. Application developers may provide their own request code by overriding TwitterAuthClient#getRequestCode.
* (IC) Added support for persisting TwitterSession. This functionality is currently used only by internal clients.
* (LTM) This library combines what was previously twitteridentity with and foundation/twitternetwork into one kit that contains all the core twitter functionality needed by other kits such as twittersocial and digits and will now be versioned as 0.0.1

## v0.7.0
**June 3 2014**

* (TS) Migrated app onboarding code from crashlytics to foundation.

## v0.6.0
**May 20 2014**

* (LTM) Fixed bug where TwitterAuthConfig#signRequest did not include post parameters when computing the signature. Added new signRequest method that accepts post parameters. Developers should use this when making post requests to the Twitter API.
* (LTM) Changed how version information is generated.
* (TS) Renamed Module to Kit.

## v0.5.0
**April 28 2014**

* (LTM) Fixed bug that could cause us to resolve the Twitter for Android SingleSignOnActivity even if it was not available, causing us to attempt SSO.
* (LTM) Fixed crash that could occur if onActivityResult was called and an authorize was not in progress. This could occur if the sdk was in an invalid state or the developer mistakenly calls onActivityResult multiple times.

## v0.4.0
**April 1 2014***

* (LTM) Added tw__ to some resources that were missing it to ensure proper namespacing.
* (LTM) Renamed Twitter module to TwitterIdentity and renamed packages.
* (LTM) Removed twitter auth permission and use new the SingleSignOnActivity from Twitter for Android that does not require permissions.
* (LTM) Updated OAuthActivity configuration to exclude it from recents.
* (LTM) Removed unnecessary OAuth realm parameter.
* (LTM) Updated Twitter pins to match Twitter pins used by the Twitter for Android client. This change is not user facing.

## v0.3.0
**February 24 2014**

* (LTM) Replaced ProgressDialog with ProgressBar, which is the recommended approach for showing progress in an activity.
* (LTM) Show loading indicator as soon as auth flow is started. Previously, loading indicator was shown after the network request to get a temp token was completed and before the web view was loaded.
* (LTM) Refactored OAuthHandler to use OAuthActivity to maintain state information across orientation changes, improving user experience. This also fixes crashes that may occur due to orientation changes.
* (LTM) Changed minSdkVersion from 10 to 8.
* (LTM) Refactored to use twitternetwork service, a collection of classes for making Twitter API requests.

## v0.2.0
**February 12 2014**

* (TS) Updated twitter AndroidManifest.xml to declare twitter auth permission so that order of install between Twitter app and SDK no longer matter.
* (LTM) Added Twitter certificate pinning.
* (LTM) Removed callback url from TwitterAuthConfig since this information isn't required from developers.

## v0.1.0
**December 2013**

* Initial version


