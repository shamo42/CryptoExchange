package com.zeitheist.cryptoexchangelib.functions.exchanges

import android.content.SharedPreferences
import android.net.Uri
import com.kyoapps.zkotlinextensions.extensions.*
import com.kyoapps.zkotlinextensions.objects.ResultObject
import com.zeitheist.cryptoexchangelib.api.BitvavoApi
import com.zeitheist.cryptoexchangelib.entities.*
import com.zeitheist.cryptoexchangelib.functions.FuncBook
import com.zeitheist.cryptoexchangelib.functions.FuncCrypt
import com.zeitheist.cryptoexchangelib.helpers.C_SETTINGS
import com.zeitheist.cryptoexchangelib.helpers.C_CONST
import com.zeitheist.cryptoexchangelib.pojo.bitvavo.*
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.NullPointerException
import java.util.concurrent.TimeUnit


object XBitvavo {

    /**
     * REST API
     */

    fun getPairs(okHttpClient: OkHttpClient = client): Single<ResultObject<List<ZHCurrencyPair>>> {
        return bitvavoApi(okHttpClient).getMarkets()
            .toResultObjectList<BitvavoMarket>()
            .mapResult { it.map {
                ZHCurrencyPair(
                    C_CONST.BITVAVO_ID,
                    it.market,
                    "${it.base}/${it.quote}"
                )
            } }
            .subscribeOn(Schedulers.io())
    }


    fun getTickerAll(okHttpClient: OkHttpClient = client): Single<ResultObject<List<ZHTicker>>> {
        return bitvavoApi(okHttpClient).getTickerAll()
            .toResultObjectList<BitvavoTickerPrice>()
            .mapResult { it.map {
                ZHTicker(
                    C_CONST.BITVAVO_ID,
                    it.market,
                    it.price,
                    symbolFormatted = it.market.replace("-", "/")
                )
            } }
            .subscribeOn(Schedulers.io())
    }

    fun getTickerSingle(okHttpClient: OkHttpClient = client, pair: String): Single<ResultObject<ZHTicker>> {
        return bitvavoApi(okHttpClient).getTickerSingleDetail(pair)
            .toResultObject<BitvavoTicker24h>()
            .mapResult {
                ZHTicker(
                    C_CONST.BITVAVO_ID,
                    it.market,
                    it.last,
                    it.bid,
                    it.bidSize,
                    it.ask,
                    it.askSize,
                    it.market.replace("-", "/")
                )
            }
            .subscribeOn(Schedulers.io())
    }


    fun getBook(okHttpClient: OkHttpClient = client, pair: String): Single<ResultObject<ZHBook>> {
        return bitvavoApi(okHttpClient).getBook(pair)
            .toResultObject<BitvavoBook>()
            .mapResult { book ->
                ZHBook(
                    book.asks.associateBy(
                        { it.first() },
                        { it.last() }).toMutableMap(),
                    (book.bids.associateBy({ it.first() }, { it.last() })).toMutableMap()
                )
            }
            .subscribeOn(Schedulers.io())
    }

    fun createOrderLimit(okHttpClient: OkHttpClient = client, securePreferences: SharedPreferences, pair: String, buy: Boolean, price: Float, amount: Float): Single<ResultObject<ZHOrder>> {
        val limitOrder = BitvavoOrderCreateLimit(
                market = pair,
                side = if (buy) "buy" else "sell",
                price = price.toString(),
                amount = amount.toString()
        )

        return bitvavoApi(okHttpClient).createOrderLimit(
            getAuthHeader(securePreferences,"POST","order", HashMap<String, String>(), limitOrder.toJsonString()),
                limitOrder
        )
            .toResultObject<BitvavoOrder>()
            .mapResult { bitvavoOrderToCommonOrder(it) }
            .subscribeOn(Schedulers.io())
    }

    fun createOrderMarket(okHttpClient: OkHttpClient = client, securePreferences: SharedPreferences, pair: String,
                          buy: Boolean, amount: Float? = null, amountQuote: Float? = null): Single<ResultObject<ZHOrder>> {
        if (amount == null && amountQuote == null) return Single.error(Throwable(NullPointerException("amount null")))

        val marketOrder = if (amount == null) {
            BitvavoOrderCreateMarket(
                market = pair,
                side = if (buy) "buy" else "sell",
                amountQuote = amountQuote.toString()
            )
        } else {
            BitvavoOrderCreateMarket(
                market = pair,
                side = if (buy) "buy" else "sell",
                amount = amount.toString()
            )
        }

        return bitvavoApi(okHttpClient).createOrderMarket(
            getAuthHeader(securePreferences,"POST","order", HashMap<String, String>(), marketOrder.toJsonString()),
                marketOrder
        )
            .toResultObject<BitvavoOrder>()
            .mapResult { bitvavoOrderToCommonOrder(it) }
            .subscribeOn(Schedulers.io())
    }


