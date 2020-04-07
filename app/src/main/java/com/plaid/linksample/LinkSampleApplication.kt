/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample

import android.app.Application
import com.plaid.link.Plaid
import com.plaid.linkbase.models.configuration.PlaidEnvironment
import com.plaid.linkbase.models.configuration.PlaidOptions
import com.plaid.log.LogLevel

class LinkSampleApplication : Application() {

  override fun onCreate() {
    super.onCreate()

    val plaidOptions = PlaidOptions(
      if (BuildConfig.DEBUG) LogLevel.VERBOSE else LogLevel.ASSERT,
      PlaidEnvironment.SANDBOX
    )
    Plaid.setOptions(plaidOptions)
  }
}
