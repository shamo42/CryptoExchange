package com.zeitheist.cryptoexchangelib.pojo.kraken

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class KrakenPairs(val error: List<String>, val result: Map<String, KrakenPair>?)

@JsonClass(generateAdapter = true)
internal data class KrakenPair (
    val altname: String?,
    val aclass_base: String?,
    val base: String,
    val aclass_quote: String?,
    val quote: String,
    val lot: String,
    val pair_decimals: Int,
    val lot_decimals: Int,
    val lot_multiplier: Int,
    val leverage_buy: List<Int>,
    val leverage_sell: List<Int>,
    val fee_volume_currency: String,
    val margin_call: Long,
    val margin_stop: Long
)