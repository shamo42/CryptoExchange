package com.zeitheist.cryptoexchangelib.pojo.common

//@Entity(tableName = "currencypair")
data class ZHCurrencyPair(
        val exchangeId: Int,
        val symbolId: String,
        val symbolFormatted: String,
        val isTrading: Boolean = true,
        val isPreTrading: Boolean = false,

        //@PrimaryKey
        //val pairId: Int = ("$exchangeId${symbolId.toUpperCase()}").hashCode()
        val pairId: String = "$exchangeId${symbolId.toUpperCase()}"

)

