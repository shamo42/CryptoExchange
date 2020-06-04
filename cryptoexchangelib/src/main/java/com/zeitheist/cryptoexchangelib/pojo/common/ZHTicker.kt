package com.zeitheist.cryptoexchangelib.pojo.common


//@Entity(tableName = "commonticker")
data class CommonTicker(
        val exchangeId: Int,
        val symbol: String,

        val lastPrice: Float,

        val bidPrice: Float = 0F,
        val bidQty: Float = 0F,
        val askPrice: Float = 0F,
        val askQty: Float = 0F,

        var symbolFormatted: String = symbol,

        val epochDateMs: Long = System.currentTimeMillis(),
        var changePercent1: Float = 0F,
        var changePercent5: Float = 0F,
        var changePercent15: Float = 0F,
        var changePercent60: Float = 0F,
        var changePercent1440: Float = 0F,

        //@PrimaryKey
        val pairId: String = "$exchangeId${symbol.toUpperCase()}"
)



enum class TickerPercentageType(val value: Int) {
    ONE_MIN(1),
    FIVE_MIN(5),
    //TEN_MIN(10),
    FIFTEEN_MIN(15),
    ONE_HOUR(60),
    ONE_DAY(1440)
}

