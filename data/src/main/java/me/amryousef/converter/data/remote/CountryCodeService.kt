package me.amryousef.converter.data.remote

import retrofit2.http.GET

interface CountryCodeService {
    @GET("countries")
    suspend fun getCountryCodes(): Map<String, String>
}