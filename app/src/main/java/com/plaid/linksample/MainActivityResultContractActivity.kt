/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.plaid.link.OpenPlaidLink
import com.plaid.link.Plaid
import com.plaid.link.PlaidActivityResultContract
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkResult
import com.plaid.link.result.LinkSuccess
import com.plaid.linksample.network.LinkTokenRequester

class MainActivityResultContractActivity : AppCompatActivity() {

  private lateinit var result: TextView
  private lateinit var tokenResult: TextView

  @OptIn(PlaidActivityResultContract::class)
  // Experimental API using ActivityResultContract for androidx.fragment:1.3.0+
  private val openPlaidLink = this.registerForActivityResult(
    OpenPlaidLink()
  ) { linkResult: LinkResult ->
    when (linkResult) {
      is LinkSuccess -> {
        tokenResult.text = getString(R.string.public_token_result, linkResult.publicToken)
        result.text = getString(R.string.content_success)
      }
      is LinkExit -> {
        tokenResult.text = ""
        if (linkResult.error != null) {
          result.text = getString(
            R.string.content_exit,
            linkResult.error?.displayMessage,
            linkResult.error?.errorCode
          )
        } else {
          result.text = getString(
            R.string.content_cancel,
            linkResult.metadata.status?.jsonValue ?: "unknown"
          )
        }
      }
      else -> {
        throw RuntimeException("Got unexpected result:$linkResult")
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    result = findViewById(R.id.result)
    tokenResult = findViewById(R.id.public_token_result)

    val button = findViewById<View>(R.id.open_link)
    button.setOnClickListener {
      setOptionalEventListener()
      openLink()
    }
  }

  override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
    val view = super.onCreateView(name, context, attrs)
    setActionBar(findViewById(R.id.toolbar))
    return view
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
    LinkTokenRequester.token.subscribe(::onLinkTokenSuccess, ::onLinkTokenError)
  }

  private fun onLinkTokenSuccess(linkToken: String) {
    val tokenConfiguration = LinkTokenConfiguration.Builder()
      .token(linkToken)
      .build()

    // Experimental API using ActivityResultContract for androidx.fragment:1.3.0+
    openPlaidLink.launch(tokenConfiguration)
  }

  private fun onLinkTokenError(error: Throwable) {
    if (error is java.net.ConnectException) {
      Toast.makeText(this, "Please run `sh start_server.sh <client_id> <sandbox_secret>`", Toast.LENGTH_LONG).show()
      return
    }
    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_java, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean =
    when (item.itemId) {
      R.id.show_kotlin -> {
        val intent = Intent(this@MainActivityResultContractActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
}
