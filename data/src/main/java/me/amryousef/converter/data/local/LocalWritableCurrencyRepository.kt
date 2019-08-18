package me.amryousef.converter.data.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import me.amryousef.converter.domain.CurrencyRate
import me.amryousef.converter.domain.WritableCurrencyRepository
import java.util.Currency
import javax.inject.Inject

class LocalWritableCurrencyRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : WritableCurrencyRepository {

    private companion object {
        const val DATA_KEY = "data"
    }

    override fun addCurrencyRates(rates: List<CurrencyRate>) = Completable.fromCallable {
        val mappedRates = rates.map {
            WritableCurrencyRate(it.currency.currencyCode, it.rate)
        }
        sharedPreferences.edit()
            .putString(
                DATA_KEY,
                gson.toJson(mappedRates)
            )
            .apply()
    }

    override fun observeCurrencyRates(): Observable<List<CurrencyRate>> = Single.fromCallable {
        val mapTypeToken = object : TypeToken<List<WritableCurrencyRate>>() {}.type
        gson.fromJson<List<WritableCurrencyRate>>(
            sharedPreferences.getString(DATA_KEY, "[]"),
            mapTypeToken
        ).map { rate ->
            CurrencyRate(
                currency = Currency.getInstance(rate.currencyCode),
                rate = rate.rate
            )
        }
    }.toObservable()

    private data class WritableCurrencyRate(
        @SerializedName("currencyCode")
        val currencyCode: String,
        @SerializedName("rate")
        val rate: Double
    )
}