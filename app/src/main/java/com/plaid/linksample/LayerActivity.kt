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
import com.plaid.link.PlaidLayerSession
import com.plaid.link.PlaidSession
import com.plaid.link.SubmissionData
import com.plaid.link.configuration.LayerTokenConfiguration
import com.plaid.link.event.LinkEventName
import com.plaid.link.result.LinkResult
import com.plaid.linksample.ui.LayerScreen
import com.plaid.linksample.ui.theme.LinkSampleTheme

private const val TAG = "PlaidLinkSample"

/**
 * Layer. The Link event listener drives the flow: `LAYER_READY` is the cue to open and
 * `LAYER_NOT_AVAILABLE` triggers the date-of-birth fallback. Because the open is event-driven, the
 * Activity owns the [PlaidLayerSession] and launches it through the field-registered [OpenPlaidLink].
 */
class LayerActivity : ComponentActivity() {
  private var result by mutableStateOf<LinkResult?>(null)
  private var layerNotAvailable by mutableStateOf(false)
  private var session: PlaidLayerSession? = null

  private val openLink: ActivityResultLauncher<PlaidSession> =
    registerForActivityResult(OpenPlaidLink()) { result = it }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Plaid.setLinkEventListener { event ->
      Log.d(TAG, "Link event: ${event.eventName.json}")
      when (event.eventName) {
        is LinkEventName.LAYER_READY -> session?.let { openLink.launch(it) }
        is LinkEventName.LAYER_NOT_AVAILABLE -> layerNotAvailable = true
        else -> Unit
      }
    }
    setContent {
      LinkSampleTheme {
        LayerScreen(
          result = result,
          onClearResult = { result = null },
          layerNotAvailable = layerNotAvailable,
          onStartLayer = ::startLayer,
          onSubmitDob = ::submitDob,
          onBack = { finish() },
        )
      }
    }
  }

  private fun startLayer(
    token: String,
    phone: String,
  ) {
    result = null
    layerNotAvailable = false
    val newSession =
      Plaid.createPlaidLayerSession(this, LayerTokenConfiguration.Builder().token(token).build())
    session = newSession
    newSession.submit(SubmissionData(phoneNumber = phone))
  }

  private fun submitDob(dob: String) {
    session?.submit(SubmissionData(dateOfBirth = dob.ifBlank { null }))
  }

  override fun onDestroy() {
    super.onDestroy()
    Plaid.clearLinkEventListener()
  }
}
