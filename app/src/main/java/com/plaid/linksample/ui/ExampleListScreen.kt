/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.plaid.linksample.model.Example
import com.plaid.linksample.ui.components.VersionInfo

/** Landing screen: a list of the Link flows, each opening its own detail screen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExampleListScreen(onSelect: (Example) -> Unit) {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Plaid Link") }) },
  ) { padding ->
    LazyColumn(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(padding)
          .padding(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item { VersionInfo() }
      items(Example.entries) { example ->
        ExampleCard(example = example, onClick = { onSelect(example) })
      }
    }
  }
}

@Composable
private fun ExampleCard(
  example: Example,
  onClick: () -> Unit,
) {
  Card(
    modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Text(example.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
      Text(
        example.description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
