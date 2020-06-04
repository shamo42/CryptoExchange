package com.zeitheist.cryptoexchangelib.pojo.bitvavo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BitvavoOrderCreateMarket(
        val market: String,
        val side: String,

        val amount: String ? = null,
        val amountQuote: String ? = null,

        val orderType: String = "market",
        val price: String? = null,

        val postOnly: Boolean? = null,
        val disableMarketProtection: Boolean? = null,
        val responseRequired: Boolean? = null,
        val timeInForce: String? = null,
        val selfTradePrevention: String? = null
)