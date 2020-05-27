package com.plaid.linksample.network

import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import retrofit2.http.POST

/**
 * API calls to our localhost token server.
 */
interface LinkSampleApi {

  @POST("/get_add_token")
  fun getItemAddToken(): Single<ItemAddToken>
}

data class ItemAddToken(
  @SerializedName("add_token") val add_token: String
)

