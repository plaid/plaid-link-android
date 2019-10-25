package com.plaid.linksample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.plaid.link.Plaid
import com.plaid.linkbase.models.LinkCancellation
import com.plaid.linkbase.models.LinkConfiguration
import com.plaid.linkbase.models.LinkConnection
import com.plaid.linkbase.models.LinkEventListener
import com.plaid.linkbase.models.PlaidApiError
import com.plaid.linkbase.models.PlaidProduct
import kotlinx.android.synthetic.main.activity_main.toolbar

class MainActivity : AppCompatActivity() {

  companion object {
    const val LINK_REQUEST_CODE = 1
  }

  private lateinit var contentTextView: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)
    contentTextView = findViewById(R.id.content)

    val fab = findViewById<FloatingActionButton>(R.id.open_link_fab)
    fab.setOnClickListener {
      Plaid.setLinkEventListener(linkEventListener = LinkEventListener {
        Log.i("Event", it.toString())
      })
      Plaid.openLink(
        this,
        LinkConfiguration(
          clientName = "Test App",
          products = listOf(PlaidProduct.TRANSACTIONS)
        ),
        LINK_REQUEST_CODE
      )
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, intent)
    if (requestCode == LINK_REQUEST_CODE) {
      when (resultCode) {
        Plaid.RESULT_SUCCESS ->
          (data?.getSerializableExtra(Plaid.LINK_RESULT) as LinkConnection).let {
            contentTextView.text = getString(
              R.string.content_success,
              it.publicToken,
              it.linkConnectionMetadata.accounts[0].accountId,
              it.linkConnectionMetadata.accounts[0].accountName,
              it.linkConnectionMetadata.institutionId,
              it.linkConnectionMetadata.institutionName
            )
          }
        Plaid.RESULT_CANCELLED ->
          (data?.getSerializableExtra(Plaid.LINK_RESULT) as LinkCancellation).let {
            contentTextView.text = getString(
              R.string.content_cancelled,
              it.institutionId,
              it.institutionName,
              it.linkSessionId,
              it.status
            )
          }
        Plaid.RESULT_EXIT ->
          (data?.getSerializableExtra(Plaid.LINK_RESULT) as PlaidApiError).let {
            contentTextView.text = getString(
              R.string.content_exit,
              it.displayMessage,
              it.errorCode,
              it.errorMessage,
              it.linkExitMetadata.institutionId,
              it.linkExitMetadata.institutionName,
              it.linkExitMetadata.status
            )
          }
        Plaid.RESULT_EXCEPTION ->
          (data?.getSerializableExtra(Plaid.LINK_RESULT) as java.lang.Exception).let {
            contentTextView.text = getString(
              R.string.content_exception,
              it.javaClass.toString(),
              it.message
            )
          }
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.kotlin_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean =
    when (item.itemId) {
      R.id.show_java -> {
        val intent = Intent(this@MainActivity, MainJavaActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
}
