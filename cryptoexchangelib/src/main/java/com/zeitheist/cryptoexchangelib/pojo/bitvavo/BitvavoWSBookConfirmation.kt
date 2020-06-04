package com.zeitheist.cryptoexchangelib.pojo.bitvavo

internal data class BitvavoWSBookConfirmation(
    val event: String,
    val subscriptions: SubBook
)


internal data class SubBook(
        val book: List<String>
)