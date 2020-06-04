package com.zeitheist.cryptoexchangelib.pojo.bitvavo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BitvavoOrderCreateLimit(
        val market: String,
        val side: String,
        val price: String,

        val amount: String,

        val amountQuote: String? = null,
        val orderType: String = "limit",
        val postOnly: Boolean? = null,
        val disableMarketProtection: Boolean? = null,
        val responseRequired: Boolean? = null,
        val timeInForce: String? = null,
        val selfTradePrevention: String? = null
)