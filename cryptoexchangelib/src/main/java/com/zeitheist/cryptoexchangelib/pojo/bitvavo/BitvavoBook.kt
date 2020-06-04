package com.zeitheist.cryptoexchangelib.pojo.bitvavo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BitvavoBook(
    val asks: List<List<Double>>,
    val bids: List<List<Double>>,
    val market: String,
    val nonce: Int
)