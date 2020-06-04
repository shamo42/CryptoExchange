package com.zeitheist.cryptoexchangelib.pojo.bitvavo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BitvavoMarket(
    val base: String,
    val market: String,
    val minOrderInBaseAsset: String,
    val minOrderInQuoteAsset: String,
    val orderTypes: List<String>,
    val pricePrecision: Int,
    val quote: String,
    val status: String
)