package com.zeitheist.cryptoexchangelib.pojo.bitvavo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BitvavoDepositHistory(
    val address: String?,
    val amount: Float,
    val fee: Float,
    val symbol: String,
    val timestamp: Long
)