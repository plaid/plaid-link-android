package com.plaid.linksample.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class LinkSampleRetrofit {

  companion object {
    // Modify this value to your PC's IP address if not testing on emulator.
    private val baseUrl = "http://10.0.2.2:8000"

    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(OkHttpClient.Builder().build())
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .build()
  }
}

