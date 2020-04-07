# Plaid Link Sample Android App [![version][link-sdk-version]][link-sdk-url]
Sample application that demonstrates Plaid Link integration for Android in both Kotlin and Java.

<center><img src="docs/images/link_demo.gif" loading="lazy" alt="Link demo gif" height="512" /></center>

> Detailed instructions on how to integrate with Plaid Link for Android in your app can be found in our [main documentation][link-android-docs].

# Getting Started
To run the sample app, you'll need a Plaid account. You can create one on [our website][plaid-signup].

## 1. Register your app id
1. Log into your [Plaid Dashboard][plaid-dashboard] at the API page
2. Next to Allowed Android package names click "Configure" then "Add New Android Package Name"
3. Enter the sample app package name: `com.plaid.linksample`
4. Click "Save Changes", you may be prompted to re-enter your password

## 2. Add your public key to the sample
1. Clone the sample repository
2. Copy your public key from your [Plaid Dashboard][plaid-dashboard] API page
3. Paste your public key in `app/src/main/res/values/donottranslate.xml`

```xml
<resources>
    <string name="plaid_public_key">TODO ADD YOUR KEY HERE</string>
</resources>
```

## 3. Run the sample application
1. 🚀

# Features
- How to integrate the Plaid Link sdk: `build.gradle` files, `public_key` configuration, `Plaid` initialization
- Kotlin and Java sample Activity that show how to start Link and receive a result
- Use of `PlaidLinkResultHandler` for easy handling of Link results
- _Optional_ use of `LinkEventListener` to get events from Link

Have a look at our [main documentation][link-android-docs] for all Plaid Link SDK features.

# Releases
Our [change log][changelog] has release history.

The latest version of Plaid Link is [![version][link-sdk-version]][link-sdk-url].

```kotlin
implementation("com.plaid.link:sdk-core:<insert latest version>")
```

R8 and ProGuard rules are already bundled in our AAR and will be interpreted by R8/Proguard automatically.

[link-sdk-version]: https://img.shields.io/bintray/v/plaid/link-android/com.plaid.link
[link-sdk-url]: https://bintray.com/plaid/link-android/com.plaid.link
[link-android-docs]: https://plaid.com/docs/link/android/
[plaid-signup]: https://dashboard.plaid.com/signup?email=
[plaid-dashboard]: https://dashboard.plaid.com/team/api
[changelog]: https://github.com/plaid/plaid-link-android/releases
