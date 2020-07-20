package com.plaid.linksample.network

import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import retrofit2.http.POST

/**
 * API calls to our localhost token server.
 */
interface LinkSampleApi {

  @POST("/get_link_token")
  fun getLinkToken(): Single<LinkToken>
}

data class LinkToken(
  @SerializedName("link_token") val link_token: String
)

