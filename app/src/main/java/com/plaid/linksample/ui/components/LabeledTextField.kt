/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Single-line labeled text input. */
@Composable
fun LabeledTextField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  placeholder: String? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label) },
    placeholder = placeholder?.let { { Text(it) } },
    modifier = modifier.fillMaxWidth(),
    singleLine = true,
    keyboardOptions = keyboardOptions,
  )
}
