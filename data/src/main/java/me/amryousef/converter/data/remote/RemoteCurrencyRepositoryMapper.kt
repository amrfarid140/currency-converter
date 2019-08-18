package me.amryousef.converter.data.remote

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import me.amryousef.converter.domain.CurrencyRate
import java.util.Currency
import javax.inject.Inject

class RemoteCurrencyRepositoryMapper @Inject constructor(private val gson: Gson) {
    @Throws(IllegalStateException::class, JsonSyntaxException::class)
    fun map(apiData: Map<String, String>) =
        apiData
            .entries
            .find { entry -> entry.key == "rates" }
            ?.let { entry ->
                val mapTypeToken = object : TypeToken<Map<String, Double>>() {}.type
                val rates = gson.fromJson<Map<String, Double>>(entry.value, mapTypeToken)
                rates.map { rateEntry ->
                    CurrencyRate(
                        currency = Currency.getInstance(rateEntry.key),
                        rate = rateEntry.value
                    )
                }
            } ?: throw IllegalStateException()
}