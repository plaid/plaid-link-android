/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Detail-screen frame: a top bar with a back action over a vertically scrolling content column. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScaffold(
  title: String,
  onBack: () -> Unit,
  content: @Composable ColumnScope.() -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(title) },
        navigationIcon = { TextButton(onClick = onBack) { Text("‹ Back") } },
      )
    },
  ) { padding ->
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(padding)
          .padding(horizontal = 20.dp, vertical = 16.dp)
          .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      content = content,
    )
  }
}
