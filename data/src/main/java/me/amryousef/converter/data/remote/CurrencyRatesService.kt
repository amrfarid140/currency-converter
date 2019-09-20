package me.amryousef.converter.data.remote

import io.reactivex.Single
import retrofit2.http.GET

interface CurrencyRatesService {
    @GET("function-1")
    fun getLatestRates(): Single<Map<String, Any>>
}