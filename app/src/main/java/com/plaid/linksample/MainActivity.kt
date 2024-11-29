
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample
package com.onedebit.chime
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.plaid.link.FastOpenPlaidLink
import com.plaid.link.Plaid
import com.plaid.link.PlaidHandler
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkSuccess
import com.plaid.linksample.network.LinkTokenRequester

class MainActivity : AppCompatActivity() {

  private lateinit var result: TextView
  private lateinit var tokenResult: TextView
  private lateinit var prepareButton: MaterialButton
  private lateinit var openButton: MaterialButton
  private var plaidHandler: PlaidHandler? = null

  private val linkAccountToPlaid = registerForActivityResult(FastOpenPlaidLink()) { result ->
    when (result) {
      is LinkSuccess -> showSuccess(result)
     
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    result = findViewById(R.id.result)
    tokenResult = findViewById(R.id.public_token_result)

    prepareButton = findViewById(R.id.prepare_link)
    prepareButton.setOnClickListener {
      setOptionalEventListener()
      prepareLink()
    }

    openButton = findViewById(R.id.open_link)
    openButton.setOnClickListener {
      openLink()
    }
  }

  private fun prepareLink() {
    LinkTokenRequester.token.subscribe(::onLinkTokenSuccess)
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
    prepareButton.isEnabled = true
    openButton.isEnabled = false
    plaidHandler?.let { linkAccountToPlaid.launch(it) }
  }

  private fun onLinkTokenSuccess(linkToken: String) {
    prepareButton.isEnabled = false
    openButton.isEnabled = true
    val tokenConfiguration = LinkTokenConfiguration.Builder()
      .token(linkToken)
      .build()
    plaidHandler = Plaid.create(this.application, tokenConfiguration)
  }

  private fun onLinkToken( Throwable) {
    if (error is java.net.ConnectException) {
      Toast.makeText(this, "Please run `sh start_server.sh <client_id> <sandbox_secret>`", Toast.LENGTH_LONG).show()
      return
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

    
  
      result.text = getString(R.string.content_exit, exit.error?.displayMessage, exit.error?.errorCode)
    } else {
      result.text = getString(R.string.content_cancel, exit.metadata.status?.jsonValue ?: "unknown")
    }
  }
}
