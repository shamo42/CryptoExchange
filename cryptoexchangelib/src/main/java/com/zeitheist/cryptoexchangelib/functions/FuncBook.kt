package com.zeitheist.cryptoexchangelib.functions

import com.kyoapps.zkotlinextensions.objects.ResultObject
import com.zeitheist.cryptoexchangelib.entities.ZHBook
import java.util.*

internal object FuncBook {


    fun updateBook(mainBookResult: ResultObject<ZHBook>, deltaBookResult: ResultObject<ZHBook>): ResultObject<ZHBook> {
        if (mainBookResult is ResultObject.Error) return mainBookResult
        if (deltaBookResult !is ResultObject.Success) return mainBookResult
        return if (mainBookResult is ResultObject.Success) {
            ResultObject.Success<ZHBook>(
                ZHBook(
                    getUpdatedAsks(mainBookResult.data.asks, deltaBookResult.data.asks),
                    getUpdatedBids(mainBookResult.data.bids, deltaBookResult.data.bids)
                )
            )
        } else ResultObject.Error<ZHBook>(UnknownError())
    }

    private fun getUpdatedAsks(mainAsks: Map<Double, Double>, deltaAsks: Map<Double, Double>): SortedMap<Double,Double> {
        val resultMap = mainAsks.toMutableMap()
        resultMap.putAll(deltaAsks)
        return resultMap.filter { it.value != 0.0 }.toSortedMap()
    }
    private fun getUpdatedBids(mainBids: Map<Double, Double>, deltaBids: Map<Double, Double>): SortedMap<Double,Double> {
        val resultMap = mainBids.toMutableMap()
        resultMap.putAll(deltaBids)
        return resultMap.filter { it.value != 0.0 }.toSortedMap(compareByDescending { it })
    }


    private const val TAG = "FuncBook"
}
