/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PlaidColors =
  lightColorScheme(
    primary = Color(0xFF0A85EA),
    onPrimary = Color.White,
  )

@Composable
fun LinkSampleTheme(content: @Composable () -> Unit) {
  MaterialTheme(colorScheme = PlaidColors, content = content)
}
