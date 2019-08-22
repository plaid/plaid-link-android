package com.plaid.linksample

import android.app.Application
import com.plaid.link.Plaid
import com.plaid.linkbase.models.PlaidOptions
import com.plaid.linkbase.models.PlaidEnvironment
import com.plaid.plog.LogLevel

class LinkSampleApplication : Application() {

  override fun onCreate() {
    super.onCreate()

    val plaidOptions = PlaidOptions(if (BuildConfig.DEBUG) LogLevel.VERBOSE else LogLevel.ASSERT, PlaidEnvironment.SANDBOX)

    Plaid.create(this, plaidOptions)
  }
}
