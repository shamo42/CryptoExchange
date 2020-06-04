package com.zeitheist.cryptoexchangelib.pojo.bitvavo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BitvavoWSTickerConfirmation(
    val event: String = "subscribed",
    val subscriptions: SubTicker
)

@JsonClass(generateAdapter = true)
internal data class SubTicker(
        val ticker: List<String>
)