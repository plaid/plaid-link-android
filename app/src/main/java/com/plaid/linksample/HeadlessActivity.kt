/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.plaid.link.OpenPlaidLink
import com.plaid.link.Plaid
import com.plaid.link.PlaidSession
import com.plaid.link.result.LinkResult
import com.plaid.linksample.ui.HeadlessScreen
import com.plaid.linksample.ui.theme.LinkSampleTheme

private const val TAG = "PlaidLinkSample"

/**
 * Headless OAuth. This flow always hands off to an external browser, so the host is guaranteed to
 * be backgrounded and may be recreated. Registering [OpenPlaidLink] as a field — before the
 * Activity is STARTED — is what lets the result survive that and be re-delivered to [result] here.
 *
 * The Link event listener is registered for the Activity's lifetime (onCreate to onDestroy); each
 * event name is logged.
 */
class HeadlessActivity : ComponentActivity() {
  private var result by mutableStateOf<LinkResult?>(null)

  private val openLink: ActivityResultLauncher<PlaidSession> =
    registerForActivityResult(OpenPlaidLink()) { result = it }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Plaid.setLinkEventListener { event -> Log.d(TAG, "Link event: ${event.eventName.json}") }
    setContent {
      LinkSampleTheme {
        HeadlessScreen(
          result = result,
          onClearResult = { result = null },
          onOpen = { session -> openLink.launch(session) },
          onBack = { finish() },
        )
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    Plaid.clearLinkEventListener()
  }
}
