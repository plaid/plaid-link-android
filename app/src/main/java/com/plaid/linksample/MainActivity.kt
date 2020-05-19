/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.plaid.link.Plaid
import com.plaid.linkbase.models.configuration.LinkConfiguration
import com.plaid.linkbase.models.configuration.PlaidProduct
import com.plaid.linkbase.models.connection.PlaidLinkResultHandler

class MainActivity : AppCompatActivity() {

  companion object {
    const val LINK_REQUEST_CODE = 1
  }

  private lateinit var result: TextView

  private val myPlaidResultHandler by lazy {
    PlaidLinkResultHandler(
      requestCode = LINK_REQUEST_CODE,
      onSuccess = {
        result.text = getString(R.string.content_success, it.publicToken)
      },
      onCancelled = {
        result.text = getString(
          R.string.content_cancelled,
          it.institutionId,
          it.institutionName,
          it.linkSessionId,
          it.status
        )
      },
      onExit = {
        result.text = getString(
          R.string.content_exit,
          it.errorMessage,
          it.errorCode
        )
      }
    )
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    result = findViewById(R.id.result)

    val button = findViewById<View>(R.id.open_link)
    button.setOnClickListener {
      setOptionalEventListener()
      openLink()
    }
  }

  /**
   * Optional, set an [event listener](https://plaid.com/docs/link/android/#handling-onevent).
   */
  private fun setOptionalEventListener() = Plaid.setLinkEventListener { event ->
    Log.i("Event", event.toString())
  }

  /**
   * For all Link configuration options, have a look at the
   * [parameter reference](https://plaid.com/docs/link/android/#parameter-reference).
   */
  private fun openLink() {
    Plaid.openLink(
      activity = this,
      linkConfiguration = LinkConfiguration(
        clientName = "Link demo",
        products = listOf(PlaidProduct.TRANSACTIONS)
      ),
      requestCode = LINK_REQUEST_CODE
    )
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, intent)
    if (!myPlaidResultHandler.onActivityResult(requestCode, resultCode, data)) {
      Log.i(MainActivity::class.java.simpleName, "Not handled")
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean =
    when (item.itemId) {
      R.id.show_java -> {
        val intent = Intent(this@MainActivity, MainActivityJava::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
}
