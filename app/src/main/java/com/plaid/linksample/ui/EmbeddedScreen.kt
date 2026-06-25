/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample.ui

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.plaid.link.Plaid
import com.plaid.link.PlaidSession
import com.plaid.link.configuration.EmbeddedLinkTokenConfiguration
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkResult
import com.plaid.linksample.ui.components.DetailScaffold
import com.plaid.linksample.ui.components.InfoCard
import com.plaid.linksample.ui.components.LabeledTextField
import com.plaid.linksample.ui.components.LinkResultCard

/**
 * Embedded Institution Search: `createPlaidEmbeddedLinkView` returns an inline [View] hosted here
 * via [AndroidView]. Picking an institution hands off to full Link, which the SDK launches through
 * the supplied [openLink] launcher — its result is the [continuationResult] shown below.
 *
 * Exits of the inline view itself (before any handoff) arrive on the required `onEmbeddedViewExit`
 * callback, kept separate from the continuation result. The host chooses the view's size; 360.dp
 * here is arbitrary.
 */
@Composable
fun EmbeddedScreen(
  continuationResult: LinkResult?,
  onClearResult: () -> Unit,
  openLink: ActivityResultLauncher<PlaidSession>,
  onBack: () -> Unit,
) {
  val context = LocalContext.current

  var linkToken by rememberSaveable { mutableStateOf("") }
  var embeddedView by remember { mutableStateOf<View?>(null) }
  var embeddedExit by remember { mutableStateOf<String?>(null) }

  fun loadEmbedded() {
    if (linkToken.isBlank()) return
    onClearResult()
    embeddedExit = null
    val config =
      EmbeddedLinkTokenConfiguration
        .Builder()
        .token(linkToken)
        .onEmbeddedViewExit { exit -> embeddedExit = summarizeExit(exit) }
        .build()
    embeddedView = Plaid.createPlaidEmbeddedLinkView(context, config, openLink)
  }

  DetailScaffold(title = "Embedded", onBack = onBack) {
    InfoCard("Embedded works with a standard link_token. Paste one above.")

    LabeledTextField(
      label = "Link token",
      value = linkToken,
      onValueChange = { linkToken = it },
      placeholder = "link-sandbox-...",
    )

    Button(
      onClick = { loadEmbedded() },
      enabled = linkToken.isNotBlank(),
      modifier = Modifier.fillMaxWidth(),
    ) { Text("Load Embedded") }

    embeddedExit?.let { InfoCard("Embedded view exit: $it") }
    continuationResult?.let { LinkResultCard(result = it, onDismiss = onClearResult) }

    embeddedView?.let { view ->
      AndroidView(
        factory = { ctx -> FrameLayout(ctx) },
        update = { container ->
          // Attach only when the hosted view actually changes. update runs on every recomposition,
          // and re-running addView would detach and reattach the inline (WebView-backed) view,
          // interrupting its in-progress state.
          if (view.parent !== container) {
            (view.parent as? ViewGroup)?.removeView(view)
            container.removeAllViews()
            container.addView(view)
          }
        },
        modifier = Modifier.fillMaxWidth().height(360.dp),
      )
    }
  }
}

private fun summarizeExit(exit: LinkExit): String {
  val error = exit.error ?: return exit.metadata.status?.jsonValue ?: "no error"
  val message = error.displayMessage?.ifBlank { null } ?: error.errorMessage
  return "${error.errorCode.json}: $message"
}
