package com.zeitheist.cryptoexchangelib.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "zhOrder")
@JsonClass(generateAdapter = true)
data class ZHOrder(
    val side: String,
    val amount: Float,
    val filledAmount: Float,
    val timestamp: Long,
    val fee: Float,
    val currencyPair: String,
    val onHoldAmount: Float,
    val onHoldCurrency: String,
    @PrimaryKey
    val orderId: String,
    val orderType: String,
    val price: Float,
    val status: String,
    val feeCurrency: String? = null,
    val amountRemaining: Float = amount - filledAmount,
    val filledAmountQuote: Float = amount * price,
    val timeInForce: String = "GTC",
    val postOnly: Boolean? = null,
    val amountQuote: Float? = null,
    val amountQuoteRemaining: Float? = null,
    val updatedTimestamp: Long? = null,
    @Embedded val fills: List<Fill>? = null,
    val tradeIds: List<String>? = null
    )

@JsonClass(generateAdapter = true)
data class Fill(
        val amount: Float,
        val fee: Float,
        val feeCurrency: String,
        val id: String,
        val price: Float,
        val settled: Boolean,
        val taker: Boolean,
        val timestamp: Long
)
