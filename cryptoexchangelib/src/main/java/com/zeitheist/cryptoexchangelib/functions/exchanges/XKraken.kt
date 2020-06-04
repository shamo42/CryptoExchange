package com.zeitheist.cryptoexchangelib.functions.exchanges

import android.content.SharedPreferences
import android.net.Uri
import com.kyoapps.zkotlinextensions.extensions.mapResult
import com.kyoapps.zkotlinextensions.extensions.toResultObject
import com.kyoapps.zkotlinextensions.objects.ResultObject
import com.zeitheist.cryptoexchangelib.api.KrakenApi
import com.zeitheist.cryptoexchangelib.functions.FuncCrypt
import com.zeitheist.cryptoexchangelib.helpers.C_CONST
import com.zeitheist.cryptoexchangelib.helpers.C_SETTINGS
import com.zeitheist.cryptoexchangelib.entities.ZHCurrencyPair
import com.zeitheist.cryptoexchangelib.pojo.kraken.KrakenPairs
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object XKraken {

    /**
     * REST API
     */

    fun getPairs(okHttpClient: OkHttpClient = client): Single<ResultObject<List<ZHCurrencyPair>>> {
        return krakenApi(okHttpClient).getMarkets()
            .toResultObject<KrakenPairs>()
            .mapResult { pairList ->
                if (pairList.result != null)
                    pairList.result.map {
                        ZHCurrencyPair(
                            C_CONST.KRAKEN_ID,
                            it.key,
                            "${it.value.base}/${it.value.quote}"
                        )
                    }
                else throw Throwable(pairList.error.toString())
            }
            .onErrorReturn { ResultObject.Error<List<ZHCurrencyPair>>(it) }
            .subscribeOn(Schedulers.io())
    }


    /**
     * Helper functions
     */



    fun setApiData(securePreferences: SharedPreferences, key:String, secret: String) {
        securePreferences.edit()
            .putString(C_SETTINGS.KRAKEN_KEY, key)
            .putString(C_SETTINGS.KRAKEN_SECRET, secret)
            .apply()
    }

    fun hasApiData(securePreferences: SharedPreferences): Boolean {
        return securePreferences.contains(C_SETTINGS.KRAKEN_KEY)
                && securePreferences.contains(C_SETTINGS.KRAKEN_SECRET)
    }

    private fun getAuthHeader(secureSettings: SharedPreferences, method: String, endPoint: String, queryMap: HashMap<String, String> = HashMap(),
                              jsonBody: String? = null, accessWindowMs: Long = 10000): HashMap<String, String> {

        val timeStamp = System.currentTimeMillis()
        val authMap = java.util.HashMap<String, String>()
        authMap["API-Key"] = secureSettings.getString(C_SETTINGS.KRAKEN_KEY, null)!!
        authMap["API-Sign"] =
            generateSignatureREST(
                secureSettings.getString(C_SETTINGS.KRAKEN_SECRET, null)!!,
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


    private fun krakenApi(okHttpClient: OkHttpClient): KrakenApi {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(C_CONST.KRAKEN_BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(KrakenApi::class.java)
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

    private const val READ_TIMEOUT_S = "READ_TIMEOUT_S"
    private const val TAG = "XKraken"

}