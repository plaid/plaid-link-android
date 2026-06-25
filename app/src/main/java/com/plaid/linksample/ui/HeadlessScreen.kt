/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.plaid.link.Plaid
import com.plaid.link.PlaidSession
import com.plaid.link.configuration.linkTokenConfiguration
import com.plaid.link.result.LinkResult
import com.plaid.linksample.ui.components.DetailScaffold
import com.plaid.linksample.ui.components.InfoCard
import com.plaid.linksample.ui.components.LabeledTextField
import com.plaid.linksample.ui.components.LinkResultCard

/**
 * Headless OAuth: created with `createPlaidHeadlessSession` and opened through the host Activity's
 * `OpenPlaidLink` launcher (via [onOpen]) — there is no `start()`. It runs an external-browser OAuth
 * handoff with no in-app WebView, so the host is always backgrounded and may be recreated; the
 * Activity-owned launcher registration is what lets the result survive that and arrive in [result].
 *
 * The token must resolve server-side to headless OAuth (e.g. an EU payment_initiation token with
 * `eu_config.headless`); a standard token fails fast with `SESSION_TYPE_MISMATCH`.
 */
@Composable
fun HeadlessScreen(
  result: LinkResult?,
  onClearResult: () -> Unit,
  onOpen: (PlaidSession) -> Unit,
  onBack: () -> Unit,
) {
  val context = LocalContext.current

  var linkToken by rememberSaveable { mutableStateOf("") }

  fun openHeadless() {
    if (linkToken.isBlank()) return
    onClearResult()
    val session =
      Plaid.createPlaidHeadlessSession(
        context,
        linkTokenConfiguration { token = linkToken },
      )
    onOpen(session)
  }

  DetailScaffold(title = "Headless", onBack = onBack) {
    InfoCard(
      "Headless needs a token that resolves to headless OAuth (e.g. an EU payment_initiation " +
        "token with eu_config.headless). Paste one above to run this flow; a standard token fails " +
        "with SESSION_TYPE_MISMATCH.",
    )

    LabeledTextField(
      label = "Link token",
      value = linkToken,
      onValueChange = { linkToken = it },
      placeholder = "link-sandbox-...",
    )

    Button(
      onClick = { openHeadless() },
      enabled = linkToken.isNotBlank(),
      modifier = Modifier.fillMaxWidth(),
    ) { Text("Open Headless") }

    result?.let { LinkResultCard(result = it, onDismiss = onClearResult) }
  }
}
