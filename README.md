# Plaid Link Sample Android App [![version][link-sdk-version]][link-sdk-url]
Sample application that demonstrates Plaid Link integration for Android in both Kotlin and Java.

<center><img src="./docs/images/link_demo.gif" loading="lazy" alt="Link demo gif" height="512" /></center>

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

# App Features
The repository contains a java and kotlin application class and a java and kotlin activity.  From the kotlin activity you can open the java activity using the menu in the action bar and similarly from the java activity you can open the kotlin activity from the same menu.  If you want to test the java appplication class instead of the kotlin application class just change the name in the application tag in the Android manifest to ```name=".LinkSampleJavaApplication"```.

When running the app, the floating action button will start the Link flow and when the flow completes (with a success, cancellation, error, or crash) you will see the results in the main activity.

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

[link-demo]: ![Link demo](./docs/images/link_demo.gif
