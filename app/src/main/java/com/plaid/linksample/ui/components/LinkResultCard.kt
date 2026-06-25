/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkResult
import com.plaid.link.result.LinkSuccess

/**
 * Compact view of a terminal [LinkResult]: the public token on success, or the error code, message
 * and exit status on exit.
 */
@Composable
fun LinkResultCard(
  result: LinkResult,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val isSuccess = result is LinkSuccess
  Card(
    modifier = modifier.fillMaxWidth(),
    colors =
      CardDefaults.cardColors(
        containerColor =
          if (isSuccess) {
            MaterialTheme.colorScheme.primaryContainer
          } else {
            MaterialTheme.colorScheme.surfaceVariant
          },
      ),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = if (isSuccess) "LinkSuccess" else "LinkExit",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.SemiBold,
        )
        TextButton(onClick = onDismiss) { Text("Dismiss") }
      }
      when (result) {
        is LinkSuccess -> {
          Text("public_token: ${result.publicToken}", style = MaterialTheme.typography.bodyMedium)
          result.metadata.institution?.let {
            Text("institution: ${it.name}", style = MaterialTheme.typography.bodyMedium)
          }
        }

        is LinkExit -> {
          val error = result.error
          if (error != null) {
            Text("error: ${error.errorCode.json}", style = MaterialTheme.typography.bodyMedium)
            Text(
              text = error.displayMessage?.ifBlank { null } ?: error.errorMessage,
              style = MaterialTheme.typography.bodyMedium,
            )
          } else {
            Text(
              "status: ${result.metadata.status?.jsonValue ?: "unknown"}",
              style = MaterialTheme.typography.bodyMedium,
            )
          }
        }
      }
    }
  }
}
