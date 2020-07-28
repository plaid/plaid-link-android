package com.plaid.linksample.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object LinkSampleApiFactory {
    // This value is setup to work with emulators. Modify this value to your PC's IP address if not.
    private val baseUrl = "http://10.0.2.2:8000"

    private val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(OkHttpClient.Builder().build())
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .build()

    val api = retrofit.create(LinkSampleApi::class.java)
}

