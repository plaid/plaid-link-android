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
import com.plaid.linksample.ui.EmbeddedScreen
import com.plaid.linksample.ui.theme.LinkSampleTheme

private const val TAG = "PlaidLinkSample"

/**
 * Embedded institution search. Selecting an institution hands off to full Link, which the SDK
 * launches through the [OpenPlaidLink] launcher passed into `createPlaidEmbeddedLinkView`. Because
 * that handoff can route through OAuth, the launcher is field-registered so the continuation result
 * is re-delivered here.
 */
class EmbeddedActivity : ComponentActivity() {
  private var continuationResult by mutableStateOf<LinkResult?>(null)

  private val openLink: ActivityResultLauncher<PlaidSession> =
    registerForActivityResult(OpenPlaidLink()) { continuationResult = it }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Plaid.setLinkEventListener { event -> Log.d(TAG, "Link event: ${event.eventName.json}") }
    setContent {
      LinkSampleTheme {
        EmbeddedScreen(
          continuationResult = continuationResult,
          onClearResult = { continuationResult = null },
          openLink = openLink,
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
