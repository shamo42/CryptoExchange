package com.zeitheist.cryptoexchangelib.api

import com.zeitheist.cryptoexchangelib.pojo.bitvavo.BitvavoOrderCreateLimit
import com.zeitheist.cryptoexchangelib.pojo.bitvavo.BitvavoOrderCreateMarket
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

internal interface BitvavoApi {


    @GET("markets")
    fun getMarkets(): Single<Response<ResponseBody>>

    @GET("ticker/price")
    fun getTickerAll(): Single<Response<ResponseBody>>

    @GET("ticker/24h")
    fun getTickerSingleDetail(
            @Query("market") market: String
    ): Single<Response<ResponseBody>>

    @GET("{market}/book")
    fun getBook(
            @Path("market") market: String
    ): Single<Response<ResponseBody>>



    @GET("deposit")
    fun getDepositAddress(
        @HeaderMap authHeaders: Map<String, String>,
        @Query("symbol") symbol: String
    ): Single<Response<ResponseBody>>

    @GET("depositHistory")
    fun getDepositHistory(
        @HeaderMap authHeaders: Map<String, String>,
        @Query("symbol") symbol: String,
        @Query("limit") limit: Int,
        @Query("start") startUnix: Long?
    ): Single<Response<ResponseBody>>

    @GET("orders")
    fun getOrdersAll(
            @HeaderMap authHeaders: Map<String, String>,
            @Query("market") market: String,
            @Query("start") startUnix: Long? = null,
            @Query("end") endUnix: Long? = null,
            @Query("orderIdFrom") orderIdFrom: String? = null,
            @Query("orderIdTo") orderIdTo: String? = null,
            @Query("limit") limit: Int? = null
    ): Single<Response<ResponseBody>>

    @GET("ordersOpen")
    fun getOrdersOpen(
            @HeaderMap authHeaders: Map<String, String>,
            @Query("market") market: String? = null
    ): Single<Response<ResponseBody>>

    @DELETE("order")
    fun deleteOrder(
            @HeaderMap authHeaders: Map<String, String>,
            @Query("market") market: String,
            @Query("orderId") orderId: String
    ):  Single<Response<ResponseBody>>



    @POST("order")
    fun createOrderLimit(
            @HeaderMap authHeaders: Map<String, String>,
            @Body body: BitvavoOrderCreateLimit
    ): Single<Response<ResponseBody>>

    @POST("order")
    fun createOrderMarket(
            @HeaderMap authHeaders: Map<String, String>,
            @Body body: BitvavoOrderCreateMarket
    ): Single<Response<ResponseBody>>



    @GET("balance")
    fun getBalance(
        @HeaderMap authHeaders: Map<String, String>,
        @Query("symbol") symbol: String? = null
    ): Single<Response<ResponseBody>>
}