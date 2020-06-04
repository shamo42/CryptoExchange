package com.zeitheist.cryptoexchangelib.entities

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ZHBalance(
    val symbol: String,
    val available: Float,
    val inOrder: Float
)

