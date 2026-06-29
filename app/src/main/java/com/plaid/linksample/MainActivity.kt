/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.plaid.linksample.model.Example
import com.plaid.linksample.ui.ExampleListScreen
import com.plaid.linksample.ui.theme.LinkSampleTheme

/**
 * Landing screen. Lists the flows; tapping one starts its Activity. Each flow runs in its own
 * Activity so it can register its own result launcher (see the per-flow Activities).
 */
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      LinkSampleTheme {
        ExampleListScreen(onSelect = ::openExample)
      }
    }
  }

  private fun openExample(example: Example) {
    val target =
      when (example) {
        Example.STANDARD -> StandardLinkActivity::class.java
        Example.HEADLESS -> HeadlessActivity::class.java
        Example.LAYER -> LayerActivity::class.java
        Example.EMBEDDED -> EmbeddedActivity::class.java
      }
    startActivity(Intent(this, target))
  }
}
