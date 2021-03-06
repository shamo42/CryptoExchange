package com.zeitheist.cryptoexchangelib.pojo.bitvavo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BitvavoDepositAddress(
    val address: String,
    val paymentId: String?
)