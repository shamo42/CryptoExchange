package com.zeitheist.cryptoexchangelib.pojo.bitvavo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BitvavoBalance(
    val symbol: String,
    val available: Float,
    val inOrder: Float
)

