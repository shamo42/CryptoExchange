package com.zeitheist.cryptoexchangelib.pojo.bitvavo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BitvavoWSRequestAuthenticate(
    val action: String,
    val key: String,
    val signature: String,
    val timestamp: Long
)