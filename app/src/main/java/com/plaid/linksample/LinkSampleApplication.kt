/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample

import android.app.Application
import com.plaid.link.Plaid
import com.plaid.linkbase.models.configuration.PlaidEnvironment
import com.plaid.linkbase.models.configuration.PlaidOptions
import com.plaid.linksample.network.LinkSampleApi
import com.plaid.linksample.network.LinkSampleRetrofit
import com.plaid.log.LogLevel

class LinkSampleApplication : Application() {

  val linkSampleApi = LinkSampleRetrofit.retrofit.create(LinkSampleApi::class.java)

  override fun onCreate() {
    super.onCreate()

    Plaid.initialize(this)
  }
}
