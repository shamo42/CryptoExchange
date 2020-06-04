package com.zeitheist.cryptoexchangelib.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "zhCurrencyPair")
data class ZHCurrencyPair(
        val exchangeId: Int,
        val symbolId: String,
        val symbolFormatted: String,
        val isTrading: Boolean = true,
        val isPreTrading: Boolean = false,

        @PrimaryKey
        val pairId: String = "$exchangeId${symbolId.toUpperCase()}"

)

