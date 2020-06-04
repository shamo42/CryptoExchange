package com.zeitheist.cryptoexchangelib.api

import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface KrakenApi {


    @GET("0/public/AssetPairs")
    fun getMarkets(): Single<Response<ResponseBody>>

    @GET("0/public/Ticker")
    fun getTicker(
        @Query("pair") pairCommaDelimitedList : String
    ): Single<Response<ResponseBody>>


}