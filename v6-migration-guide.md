# Migrating to Link Android SDK 6.0.0

SDK 6.0.0 replaces the handler-based API with a **session-based** one. You create a typed session for
each flow, then open it through the `OpenPlaidLink` `ActivityResultContract`. Success and exit arrive
through that result; events arrive through a single global listener.

**Quick links:** [Standard Link](#standard-link) · [Layer](#layer) · [Headless](#headless) · [Embedded](#embedded) · [Breaking changes](#breaking-changes)

## Why upgrade

- A Kotlin-first session API with one create-and-open pattern across every flow.
- `onLoad` readiness so you can preload a session and enable your "Connect" button when it's ready.
- First-class Layer, Headless, and Embedded sessions.
- `Plaid.parseResult(...)` is public, so Activity-based apps and wrappers can parse results directly.

## Overview

| 5.x | 6.0.0 |
|---|---|
| `Plaid.create(app, config)` → `PlaidHandler` | `Plaid.createPlaidLinkSession(context, config)` → `PlaidLinkSession` |
| `FastOpenPlaidLink : ActivityResultContract<PlaidHandler, …>` | `OpenPlaidLink : ActivityResultContract<PlaidSession, …>` |
| `LinkResultHandler(onSuccess, onExit)` | `Plaid.parseResult(requestCode, resultCode, data)` |
| `handler.submit(…)` (Layer) | `PlaidLayerSession.submit(SubmissionData(…))` |
| `config.noLoadingState = true` | `Plaid.createPlaidHeadlessSession(…)` |

> **Token ↔ session compatibility:** the server decides a token's flow type when the session
> preloads. Open a session with a token created for a different flow and it fails fast — for example,
> a standard token opened as a headless session returns `LinkErrorCode.SdkError.SESSION_TYPE_MISMATCH`.

## Standard Link

Create a session, optionally preload with `onLoad`, then launch it.

| 5.x | 6.0.0 |
|---|---|
| `Plaid.create(app, config)` → `PlaidHandler` | `Plaid.createPlaidLinkSession(context, config)` → `PlaidLinkSession` |
| `registerForActivityResult(FastOpenPlaidLink())` | `registerForActivityResult(OpenPlaidLink())` |
| `launcher.launch(handler)` | `launcher.launch(session)` |

```kotlin
private val launcher = registerForActivityResult(OpenPlaidLink()) { result ->
    when (result) {
        is LinkSuccess -> handleSuccess(result)   // result.publicToken, result.metadata
        is LinkExit -> handleExit(result)         // result.error, result.metadata
    }
}

fun open(token: String) {
    val config = linkTokenConfiguration {
        this.token = token
        onLoad = OnLoadCallback { /* preloaded — enable your Connect button */ }
    }
    val session = Plaid.createPlaidLinkSession(this, config)
    launcher.launch(session)
}
```

A session is single-use: build a fresh one for each open.

### Classic `open(Activity)`

If you don't use an `ActivityResultLauncher`, call `session.open(this)`. The result is delivered to
the **host Activity** and parsed with `Plaid.parseResult`:

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    Plaid.parseResult(requestCode, resultCode, data)?.let { handleResult(it) }
}
```

## Layer

Layer is its own session and config. Submit the phone number, then open when Layer reports it's ready
via the `LAYER_READY` event. `submit` buffers, so it's safe before or after open (last write wins).

```kotlin
val session = Plaid.createPlaidLayerSession(
    context,
    LayerTokenConfiguration.Builder().token(token).build(),
)
session.submit(SubmissionData(phoneNumber = "+14155550015"))

Plaid.setLinkEventListener { event ->
    when (event.eventName) {
        is LinkEventName.LAYER_READY -> launcher.launch(session)
        is LinkEventName.LAYER_NOT_AVAILABLE -> { /* fall back, e.g. submit a date of birth */ }
        else -> Unit
    }
}
```

## Headless

Headless runs an external-browser OAuth flow with no WebView. There is **no `start()`** — create the
session and launch it like any other. The token must resolve server-side to headless OAuth (e.g. an
EU `payment_initiation` token with `eu_config.headless`).

```kotlin
val session = Plaid.createPlaidHeadlessSession(
    context,
    linkTokenConfiguration { token = token },
)
launcher.launch(session)   // success/exit arrive on the OpenPlaidLink result
```

## Embedded

`createPlaidEmbeddedLinkView` returns an inline `View` you host in your layout. `onEmbeddedViewExit`
is **required**. Picking an institution hands off to full Link, which the SDK launches through the
launcher you pass in; that flow's result arrives on the `OpenPlaidLink` result.

```kotlin
val config = EmbeddedLinkTokenConfiguration.Builder()
    .token(token)
    .onEmbeddedViewExit { exit -> /* exit of the inline view, before any handoff */ }
    .build()

val view: View = Plaid.createPlaidEmbeddedLinkView(context, config, launcher)
// add `view` to your layout; the host chooses its size.
```

For wrappers that drive opening themselves, the `OnLinkContinuation` overload hands back the session
instead of launching it.

## Breaking changes

- **Requirements:** minSdk 21 → **26**, compileSdk **36**, Kotlin **2.x**, Java **11** source/target compatibility.
- `Plaid.create` / `PlaidHandler` / `FastOpenPlaidLink` removed → `createPlaid*Session` /
  `PlaidSession` / `OpenPlaidLink` (contract input retyped to `PlaidSession`).
- `LinkResultHandler` removed → `Plaid.parseResult(requestCode, resultCode, data)`.
- Config slimmed to `token` (+ `onLoad`): `logLevel` removed (no replacement); `noLoadingState`
  removed → `createPlaidHeadlessSession`.
- `LinkEventListener` is now a `fun interface` (was a `typealias`). Call-position lambdas are fine;
  assigning a lambda to a `LinkEventListener` variable no longer compiles. From Java, `onEvent`
  returns `void`.
- `open(Fragment)` removed. Classic `open(Activity)` delivers to the host Activity's
  `onActivityResult`; for Fragment-scoped results, register `OpenPlaidLink` with
  `registerForActivityResult`.
- Exception hierarchy collapsed (16 types → 2); code that caught the old config-validation
  exceptions won't compile. Failures now surface as typed `LinkErrorCode` / `LinkErrorType`.

See the example app in this repo for a runnable version of each flow.
