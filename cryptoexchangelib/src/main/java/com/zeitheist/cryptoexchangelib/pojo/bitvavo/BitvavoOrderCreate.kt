package com.zeitheist.cryptoexchangelib.pojo.bitvavo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BitvavoOrderCreate(
        val market: String,
        val side: String,
        val orderType: String,

        val amount: Float,
        val amountQuote: Float,
        val price: Float,

        val postOnly: Boolean = false,
        val disableMarketProtection: Boolean = false,
        val responseRequired: Boolean = true,
        val timeInForce: String = "GTC",
        val selfTradePrevention: String = "decrementAndCancel"
)