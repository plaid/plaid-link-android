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
import com.plaid.link.Plaid
import com.plaid.link.linkTokenConfiguration
import com.plaid.link.openPlaidLink
import com.plaid.link.result.PlaidLinkResultHandler
import com.plaid.linksample.network.LinkTokenRequester

class MainActivity : AppCompatActivity() {

  private lateinit var result: TextView
  private lateinit var tokenResult: TextView

  private val myPlaidResultHandler by lazy {
    PlaidLinkResultHandler(
      onSuccess = {
        tokenResult.text = getString(R.string.public_token_result, it.publicToken)
        result.text = getString(R.string.content_success)
      },
      onExit = {
        tokenResult.text = ""
        if (it.error != null) {
          result.text = getString(
            R.string.content_exit,
            it.error?.displayMessage,
            it.error?.errorCode
          )
        } else {
          result.text = getString(
            R.string.content_cancel,
            it.metadata.status?.jsonValue ?: "unknown"
          )
        }
      }
    )
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
    this@MainActivity.openPlaidLink(
      linkTokenConfiguration = linkTokenConfiguration {
        token = linkToken
      }
    )
  }

  private fun onLinkTokenError(error: Throwable) {
    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
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
