package com.zeitheist.cryptoexchangelib.pojo.bitvavo

import com.squareup.moshi.JsonClass
import com.zeitheist.cryptoexchangelib.entities.Fill

@JsonClass(generateAdapter = true)
internal data class BitvavoOrder(
    val amount: Float,
    val amountRemaining: Float,
    val amountQuote: Float?,
    val amountQuoteRemaining: Float?,
    val created: Long,
    val feeCurrency: String,
    val feePaid: Float,
    val filledAmount: Float,
    val filledAmountQuote: Float,
    val fills: List<Fill>,
    val market: String,
    val onHold: Float,
    val onHoldCurrency: String,
    val orderId: String,
    val orderType: String,
    val postOnly: Boolean,
    val price: Float,
    val selfTradePrevention: String,
    val side: String,
    val status: String,
    val timeInForce: String,
    val updated: Long,
    val visible: Boolean
)