    fun getOrders(okHttpClient: OkHttpClient = client, securePreferences: SharedPreferences, pair: String,
                  limit: Int? = null, startUnix: Long? = null, orderIdFrom: String? = null): Single<ResultObject<List<ZHOrder>>> {
        val queryMap = HashMap<String, String>()
        queryMap["market"] = pair
        if (limit != null) queryMap["limit"] = limit.toString()
        if (startUnix != null) queryMap["start"] = startUnix.toString()
        if (orderIdFrom != null) queryMap["orderIdFrom"] = orderIdFrom

        return bitvavoApi(okHttpClient).getOrdersAll(
            getAuthHeader(securePreferences,"GET","orders", queryMap),
                pair,
                startUnix = startUnix,
                orderIdFrom = orderIdFrom
        )
            .toResultObjectList<BitvavoOrder>()
            .mapResult { it.map { bitvavoOrderToCommonOrder(it) } }
            .subscribeOn(Schedulers.io())
    }

    fun getOrdersOpen(okHttpClient: OkHttpClient = client, securePreferences: SharedPreferences, pair: String): Single<ResultObject<List<ZHOrder>>> {
        val queryMap = HashMap<String, String>()
        queryMap["market"] = pair

        return bitvavoApi(okHttpClient).getOrdersOpen(
            getAuthHeader(securePreferences,"GET","ordersOpen", queryMap),
                pair
        )
            .toResultObjectList<BitvavoOrder>()
            .mapResult { it.map { bitvavoOrderToCommonOrder(it) } }
            .subscribeOn(Schedulers.io())
    }

    fun deleteOrder(okHttpClient: OkHttpClient = client, securePreferences: SharedPreferences, pair: String, orderId: String): Single<ResultObject<String>> {
        val queryMap = HashMap<String, String>()
        queryMap["market"] = pair
        queryMap["orderId"] = orderId

        return bitvavoApi(okHttpClient).deleteOrder(
            getAuthHeader(securePreferences, "DELETE","order", queryMap ),
                pair, orderId
        )
            .toResultObject<BitvavoOrderId>()
            .mapResult { it.orderId }
            .subscribeOn(Schedulers.io())
    }

    fun getDepositAddress(okHttpClient: OkHttpClient = client, securePreferences: SharedPreferences, symbol: String): Single<ResultObject<String>> {
        val queryMap = HashMap<String, String>()
        queryMap["symbol"] = symbol

         return bitvavoApi(okHttpClient).getDepositAddress(
             getAuthHeader(securePreferences, "GET", "deposit", queryMap),
             symbol
         )
             .toResultObject<BitvavoDepositAddress>()
             .mapResult { it.address }
             .subscribeOn(Schedulers.io())
    }

    fun getDepositHistory(okHttpClient: OkHttpClient = client, securePreferences: SharedPreferences,
                          symbol: String, limit: Int = 12, startUnix: Long? = null): Single<ResultObject<List<ZHDeposit>>> {
        val queryMap = HashMap<String, String>()
        queryMap["symbol"] = symbol
        queryMap["limit"] = limit.toString()
        if (startUnix != null)  queryMap["start"] = startUnix.toString()

        return bitvavoApi(okHttpClient).getDepositHistory(
            getAuthHeader(securePreferences, "GET", "depositHistory", queryMap),
            symbol,
            limit,
            startUnix
        )
            .toResultObjectList<BitvavoDepositHistory>()
            .mapResult { it.map {
                ZHDeposit(
                    it.address,
                    it.amount,
                    it.fee,
                    it.symbol,
                    it.timestamp
                )
            } }
            .subscribeOn(Schedulers.io())
    }

    fun getBalance(okHttpClient: OkHttpClient = client, securePreferences: SharedPreferences, symbol: String? = null): Single<ResultObject<List<ZHBalance>>> {
        val queryMap = HashMap<String, String>()
        if (symbol != null) queryMap["symbol"] = symbol

        return bitvavoApi(okHttpClient).getBalance(
            getAuthHeader(securePreferences, "GET", "balance", queryMap),
            symbol
        )
            .toResultObjectList<BitvavoBalance>()
            .mapResult { it.map {
                ZHBalance(
                    it.symbol,
                    it.available,
                    it.inOrder
                )
            } }
            .subscribeOn(Schedulers.io())
    }





    /**
     * WebSocket
     */

    fun getTickerWebSocket(okHttpClient: OkHttpClient, pairList: List<String>): Flowable<ResultObject<ZHTicker>> {
        return okHttpClient.toWebSocketFlowable(C_CONST.BITVAVO_WEBSOCKET_URL)
            .toResultObject<BitvavoWSTicker24Result, BitvavoWSTickerConfirmation>(
                    BitvavoWSRequest(listOf(Channel(pairList, "ticker24h"))).toJsonString()
            )
            .flattenResultObject { it.data }
            .mapResult {
                ZHTicker(
                    C_CONST.BITVAVO_ID,
                    it.market,
                    it.last,
                    it.bid,
                    it.bidSize,
                    it.ask,
                    it.askSize,
                    it.market.replace("-", "/")
                )
            }
            .subscribeOn(Schedulers.io())
    }


