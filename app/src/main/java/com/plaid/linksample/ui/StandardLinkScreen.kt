/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.plaid.link.OnLoadCallback
import com.plaid.link.Plaid
import com.plaid.link.PlaidLinkSession
import com.plaid.link.configuration.linkTokenConfiguration
import com.plaid.link.result.LinkResult
import com.plaid.linksample.model.Example
import com.plaid.linksample.model.LoadingState
import com.plaid.linksample.ui.components.DetailScaffold
import com.plaid.linksample.ui.components.LabeledTextField
import com.plaid.linksample.ui.components.LinkResultCard

/**
 * Standard Link: paste a token, preload the [PlaidLinkSession], then open it. "Open" unlocks once
 * `onLoad` fires, and the host Activity opens the session through its `OpenPlaidLink` launcher. A
 * session opens once, so after a result it's discarded and Prepare builds a fresh one.
 */
@Composable
fun StandardLinkScreen(
  result: LinkResult?,
  onClearResult: () -> Unit,
  onOpen: (PlaidLinkSession) -> Unit,
  onBack: () -> Unit,
) {
  val context = LocalContext.current

  var linkToken by rememberSaveable { mutableStateOf("") }
  var loadingState by remember { mutableStateOf<LoadingState>(LoadingState.Idle) }
  var session by remember { mutableStateOf<PlaidLinkSession?>(null) }

  // A spent session can't be reopened: drop it on a result and reset to Idle for the next Prepare.
  LaunchedEffect(result) {
    if (result != null) {
      session = null
      loadingState = LoadingState.Idle
    }
  }

  fun prepare() {
    if (linkToken.isBlank()) return
    onClearResult()
    loadingState = LoadingState.Loading
    session =
      Plaid.createPlaidLinkSession(
        context,
        linkTokenConfiguration {
          token = linkToken
          onLoad = OnLoadCallback { loadingState = LoadingState.Ready }
        },
      )
  }

  val isReady = loadingState is LoadingState.Ready && session != null

  DetailScaffold(title = "Standard Link", onBack = onBack) {
    Text(
      Example.STANDARD.description,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    LabeledTextField(
      label = "Link token",
      value = linkToken,
      onValueChange = { linkToken = it },
      placeholder = "link-sandbox-...",
    )

    Button(
      onClick = { prepare() },
      enabled = linkToken.isNotBlank() && loadingState != LoadingState.Loading,
      modifier = Modifier.fillMaxWidth(),
    ) { Text("Prepare Link") }

    when (loadingState) {
      LoadingState.Loading -> Text("Preloading…", style = MaterialTheme.typography.bodySmall)
      LoadingState.Ready -> Text("Ready to open", style = MaterialTheme.typography.bodySmall)
      LoadingState.Idle -> Unit
    }

    Button(
      onClick = { session?.let { onOpen(it) } },
      enabled = isReady,
      modifier = Modifier.fillMaxWidth(),
    ) { Text("Open") }

    result?.let { LinkResultCard(result = it, onDismiss = onClearResult) }
  }
}
