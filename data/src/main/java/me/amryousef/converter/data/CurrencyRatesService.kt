package me.amryousef.converter.data

import io.reactivex.Single
import retrofit2.http.GET

interface CurrencyRatesService {
    @GET("latest?base=EUR")
    fun getLatestRates(): Single<Map<String, String>>
}