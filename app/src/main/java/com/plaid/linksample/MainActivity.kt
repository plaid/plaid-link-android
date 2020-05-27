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
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.configuration.PlaidProduct
import com.plaid.link.linkTokenConfiguration
import com.plaid.link.openPlaidLink
import com.plaid.link.result.PlaidLinkResultHandler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

  private lateinit var result: TextView
  private lateinit var tokenResult: TextView
  private val linkSampleApi by lazy { (application as LinkSampleApplication).linkSampleApi }

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
    tokenResult = findViewById<TextView>(R.id.public_token_result)

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
    this@MainActivity.openPlaidLink(
      linkTokenConfiguration = linkTokenConfiguration {
        token = getLinkTokenFromServer()
      }
    )
  }

  /**
   * In production, make an API request to your server to fetch
   * a new link_token. Learn more at https://plaid.com/docs/#create-link-token.
   *
   * This is a dummy implementation. If you curl for a link_token, you can
   * copy and paste the link_token value here.
   */
  private fun getLinkTokenFromServer(): String {
    return linkSampleApi.getItemAddToken().blockingGet().add_token
    //return "<GENERATED_LINK_TOKEN>"
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
