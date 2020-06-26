package me.amryousef.converter.data.remote

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import me.amryousef.converter.domain.CurrencyMetadata
import me.amryousef.converter.domain.CurrencyRate
import javax.inject.Inject

class RemoteCurrencyRepositoryMapper @Inject constructor(private val gson: Gson) {
    @Throws(IllegalStateException::class, JsonSyntaxException::class)
    fun map(apiData: Map<String, Any>, flagsData: Map<String, String>) =
        apiData
            .entries
            .find { entry -> entry.key == "rates" }
            ?.let { entry ->
                val rates = entry.value as Map<String, Double>
                rates.map { rateEntry ->
                    val currencyCode = rateEntry.key
                    CurrencyRate(
                        currency = CurrencyMetadata(
                            currencyCode = currencyCode,
                            flagUrl = flagsData[currencyCode]
                        ),
                        rate = rateEntry.value
                    )
                }.toMutableList().apply {
                    add(
                        0,
                        CurrencyRate(
                            CurrencyMetadata(
                                currencyCode = apiData["base"] as String,
                                flagUrl = flagsData[apiData["base"] as String]
                            ),
                            1.0,
                            true
                        )
                    )

                }.toList()
            } ?: throw IllegalStateException()
}