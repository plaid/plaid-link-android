# Plaid Link Sample Android App [![version][link-sdk-version]][link-sdk-url]
This sample app shows how the Link Android SDK integrates with your own app. It's built with Jetpack
Compose and demonstrates four Link flows — Standard Link, Headless, Layer, and Embedded — each on its
own screen. Check out [the benefits](./docs/sdk-vs-webview-comparison.md) of using the SDK.

> Detailed instructions on how to integrate with Plaid Link for Android can be found in our [main documentation][link-android-docs].
> Upgrading from 5.x? See the [v6 migration guide](./v6-migration-guide.md).

# Getting Started
To run the sample app, you'll need a Plaid account. You can create one on [our website][plaid-signup].

## 1. Register your app's package name
1. Log into your [Plaid Dashboard][plaid-dashboard-api] at the API page
2. Next to "Allowed Android package names" click "Configure" then "Add New Android Package Name"
3. Enter the sample app package name: `com.plaid.linksample`
4. Click "Save Changes", you may be prompted to re-enter your password

## 2. Provide a link_token
Each screen has its own **Link token** field. Create a token with
[/link/token/create](https://plaid.com/docs/#create-link-token) and paste it into the field.

A standard `transactions` token works for **Standard Link** and **Embedded**. **Headless** and
**Layer** need product-specific tokens (see the note on each screen).

## 3. Run the sample application
1. Open the project in Android Studio and run the `app` configuration on a device or emulator (API 26+).

# Features
- Integrating the SDK: `build.gradle` setup, `link_token` configuration, and session creation
- The session API for all four flows: `createPlaidLinkSession`, `createPlaidHeadlessSession`,
  `createPlaidLayerSession`, and `createPlaidEmbeddedLinkView`
- Opening Link with the `OpenPlaidLink` `ActivityResultContract`
- Receiving the typed `LinkResult` from the `OpenPlaidLink` launcher callback
- Subscribing to Link events with `Plaid.setLinkEventListener`
- Surviving process death during OAuth: each flow registers its result launcher in `onCreate` so results are re-delivered, not dropped

Have a look at our [main documentation][link-android-docs] for all Plaid Link SDK features.

# Releases
Our [change log][changelog] has release history.

We create release candidates (e.g. 3.2.0-rc1) as beta previews for developers. These are helpful for customers who either are 1. waiting for a specific fix or 2. extremely eager for specific features. They do not hold the same quality guarantee as our official releases, and should NOT be used in production. The official releases come ~2 weeks after the first release candidate (rc1).

The latest version of Plaid Link is [![version][link-sdk-version]][link-sdk-url].

```kotlin
implementation("com.plaid.link:sdk-core:<insert latest version>")
```

R8 and ProGuard rules are already bundled in our AAR and will be used automatically.

## Upgrading

Plaid releases updates to the SDK approximately every few months. For the best user experience, we recommend using the latest version of the SDK.

Major SDK versions are released annually. SDK versions are supported for two years; with each major SDK release, Plaid will stop officially supporting any previous SDK versions that are more than two years old.

While these older versions are expected to continue to work without disruption, Plaid will not provide assistance with unsupported SDK versions.

# Migration Guide

### Changes from SDK 5.x to 6.0

6.0 replaces the handler-based API (`Plaid.create` / `PlaidHandler` / `FastOpenPlaidLink`) with a
session-based one: create a typed session per flow and open it through `OpenPlaidLink`. It also adds
Layer, Headless, and Embedded sessions and raises minSdk to 26. See the full
[v6 migration guide](./v6-migration-guide.md) for before/after code and the complete list of breaking
changes.

### Changes from SDK 4.x to 5.0

#### 1. Link Open changes

Plaid SDK 4.2 introduced `FastOpenPlaidLink` with the `PlaidHandler` to warm up a Link session and `destroy()` to end a warmed up session. `OpenPlaidLink` has been deprecated, as `FastOpenPlaidLink` is now the recommended method to open Link, as it results in lower user-facing latency. It is also required if using Plaid Layer. This is not a breaking change, as `OpenPlaidLink` has not been removed from the SDK, but all customers are encouraged to migrate to `FastOpenPlaidLink`. For example integration, see the sample app in this SDK or the [Opening Link documentation for Plaid Link Android](https://plaid.com/docs/link/android/#opening-link).

#### 2. Upgrade to Kotlin 1.9.25
The Link Android SDK version of Kotlin has been upgraded to 1.9.25 and may need to be updated in your project.

### Changes from SDK 3.x to 4.0

#### 1. Authentication Method Changes
**BREAKING**: Removed deprecated support for public key authentication.

If your integration is using public key authentication, it's essential to migrate to Link Tokens. This change does not affect you if you've already made this transition. For detailed instructions, refer to Plaid's [migration guide](https://plaid.com/docs/link-token-migration-guide).

#### 2. Open Options Configuration
**BREAKING**: Removed `extraParams(extraParams: Map<String, String>)` setter method from  `LinkTokenConfiguration#Builder`.

If your integration relies on `extraParams`, you must now configure these parameters while creating your [Link Tokens](https://plaid.com/docs/api/link/). Update your implementation accordingly.

#### 3. Upgrade to Kotlin 1.8
The Link Android SDK version of Kotlin has been upgraded to 1.8 and may need to be updated in your project.

# License
```
MIT License

Copyright (c) 2020 Plaid

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```


[link-sdk-version]: https://img.shields.io/maven-central/v/com.plaid.link/sdk-core
[link-sdk-url]: https://search.maven.org/artifact/com.plaid.link/sdk-core
[link-android-docs]: https://plaid.com/docs/link/android/
[plaid-signup]: https://dashboard.plaid.com/signup?email=
[plaid-dashboard-api]: https://dashboard.plaid.com/team/api
[changelog]: https://github.com/plaid/plaid-link-android/releases
