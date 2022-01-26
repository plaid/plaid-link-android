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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.plaid.link.OpenPlaidLink
import com.plaid.link.Plaid
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkSuccess
import com.plaid.linksample.network.LinkTokenRequester

class MainActivity : AppCompatActivity() {

  private lateinit var result: TextView
  private lateinit var tokenResult: TextView
  private val linkAccountToPlaid = registerForActivityResult(OpenPlaidLink()) { result ->
    when (result) {
      is LinkSuccess -> showSuccess(result)
      is LinkExit -> showFailure(result)
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
    linkAccountToPlaid.launch(tokenConfiguration)
  }

  private fun onLinkTokenError(error: Throwable) {
    if (error is java.net.ConnectException) {
      Toast.makeText(this, "Please run `sh start_server.sh <client_id> <sandbox_secret>`", Toast.LENGTH_LONG).show()
      return
    }
    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
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
      R.id.show_activity_result -> {
        val intent = Intent(this@MainActivity, MainActivityStartActivityForResult::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        true
      }
      R.id.show_activity_result_java -> {
        val intent = Intent(this@MainActivity, MainActivityStartActivityForResultJava::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }

  private fun showSuccess(success: LinkSuccess) {
    tokenResult.text = getString(R.string.public_token_result, success.publicToken)
    result.text = getString(R.string.content_success)
  }

  private fun showFailure(exit: LinkExit) {
    tokenResult.text = ""
    if (exit.error != null) {
      result.text = getString(R.string.content_exit, exit.error?.displayMessage, exit.error?.errorCode)
    } else {
      result.text = getString(R.string.content_cancel, exit.metadata.status?.jsonValue ?: "unknown")
    }
  }
}
