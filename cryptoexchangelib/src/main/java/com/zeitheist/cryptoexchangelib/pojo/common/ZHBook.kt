package com.zeitheist.cryptoexchangelib.pojo.common

data class ZHBook(
	val asks: Map<Double, Double> = mutableMapOf<Double, Double>(),
	val bids: Map<Double, Double> = mutableMapOf<Double, Double>()
		//val lastUpdated: Long = System.currentTimeMillis()
)