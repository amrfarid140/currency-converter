package me.amryousef.converter.data.remote

import retrofit2.http.GET

interface CurrencyRatesService {
    @GET("rates")
    suspend fun getLatestRates(): Map<String, Any>
}