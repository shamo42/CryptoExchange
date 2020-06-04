package com.zeitheist.cryptoexchangelib.pojo.bitvavo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BitvavoTicker24h(
    val ask: Float,
    val askSize: Float,
    val bid: Float,
    val bidSize: Float,
    val high: Float,
    val last: Float,
    val low: Float,
    val market: String,
    val `open`: Float,
    val timestamp: Long,
    val volume: Float,
    val volumeQuote: Float
)