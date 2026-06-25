/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.plaid.link.Plaid

/** Displays the linked SDK version via [Plaid.VERSION_NAME]. */
@Composable
fun VersionInfo(modifier: Modifier = Modifier) {
  Text(
    text = "Plaid Link SDK v${Plaid.VERSION_NAME}",
    style = MaterialTheme.typography.labelMedium,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = modifier,
  )
}
