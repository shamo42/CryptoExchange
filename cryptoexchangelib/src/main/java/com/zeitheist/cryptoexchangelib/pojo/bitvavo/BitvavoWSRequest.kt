package com.zeitheist.cryptoexchangelib.pojo.bitvavo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BitvavoWSRequest(
    val channels: List<Channel>,
    val action: String = "subscribe"
)

@JsonClass(generateAdapter = true)
internal data class Channel(
        val markets: List<String>,
        val name: String
)