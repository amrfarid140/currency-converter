package me.amryousef.converter.data.remote

import io.reactivex.Single
import retrofit2.http.GET

interface CountryCodeService {
    @GET("currency.json")
    fun getCountryCodes(): Single<Map<String, String>>
}