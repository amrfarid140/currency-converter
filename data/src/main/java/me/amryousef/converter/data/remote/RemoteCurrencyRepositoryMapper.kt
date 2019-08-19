package me.amryousef.converter.data.remote

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import me.amryousef.converter.domain.CurrencyRate
import java.util.Currency
import javax.inject.Inject

class RemoteCurrencyRepositoryMapper @Inject constructor(private val gson: Gson) {
    @Throws(IllegalStateException::class, JsonSyntaxException::class)
    fun map(apiData: Map<String, Any>) =
        apiData
            .entries
            .find { entry -> entry.key == "rates" }
            ?.let { entry ->
                val rates = entry.value as Map<String, Double>
                rates.map { rateEntry ->
                    CurrencyRate(
                        currency = Currency.getInstance(rateEntry.key),
                        rate = rateEntry.value
                    )
                }.toMutableList().apply {
                    add(
                        0,
                        CurrencyRate(
                            Currency.getInstance(apiData["base"] as String),
                            1.0
                        )
                    )

                }.toList()
            } ?: throw IllegalStateException()
}