    private fun getBookDeltaWebSocket(okHttpClient: OkHttpClient, pairList: List<String>): Flowable<ResultObject<ZHBook>> {
        return okHttpClient.toWebSocketFlowable(C_CONST.BITVAVO_WEBSOCKET_URL)
            .toResultObject<BitvavoBook, BitvavoBook>(
                    BitvavoWSRequest(listOf(Channel(pairList, "book"))).toJsonString()
            )
            .mapResult { book ->
                ZHBook(
                    book.asks.associateBy(
                        { it.first() },
                        { it.last() }).toMutableMap(),
                    (book.bids.associateBy({ it.first() }, { it.last() })).toMutableMap()
                )
            }
            .subscribeOn(Schedulers.io())
    }



    fun getBookWebSocket(okHttpClient: OkHttpClient = client, pair: String): Flowable<ResultObject<ZHBook>> {

        return getBook(okHttpClient, pair)
            .flatMapPublisher { initialBookResult ->
                getBookDeltaWebSocket(okHttpClient, listOf(pair))
                    .scan(initialBookResult, { mainBookResult, deltaBookResult ->
                        FuncBook.updateBook(mainBookResult, deltaBookResult)
                    })
            }
            .subscribeOn(Schedulers.io())
    }





    /**
     * Helper functions
     */


    fun setApiData(securePreferences: SharedPreferences, key:String, secret: String) {
        securePreferences.edit()
            .putString(C_SETTINGS.BITVAVO_KEY, key)
            .putString(C_SETTINGS.BITVAVO_SECRET, secret)
            .apply()
    }

    fun hasApiData(securePreferences: SharedPreferences): Boolean {
        return securePreferences.contains(C_SETTINGS.BITVAVO_KEY)
                && securePreferences.contains(C_SETTINGS.BITVAVO_SECRET)
    }

    private fun getAuthHeader(secureSettings: SharedPreferences, method: String, endPoint: String, queryMap: HashMap<String, String> = HashMap(),
                      jsonBody: String? = null, accessWindowMs: Long = 10000): HashMap<String, String> {

        val timeStamp = System.currentTimeMillis()
        val authMap = java.util.HashMap<String, String>()
        authMap["Bitvavo-Access-Key"] = secureSettings.getString(C_SETTINGS.BITVAVO_KEY, null)!!
        authMap["Bitvavo-Access-Timestamp"] = "$timeStamp"
        authMap["Bitvavo-Access-Signature"] =
            generateSignatureREST(
                secureSettings.getString(C_SETTINGS.BITVAVO_SECRET, null)!!,
                method,
                endPoint,
                timeStamp,
                queryMap,
                jsonBody
            )
        authMap["Bitvavo-Access-Window"] = accessWindowMs.toString()

        return authMap
    }


    private fun generateSignatureREST(apiSecret: String, method: String, urlEndpoint: String, timestamp: Long, queryMap: HashMap<String, String>, jsonBody: String?): String {

        val url = Uri.Builder()
            .encodedPath(C_CONST.BITVAVO_BASE_URL)
            .appendEncodedPath(urlEndpoint)
            .run {
                queryMap.forEach { appendQueryParameter(it.key, it.value) }
                build().toString()
                    .replace(C_CONST.BITVAVO_BASE_URL, "$method/v2/")
            }

        return FuncCrypt.hmacShaHexString("$timestamp$url${jsonBody ?: ""}", apiSecret,256)
    }


    private fun generateSignatureWebSocket(apiKey: String, apiSecret: String, method: String): String {
        val timestamp = System.currentTimeMillis()

        val signature = FuncCrypt.hmacShaHexString("$timestamp$method/v2/webbsocket}", apiSecret,256)
        return BitvavoWSRequestAuthenticate("authenticate", apiKey, signature, timestamp).toJsonString()


    }


    private fun bitvavoApi(okHttpClient: OkHttpClient): BitvavoApi {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(C_CONST.BITVAVO_BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(BitvavoApi::class.java)
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .build()
    }


    private val headerInterceptor: Interceptor by lazy { Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("User-Agent", C_CONST.USER_AGENT)
            .addHeader("Content-Type", "application/json")
            .build()
        val readNew = request.header(READ_TIMEOUT_S)
        val readTimeout = if (readNew != null && !readNew.isEmpty()) readNew.toInt() else 6

        chain
            .withConnectTimeout(8, TimeUnit.SECONDS)
            .withReadTimeout(readTimeout, TimeUnit.SECONDS)
            .withWriteTimeout(8, TimeUnit.SECONDS)
            .proceed(request)
        }
    }

    private fun bitvavoOrderToCommonOrder(order: BitvavoOrder): ZHOrder {
        return ZHOrder(
            order.side,
            order.amount,
            order.filledAmount,
            order.created,
            order.feePaid,
            order.market,
            order.onHold,
            order.onHoldCurrency,
            order.orderId,
            order.orderType,
            order.price,
            order.status,
            order.feeCurrency,
            order.amountRemaining,
            order.filledAmountQuote,
            order.timeInForce,
            order.postOnly,
            order.amountQuote,
            order.amountQuoteRemaining,
            order.updated,
            order.fills
        )
    }

    private const val TAG = "XBitvavo"
    private const val READ_TIMEOUT_S = "READ_TIMEOUT_S"

}


