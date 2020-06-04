package com.zeitheist.cryptoexchangelib.pojo.common

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ZHDeposit(
    val address: String?,
    val amount: Float,
    val fee: Float,
    val symbol: String,
    val timestamp: Long,
    val id: String? = null
)