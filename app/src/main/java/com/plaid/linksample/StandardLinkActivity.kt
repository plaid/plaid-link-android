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
import com.plaid.linksample.ui.StandardLinkScreen
import com.plaid.linksample.ui.theme.LinkSampleTheme

private const val TAG = "PlaidLinkSample"

/**
 * Standard Link. Registers the [OpenPlaidLink] launcher as a field so a result returned after
 * process death during OAuth is re-delivered, not dropped.
 */
class StandardLinkActivity : ComponentActivity() {
  private var result by mutableStateOf<LinkResult?>(null)

  private val openLink: ActivityResultLauncher<PlaidSession> =
    registerForActivityResult(OpenPlaidLink()) { result = it }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Plaid.setLinkEventListener { event -> Log.d(TAG, "Link event: ${event.eventName.json}") }
    setContent {
      LinkSampleTheme {
        StandardLinkScreen(
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
