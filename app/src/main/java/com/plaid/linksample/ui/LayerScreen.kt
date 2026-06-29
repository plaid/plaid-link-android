/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.plaid.link.result.LinkResult
import com.plaid.linksample.ui.components.DetailScaffold
import com.plaid.linksample.ui.components.InfoCard
import com.plaid.linksample.ui.components.LabeledTextField
import com.plaid.linksample.ui.components.LinkResultCard

/**
 * Layer: enter a phone number and start the flow. Layer readiness is not an `onLoad` callback; it
 * arrives as the `LAYER_READY` event, which is the cue to open. The host Activity owns the event
 * listener and the [com.plaid.link.PlaidLayerSession], opens on `LAYER_READY`, and on `LAYER_NOT_AVAILABLE` falls
 * back to submitting a date of birth.
 */
@Composable
fun LayerScreen(
  result: LinkResult?,
  onClearResult: () -> Unit,
  layerNotAvailable: Boolean,
  onStartLayer: (token: String, phone: String) -> Unit,
  onSubmitDob: (dob: String) -> Unit,
  onBack: () -> Unit,
) {
  var linkToken by rememberSaveable { mutableStateOf("") }
  var phone by rememberSaveable { mutableStateOf("4155550015") }
  var dateOfBirth by rememberSaveable { mutableStateOf("") }

  DetailScaffold(title = "Layer", onBack = onBack) {
    InfoCard("Layer needs a Layer-enabled link_token. Submit a phone number, then Layer opens once it reports LAYER_READY.")

    LabeledTextField(
      label = "Link token",
      value = linkToken,
      onValueChange = { linkToken = it },
      placeholder = "link-sandbox-...",
    )
    LabeledTextField(
      label = "Phone number",
      value = phone,
      onValueChange = { phone = it },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
    )

    Button(
      onClick = { onStartLayer(linkToken, formatPhone(phone)) },
      enabled = linkToken.isNotBlank(),
      modifier = Modifier.fillMaxWidth(),
    ) { Text("Start Layer") }

    if (layerNotAvailable) {
      InfoCard("Layer is not available for this user. Fallback: submit a date of birth (YYYY-MM-DD).")
      LabeledTextField(
        label = "Date of birth",
        value = dateOfBirth,
        onValueChange = { dateOfBirth = it },
        placeholder = "YYYY-MM-DD",
      )
      Button(
        onClick = { onSubmitDob(dateOfBirth) },
        enabled = dateOfBirth.isNotBlank(),
      ) { Text("Submit date of birth") }
    }

    result?.let { LinkResultCard(result = it, onDismiss = onClearResult) }
  }
}

private fun formatPhone(raw: String): String {
  if (raw.startsWith("+")) return raw
  val digits = raw.filter { it.isDigit() }
  return if (digits.length == 10) "+1$digits" else "+$digits"
}
