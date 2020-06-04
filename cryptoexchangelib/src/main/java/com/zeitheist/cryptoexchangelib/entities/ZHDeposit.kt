package com.zeitheist.cryptoexchangelib.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "zhDeposit")
@JsonClass(generateAdapter = true)
data class ZHDeposit(
    val address: String?,
    val amount: Float,
    val fee: Float,
    val symbol: String,
    @PrimaryKey
    val timestamp: Long,
    val id: String? = null
)